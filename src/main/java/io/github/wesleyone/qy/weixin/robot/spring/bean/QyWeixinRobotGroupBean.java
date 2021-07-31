package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClientApi;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotResponse;
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
        // HTTP请求客户端
        QyWeixinRobotHttpClientComponent httpClientComponent
                = applicationContext.getBean(QyWeixinRobotHttpClientComponent.class);
        // 消息队列管理器（工厂类产出多例）
        QyWeixinRobotQueueManagerFactoryBean queueManagerFactoryBean
                = applicationContext.getBean(QyWeixinRobotQueueManagerFactoryBean.class);
        QyWeixinRobotQueueManagerComponent queueManagerComponent
                = queueManagerFactoryBean.getObject();
        // KEY管理器（工厂类产出多例）
        QyWeixinRobotKeyManagerFactoryBean keyManagerFactoryBean
                = applicationContext.getBean(QyWeixinRobotKeyManagerFactoryBean.class);
        QyWeixinRobotKeyManagerComponent keyManagerComponent
                = keyManagerFactoryBean.getObject();
        // 周期调度管理器
        QyWeixinRobotScheduledManagerComponent scheduledManagerComponent
                = applicationContext.getBean(QyWeixinRobotScheduledManagerComponent.class);
        // 构建群客户端
        QyWeixinRobotGroupClient groupClient
                = new QyWeixinRobotGroupClient(this.keyList, httpClientComponent, queueManagerComponent, keyManagerComponent, scheduledManagerComponent);
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
            return QyWeixinRobotResponse.err("客户端未初始化:"+getBeanName());
        }
        return groupClient.send(message);
    }

    @Override
    public QyWeixinRobotResponse sendDirect(QyWeixinRobotBaseMessage message) {
        if (groupClient == null) {
            return QyWeixinRobotResponse.err("客户端未初始化:"+getBeanName());
        }
        return groupClient.sendDirect(message);
    }

    @Override
    public QyWeixinRobotResponse uploadMedia(String filename, byte[] content) {
        if (groupClient == null) {
            return QyWeixinRobotResponse.err("客户端未初始化:"+getBeanName());
        }
        return groupClient.uploadMedia(filename, content);
    }

    public String getBeanName() {
        return beanName;
    }
}
