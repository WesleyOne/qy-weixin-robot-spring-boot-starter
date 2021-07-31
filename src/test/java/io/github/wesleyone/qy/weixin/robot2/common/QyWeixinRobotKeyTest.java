package io.github.wesleyone.qy.weixin.robot2.common;

import io.github.wesleyone.qy.weixin.robot2.BasicMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotKeyTest {

    public static class Mock extends BasicMock {
    }

    private static final String KEY_NAME = "xxx";

    @Test
    public void getKey() {
        QyWeixinRobotKey qyWeixinRobotKey = new QyWeixinRobotKey(KEY_NAME);
        Assert.assertEquals(KEY_NAME, qyWeixinRobotKey.getKey());
    }

    @Test
    public void decrement() {
        QyWeixinRobotKey qyWeixinRobotKey = new QyWeixinRobotKey(KEY_NAME);
        for (int i=0;i<25;i++) {
            QyWeixinRobotKey.SelectKey decrement = qyWeixinRobotKey.decrement();
            if (i>=20) {
                Assert.assertNull(decrement);
            } else {
                Assert.assertNotNull(decrement);
            }
        }
    }

    @Test
    public void increment() {
        QyWeixinRobotKey qyWeixinRobotKey = new QyWeixinRobotKey(KEY_NAME);
        QyWeixinRobotKey.SelectKey selectKey = null;
        for (int i=0;i<25;i++) {
            QyWeixinRobotKey.SelectKey decrement = qyWeixinRobotKey.decrement();
            if (i>=20) {
                Assert.assertNull(decrement);
            } else {
                selectKey = decrement;
                Assert.assertNotNull(decrement);
            }
        }
        Assert.assertNotNull(selectKey);
        // 返还一个
        int increment = qyWeixinRobotKey.increment(selectKey);
        Assert.assertEquals(1, increment);
        // 获取一个并验证
        QyWeixinRobotKey.SelectKey decrement = qyWeixinRobotKey.decrement();
        Assert.assertNotNull(decrement);
        // 校验
        for (int i=0;i<25;i++) {
            increment = qyWeixinRobotKey.increment(selectKey);
            if (i>=20) {
                Assert.assertEquals(20, increment);
            } else {
                Assert.assertEquals(i+1, increment);
            }
        }
    }
}