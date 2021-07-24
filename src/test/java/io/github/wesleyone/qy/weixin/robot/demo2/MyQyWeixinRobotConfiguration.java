package io.github.wesleyone.qy.weixin.robot.demo2;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.enhance.DefaultQyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactoryImpl;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotHttpClient;
import io.github.wesleyone.qy.weixin.robot.spring.bean.QyWeixinRobotBean;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    /**
     * 添加机器人B
     * @return 机器人B
     */
    @Bean
    public QyWeixinRobotBean robotB() {
        return new QyWeixinRobotBean(Constant.WEBHOOK_URL_KEY_B);
    }

    /**
     * 自定义实现{@link QyWeixinRobotHttpClient}
     *
     * @return Http请求客户端
     */
    @Bean
    @Primary
    public QyWeixinRobotHttpClient myHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        QyWeixinRobotHttpClient qyWeixinRobotHttpClient = new QyWeixinRobotHttpClient();
        qyWeixinRobotHttpClient.setClient(client);
        return qyWeixinRobotHttpClient;
    }

    /**
     * 自定义实现{@link QyWeixinQueueProcessStrategy}
     *
     * @return 异步消息策略
     */
    @Bean
    @Primary
    public QyWeixinQueueProcessStrategy myQueueProcessStrategy() {
        final DefaultQyWeixinQueueProcessStrategy strategy
                = new DefaultQyWeixinQueueProcessStrategy();
        strategy.setMaxBatchMsgCounts(10);
        return strategy;
    }

    /**
     * 自定义实现{@link QyWeixinRobotScheduledExecutorService}
     *
     * @return 调度线程池执行器
     */
    @Bean
    @Primary
    public QyWeixinRobotScheduledExecutorService myScheduledExecutorService() {
        final ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(2,new QyWeixinRobotThreadFactoryImpl("my-weixin-demo-")
                );
        return new QyWeixinRobotScheduledExecutorService(0,1,TimeUnit.SECONDS, true, scheduledExecutorService);
    }
}
