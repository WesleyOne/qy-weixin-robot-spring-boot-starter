package io.github.wesleyone.qy.weixin.robot.client;


import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactoryImpl;
import io.github.wesleyone.qy.weixin.robot.enhance.*;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseAsyncMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinFileMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 企业微信机器人客户端
 * <p>消息发送频率限制：每个机器人发送的消息不能超过20条/分钟。
 * <p>核心发消息方法：
 * <ul>
 *     <li>批量异步发消息{@link #postMsgAsyncQueue(QyWeixinBaseAsyncMessage)},推荐,防止被限流。</li>
 *     <li>同步发消息{@link #postMsgSync(QyWeixinBaseMessage)}</li>
 *     <li>异步发消息{@link #postMsgAsyncDirect(QyWeixinBaseMessage)}</li>
 * </ul>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotClient {

    private static final Logger logger = LoggerFactory.getLogger(QyWeixinRobotClient.class.getName());
    /**
     * 完整webhook地址
     */
    private final String key;
    /**
     * 启动状态
     */
    private boolean status = false;
    /**
     * 消息队列
     */
    private BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue;
    /**
     * API客户端
     */
    private QyWeixinRobotHttpClient qyWeixinRobotHttpClient;
    /**
     * 异步消费策略
     */
    private QyWeixinQueueProcessStrategy strategy;
    /**
     * 调度线程池执行器
     */
    private QyWeixinRobotScheduledExecutorService scheduledExecutorService;


    public QyWeixinRobotClient(String key) {
        this.key = key;
    }

    /**
     * 初始化
     */
    public synchronized void init() {
        if (status) {
            return;
        }
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (msgQueue == null) {
            this.msgQueue = new LinkedBlockingQueue<>(1024);
        }
        if (qyWeixinRobotHttpClient == null) {
            this.qyWeixinRobotHttpClient = new QyWeixinRobotHttpClient();
        }
        if (strategy == null) {
            this.strategy = new DefaultQyWeixinQueueProcessStrategy();
        }
        if (scheduledExecutorService == null) {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
                    new QyWeixinRobotThreadFactoryImpl("qy-weixin-robot-"));
            this.scheduledExecutorService = new QyWeixinRobotScheduledExecutorService(scheduledExecutorService);
        }
        this.qyWeixinRobotHttpClient.init();
        this.strategy.init();
        this.scheduledExecutorService.init();
        status = true;
    }

    private Runnable consumeQueueRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (!status) {
                    return;
                }
                // 异步处理消息
                final QyWeixinBaseAsyncMessage message = strategy.consumeProcess(msgQueue);
                if (message == null) {
                    return;
                }
                if (!status) {
                    return;
                }
                // 发送异步消息
                postMsgAsyncDirect(message);
            }
        };
    }

    /**
     * 销毁
     */
    public void destroy() {
        status = false;
        scheduledExecutorService.shutdown();
    }

    /**
     * 同步发送消息
     * <p>注意并发情形下会被限流
     *
     * @param message   消息
     */
    public boolean postMsgSync(QyWeixinBaseMessage message) {
        if (!status) {
            return false;
        }
        QyWeixinResponse response = qyWeixinRobotHttpClient.sendSync(key, message.toMap());
        if (response == null) {
            return false;
        }
        return response.isSuccess();
    }

    /**
     * 异步发送消息（非队列）
     * <p>注意并发情形下会被限流
     *
     * @param message   消息
     */
    public void postMsgAsyncDirect(QyWeixinBaseMessage message) {
        if (!status) {
            return;
        }
        // 发送异步消息
        qyWeixinRobotHttpClient.sendAsync(key, message.toMap(), new Callback<QyWeixinResponse>() {
            @Override
            public void onResponse(Call<QyWeixinResponse> call, Response<QyWeixinResponse> response) {
                if (response.isSuccessful()) {
                    logger.info("sendAsync onResponse {}",response.body());
                } else {
                    logger.error("sendAsync onResponse err {}",response);
                }
            }
            @Override
            public void onFailure(Call<QyWeixinResponse> call, Throwable e) {
                logger.error("sendAsync onFailure", e);
            }
        });
    }

    /**
     * 添加到队列，异步批量发消息（推荐）
     *
     * <p>注意事项：
     * <ul>
     * <li>队列满了快速失败返回</li>
     * <li>仅支持text和markdown类型</li>
     * <li>发送可靠性见异步消费策略{@link QyWeixinQueueProcessStrategy}</li>
     * </ul>
     *
     * @param msg   可批量发的消息
     * @return true添加队列成功
     */
    public boolean postMsgAsyncQueue(QyWeixinBaseAsyncMessage msg) {
        if (!status) {
            return false;
        }
        return this.strategy.addProcess(msg, msgQueue);
    }

    /**
     * 上传文件
     * @param path
     */
    public void uploadMedia(String path) {
        qyWeixinRobotHttpClient.uploadMedia(key, path, new Callback<QyWeixinResponse>() {
            @Override
            public void onResponse(Call<QyWeixinResponse> call, Response<QyWeixinResponse> response) {
                if (!response.isSuccessful()) {
                    logger.error("uploadMedia onResponse err {}",response);
                    return;
                }
                QyWeixinResponse body = response.body();
                if (body == null) {
                    logger.error("uploadMedia onResponse body null");
                    return;
                }
                if (!body.isSuccess()) {
                    logger.error("uploadMedia onResponse body fail {}",body);
                    return;
                }
                String mediaId = body.getMedia_id();
                QyWeixinFileMessage qyWeixinFileMessage = new QyWeixinFileMessage(mediaId);
                postMsgAsyncDirect(qyWeixinFileMessage);
                logger.info("uploadMedia onResponse suc {}",body);
            }

            @Override
            public void onFailure(Call<QyWeixinResponse> call, Throwable e) {
                logger.error("uploadMedia onFailure", e);
            }
        });
    }

    /* getter and setter */

    public void setMsgQueue(BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue) {
        if (msgQueue == null) {
            throw new IllegalArgumentException("msgQueue is null");
        }
        if (status) {
            throw new IllegalStateException("client is running");
        }
        this.msgQueue = msgQueue;
    }

    public void setScheduledExecutorService(QyWeixinRobotScheduledExecutorService scheduledExecutorService) {
        if (scheduledExecutorService == null) {
            throw new IllegalArgumentException("scheduledExecutorService is null");
        }
        if (status) {
            throw new IllegalStateException("client is running");
        }
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public QyWeixinRobotScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setStrategy(QyWeixinQueueProcessStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy is null");
        }
        if (status) {
            throw new IllegalStateException("client is running");
        }
        this.strategy = strategy;
    }

    public QyWeixinQueueProcessStrategy getStrategy() {
        return strategy;
    }

    public void setQyWeixinRobotHttpClient(QyWeixinRobotHttpClient qyWeixinRobotHttpClient) {
        if (qyWeixinRobotHttpClient == null) {
            throw new IllegalArgumentException("qyWeixinRobotHttpClient is null");
        }
        if (status) {
            throw new IllegalStateException("client is running");
        }
        this.qyWeixinRobotHttpClient = qyWeixinRobotHttpClient;
    }

    public QyWeixinRobotHttpClient getQyWeixinRobotHttpClient() {
        return qyWeixinRobotHttpClient;
    }

}
