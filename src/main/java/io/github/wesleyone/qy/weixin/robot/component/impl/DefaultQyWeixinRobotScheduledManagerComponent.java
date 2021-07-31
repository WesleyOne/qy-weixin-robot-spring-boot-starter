package io.github.wesleyone.qy.weixin.robot.component.impl;

import io.github.wesleyone.qy.weixin.robot.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 默认调度管理器
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotScheduledManagerComponent implements QyWeixinRobotScheduledManagerComponent {

    /**
     * 调度配置-首次运行延迟时长
     * <p>仅初始化时可设置
     */
    private final long initialDelay;
    /**
     * 调度配置-运行间隔延迟时长
     * <p>仅初始化时可设置
     */
    private final long delay;
    /**
     * 调度配置-时长单位
     * <p>仅初始化时可设置
     */
    private final TimeUnit unit;
    /**
     * 任务间隔执行方式
     * <p>true: scheduleAtFixedRate按照上次执行开始时间加上延迟时间。（推荐，减少延迟）
     * <p>false: scheduleWithFixedDelay按照本次执行结束时间加上延迟时间。
     */
    private final boolean isAtFixedRate;

    private final ScheduledExecutorService scheduledExecutorService;

    public DefaultQyWeixinRobotScheduledManagerComponent() {
        this(Executors.newSingleThreadScheduledExecutor(new QyWeixinRobotThreadFactory("QY-WEIXIN-ROB-SCHEDULE-")));
    }

    public DefaultQyWeixinRobotScheduledManagerComponent(ScheduledExecutorService scheduledExecutorService) {
        this(3,3,TimeUnit.SECONDS,true,scheduledExecutorService);
    }

    public DefaultQyWeixinRobotScheduledManagerComponent(long initialDelay, long delay, TimeUnit unit, boolean isAtFixedRate, ScheduledExecutorService scheduledExecutorService) {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.unit = unit;
        this.isAtFixedRate = isAtFixedRate;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void destroy(QyWeixinRobotGroupClient client) {
        scheduledExecutorService.shutdown();
    }

    @Override
    public ScheduledFuture<?> scheduled(Runnable command) {
        ScheduledFuture<?> scheduledFuture;
        if (isAtFixedRate) {
            scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, delay, unit);
        } else {
            scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
        return scheduledFuture;
    }
}
