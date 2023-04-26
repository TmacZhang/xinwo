package com.cavalry.androidlib.toolbox.managers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class ThreadPoolManager {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT +1;//核心线程数
    private static final int MAXMUM_POOL_SIZE = CPU_COUNT*2 +1;//非核心线程数
    private static final int KEEP_ALIVE = 1;//非核心线程存活时间

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingDeque<>(32);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"ThreadPoolManager # ");
        }
    };

    private static ThreadPoolExecutor mExcutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            sPoolWorkQueue,
            sThreadFactory);

    public static ThreadPoolExecutor getThreadPoolExecutor(){
        return mExcutor;
    }

    private ThreadPoolManager(){}
}
