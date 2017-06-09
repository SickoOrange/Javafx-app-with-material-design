package org.tum.project.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * this is a thread pool
 * execute the async task for the app
 * Created by heylbly on 17-6-9.
 */
public class TaskExecutorPool {


    private static ThreadPoolExecutor executor;

    public static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(20, 30, 200, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));
        }
        return executor;
    }

}
