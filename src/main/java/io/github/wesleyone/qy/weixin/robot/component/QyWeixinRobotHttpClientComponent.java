package io.github.wesleyone.qy.weixin.robot.component;

import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotResponse;

import java.util.function.Supplier;

/**
 * HTTP请求模块接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinRobotHttpClientComponent<T> extends QyWeixinRobotComponent {

    /**
     * 同步请求
     * @param key       机器人KEY
     * @param message   请求消息
     * @return  发送结果
     */
    QyWeixinRobotResponse send(String key, QyWeixinRobotBaseMessage message);

    /**
     * 异步回调请求
     * @param key       机器人KEY
     * @param message   请求消息
     * @param supplier  回调函数
     */
    void sendAsync(String key, QyWeixinRobotBaseMessage message, Supplier<T> supplier);

    /**
     * 上传文件
     * @param key       机器人KEY
     * @param filename  文件名称
     * @param content   文件字节数组
     */
    QyWeixinRobotResponse uploadMedia(String key, String filename, byte[] content);
}
