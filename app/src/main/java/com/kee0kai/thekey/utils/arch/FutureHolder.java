package com.kee0kai.thekey.utils.arch;

import com.kee0kai.thekey.utils.Logs;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureHolder<T> {

    private Future<T> future = null;

    public Future<T> set(Future<T> future) {
        return this.future = future;
    }

    public Future<T> get() {
        return future;
    }

    public boolean isInProcess() {
        return future != null && !future.isDone() && !future.isCancelled();
    }

    public void cancel() {
        if (future != null && !future.isDone())
            future.cancel(false);
    }


    /**
     * Ждем результат
     */
    public T tryAwait() {
        try {
            return future != null ? future.get() : null;
        } catch (Exception e) {
            Logs.w(e);
            return null;
        }
    }

    /**
     * получаем результат
     */
    public T getResult() {
        try {
            return future != null && future.isDone() ? future.get(1, TimeUnit.MICROSECONDS) : null;
        } catch (Exception e) {
            Logs.w(e);
            return null;
        }
    }

    /**
     * получаем последний результат и сбрасываем задачу
     */
    public T popResult() {
        try {
            T res = future != null && future.isDone() ? future.get(1, TimeUnit.MICROSECONDS) : null;
            if (res != null)
                future = null;
            return res;
        } catch (Exception e) {
            Logs.w(e);
            return null;
        }
    }


}
