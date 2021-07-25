package io.github.wesleyone.qy.weixin.robot.enhance;

import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseAsyncMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinTextMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinQueueProcessStrategyTest {

    private DefaultQyWeixinQueueProcessStrategy strategy;

    @Before
    public void setUp() {
        strategy = new DefaultQyWeixinQueueProcessStrategy();
        strategy.init();
    }

    @After
    public void tearDown() {
        strategy.shutdown();
    }

    @Test
    public void addProcess() {
        QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("TEST");
        LinkedBlockingQueue<QyWeixinBaseAsyncMessage> blockingQueue = new LinkedBlockingQueue<>(1024);
        strategy.addProcess(qyWeixinTextMessage, blockingQueue);
        Assert.assertFalse(blockingQueue.isEmpty());
        Assert.assertEquals(1, blockingQueue.size());
    }

    @Test
    public void consumeProcess() {
        QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("TEST");
        LinkedBlockingQueue<QyWeixinBaseAsyncMessage> blockingQueue = new LinkedBlockingQueue<>(1024);
        QyWeixinBaseAsyncMessage qyWeixinBaseAsyncMessage = strategy.consumeProcess(blockingQueue);
        Assert.assertNull(qyWeixinBaseAsyncMessage);
        strategy.addProcess(qyWeixinTextMessage, blockingQueue);
        qyWeixinBaseAsyncMessage = strategy.consumeProcess(blockingQueue);
        Assert.assertNotNull(qyWeixinBaseAsyncMessage);
    }

}