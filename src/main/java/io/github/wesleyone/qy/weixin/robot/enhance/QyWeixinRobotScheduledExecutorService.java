package io.github.wesleyone.qy.weixin.robot.enhance;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 调度执行器
 *
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotScheduledExecutorService implements EnhanceInterface {

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

    public QyWeixinRobotScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.initialDelay = 5;
        this.delay = 5;
        this.unit = TimeUnit.SECONDS;
        this.isAtFixedRate = true;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public QyWeixinRobotScheduledExecutorService(long initialDelay, long delay, TimeUnit unit, boolean isAtFixedRate, ScheduledExecutorService scheduledExecutorService) {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.unit = unit;
        this.isAtFixedRate = isAtFixedRate;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

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
