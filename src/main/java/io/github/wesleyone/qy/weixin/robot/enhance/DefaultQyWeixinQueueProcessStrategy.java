package io.github.wesleyone.qy.weixin.robot.enhance;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinBaseAsyncMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinMarkdownMessage;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 默认队列处理策略
 * <p>批量聚合成一条markdown消息发送
 * <p>注意事项：
 * <ul>
 * <li>批量发送聚合后按照markdown类型消息发送</li>
 * <li>多条消息换行符<code>\n</code>拼接</li>
 * <li>拼接后内容超长截断</li>
 * </ul>
 *
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinQueueProcessStrategy implements QyWeixinQueueProcessStrategy {

    private static final Logger logger = LoggerFactory.getLogger(QyWeixinQueueProcessStrategy.class.getName());
    /**
     * 单批消费消息数量
     */
    private int maxBatchMsgCounts = 20;

    @Override
    public boolean addProcess(QyWeixinBaseAsyncMessage message, BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue) {
        // 添加失败立即返回
        return msgQueue.offer(message);
    }

    @Override
    public QyWeixinBaseAsyncMessage consumeProcess(BlockingQueue<QyWeixinBaseAsyncMessage> msgQueue) {
        try {
            // 批次取出消息，提高性能
            List<QyWeixinBaseAsyncMessage> msgList = new ArrayList<>();
            msgQueue.drainTo(msgList, maxBatchMsgCounts);
            if (msgList.size() == 0) {
                return null;
            }
            // 聚合消息内容
            StringBuilder newContent = new StringBuilder();
            for (QyWeixinBaseAsyncMessage msg : msgList) {
                String tmpContent = null;
                if (msg instanceof QyWeixinTextMessage){
                    tmpContent = ((QyWeixinTextMessage) msg).getContent();
                } else if (msg instanceof QyWeixinMarkdownMessage) {
                    tmpContent = ((QyWeixinMarkdownMessage) msg).getContent();
                }
                if (tmpContent==null) {
                    continue;
                }
                newContent.append(tmpContent).append("\n");
            }
            String newContentStr = newContent.toString();
            // 截断超长字节
            newContentStr = QyWeixinRobotUtil.subStringByByteArrayLength(newContentStr, QyWeixinMarkdownMessage.MAX_MARKDOWN_CONTENT_LEN);
            return new QyWeixinMarkdownMessage(newContentStr);
        } catch (Throwable e) {
            logger.error("consumeProcess err",e);
        }
        return null;
    }

    public int getMaxBatchMsgCounts() {
        return maxBatchMsgCounts;
    }

    public void setMaxBatchMsgCounts(int maxBatchMsgCounts) {
        this.maxBatchMsgCounts = maxBatchMsgCounts;
    }

}
