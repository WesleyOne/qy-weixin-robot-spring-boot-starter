package io.github.wesleyone.qy.weixin.robot.demo1;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.spring.bean.QyWeixinRobotBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>可在{@code @Configuration}注解的类里，添加多个机器人。
 * <p>可通过依赖注入指定beanName使用,如下所示：
 * <pre>{@code
 * @Autowired
 * private QyWeixinRobotBean robotA;
 * @Autowired</p>
 * private QyWeixinRobotBean robotB;
 * }
 * </pre>
 * <p>可通过实现扩展点优先使用自定义配置
 * <p>{@code @Primary}注解指定最高优先级配置
 * <ul>
 *     <li>自定义Http请求客户端</li>
 *     <li>自定义异步消息处理策略</li>
 *     <li>自定义调度线程池执行器</li>
 * </ul>
 *
 *
 * @author http://wesleyone.github.io/
 */
@Configuration
public class MyQyWeixinRobotConfiguration {

    /**
     * <p>可在{@code @Configuration}注解的类里，添加多个机器人。
     * <p>可通过依赖注入指定beanName使用,如下所示：
     * <p>@Autowired</p>
     * <p>private QyWeixinRobotBean robotA;</p>
     * <p>@Autowired</p>
     * <p>private QyWeixinRobotBean robotB;</p>
     */
    @Bean
    public QyWeixinRobotBean robotA() {
        return new QyWeixinRobotBean(Constant.WEBHOOK_URL_KEY);
    }
}
