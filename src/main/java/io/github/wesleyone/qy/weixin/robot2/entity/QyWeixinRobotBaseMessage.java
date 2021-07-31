package io.github.wesleyone.qy.weixin.robot2.entity;


import io.github.wesleyone.qy.weixin.robot2.common.QyWeixinRobotMessageType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求消息的抽象基类
 * @author http://wesleyone.github.io/
 */
public abstract class QyWeixinRobotBaseMessage implements Serializable {

    private static final long serialVersionUID = 7577427585722658279L;
    /**
     * 创建时间戳
     */
    private final long timestamp = System.currentTimeMillis();
    /**
     * 消息类型
     * @return 消息类型
     */
    public abstract QyWeixinRobotMessageType getMsgType();

    /**
     * 消息内容
     * @return 消息内容
     */
    protected abstract Map<String,Object> getMsgBody();

    /**
     * 请求消息体Map
     * @return 请求消息体Map
     */
    public Map<String,Object> toMap() {
        if (getMsgType() == null) {
            throw new IllegalArgumentException("msgtype is null");
        }
        if (getMsgBody() == null) {
            throw new IllegalArgumentException("msg body is null");
        }
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("msgtype", getMsgType());
        resultMap.put(getMsgType().name(), getMsgBody());
        return resultMap;
    }

    @Override
    public String toString() {
        return toMap().toString();
    }
}
