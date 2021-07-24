package io.github.wesleyone.qy.weixin.robot.demo1;

import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinTextMessage;
import io.github.wesleyone.qy.weixin.robot.spring.bean.QyWeixinRobotBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * SpringBoot环境使用参考
 * @author http://wesleyone.github.io/
 */
@SpringBootApplication
public class SpringApplicationStartUp {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringApplicationStartUp.class, args);
        final BizBean bizBean = context.getBean(BizBean.class);
        bizBean.postMsgAsync();
        bizBean.postMsgSync();
    }

    @Component
    public static class BizBean {

        @Autowired
        private QyWeixinRobotBean robotA;

        public void postMsgAsync () {
            // 延迟批量聚合发送(推荐，防止被限流)
            final QyWeixinTextMessage batchMessage
                    = new QyWeixinTextMessage("文本类型异步发送测试-spring");
            robotA.postMsgAsyncQueue(batchMessage);
        }

        public void postMsgSync () {
            // 直接发送文本(不推荐，容易被限流)
            final QyWeixinTextMessage textMessage
                    = new QyWeixinTextMessage("文本类型同步发送测试-spring");
            robotA.postMsgSync(textMessage);
        }
    }

}
