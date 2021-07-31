package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotRetrofit2HttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotScheduledManagerComponent;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义配置
 * @author http://wesleyone.github.io/
 */
@Configuration
public class MyQyWeixinRobotConfiguration {

    @Bean
    public QyWeixinRobotGroupBean groupABean() {
        return new QyWeixinRobotGroupBean(Constant.KEY_LIST);
    }

    /**
     * 自定义HTTP请求客户端
     * @return HTTP请求客户端
     */
    @Bean
    @Primary
    public QyWeixinRobotHttpClientComponent myQyWeixinRobotHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5,5L,TimeUnit.MINUTES))
                .build();
        return new DefaultQyWeixinRobotRetrofit2HttpClientComponent(client);
    }

    /**
     * 自定义消息队列管理器工厂（多例）
     * @return  消息队列管理器工厂
     */
    @Bean
    @Primary
    public QyWeixinRobotQueueManagerFactoryBean myQueueManagerFactoryBean() {
        return new QyWeixinRobotQueueManagerFactoryBean(DefaultQyWeixinRobotQueueManagerComponent.class);
    }

    /**
     * 自定义KEY管理器工厂（多例）
     * @return  KEY管理器工厂
     */
    @Bean
    @Primary
    public QyWeixinRobotKeyManagerFactoryBean myKeyManagerFactoryBean() {
        return new QyWeixinRobotKeyManagerFactoryBean(DefaultQyWeixinRobotKeyManagerComponent.class);
    }

    /**
     * 自定义调度线程池执行器
     * @return 调度线程池执行器
     */
    @Bean
    @Primary
    public QyWeixinRobotScheduledManagerComponent myQyWeixinRobotScheduledExecutorService() {
        final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                new QyWeixinRobotThreadFactory("QY-WEIXIN-ROB-SCHEDULE-SPR-"));
        return new DefaultQyWeixinRobotScheduledManagerComponent(0,1,TimeUnit.SECONDS, true, scheduledExecutorService);
    }
}
