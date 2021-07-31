package io.github.wesleyone.qy.weixin.robot.demo;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotTextMessage;

import java.util.concurrent.TimeUnit;

/**
 * 非SpringBoot环境使用参考
 * @author http://wesleyone.github.io/
 */
public class NoneSpringApplicationStartUp {

    public static void main(String[] args) {
        QyWeixinRobotGroupClient groupClient
                = new QyWeixinRobotGroupClient(Constant.KEY_LIST);
        // 初始化
        groupClient.init();

        // 异步发送(推荐，防止被限流)
        final QyWeixinRobotTextMessage textMessage
                = new QyWeixinRobotTextMessage("文本类型异步发送测试(推荐，防止被限流)");
        groupClient.send(textMessage);

        // 同步发送(不推荐，容易被限流)
        final QyWeixinRobotTextMessage textMessage2
                = new QyWeixinRobotTextMessage("文本类型同步发送测试(不推荐，容易被限流)");
        groupClient.sendDirect(textMessage2);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            // ignore
        }
        System.exit(0);
    }
}
