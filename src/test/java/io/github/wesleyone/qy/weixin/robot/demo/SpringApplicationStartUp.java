package io.github.wesleyone.qy.weixin.robot.demo;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotTextMessage;
import io.github.wesleyone.qy.weixin.robot.spring.bean.QyWeixinRobotGroupBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * SpringBoot环境使用参考
 * @author http://wesleyone.github.io/
 */
@SpringBootApplication
public class SpringApplicationStartUp {

    /**
     * 新建群对象
     */
    @Bean
    public QyWeixinRobotGroupBean groupABean() {
        // 传入机器人KEY参数值
        return new QyWeixinRobotGroupBean(Constant.KEY_LIST);
    }

    public static void main(String[] args) {
        final ApplicationContext context = SpringApplication.run(SpringApplicationStartUp.class, args);
        final QyWeixinRobotGroupBean groupABean = context.getBean("groupABean", QyWeixinRobotGroupBean.class);

        // 发送消息
        final QyWeixinRobotTextMessage textMessage
                = new QyWeixinRobotTextMessage("文本类型异步发送测试(推荐，防止被限流)");
        groupABean.send(textMessage);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            // ignore
        }
        System.exit(0);
    }

}
