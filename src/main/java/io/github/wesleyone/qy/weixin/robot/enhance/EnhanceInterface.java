package io.github.wesleyone.qy.weixin.robot.enhance;

/**
 * 增强接口
 * @author http://wesleyone.github.io/
 */
public interface EnhanceInterface {
    /**
     * 初始化
     */
    default void init() {
    }
    /**
     * 销毁
     */
    default void shutdown() {
    }
}
