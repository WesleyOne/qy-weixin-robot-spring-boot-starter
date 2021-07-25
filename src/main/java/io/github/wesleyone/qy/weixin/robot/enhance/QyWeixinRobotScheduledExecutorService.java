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
    private long initialDelay = 5;
    /**
     * 调度配置-运行间隔延迟时长
     * <p>仅初始化时可设置
     */
    private long delay = 5;
    /**
     * 调度配置-时长单位
     * <p>仅初始化时可设置
     */
    private TimeUnit unit = TimeUnit.SECONDS;
    /**
     * 任务间隔执行方式
     * <p>true: scheduleAtFixedRate按照上次执行开始时间加上延迟时间。（推荐，减少延迟）
     * <p>false: scheduleWithFixedDelay按照本次执行结束时间加上延迟时间。
     */
    private boolean isAtFixedRate = true;

    private final ScheduledExecutorService scheduledExecutorService;

    public QyWeixinRobotScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public QyWeixinRobotScheduledExecutorService(long initialDelay, long delay, TimeUnit unit, boolean isAtFixedRate, ScheduledExecutorService scheduledExecutorService) {
        setInitialDelay(initialDelay);
        setDelay(delay);
        setUnit(unit);
        setAtFixedRate(isAtFixedRate);
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void shutdown() {
        getScheduledExecutorService().shutdown();
    }

    public ScheduledFuture<?> scheduled(Runnable command) {
        if (isAtFixedRate()) {
            return this.scheduledExecutorService.scheduleAtFixedRate(command,getInitialDelay(),getDelay(),getUnit());
        } else {
            return this.scheduledExecutorService.scheduleWithFixedDelay(command,getInitialDelay(),getDelay(),getUnit());
        }
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("initialDelay < 0");
        }
        this.initialDelay = initialDelay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay < 0");
        }
        this.delay = delay;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public boolean isAtFixedRate() {
        return isAtFixedRate;
    }

    public void setAtFixedRate(boolean atFixedRate) {
        isAtFixedRate = atFixedRate;
    }

}
