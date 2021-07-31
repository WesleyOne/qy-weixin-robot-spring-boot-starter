package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotKeyManagerComponent;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotKeyManagerFactoryBean implements FactoryBean<QyWeixinRobotKeyManagerComponent> {

    public final Class<? extends QyWeixinRobotKeyManagerComponent> clazz;

    public QyWeixinRobotKeyManagerFactoryBean(Class<? extends QyWeixinRobotKeyManagerComponent> clazz) {
        this.clazz = clazz;
    }

    @Override
    public QyWeixinRobotKeyManagerComponent getObject() throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return QyWeixinRobotKeyManagerComponent.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
