package io.github.wesleyone.qy.weixin.robot2.component.impl;

import io.github.wesleyone.qy.weixin.robot2.component.QyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotBaseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 默认消息队列管理
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotQueueManagerComponent implements QyWeixinRobotQueueManagerComponent {

    private final BlockingQueue<QyWeixinRobotBaseMessage> queue;

    public DefaultQyWeixinRobotQueueManagerComponent() {
        this(1 << 10);
    }

    public DefaultQyWeixinRobotQueueManagerComponent(int queueSize) {
        queue = new LinkedBlockingQueue<>(queueSize);
    }

    @Override
    public boolean provide(QyWeixinRobotBaseMessage message) {
        return queue.offer(message);
    }

    @Override
    public List<QyWeixinRobotBaseMessage> consume(int count) {
        List<QyWeixinRobotBaseMessage> consumeList = new ArrayList<>();
        queue.drainTo(consumeList, count);
        return consumeList;
    }
}
