package com.android.esdudnik.animatedprogressbar.ui.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * author   : Eugene Dudnik
 * date     : 1/11/17
 * e-mail   : esdudnik@gmail.com
 */
public class ViewRefreshHandler {

    private static final long MINI_SECS_ONE_MINUTE = 1000 * 60;
    private static final long MINI_SECS_ONE_SECOND = 1000;

    private final Handler mHandler;
    private final Map<Class<? extends ViewRunnable>, RunnableDecorator> decoratorMap;

    public ViewRefreshHandler() {
        mHandler = new Handler(Looper.getMainLooper());
        decoratorMap = new HashMap<>();
    }

    public final void executePerSecond(ViewRunnable task) {
        executePeriodically(task, MINI_SECS_ONE_SECOND);
    }

    public final void executePerMinute(ViewRunnable task) {
        executePeriodically(task, MINI_SECS_ONE_MINUTE);
    }

    public final void executePerInterval(ViewRunnable task, long interval) {
        executePeriodically(task, interval);
    }

    private final void executePeriodically(final ViewRunnable task, final long interval) {
        cancelPendingTask(task);

        /**
         * creates a decorator class of the runnable being passed in.
         * after executing the run method in the runnable, call {@link #scheduleNext(Class, long)}
         * to schedule the next call.
         */
        final RunnableDecorator runnableDecorator = new RunnableDecorator(task, interval);
        mHandler.post(runnableDecorator);

        decoratorMap.put(task.getClass(), runnableDecorator);
    }


    private class RunnableDecorator implements Runnable {

        private final ViewRunnable runnable;
        private final long interval;

        public RunnableDecorator(ViewRunnable runnable, long interval) {
            this.runnable = runnable;
            this.interval = interval;
        }

        @Override
        public void run() {
            runnable.run();
            if (runnable.viewRef.get() != null && !runnable.terminate) {
                scheduleNext(this, interval);
            } else {
                // stop refreshing when the View is gone.
                cancelPendingTask(runnable);
            }
        }
    }

    private void scheduleNext(RunnableDecorator runnable, long interval) {
        if (runnable != null) {
            mHandler.postDelayed(runnable, interval);
        }
    }

    private void cancelPendingTask(ViewRunnable task) {
        if (task != null) {
            RunnableDecorator runnableDecorator = decoratorMap.get(task.getClass());
            if (runnableDecorator != null) {
                mHandler.removeCallbacks(runnableDecorator);
                decoratorMap.remove(task.getClass());
            }
        }
    }

    public final void cancelAll() {
        mHandler.removeCallbacksAndMessages(null);
        decoratorMap.clear();
    }

    /**
     * A subclass class of {@link Runnable} which only holds a WeakReference of a View object,
     * <p>
     * as a result, it avoids the problem of memory leak caused by not releasing references of View objects.
     *
     * @param <T> - the View instance to be operated on.
     */
    public static abstract class ViewRunnable<T extends View> implements Runnable {
        private final WeakReference<T> viewRef;
        private final Bundle mArgs;
        private boolean terminate = false; // set true to stop executing repeating tasks

        public ViewRunnable(T view, @Nullable Bundle args) {
            viewRef = new WeakReference<>(view);
            if (args == null) {
                mArgs = new Bundle();
            } else {
                mArgs = args;
            }
        }

        @Override
        public final void run() {
            T view = viewRef.get();
            if (view != null) {
                run(view, mArgs);
            }
        }

        /**
         * stop scheduling tasks
         */
        protected final void terminate() {
            terminate = true;
        }

        protected abstract void run(T view, Bundle args);
    }
}
