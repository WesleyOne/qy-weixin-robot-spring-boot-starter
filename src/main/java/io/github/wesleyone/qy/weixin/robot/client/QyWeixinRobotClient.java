package io.github.wesleyone.qy.weixin.robot.client;


import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactoryImpl;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;
import io.github.wesleyone.qy.weixin.robot.enhance.DefaultQyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotHttpClient;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseAsyncMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinFileMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

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
     * 启动状态
     */
    private volatile boolean status = false;
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
    /**
     * 解析后的KEY列表
     */
    private final List<String> keys;
    /**
     * 使用KEY次数
     */
    private final AtomicLong useKeyCount = new AtomicLong(0);

    public QyWeixinRobotClient(String... key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        List<String> keyList = Arrays.asList(key);
        this.keys = Collections.unmodifiableList(keyList);
    }

    /**
     * 初始化
     */
    public void init() {
        if (status) {
            return;
        }
        status = true;
        if (getMsgQueue() == null) {
            setMsgQueue(new LinkedBlockingQueue<>(1024));
        }
        if (getQyWeixinRobotHttpClient() == null) {
            QyWeixinRobotHttpClient qyWeixinRobotHttpClient = new QyWeixinRobotHttpClient();
            setQyWeixinRobotHttpClient(qyWeixinRobotHttpClient);
        }
        if (getStrategy() == null) {
            DefaultQyWeixinQueueProcessStrategy strategy = new DefaultQyWeixinQueueProcessStrategy();
            setStrategy(strategy);
        }
        if (getScheduledExecutorService() == null) {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
                    new QyWeixinRobotThreadFactoryImpl("QyWeixinRbt-"));
            QyWeixinRobotScheduledExecutorService robotScheduledExecutorService
                    = new QyWeixinRobotScheduledExecutorService(scheduledExecutorService);
            setScheduledExecutorService(robotScheduledExecutorService);
        }
        getQyWeixinRobotHttpClient().init();
        getStrategy().init();
        getScheduledExecutorService().init();
        // 提交任务
        Runnable consumeQueueRunnable = new ConsumeQueueRunnable(this);
        this.scheduledExecutorService.scheduled(consumeQueueRunnable);
    }

    private static class ConsumeQueueRunnable implements Runnable {

        private final QyWeixinRobotClient client;

        public ConsumeQueueRunnable(QyWeixinRobotClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            if (!client.status) {
                return;
            }
            // 异步处理消息
            final QyWeixinBaseAsyncMessage message
                    = client.getStrategy().consumeProcess(client.getMsgQueue());
            if (message == null) {
                return;
            }
            if (!client.status) {
                return;
            }
            // 发送异步消息
            client.postMsgAsyncDirect(message);
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (!status) {
            return;
        }
        status = false;
        scheduledExecutorService.shutdown();
    }


    /**
     * 同步发送消息
     * <p>注意并发情形下会被限流
     *
     * @param message   消息
     * @return true发送成功
     */
    public boolean postMsgSync(QyWeixinBaseMessage message) {
        if (!status) {
            return false;
        }
        String loadBalanceKey = getLoadBalanceKey();
        if (QyWeixinRobotUtil.isBlank(loadBalanceKey)) {
            return false;
        }
        QyWeixinResponse response = qyWeixinRobotHttpClient.sendSync(loadBalanceKey, message.toMap());
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
        String loadBalanceKey = getLoadBalanceKey();
        if (QyWeixinRobotUtil.isBlank(loadBalanceKey)) {
            return;
        }
        // 发送异步消息
        qyWeixinRobotHttpClient.sendAsync(loadBalanceKey, message.toMap(), new Callback<QyWeixinResponse>() {
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
     * @param path  文件路径
     */
    public void uploadMedia(String path) {
        if (!status) {
            return;
        }
        String loadBalanceKey = getLoadBalanceKey();
        if (QyWeixinRobotUtil.isBlank(loadBalanceKey)) {
            return;
        }
        qyWeixinRobotHttpClient.uploadMedia(loadBalanceKey, path, new Callback<QyWeixinResponse>() {
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

    /**
     * 简单实现均衡返回KEY
     * @return KEY
     */
    private String getLoadBalanceKey(){
        if (getKeys().size() == 0) {
            return null;
        }
        long count = useKeyCount.incrementAndGet();
        int index = (int) (count % getKeys().size());
        return getKeys().get(index);
    }

    /* getter and setter */

    public List<String> getKeys() {
        return keys;
    }

    public BlockingQueue<QyWeixinBaseAsyncMessage> getMsgQueue() {
        return msgQueue;
    }

    public synchronized void setMsgQueue(BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue) {
        if (msgQueue == null) {
            throw new IllegalArgumentException("msgQueue is null");
        }
        if (status) {
            throw new IllegalStateException("client is running");
        }
        this.msgQueue = msgQueue;
    }

    public synchronized void setScheduledExecutorService(QyWeixinRobotScheduledExecutorService scheduledExecutorService) {
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

    public synchronized void setStrategy(QyWeixinQueueProcessStrategy strategy) {
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

    public synchronized void setQyWeixinRobotHttpClient(QyWeixinRobotHttpClient qyWeixinRobotHttpClient) {
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
