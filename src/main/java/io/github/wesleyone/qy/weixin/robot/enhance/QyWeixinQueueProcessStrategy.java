package io.github.wesleyone.qy.weixin.robot.enhance;

import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseAsyncMessage;

import java.util.concurrent.BlockingQueue;

/**
 * 消息队列处理策略接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinQueueProcessStrategy extends EnhanceInterface {

    /**
     * 添加消息处理
     *
     * @param message   消息
     * @param msgQueue  等待队列
     * @return  true添加成功
     */
    boolean addProcess(QyWeixinBaseAsyncMessage message, BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue);

    /**
     * 一个周期处理一次异步消息队列
     * <p>产出一条消息
     * @param msgQueue  消息队列
     * @return 消息
     */
    QyWeixinBaseAsyncMessage consumeProcess(BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue);
}
