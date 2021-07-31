package io.github.wesleyone.qy.weixin.robot.component.impl;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotKey;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotKeyManagerComponentTest {

    public static class Mock extends BasicMock {
    }

    private static final List<String> KEY_LIST = Arrays.asList("KEY1","KEY2","KEY3");

    private QyWeixinRobotGroupClient qyWeixinRobotClient;

    @Before
    public void setUp() {
        QyWeixinRobotScheduledManagerComponent scheduledExecutorService
                = new DefaultQyWeixinRobotScheduledManagerComponent(0,1, TimeUnit.SECONDS,true
                , Executors.newSingleThreadScheduledExecutor(new QyWeixinRobotThreadFactory("qy-weixin-test-")));
        qyWeixinRobotClient = new QyWeixinRobotGroupClient(Constant.KEY_LIST,null,null,null,scheduledExecutorService);
        qyWeixinRobotClient.init();
    }

    @Test
    public void selectKeys() {
        DefaultQyWeixinRobotKeyManagerComponent keyManagerComponent = new DefaultQyWeixinRobotKeyManagerComponent();
        keyManagerComponent.init(qyWeixinRobotClient);
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
        DefaultQyWeixinRobotKeyManagerComponent keyManagerComponent = new DefaultQyWeixinRobotKeyManagerComponent();
        keyManagerComponent.init(qyWeixinRobotClient);
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