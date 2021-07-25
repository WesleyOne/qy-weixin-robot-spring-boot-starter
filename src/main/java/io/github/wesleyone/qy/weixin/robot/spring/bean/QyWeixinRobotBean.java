package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotClient;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotHttpClient;
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
public class QyWeixinRobotBean extends QyWeixinRobotClient implements ApplicationContextAware, BeanNameAware, InitializingBean, DisposableBean {

    private ApplicationContext applicationContext;

    private String beanName;

    public QyWeixinRobotBean(@NonNull String... key) {
        super(key);
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
        // Http请求客户端，未手动setter时，自动从容器中获取
        if (getQyWeixinRobotHttpClient() == null) {
            QyWeixinRobotHttpClient qyWeixinRobotHttpClient
                    = applicationContext.getBean(QyWeixinRobotHttpClient.class);
            setQyWeixinRobotHttpClient(qyWeixinRobotHttpClient);
        }
        // 消息队列处理策略，未手动setter时，自动从容器中获取
        if (getStrategy() == null) {
            QyWeixinQueueProcessStrategy qyWeixinQueueProcessStrategy
                    = applicationContext.getBean(QyWeixinQueueProcessStrategy.class);
            setStrategy(qyWeixinQueueProcessStrategy);
        }
        // 调度执行器，未手动setter时，自动从容器中获取
        if (getScheduledExecutorService() == null) {
            QyWeixinRobotScheduledExecutorService qyWeixinRobotScheduledExecutorService
                    = applicationContext.getBean(QyWeixinRobotScheduledExecutorService.class);
            setScheduledExecutorService(qyWeixinRobotScheduledExecutorService);
        }
        super.init();
    }

    public String getBeanName() {
        return beanName;
    }
}
