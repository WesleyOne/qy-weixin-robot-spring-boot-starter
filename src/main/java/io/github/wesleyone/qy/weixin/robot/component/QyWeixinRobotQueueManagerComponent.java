package io.github.wesleyone.qy.weixin.robot.component;

import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotBaseMessage;

import java.util.List;

/**
 * 消息队列管理器接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinRobotQueueManagerComponent extends QyWeixinRobotComponent {

    /**
     * 添加消息到队列
     * @param message   消息
     * @return  true添加成功
     */
    boolean provide(QyWeixinRobotBaseMessage message);

    /**
     * 消费消息队列
     * @param maxCount 最大返回消息数量
     * @return 返回消息
     */
    List<QyWeixinRobotBaseMessage> consume(int maxCount);
}
