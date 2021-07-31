package io.github.wesleyone.qy.weixin.robot.component.impl;

import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotKey;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotKeyManagerComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认你KEY管理器
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotKeyManagerComponent implements QyWeixinRobotKeyManagerComponent {

    /**
     * KEY配置及活跃管理
     * <p>KEY:key值
     * <p>VALUE:KEY信息
     */
    private final Map<String, QyWeixinRobotKey> keyRobotMap = new ConcurrentHashMap<>();

    @Override
    public void init(QyWeixinRobotGroupClient client) {
        // 添加KEY值
        List<String> keyList = client.getKeyList();
        for (String key : keyList) {
            keyRobotMap.put(key, new QyWeixinRobotKey(key));
        }
    }

    @Override
    public List<QyWeixinRobotKey.SelectKey> selectKeys(int count) {
        int index = 0;
        int maxCount = Math.min(keyRobotMap.keySet().size(), count);
        List<QyWeixinRobotKey.SelectKey> selectKeys = new ArrayList<>();
        for (QyWeixinRobotKey robotKey : keyRobotMap.values()) {
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
        keyRobotMap.get(key.getKey()).increment(key);
    }
}
