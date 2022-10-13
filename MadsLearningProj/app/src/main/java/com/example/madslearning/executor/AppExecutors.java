package com.example.madslearning.executor;

import android.util.Log;

import com.example.bitmapdemo.TaskType;
import com.example.madslearning.utils.DeviceUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

public class AppExecutors {
    private static final String TAG = "AppExecutors";
    private static final int THREAD_BACKGROUND_PRIORITY = 3;
    private int mBgCoreCount;
    private static volatile AppExecutors mAppExecutors;
    private ThreadPoolExecutor mBackgroundService;
    private ExecutorService mIOService;
    private ExecutorService mNetworkService;

    private AppExecutors(){

    }

    public static AppExecutors get() {
        if (mAppExecutors == null) {
            synchronized (AppExecutors.class) {
                if (mAppExecutors == null) {
                    mAppExecutors = new AppExecutors();
                }
            }
        }
        return mAppExecutors;
    }

    private synchronized void createBackgroundExecutor() {
        if (mBackgroundService == null) {
            int CPUCount = DeviceUtils.getNumberOfCPUCores();
            if (CPUCount < 2) {
                CPUCount = 2;
            }
            mBackgroundService = new ThreadPoolExecutor(CPUCount + 2, CPUCount + 2, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("background_thread", 3));
            mBackgroundService.allowCoreThreadTimeOut(true);//允许核心线程超时释放
        }
    }

    private synchronized void createIoExecutor() {
        if (mIOService == null) {
            mIOService = Executors.newFixedThreadPool(2, new NamedThreadFactory("IO_thread", 3));
        }
    }

    private synchronized void createNetworkExecutor(){
        if (mNetworkService == null) {
            mNetworkService = Executors.newFixedThreadPool(2, new NamedThreadFactory("Network_thread", 3));
        }
    }

    /**
     * 在Rxjava2上不能直接发射null值，所以需要强制返回0
     */
    public Disposable execute(TaskType type, Runnable task) {
        return execute(type, () -> {
            if (task != null) {
                task.run();
            }
            return 0;
        }, null, null);
    }

    public <T> Disposable execute(TaskType type, Callable<T> task, final Consumer<T> UICallback) {
        return execute(type, task, UICallback, null);
    }

    public Disposable execute(TaskType type, Runnable task, final Consumer<Throwable> errorHandle) {
        return execute(type,  () -> {
            if (task != null) {
                task.run();
            }
            return 0;
        }, null, errorHandle);
    }

    public <T> Disposable execute(TaskType type, Callable<T> task, final Consumer<T> UICallback, final Consumer<Throwable> errorHandler) {
        Scheduler scheduler;
        switch (type) {
            case BACKGROUND: {
                if (mBackgroundService == null) {
                    createBackgroundExecutor();
                }
                scheduler = Schedulers.from(mBackgroundService);
                break;
            }
            case IO: {
                if (mIOService == null) {
                    createIoExecutor();
                }
                scheduler = Schedulers.from(mIOService);
                break;
            }
            case NETWORK:{
                if (mNetworkService == null){
                    createNetworkExecutor();
                }
                scheduler = Schedulers.from(mNetworkService);
                break;
            }
            case WORK:{
                scheduler = Schedulers.newThread();
                break;
            }
            default:{
                throw new IllegalArgumentException("task type is not supported!!!");
            }
        }
        Single<T> single = Single.fromCallable(task).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        return errorHandler == null ? single.subscribe(t -> {
            if (UICallback != null) {
                UICallback.accept(t);
            }
        }) : single.subscribe(t -> {
            if (UICallback != null) {
                UICallback.accept(t);
            }
        }, throwable -> {
            if (errorHandler != null) {
                errorHandler.accept(throwable);
            }
        });
    }

    public Disposable executeDelay(TaskType type, int delay, Runnable task) {
        return executeDelay(type, delay,  () -> {
            if (task != null) {
                task.run();
            }
            return 0;
        }, null, null);
    }

    public <T> Disposable executeDelay(TaskType type, int delay ,Callable<T> task, final Consumer<T> UICallback) {
        return executeDelay(type, delay, task, UICallback, null);
    }

    public Disposable executeDelay(TaskType type, int delay, Runnable task, final Consumer<Throwable> errorHandler) {
        return executeDelay(type, delay,  () -> {
            if (task !=null) {
                task.run();
            }
            return 0;
        }, null, errorHandler);
    }

    public <T> Disposable executeDelay(TaskType type, int delay ,Callable<T> task, final Consumer<T> UICallback, final Consumer<Throwable> errorHandler) {
        Scheduler scheduler;
        switch (type) {
            case BACKGROUND: {
                if (mBackgroundService == null) {
                    createBackgroundExecutor();
                }
                scheduler = Schedulers.from(mBackgroundService);
                break;
            }
            case IO: {
                if (mIOService == null) {
                    createIoExecutor();
                }
                scheduler = Schedulers.from(mIOService);
                break;
            }
            case NETWORK:{
                if (mNetworkService == null){
                    createNetworkExecutor();
                }
                scheduler = Schedulers.from(mNetworkService);
                break;
            }
            case WORK:{
                scheduler = Schedulers.newThread();
                break;
            }
            default:{
                throw new IllegalArgumentException("task type is not supported!!!");
            }
        }
        Single<T> single = Single.just(0).delay(delay, TimeUnit.MILLISECONDS).map(new Function<Integer, T>() {
            @Override
            public T apply(Integer integer) throws Exception {
                return task.call();
            }
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        return errorHandler == null ? single.subscribe(t -> {
            if (UICallback != null) {
                UICallback.accept(t);
            }
        }) : single.subscribe(t -> {
            if (UICallback != null) {
                UICallback.accept(t);
            }
        }, throwable -> {
            if (errorHandler != null) {
                errorHandler.accept(throwable);
            }
        });
    }

    public final static void cancel(Disposable task) {
        if (!task.isDisposed()) {
            task.dispose();
        }
    }
}
