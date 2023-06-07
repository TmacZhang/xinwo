package com.xinwo.produce.gestureheart.mediacodec;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 25623 on 2018/5/9.
 */

public class MediaThreadPool {
    public static ExecutorService executorService = Executors.newFixedThreadPool(6);
}
