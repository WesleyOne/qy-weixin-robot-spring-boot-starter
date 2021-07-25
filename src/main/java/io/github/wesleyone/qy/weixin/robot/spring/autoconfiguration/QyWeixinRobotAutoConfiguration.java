package io.github.wesleyone.qy.weixin.robot.spring.autoconfiguration;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactoryImpl;
import io.github.wesleyone.qy.weixin.robot.enhance.DefaultQyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotHttpClient;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自动装配类
 * <ul>
 *     <li>默认Http请求客户端</li>
 *     <li>默认异步消息策略</li>
 *     <li>默认调度线程池执行器</li>
 * </ul>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotAutoConfiguration {

    /**
     * 默认Http请求客户端，全局机器人共用策略，减少资源占用
     * <p>自定义实现{@link QyWeixinRobotHttpClient}可以覆盖当前默认值
     * @return  Http请求客户端
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinRobotHttpClient.class)
    public QyWeixinRobotHttpClient defaultQyWeixinRobotApiClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5,5L,TimeUnit.MINUTES))
                .build();
        QyWeixinRobotHttpClient qyWeixinRobotHttpClient = new QyWeixinRobotHttpClient();
        qyWeixinRobotHttpClient.setClient(client);
        return qyWeixinRobotHttpClient;
    }

    /**
     * 默认异步消息策略，全局机器人共用策略，减少资源占用
     * <p>自定义实现{@link QyWeixinQueueProcessStrategy}可以覆盖当前默认执行器
     *
     * @return 异步消息策略
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinQueueProcessStrategy.class)
    public QyWeixinQueueProcessStrategy defaultQyWeixinConsumeQueueStrategy() {
        return new DefaultQyWeixinQueueProcessStrategy();
    }

    /**
     * 默认提供调度线程池执行器，全局机器人共用线程，减少资源占用
     * <p>自定义实现{@link QyWeixinRobotScheduledExecutorService}可以覆盖当前默认执行器
     *
     * @return 调度线程池执行器
     */
    @Bean
    @ConditionalOnMissingBean(QyWeixinRobotScheduledExecutorService.class)
    public QyWeixinRobotScheduledExecutorService defaultQyWeixinRobotScheduledExecutorService() {
        final ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(
                        new QyWeixinRobotThreadFactoryImpl("QyWeixinRbt-"));
        return new QyWeixinRobotScheduledExecutorService(scheduledExecutorService);
    }

}
