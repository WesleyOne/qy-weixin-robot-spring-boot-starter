package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotQueueManagerComponent;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotQueueManagerFactoryBean implements FactoryBean<QyWeixinRobotQueueManagerComponent> {

    public Class<? extends QyWeixinRobotQueueManagerComponent> clazz;

    public QyWeixinRobotQueueManagerFactoryBean(Class<? extends QyWeixinRobotQueueManagerComponent> clazz) {
        this.clazz = clazz;
    }

    @Override
    public QyWeixinRobotQueueManagerComponent getObject() throws Exception {
        return clazz.newInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return QyWeixinRobotQueueManagerComponent.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
