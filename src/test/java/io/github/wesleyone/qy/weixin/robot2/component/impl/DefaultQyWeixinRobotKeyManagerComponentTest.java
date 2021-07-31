package io.github.wesleyone.qy.weixin.robot2.component.impl;

import io.github.wesleyone.qy.weixin.robot2.BasicMock;
import io.github.wesleyone.qy.weixin.robot2.common.QyWeixinRobotKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotKeyManagerComponentTest {

    public static class Mock extends BasicMock {
    }

    private static final List<String> KEY_LIST = Arrays.asList("KEY1","KEY2","KEY3");

    @Test
    public void selectKeys() {
        DefaultQyWeixinRobotKeyManagerComponent keyManagerComponent = new DefaultQyWeixinRobotKeyManagerComponent(KEY_LIST);
        for (int i=0;i<21;i++) {
            List<QyWeixinRobotKey.SelectKey> selectKeys = keyManagerComponent.selectKeys(3);
            if (i>=20) {
                Assert.assertEquals(0, selectKeys.size());
            } else {
                Assert.assertEquals(3, selectKeys.size());
            }
        }
    }

    @Test
    public void recover() {
        DefaultQyWeixinRobotKeyManagerComponent keyManagerComponent = new DefaultQyWeixinRobotKeyManagerComponent(KEY_LIST);
        List<QyWeixinRobotKey.SelectKey> selectKeys = null;
        for (int i=0;i<21;i++) {
            List<QyWeixinRobotKey.SelectKey> keyList = keyManagerComponent.selectKeys(3);
            if (i>=20) {
                Assert.assertEquals(0, keyList.size());
            } else {
                selectKeys = keyList;
                Assert.assertEquals(3, keyList.size());
            }
        }
        selectKeys.forEach(keyManagerComponent::recover);
        List<QyWeixinRobotKey.SelectKey> keyList = keyManagerComponent.selectKeys(3);
        Assert.assertEquals(3, keyList.size());
    }
}