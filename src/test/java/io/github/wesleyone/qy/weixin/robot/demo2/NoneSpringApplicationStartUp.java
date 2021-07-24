package io.github.wesleyone.qy.weixin.robot.demo2;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotClient;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinTextMessage;

/**
 * 非SpringBoot环境使用参考
 * @author http://wesleyone.github.io/
 */
public class NoneSpringApplicationStartUp {

    public static void main(String[] args) {
        QyWeixinRobotClient qyWeixinRobotClient
                = new QyWeixinRobotClient(Constant.WEBHOOK_URL_KEY);
        // 初始化
        qyWeixinRobotClient.init();

        // 异步发送(推荐，防止被限流)
        final QyWeixinTextMessage batchMessage
                = new QyWeixinTextMessage("文本类型异步发送测试");
        qyWeixinRobotClient.postMsgAsyncQueue(batchMessage);

        // 同步发送(不推荐，容易被限流)
        final QyWeixinTextMessage textMessage
                = new QyWeixinTextMessage("文本类型同步发送测试");
        qyWeixinRobotClient.postMsgSync(textMessage);

    }
}
