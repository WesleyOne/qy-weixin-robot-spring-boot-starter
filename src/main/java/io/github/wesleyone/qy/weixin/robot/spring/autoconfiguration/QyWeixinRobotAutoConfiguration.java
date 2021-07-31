package io.github.wesleyone.qy.weixin.robot.spring.autoconfiguration;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotRetrofit2HttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.spring.bean.QyWeixinRobotKeyManagerFactoryBean;
import io.github.wesleyone.qy.weixin.robot.spring.bean.QyWeixinRobotQueueManagerFactoryBean;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自动装配类
 * <ul>
 *     <li>默认Http请求客户端</li>
 *     <li>默认消息队列管理器</li>
 *     <li>默认调度线程池执行器</li>
 * </ul>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotAutoConfiguration {

    /**
     * 默认Http请求客户端，全局机器人共用策略，减少资源占用
     * <p>自定义实现{@link QyWeixinRobotHttpClientComponent}可以覆盖当前默认值
     * @return  Http请求客户端
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinRobotHttpClientComponent.class)
    public QyWeixinRobotHttpClientComponent defaultQyWeixinRobotHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5,5L,TimeUnit.MINUTES))
                .build();
        return new DefaultQyWeixinRobotRetrofit2HttpClientComponent(client);
    }

    /**
     * 默认消息队列管理器工厂类。
     * <p>注意，每个群必须各自有一个。
     * <p>自定义实现{@link QyWeixinRobotQueueManagerFactoryBean}、{@link QyWeixinRobotQueueManagerComponent}
     * @return 消息队列管理器工厂类
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinRobotQueueManagerFactoryBean.class)
    public QyWeixinRobotQueueManagerFactoryBean defaultQueueManagerFactoryBean() {
        return new QyWeixinRobotQueueManagerFactoryBean(DefaultQyWeixinRobotQueueManagerComponent.class);
    }

    /**
     * 默认KEY管理器工厂类
     * <p>注意，每个群必须各自有一个。
     * <p>自定义实现{@link QyWeixinRobotKeyManagerFactoryBean}、{@link QyWeixinRobotKeyManagerComponent}
     * @return  KEY管理器工厂类
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinRobotKeyManagerFactoryBean.class)
    public QyWeixinRobotKeyManagerFactoryBean defaultKeyManagerFactoryBean() {
        return new QyWeixinRobotKeyManagerFactoryBean(DefaultQyWeixinRobotKeyManagerComponent.class);
    }

    /**
     * 默认提供调度线程池执行器，全局机器人共用线程，减少资源占用
     * <p>自定义实现{@link QyWeixinRobotScheduledManagerComponent}可以覆盖当前默认执行器
     *
     * @return 调度线程池执行器
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinRobotScheduledManagerComponent.class)
    public QyWeixinRobotScheduledManagerComponent defaultQyWeixinRobotScheduledManager() {
        final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(3,
               new QyWeixinRobotThreadFactory("QY-WEIXIN-ROB-SCHEDULE-SPR-"));
        return new DefaultQyWeixinRobotScheduledManagerComponent(3,3,TimeUnit.SECONDS, true, scheduledExecutorService);
    }

}
