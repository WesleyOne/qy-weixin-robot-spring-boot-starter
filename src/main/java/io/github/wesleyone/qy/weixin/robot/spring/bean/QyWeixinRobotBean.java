package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotClient;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotHttpClient;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseAsyncMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseMessage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * 适配SpringBean
 * <p>详情见{@link QyWeixinRobotClient}
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotBean implements ApplicationContextAware, BeanNameAware, InitializingBean, DisposableBean {

    private ApplicationContext applicationContext;

    private String beanName;

    private QyWeixinRobotClient qyWeixinRobotClient;

    private final String[] keyArray;

    private final int capacity;

    public QyWeixinRobotBean(@NonNull String[] keyArray) {
        this.capacity = 1024;
        this.keyArray = keyArray;
    }

    public QyWeixinRobotBean(@NonNull int capacity,@NonNull String[] keyArray) {
        this.capacity = capacity;
        this.keyArray = keyArray;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        // Http请求客户端，自动从容器中获取
        QyWeixinRobotHttpClient qyWeixinRobotHttpClient
                = applicationContext.getBean(QyWeixinRobotHttpClient.class);
        // 消息队列处理策略，自动从容器中获取
        QyWeixinQueueProcessStrategy qyWeixinQueueProcessStrategy
                = applicationContext.getBean(QyWeixinQueueProcessStrategy.class);
        // 调度执行器，自动从容器中获取
        QyWeixinRobotScheduledExecutorService qyWeixinRobotScheduledExecutorService
                = applicationContext.getBean(QyWeixinRobotScheduledExecutorService.class);

        QyWeixinRobotClient qyWeixinRobotClient
                = new QyWeixinRobotClient(capacity, qyWeixinRobotHttpClient, qyWeixinQueueProcessStrategy, qyWeixinRobotScheduledExecutorService, keyArray);
        qyWeixinRobotClient.init();
        this.qyWeixinRobotClient = qyWeixinRobotClient;
    }

    public String getBeanName() {
        return beanName;
    }

    public boolean postMsgSync(QyWeixinBaseMessage message) {
        return qyWeixinRobotClient.postMsgSync(message);
    }

    public void postMsgAsyncDirect(QyWeixinBaseMessage message) {
        qyWeixinRobotClient.postMsgAsyncDirect(message);
    }

    public boolean postMsgAsyncQueue(QyWeixinBaseAsyncMessage message) {
        return qyWeixinRobotClient.postMsgAsyncQueue(message);
    }

    @Override
    public void destroy() {
        qyWeixinRobotClient.destroy();
    }
}
