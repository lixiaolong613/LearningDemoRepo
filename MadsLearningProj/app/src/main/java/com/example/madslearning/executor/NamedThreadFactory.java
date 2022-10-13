package com.example.madslearning.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final ThreadFactory mDefaultThreadFactory = Executors.defaultThreadFactory();
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final String baseString;
    private final int priority;
    public NamedThreadFactory(String baseString, int priority) {
        this.baseString = baseString;
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = mDefaultThreadFactory.newThread(r);
        thread.setName(baseString + "_" + this.poolNumber.getAndIncrement());
        thread.setPriority(this.priority);
        return thread;
    }
}
