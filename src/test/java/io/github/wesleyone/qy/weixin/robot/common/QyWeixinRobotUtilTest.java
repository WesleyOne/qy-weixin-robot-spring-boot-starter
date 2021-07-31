package io.github.wesleyone.qy.weixin.robot.common;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotUtilTest {

    public static class Mock extends BasicMock {
    }

    @Test
    public void isNotEmpty() {
        List<Object> emptyList = Collections.emptyList();
        boolean notEmpty = QyWeixinRobotUtil.isNotEmpty(emptyList);
        Assert.assertFalse(notEmpty);

        List<String> singletonList = Collections.singletonList("hello");
        notEmpty = QyWeixinRobotUtil.isNotEmpty(singletonList);
        Assert.assertTrue(notEmpty);
    }

    @Test
    public void isEmpty() {
        List<Object> emptyList = Collections.emptyList();
        boolean empty = QyWeixinRobotUtil.isEmpty(emptyList);
        Assert.assertTrue(empty);

        List<String> singletonList = Collections.singletonList("hello");
        empty = QyWeixinRobotUtil.isEmpty(singletonList);
        Assert.assertFalse(empty);
    }
}