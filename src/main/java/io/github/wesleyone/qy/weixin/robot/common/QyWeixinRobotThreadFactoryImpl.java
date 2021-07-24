package io.github.wesleyone.qy.weixin.robot.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程工厂
 * <ul>
 * <li>指定线程前缀
 * <li>指定是否守护线程
 * </ul>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotThreadFactoryImpl implements ThreadFactory {
    private static final AtomicLong THREAD_INDEX = new AtomicLong(0);
    private final String threadNamePrefix;
    private final boolean daemon;

    public QyWeixinRobotThreadFactoryImpl(final String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public QyWeixinRobotThreadFactoryImpl(final String threadNamePrefix, boolean daemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread thread = new Thread(r, threadNamePrefix + THREAD_INDEX.getAndIncrement());
        thread.setDaemon(daemon);
        return thread;
    }

}
