package com.cavalry.androidlib.toolbox.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class LibIoUtils {

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                closeable = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除目录及目录下的文件
     * @param dir
     * @throws IOException
     */
    public static void deleteDirectory(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
        if(dir.exists()){
            dir.delete();
        }
    }
}
