package io.github.wesleyone.qy.weixin.robot2.spring.bean;

import io.github.wesleyone.qy.weixin.robot2.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot2.client.QyWeixinRobotGroupClientApi;
import io.github.wesleyone.qy.weixin.robot2.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot2.component.QyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot2.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

/**
 * 适配SpringBean
 * <p>详情见{@link QyWeixinRobotGroupClient}
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotGroupBean implements ApplicationContextAware, BeanNameAware, InitializingBean, DisposableBean, QyWeixinRobotGroupClientApi {

    private ApplicationContext applicationContext;
    private String beanName;
    private List<String> keyList;
    private QyWeixinRobotGroupClient groupClient;

    public QyWeixinRobotGroupBean(List<String> keyList) {
        this.keyList = keyList;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        QyWeixinRobotHttpClientComponent httpClientComponent
                = applicationContext.getBean(QyWeixinRobotHttpClientComponent.class);
        QyWeixinRobotQueueManagerFactoryBean queueManagerFactoryBean
                = applicationContext.getBean(QyWeixinRobotQueueManagerFactoryBean.class);
        QyWeixinRobotQueueManagerComponent queueManagerComponent
                = queueManagerFactoryBean.getObject();
        QyWeixinRobotScheduledManagerComponent scheduledManagerComponent
                = applicationContext.getBean(QyWeixinRobotScheduledManagerComponent.class);

        QyWeixinRobotGroupClient groupClient
                = new QyWeixinRobotGroupClient(this.keyList, httpClientComponent, queueManagerComponent, null, scheduledManagerComponent);
        groupClient.init();
        this.groupClient = groupClient;
    }

    @Override
    public void destroy() {
        if (groupClient != null) {
            groupClient.destroy();
        }
    }


    @Override
    public QyWeixinRobotResponse send(QyWeixinRobotBaseMessage message) {
        if (groupClient == null) {
            return QyWeixinRobotResponse.err("客户端为初始化");
        }
        return groupClient.send(message);
    }

    @Override
    public QyWeixinRobotResponse sendDirect(QyWeixinRobotBaseMessage message) {
        if (groupClient == null) {
            return QyWeixinRobotResponse.err("客户端为初始化");
        }
        return groupClient.sendDirect(message);
    }

    @Override
    public QyWeixinRobotResponse uploadMedia(String filename, byte[] content) {
        if (groupClient == null) {
            return QyWeixinRobotResponse.err("客户端为初始化");
        }
        return groupClient.uploadMedia(filename, content);
    }
}
