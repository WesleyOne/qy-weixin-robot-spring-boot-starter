package io.github.wesleyone.qy.weixin.robot.enhance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotScheduledExecutorServiceTest {
    private QyWeixinRobotScheduledExecutorService qyWeixinRobotScheduledExecutorService;
    @Before
    public void setUp() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        qyWeixinRobotScheduledExecutorService = new QyWeixinRobotScheduledExecutorService(0,1,TimeUnit.SECONDS,true,executorService);
        qyWeixinRobotScheduledExecutorService.init();
    }

    @After
    public void tearDown() {
        qyWeixinRobotScheduledExecutorService.shutdown();
    }

    @Test
    public void shutdown() {
        qyWeixinRobotScheduledExecutorService.shutdown();
    }

    @Test
    public void scheduled() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        qyWeixinRobotScheduledExecutorService.scheduled(countDownLatch::countDown);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(true);
    }
}