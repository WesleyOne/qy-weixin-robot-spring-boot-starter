package io.github.wesleyone.qy.weixin.robot.entity;

/**
 * 异步消息的抽象基类
 * @author http://wesleyone.github.io/
 */
public abstract class QyWeixinBaseAsyncMessage extends QyWeixinBaseMessage {

    private static final long serialVersionUID = 6990796118641718328L;
    /**
     * 创建时间
     * <p>可用于异步消息处理
     */
    private final long createTime = System.currentTimeMillis();

    public long getCreateTime() {
        return createTime;
    }
}
