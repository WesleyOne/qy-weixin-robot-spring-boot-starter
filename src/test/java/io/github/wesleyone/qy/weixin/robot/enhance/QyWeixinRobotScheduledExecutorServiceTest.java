package io.github.wesleyone.qy.weixin.robot.enhance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotScheduledExecutorServiceTest {
    private QyWeixinRobotScheduledExecutorService qyWeixinRobotScheduledExecutorService;
    @Before
    public void setUp() throws Exception {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        qyWeixinRobotScheduledExecutorService = new QyWeixinRobotScheduledExecutorService(0,1,TimeUnit.SECONDS,true,executorService);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shutdown() {
        qyWeixinRobotScheduledExecutorService.shutdown();
    }

    @Test
    public void scheduled() {
        qyWeixinRobotScheduledExecutorService.scheduled(new Runnable() {
            @Override
            public void run() {
                Assert.assertTrue(true);
            }
        });
        Assert.assertTrue(true);
    }
}