package io.github.wesleyone.qy.weixin.robot.component.impl;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotTextMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotQueueManagerComponentTest {

    public static class Mock extends BasicMock {
    }

    @Test
    public void provide() {
        QyWeixinRobotTextMessage textMessage = new QyWeixinRobotTextMessage("hello");
        DefaultQyWeixinRobotQueueManagerComponent queueManagerComponent = new DefaultQyWeixinRobotQueueManagerComponent();
        boolean provide = queueManagerComponent.provide(textMessage);
        Assert.assertTrue(provide);
    }

    @Test
    public void consume() {
        QyWeixinRobotTextMessage textMessage = new QyWeixinRobotTextMessage("hello");
        DefaultQyWeixinRobotQueueManagerComponent queueManagerComponent = new DefaultQyWeixinRobotQueueManagerComponent();
        boolean provide = queueManagerComponent.provide(textMessage);
        Assert.assertTrue(provide);
        List<QyWeixinRobotBaseMessage> messageList = queueManagerComponent.consume(1);
        Assert.assertNotNull(messageList);
        Assert.assertEquals(textMessage, messageList.get(0));
    }
}