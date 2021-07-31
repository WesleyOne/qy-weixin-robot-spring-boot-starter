package io.github.wesleyone.qy.weixin.robot2.client;

import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotResponse;

/**
 * 企业微信群机器人接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinRobotGroupClientApi {

    /**
     * 异步发送
     * @param message   消息
     * @return  响应结果
     */
    QyWeixinRobotResponse send(QyWeixinRobotBaseMessage message);

    /**
     * 同步发送
     * @param message   消息
     * @return  响应结果
     */
    QyWeixinRobotResponse sendDirect(QyWeixinRobotBaseMessage message);

    /**
     * 上传图片
     * @param filename  文件名（包含后缀）
     * @param content   文件字节数组
     * @return  响应结果
     */
    QyWeixinRobotResponse uploadMedia(String filename, final byte[] content);
}
