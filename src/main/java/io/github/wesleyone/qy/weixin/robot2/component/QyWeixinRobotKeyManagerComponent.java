package io.github.wesleyone.qy.weixin.robot2.component;

import io.github.wesleyone.qy.weixin.robot2.common.QyWeixinRobotKey;

import java.util.List;

/**
 * KEY管理器接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinRobotKeyManagerComponent extends QyWeixinRobotComponent {

    /**
     * 选择活跃KEY策略
     * @param count 选择数量
     * @return  返回活跃的KEY
     */
    List<QyWeixinRobotKey.SelectKey> selectKeys(int count);

    /**
     * 恢复使用次数
     * @param key   KEY
     */
    void recover(QyWeixinRobotKey.SelectKey key);



}
