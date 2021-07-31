package io.github.wesleyone.qy.weixin.robot2.common;

import java.util.Collection;

/**
 * 工具类
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotUtil {

    /**
     * 集合判非空
     * @param collection    集合
     * @return  true非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 集合判空
     * @param collection    集合
     * @return  true空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return !isNotEmpty(collection);
    }

}
