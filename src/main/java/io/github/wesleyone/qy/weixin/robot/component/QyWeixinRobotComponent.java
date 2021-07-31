package io.github.wesleyone.qy.weixin.robot.component;

import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClient;

/**
 * 组件接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinRobotComponent {

    /**
     * 初始化
     */
    default void init(QyWeixinRobotGroupClient client) {

    }

    /**
     * 销毁
     */
    default void destroy(QyWeixinRobotGroupClient client) {

    }
}
