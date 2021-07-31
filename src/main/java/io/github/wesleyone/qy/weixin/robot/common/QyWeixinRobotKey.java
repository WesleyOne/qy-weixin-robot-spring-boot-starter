package io.github.wesleyone.qy.weixin.robot.common;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * KEY信息
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotKey {
    /**
     * 消息发送频率限制：每个机器人发送的消息不能超过20条/分钟。
     */
    private static final int MAX_COUNTS_PER_MINUTE = 20;
    /**
     * 清理缓存的最小个数
     */
    private static final int MIN_CLEAR_SIZE = 5;
    /**
     * 机器人KEY
     */
    private final String key;
    /**
     * 维护分钟时间戳和调用次数的关系缓存
     * KEY:最近活跃分钟时戳
     * VALUE:当前分钟调用次数
     */
    private final Map<Long, AtomicInteger> timestampAndCountMap = new ConcurrentHashMap<>();

    public QyWeixinRobotKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * 尝试选择key
     * @return  选择成功，返回选择KEY信息；否则null
     */
    public SelectKey decrement() {
        long thisLiveMinute = System.currentTimeMillis() / 1000 / 60;
        AtomicInteger lastLiveMinuteCounts = timestampAndCountMap.computeIfAbsent(thisLiveMinute, new Function<Long, AtomicInteger>() {
            @Override
            public AtomicInteger apply(Long aLong) {
                // 超过大小清空map
                if (timestampAndCountMap.size() > MIN_CLEAR_SIZE) {
                    timestampAndCountMap.clear();
                }
                // 初始化当前时间值
                return new AtomicInteger(MAX_COUNTS_PER_MINUTE);
            }
        });
        if (lastLiveMinuteCounts.get() <= 0) {
            return null;
        }
        long thisLiveMinuteCounts = lastLiveMinuteCounts.getAndDecrement();
        if (thisLiveMinuteCounts > 0) {
            return new SelectKey(this.key, thisLiveMinute);
        }
        return null;
    }

    /**
     * 恢复key一次调用(之前占用的KEY未使用，恢复次数)
     * @param selectKey 选择的KEY
     */
    public int increment(SelectKey selectKey) {
        AtomicInteger counts = timestampAndCountMap.get(selectKey.getTimestamp());
        if (counts == null) {
            return 0;
        }
        if (counts.get() >= MAX_COUNTS_PER_MINUTE) {
            return MAX_COUNTS_PER_MINUTE;
        }
        if (counts.get() <= 0) {
            counts.set(1);
            return 1;
        }
        return counts.incrementAndGet();
    }

    /**
     * 选择的KEY
     */
    public static class SelectKey {
        /**
         * 选择的KEY值
         */
        private final String key;
        /**
         * 选择的KEY所在时间戳
         */
        private final long timestamp;

        public SelectKey(String key, long timestamp) {
            this.key = key;
            this.timestamp = timestamp;
        }

        public String getKey() {
            return key;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", SelectKey.class.getSimpleName() + "[", "]")
                    .add("key='" + key + "'")
                    .add("timestamp=" + timestamp)
                    .toString();
        }
    }

}
