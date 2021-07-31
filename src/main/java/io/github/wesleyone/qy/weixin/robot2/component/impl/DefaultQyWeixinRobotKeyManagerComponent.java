package io.github.wesleyone.qy.weixin.robot2.component.impl;

import io.github.wesleyone.qy.weixin.robot2.common.QyWeixinRobotKey;
import io.github.wesleyone.qy.weixin.robot2.component.AbstractQyWeixinRobotKeyManagerComponent;

import java.util.*;

/**
 * 默认你KEY管理器
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotKeyManagerComponent extends AbstractQyWeixinRobotKeyManagerComponent {

    /**
     * KEY配置及活跃管理
     * <p>KEY:key值
     * <p>VALUE:KEY信息
     */
    private final Map<String, QyWeixinRobotKey> robotKeyMap;

    public DefaultQyWeixinRobotKeyManagerComponent(List<String> keyList) {
        super(keyList);
        Map<String/*Key*/, QyWeixinRobotKey> robotKeyMap = new HashMap<>();
        for (String key : keyList) {
            QyWeixinRobotKey robotKey = new QyWeixinRobotKey(key);
            robotKeyMap.put(key,robotKey);
        }
        this.robotKeyMap = Collections.unmodifiableMap(robotKeyMap);
    }

    @Override
    public List<QyWeixinRobotKey.SelectKey> selectKeys(int count) {
        int index = 0;
        int maxCount = Math.min(robotKeyMap.keySet().size(), count);
        List<QyWeixinRobotKey.SelectKey> selectKeys = new ArrayList<>();
        for (QyWeixinRobotKey robotKey : robotKeyMap.values()) {
            QyWeixinRobotKey.SelectKey decrement = robotKey.decrement();
            if (decrement == null) {
                continue;
            }
            selectKeys.add(decrement);
            if ((++index) >= maxCount) {
                break;
            }
        }
        return selectKeys;
    }

    @Override
    public void recover(QyWeixinRobotKey.SelectKey key) {
        if (key == null || key.getKey() == null) {
            return;
        }
        robotKeyMap.get(key.getKey()).increment(key);
    }
}
