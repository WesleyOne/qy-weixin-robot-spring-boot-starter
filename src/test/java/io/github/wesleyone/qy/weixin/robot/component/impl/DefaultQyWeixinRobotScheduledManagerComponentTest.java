package io.github.wesleyone.qy.weixin.robot.component.impl;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotScheduledManagerComponentTest {

    public static class Mock extends BasicMock {
    }

    @Test
    public void destroy() {
        DefaultQyWeixinRobotScheduledManagerComponent scheduledManagerComponent
                = new DefaultQyWeixinRobotScheduledManagerComponent();
        try {
            scheduledManagerComponent.destroy(null);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void scheduled() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DefaultQyWeixinRobotScheduledManagerComponent scheduledManagerComponent
                = new DefaultQyWeixinRobotScheduledManagerComponent();
        scheduledManagerComponent.scheduled(new Runnable() {
            @Override
            public void run() {
                Assert.assertTrue(true);
                countDownLatch.countDown();
            }
        });
        Assert.assertTrue(true);
    }
}