package io.github.wesleyone.qy.weixin.robot.entity;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片类型消息
 * <p>数据格式：
 * <pre><code>
 * {
 *     "msgtype": "image",
 *     "image": {
 *         "base64": "DATA",
 *         "md5": "MD5"
 *     }
 * }
 * </code></pre>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinImageMessage extends QyWeixinBaseMessage {

    private static final long serialVersionUID = -5704177757130409967L;
    /**
     * 图片内容的base64编码
     * <p>注：图片（base64编码前）最大不能超过2M，支持JPG,PNG格式
     */
    private String base64;
    /**
     * 图片内容（base64编码前）的md5值
     */
    private String md5;

    public QyWeixinImageMessage(String base64, String md5) {
        this.base64 = base64;
        this.md5 = md5;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public QyWeixinRobotMessageType getMsgType() {
        return QyWeixinRobotMessageType.image;
    }

    @Override
    public Map<String, Object> getMsgBody() {
        if (getBase64() == null) {
            throw new IllegalArgumentException("base64 is null");
        }
        if (getMd5() == null) {
            throw new IllegalArgumentException("md5 is null");
        }
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("base64", getBase64());
        resultMap.put("md5", getMd5());
        return resultMap;
    }
}
