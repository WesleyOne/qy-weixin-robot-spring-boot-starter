package io.github.wesleyone.qy.weixin.robot2.component;

import java.util.List;

/**
 * KEY管理器抽象类
 * @author http://wesleyone.github.io/
 */
public abstract class AbstractQyWeixinRobotKeyManagerComponent implements QyWeixinRobotKeyManagerComponent {

    private final List<String> keyList;

    public AbstractQyWeixinRobotKeyManagerComponent(List<String> keyList) {
        this.keyList = keyList;
    }

    public List<String> getKeyList() {
        return keyList;
    }
}
