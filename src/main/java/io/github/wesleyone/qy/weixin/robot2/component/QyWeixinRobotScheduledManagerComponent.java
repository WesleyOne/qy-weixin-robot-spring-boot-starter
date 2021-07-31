package io.github.wesleyone.qy.weixin.robot2.component;

import java.util.concurrent.ScheduledFuture;

/**
 * 调度管理器接口
 * @author http://wesleyone.github.io/
 */
public interface QyWeixinRobotScheduledManagerComponent extends QyWeixinRobotComponent {

    /**
     *  提交调度任务
     * @param command   任务
     * @return  结果
     */
    ScheduledFuture<?> scheduled(Runnable command);

}
