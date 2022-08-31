package com.kee0kai.thekey.utils.arch;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.kee0kai.thekey.BuildConfig;
import com.kee0kai.thekey.utils.Logs;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Threads {


    private static final Thread.UncaughtExceptionHandler defUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    public static Looper createLoopedThread(String nameThread) {
        HandlerThread handlerThread = new HandlerThread(nameThread);
        handlerThread.setDaemon(true);
        handlerThread.start();
        return handlerThread.getLooper();
    }

    public static void startThread(Runnable r) {
        new Thread(r).start();
    }

    public static ThreadPoolExecutor newSingleThreadExecutor(String poolName) {
        return new ThreadPoolExecutor(0, 1, 0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new Threads.DefaultThreadFactory(poolName, false),
                BuildConfig.DEBUG ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException ce) {
                        Logs.w(ce);
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    if (t == null)
                        return;
                    Logs.e(t);
                    if (BuildConfig.DEBUG && defUncaughtExceptionHandler != null)
                        defUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }
        };
    }

    /**
     * @param poolName
     * @param daemon   - рекомедуется true для потоков с бесконечными циклами
     * @return
     */
    public static ThreadPoolExecutor newSingleThreadExecutor(String poolName, boolean daemon) {
        return new ThreadPoolExecutor(0, 1, 0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new Threads.DefaultThreadFactory(poolName, daemon),
                BuildConfig.DEBUG ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException ce) {
                        Logs.w(ce);
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    if (t == null)
                        return;
                    Logs.e(t);
                    if (BuildConfig.DEBUG && defUncaughtExceptionHandler != null)
                        defUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }
        };
    }

    public static ThreadPoolExecutor newCachedPoolThreadExecutor(String poolName) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new Threads.DefaultThreadFactory(poolName, false),
                BuildConfig.DEBUG ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException ce) {
                        Logs.w(ce);
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    if (t == null)
                        return;
                    Logs.e(t);
                    if (BuildConfig.DEBUG && defUncaughtExceptionHandler != null)
                        defUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }
        };
    }


    public static void runMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static Handler runMainDelayed(Runnable runnable, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
        return handler;
    }

    // from Executors.defaultThreadFactory
    public static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final boolean isDaemonThreads;

        public DefaultThreadFactory(String poolname, boolean isDaemonThreads) {
            this.isDaemonThreads = isDaemonThreads;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = poolname + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(isDaemonThreads);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }


    }

    public static void trySleep(long millis) {
        if (millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static <T> T tryGet(Future<T> future) {
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException ignore) {
            return null;
        }
    }


}
