package io.github.wesleyone.qy.weixin.robot.entity;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型消息
 * <p>数据格式：</p>
 * <pre><code>
 * {
 *     "msgtype": "file",
 *     "file": {
 *          "media_id": "3a8asd892asd8asd"
 *     }
 * }
 * </code></pre>
 *
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotFileMessage extends QyWeixinRobotBaseMessage {

    private static final long serialVersionUID = -4900556983046316616L;
    /**
     * 文件id，
     * 通过下文的文件上传接口获取
     */
    private String mediaId;

    public QyWeixinRobotFileMessage(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public QyWeixinRobotMessageType getMsgType() {
        return QyWeixinRobotMessageType.file;
    }

    @Override
    public Map<String, Object> getMsgBody() {
        if (getMediaId() == null) {
            throw new IllegalArgumentException("mediaId is null");
        }
        Map<String, Object> resultMap = new HashMap<>(1);
        resultMap.put("media_id",getMediaId());
        return resultMap;
    }
}
