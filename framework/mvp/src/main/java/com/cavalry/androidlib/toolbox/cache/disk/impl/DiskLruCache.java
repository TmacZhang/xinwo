package com.cavalry.androidlib.toolbox.cache.disk.impl;

import com.cavalry.androidlib.toolbox.cache.disk.DiskCache;
import com.cavalry.androidlib.toolbox.utils.LibConvertNumberUtils;
import com.cavalry.androidlib.toolbox.utils.LibIoUtils;
import com.cavalry.androidlib.toolbox.utils.LibLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * <p>实现原理:</p>
 * <p>
 * 通过一个journal文件记录key 和 size. 又以key作为文件名存储value, size是value的大小.
 * 内部有一个具有LRU功能的LinkedHashMap: lruMap,open时会将journal的内容读取到Map中,
 * 每次{@link #save(String, String)},{@link #get(String)},{@link #remove(String)}
 * 都会调用lruMap使之从新排序,每调用一定次数(cycleBase决定)会按照lruMap中数据数据的顺序,重新建立journal文件.
 *
 * 缓存使用完毕应调用{@link #close()}:
 * 1.将journalWriter关闭
 *
 * 缺点: 关闭之前没有将最近几次变动更新到journal文件中
 *
 * </p>
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class DiskLruCache implements DiskCache<String, String> {

    private final String TAG = "DiskLruCache";

    static final String JOURNAL_FILE = "androidlib_journal";
    static final String MAGIC = "com.cavalry.androidlib";
    private final static String VALUE_SEPARATOR = " ";
    private final static String CLEAN = "CLEAN";
    private final static String REMOVE = "REMOVE";

    private final LinkedHashMap<String, Long> lruMap =
            new LinkedHashMap<String, Long>(0, 0.75f, true);//存储key和对应的size

    private final File directory;
    private final long maxSize;
    private final int appVersion;

    private long currentSize;//当前缓存大小
    private File journal;
    private BufferedWriter journalWriter;

    //每一次有效的save,get,remove都会使cycleCount+1,当cycleCount==cycleBase,会触发trimAndRebuildJournal. 然后cycleCount置为0
    private int cycleCount = 0;
    private final static int DEFAULT_CYCLE_BASE = 8;


    private int cycleBase = DEFAULT_CYCLE_BASE;

    final ThreadPoolExecutor executorService =
            new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    //更新journal
    private final Callable<Void> updateJournal = new Callable<Void>() {
        public Void call() throws Exception {
            updateJournal();
            return null;
        }
    };


    /*
        JOURNAL_FILE 是一个记录读写日志的文件.格式如下:
            com.cavalry.androidlib
            1

            CLEAN 3400330d1dfc7f3f7f4b8d4d803dfcf6 832
            REMOVE 335c4c6028171cfddfbaae1a9c313c52 1048
            CLEAN 1ab96a171faeeee38496d8b330771a7a 1600

        第一行的"com.cavalry.androidlib"是一个标识
        第二行的数字是appVersion
        第三行是一个空行
        之后的数据是缓存的读写记录,以第4行为例:
            CLEAN缓存状态良好,可以读写.
            3400330d1dfc7f3f7f4b8d4d803dfcf6是缓存的key
            832是缓存的长度

        CLEAN:缓存状态良好,可以读写
        REMOVE:缓存已被移除

        缓存读写记录以lruEntries中的顺序记录.
     */

    /**
     * @param directory  缓存目录
     * @param maxSize    最大缓存,以byte为单位
     * @param appVersion 版本不同,将会清除旧缓存
     */
    private DiskLruCache(File directory, long maxSize, int appVersion) {

        this.directory = directory;
        this.maxSize = maxSize;
        this.appVersion = appVersion;
        journal = new File(directory, JOURNAL_FILE);

    }

    public static DiskLruCache open(File directory, long maxSize, int appVersion) {
        if (directory == null) {
            throw new NullPointerException("directory = null");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }

        DiskLruCache cache = new DiskLruCache(directory, maxSize, appVersion);

        try {
            if (!directory.exists()) {//缓存目录不存在,创建
                directory.mkdirs();
                cache.rebuildJournal();
            } else {
                if (!cache.journal.exists()) {//缓存目录存在,但journal文件不存在,创建journal文件
                    cache.rebuildJournal();
                } else {
                    cache.readJournal();//journal文件存在,读取其内容. 如果journal文件格式不正确,重建journal文件
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cache;
    }

    //重建journal文件
    //使用journalWriter的地方都要同步
    private synchronized void rebuildJournal() throws IOException {
        if (journal.exists()) {//日志文件存在, 删除
            journal.delete();
        }

        LibIoUtils.closeSilently(journalWriter);//如果有旧的journalWriter,需关闭

        journalWriter = new BufferedWriter(new FileWriter(journal));
        journalWriter.write(MAGIC);
        journalWriter.write("\n");
        journalWriter.write(appVersion + "");
        journalWriter.write("\n");
        journalWriter.write("\n");
        journalWriter.flush();

    }

    //检测journal文件,如果journal文件格式不正确,重建journal文件
    //将CLEAN和DIRTY的journal添加到lruEntries中,将REMOVE的journal从lruEnties中删除
    private synchronized void readJournal() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(journal));
            String magic = reader.readLine();
            String appVersionString = reader.readLine();
            String blank = reader.readLine();
            if (!MAGIC.equals(magic)
                    || !Integer.toString(appVersion).equals(appVersionString)
                    || !"".equals(blank)) {//如果JOURNAL_FILE格式不正确,并重建
                rebuildJournal();
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                readJournalLine(line);
            }

            LibIoUtils.closeSilently(journalWriter);//如果有旧的journalWriter,需关闭
            journalWriter = new BufferedWriter(new FileWriter(journal));

        } finally {
            LibIoUtils.closeSilently(reader);
        }
    }


    /**
     * 将CLEAN状态的key加入到lruMap中,将size累加到currentSize中
     * 将REMOVE状态的key从lruMap中移除,并currentSize -= size
     * 如果journal中存储的数据有问题,不会被添加到lurList中,size也不会被累加
     * @param line
     */
    private void readJournalLine(String line) {
        String[] lineArray = line.split(VALUE_SEPARATOR);

        if (lineArray != null && lineArray.length == 3) {
            String status = lineArray[0];
            String key = lineArray[1];
            long size = LibConvertNumberUtils.convert2Long(lineArray[2], 0);
            if(CLEAN.equals(status)){
                currentSize += size;
                lruMap.put(key, size);
            }else if(REMOVE.equals(status)){
                if(lruMap.containsKey(key)){
                    currentSize -= size;
                    lruMap.remove(key);
                }
            }
        }
    }


    private synchronized void updateJournal() {
        //防止升级过程中出错,原有journal损坏,所以先将journal写到临时文件中
        File tmpJournal = new File(directory, JOURNAL_FILE + ".tmp");
        if (tmpJournal.exists()) {
            tmpJournal.delete();
        }

        LibIoUtils.closeSilently(journalWriter);
        journalWriter = null;
        try {
            journalWriter = new BufferedWriter(new FileWriter(tmpJournal));
            journalWriter.write(MAGIC);
            journalWriter.write("\n");
            journalWriter.write(appVersion + "");
            journalWriter.write("\n");
            journalWriter.write("\n");

            Iterator<Map.Entry<String, Long>> iterator = lruMap.entrySet().iterator();
            Map.Entry<String, Long> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                journalWriter.write(entry.getKey() + VALUE_SEPARATOR + entry.getValue() + "\n");
            }
            journalWriter.flush();

            if (journal.exists()) {
                journal.delete();
            }

            tmpJournal.renameTo(journal);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LibIoUtils.closeSilently(journalWriter);
        }
    }

    private long sizeOf(String value) {
        byte[] bytes = value.getBytes();
        return bytes.length;
    }

    private void trimToSize() {
        while (currentSize > maxSize) {
            LibLog.d(TAG,"trimToSize,   currentSize="+currentSize+" ,maxSize="+maxSize);

            Map.Entry<String, Long> toEvict = lruMap.entrySet().iterator().next();
            remove(toEvict.getKey());
        }
    }

    private void cycleCount() {
        cycleCount = ++cycleCount % cycleBase;
        if (cycleCount == 0) {
            executorService.submit(updateJournal);
        }
    }

    public void setCycleBase(int cycleBase) {
        this.cycleBase = cycleBase;
    }

    //使用journalWriter的地方都要同步
    @Override
    public synchronized boolean save(String key, String value) {
        LibLog.d(TAG,"save");

        String smallValue = value.replace("\n","");

        long size = sizeOf(smallValue);

        LibLog.d(TAG,"save-->smallValue="+smallValue);

        BufferedWriter writer = null;
        try {
            File file = new File(directory, key);
            file.setLastModified(System.currentTimeMillis());
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(smallValue);

            //为了防止updateJournal失败,每save一次缓存append一次journal,以减少损失
            journalWriter.append(CLEAN + VALUE_SEPARATOR + key + VALUE_SEPARATOR + size + "\n");
            journalWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            //删掉记录和缓存文件
            remove(key);
            return false;
        } finally {
            LibIoUtils.closeSilently(writer);
        }

        if (lruMap.containsKey(key)) {
            currentSize -= lruMap.get(key);
        }
        currentSize += size;

        lruMap.put(key, size);
        cycleCount();

        trimToSize();
        return true;
    }


    /**
     * 获取key对应的value,如果journal中存储的此key的数据有错误, 将会删除此条缓存并返回null
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        LibLog.d(TAG,"get");

        if (lruMap.get(key) != null) {//lruMap.get(key)是为了触发lru的排序
            BufferedReader reader = null;
            try {

                File file = new File(directory, key);
                reader = new BufferedReader(new FileReader(file));

                if(file.exists()){
                    String line = reader.readLine();
                    LibLog.d(TAG,"line="+line);
                    return line;
                } else {
                    remove(key);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                LibIoUtils.closeSilently(reader);
            }
        }

        return null;
    }



    @Override
    public synchronized boolean remove(String key) {
        LibLog.d(TAG,"remove");

        try {
            long size = 0;
            if (lruMap.containsKey(key)) {
                size = lruMap.remove(key);
                currentSize -= size;
                journalWriter.append(REMOVE + VALUE_SEPARATOR + key + VALUE_SEPARATOR + size + "\n");
                journalWriter.flush();
            }

            //为了防止发生lruMap中不存在,但磁盘中仍存在的情况
            File toEvict = new File(directory, key);
            if (toEvict.exists())
                toEvict.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        cycleCount();

        return true;
    }

    /**
     * 获取此key的上次缓存时间
     *
     * @param key
     * @return 如果没有缓存过, 返回-1L
     */
    @Override
    public long getLastModified(String key) {
        if (lruMap.get(key) != null) {
            File file = new File(directory, key);
            if (file.exists()) {
                return file.lastModified();
            }
        }
        return -1L;
    }

    @Override
    public void clear() {
        try {
            if (directory.exists())
                LibIoUtils.deleteDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        LibIoUtils.closeSilently(journalWriter);
    }
}
