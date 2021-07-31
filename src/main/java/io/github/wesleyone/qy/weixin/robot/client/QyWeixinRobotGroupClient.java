package io.github.wesleyone.qy.weixin.robot.client;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotKey;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotRetrofit2HttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotFileMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinRobotResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 一个企业微信群
 * <p>维护多个机器人KEY。维护的KEY越多，吞吐量越高。</p>
 * <p>官方消息发送频率限制：每个机器人发送的消息不能超过20条/分钟。
 * <p>用户使用核心方法：
 * <ul>
 *     <li>异步发消息{@link #send(QyWeixinRobotBaseMessage)}（推荐,提高并发量）</li>
 *     <li>同步发消息{@link #sendDirect(QyWeixinRobotBaseMessage)}</li>
 *     <li>上传文件{@link #uploadMedia(String, byte[])}}</li>
 * </ul>
 * <p>开发者参考：
 *
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotGroupClient implements QyWeixinRobotGroupClientApi {
    private static final Logger logger = LoggerFactory.getLogger(QyWeixinRobotGroupClient.class.getName());
    /**
     * HTTP请求客户端
     */
    private final QyWeixinRobotHttpClientComponent httpClient;
    /**
     * 消息队列管理器
     */
    private final QyWeixinRobotQueueManagerComponent queueManager;
    /**
     * KEY管理器
     */
    private final QyWeixinRobotKeyManagerComponent keyManager;
    /**
     * 调度管理器
     */
    private final QyWeixinRobotScheduledManagerComponent scheduledManager;
    /**
     * 启动状态
     */
    private volatile boolean status = false;
    /**
     * 维护KEY值
     */
    private final List<String> keyList;
    /**
     * 发送用线程池
     */
    private static final ExecutorService sendExecutorService = new ThreadPoolExecutor(5,5,0,TimeUnit.MILLISECONDS
            , new LinkedBlockingQueue<Runnable>(),new QyWeixinRobotThreadFactory("QY-WEIXIN-ROB-SEND-"));

    public QyWeixinRobotGroupClient(List<String> keyList) {
        this(keyList, null, null, null, null);
    }

    public QyWeixinRobotGroupClient(List<String> keyList, QyWeixinRobotHttpClientComponent httpClient, QyWeixinRobotQueueManagerComponent queueManager, QyWeixinRobotKeyManagerComponent keyManager, QyWeixinRobotScheduledManagerComponent scheduledManager) {
        if (httpClient == null) {
            httpClient = new DefaultQyWeixinRobotRetrofit2HttpClientComponent();
        }
        if (queueManager == null) {
            queueManager = new DefaultQyWeixinRobotQueueManagerComponent();
        }
        if (keyManager == null) {
            keyManager = new DefaultQyWeixinRobotKeyManagerComponent();
        }
        if (scheduledManager == null) {
            scheduledManager = new DefaultQyWeixinRobotScheduledManagerComponent();
        }
        this.keyList = keyList;
        this.httpClient = httpClient;
        this.queueManager = queueManager;
        this.keyManager = keyManager;
        this.scheduledManager = scheduledManager;
    }

    public void init() {
        if (status) {
            return;
        }
        synchronized(this){
            if (status) {
                return;
            }
            status = true;
            httpClient.init(this);
            queueManager.init(this);
            keyManager.init(this);
            scheduledManager.init(this);
            // 提交任务
            ScheduledRunnable scheduledRunnable = new ScheduledRunnable(this);
            ScheduledFuture<?> scheduledFuture = scheduledManager.scheduled(scheduledRunnable);
            scheduledFuture.isDone();
        }
    }

    public void destroy() {
        if (!status) {
            return;
        }
        synchronized(this){
            if (!status) {
                return;
            }
            status = false;
            httpClient.destroy(this);
            queueManager.destroy(this);
            keyManager.destroy(this);
            scheduledManager.destroy(this);
        }
    }

    static class ScheduledRunnable implements Runnable {
        private final QyWeixinRobotGroupClient client;
        public ScheduledRunnable(QyWeixinRobotGroupClient client) {
            this.client = client;
        }
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("Client Status:{}",client.status);
            }
            if (!client.status) {
                return;
            }
            int keySize;
            int messageSize;
            try {
                // 选择KEY
                List<QyWeixinRobotKey.SelectKey> selectKeys = client.keyManager.selectKeys(10);
                if (logger.isDebugEnabled()) {
                    logger.debug("Client selectKeys:{}", selectKeys);
                }
                if (QyWeixinRobotUtil.isEmpty(selectKeys)) {
                    return;
                }
                // 消费策略
                List<QyWeixinRobotBaseMessage> messages = client.queueManager.consume(selectKeys.size());
                if (logger.isDebugEnabled()) {
                    logger.debug("Client consume messages:{}",messages);
                }
                if (messages == null) {
                    messages = new ArrayList<>();
                }

                // 发送异步消息
                keySize = selectKeys.size();
                messageSize = messages.size();
                int minSize = Math.min(keySize, messageSize);
                if (keySize > messageSize) {
                    // 恢复用不到的KEY的调用次数
                    List<QyWeixinRobotKey.SelectKey> nonUsedKeys = selectKeys.subList(minSize, keySize);
                    nonUsedKeys.forEach(client.keyManager::recover);
                } else if (keySize < messageSize){
                    // 这是不正常的.处理方式：放回消费不到的消息.
                    logger.warn("返回消息数量不正常，比KEY数量多.keySize[{}] < messageSize[{}]", keySize, messageSize);
                    List<QyWeixinRobotBaseMessage> nonConsumeMessages = messages.subList(minSize, messageSize);
                    nonConsumeMessages.forEach(client.queueManager::provide);
                }
                if (minSize == 0) {
                    return;
                }
                for (int index=0;index<minSize;index++) {
                    QyWeixinRobotKey.SelectKey selectKey = selectKeys.get(index);
                    QyWeixinRobotBaseMessage message = messages.get(index);
                    sendExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.httpClient.sendAsync(selectKey.getKey(), message, null);
                            } catch (Exception e) {
                                logger.error("调度发送异常.key:{},message:{}",selectKeys, message);
                            }
                        }
                    });
                }
            } catch (Throwable e) {
                logger.error("调度异常",e);
            }
        }
    }

    /**
     * 异步发送
     * <p>存入消息队列，调度执行器周期消费队列，发送消息</p>
     * <p>使用须知：</p>
     * <ul>
     *     <li>允许延迟</li>
     *     <li>允许丢失</li>
     * </ul>
     * @param message   消息
     * @return  响应内容
     */
    @Override
    public QyWeixinRobotResponse send(QyWeixinRobotBaseMessage message) {
        if (!status) {
            return QyWeixinRobotResponse.err("状态已关闭");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("send_queue.message:{}", message);
        }
        boolean result = this.queueManager.provide(message);
        if (result) {
            return QyWeixinRobotResponse.ok();
        } else {
            return QyWeixinRobotResponse.err("投递队列失败");
        }
    }

    /**
     * 直接发送（不推荐，并发下会导致限流）
     * @param message   消息
     * @return  响应内容
     */
    @Override
    public QyWeixinRobotResponse sendDirect(QyWeixinRobotBaseMessage message) {
        if (!status) {
            return QyWeixinRobotResponse.err("状态已关闭");
        }
        // 选择KEY
        List<QyWeixinRobotKey.SelectKey> selectKeys = this.keyManager.selectKeys(1);
        if (QyWeixinRobotUtil.isEmpty(selectKeys)) {
            return QyWeixinRobotResponse.err("找不到KEY");
        }
        QyWeixinRobotKey.SelectKey selectKey = selectKeys.get(0);
        // 发送消息
        return this.httpClient.send(selectKey.getKey(), message);
    }

    /**
     * 上传文件
     * @param filename  文件名
     * @param content   文件内容字节数组
     * @return  响应内容
     */
    @Override
    public QyWeixinRobotResponse uploadMedia(String filename, final byte[] content) {
        if (!status) {
            return QyWeixinRobotResponse.err("状态已关闭");
        }
        // 选择KEY
        List<QyWeixinRobotKey.SelectKey> selectKeys = this.keyManager.selectKeys(1);
        if (QyWeixinRobotUtil.isEmpty(selectKeys)) {
            return QyWeixinRobotResponse.err("找不到KEY");
        }
        // 上传文件获取media_id
        QyWeixinRobotKey.SelectKey selectKey = selectKeys.get(0);
        if (logger.isDebugEnabled()) {
            logger.debug("uploadMedia_request.key:{},filename:{},content.length:{}", selectKey, filename, content.length);
        }
        QyWeixinRobotResponse response = this.httpClient.uploadMedia(selectKey.getKey(), filename, content);
        if (logger.isDebugEnabled()) {
            logger.debug("uploadMedia_response.key:{},filename:{},response:{}", selectKey, filename, response);
        }
        if (!response.isSuccess()) {
            return response;
        }
        String mediaId = response.getMedia_id();
        // 发送文件消息
        QyWeixinRobotFileMessage fileMessage = new QyWeixinRobotFileMessage(mediaId);
        return send(fileMessage);
    }

    public QyWeixinRobotHttpClientComponent getHttpClient() {
        return httpClient;
    }

    public QyWeixinRobotQueueManagerComponent getQueueManager() {
        return queueManager;
    }

    public QyWeixinRobotKeyManagerComponent getKeyManager() {
        return keyManager;
    }

    public QyWeixinRobotScheduledManagerComponent getScheduledManager() {
        return scheduledManager;
    }

    public boolean isStatus() {
        return status;
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public static ExecutorService getSendExecutorService() {
        return sendExecutorService;
    }
}
