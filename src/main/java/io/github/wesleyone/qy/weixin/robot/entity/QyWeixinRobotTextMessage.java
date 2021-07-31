package io.github.wesleyone.qy.weixin.robot.entity;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotMessageType;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本消息
 * <p>数据格式：
 * <pre><code>
 * {
 *     "msgtype": "text",
 *     "text": {
 *         "content": "广州今日天气：29度，大部分多云，降雨概率：60%",
 *         "mentioned_list":["wangqing","@all"],
 *         "mentioned_mobile_list":["13800001111","@all"]
 *     }
 * }
 * </code></pre>
 * <p>text类型消息支持在mentioned_list、mentioned_mobile_list字段中使用来@群所有人
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotTextMessage extends QyWeixinRobotBaseMessage {

    private static final long serialVersionUID = -6821666971134223701L;
    /**
     * <p>文本内容，
     * <p>最长不超过2048个字节，必须是utf8编码
     * <p>必填
     */
    private String content;
    /**
     * <p>userid的列表，
     * <p>提醒群中的指定成员(@某个成员)，@all表示提醒所有人，
     * <p>如果开发者获取不到userid，可以使用mentioned_mobile_list
     */
    private List<String> mentionedList;
    /**
     * 手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
     */
    private List<String> mentionedMobileList;

    public QyWeixinRobotTextMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMentionedList() {
        return mentionedList;
    }

    public void setMentionedList(List<String> mentionedList) {
        this.mentionedList = mentionedList;
    }

    public List<String> getMentionedMobileList() {
        return mentionedMobileList;
    }

    public void setMentionedMobileList(List<String> mentionedMobileList) {
        this.mentionedMobileList = mentionedMobileList;
    }

    @Override
    public QyWeixinRobotMessageType getMsgType() {
        return QyWeixinRobotMessageType.text;
    }

    @Override
    public Map<String, Object> getMsgBody() {
        if (getContent() == null) {
            throw new IllegalArgumentException("content is null");
        }
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("content",getContent());
        if (QyWeixinRobotUtil.isNotEmpty(getMentionedList())) {
            resultMap.put("mentioned_list",getMentionedList());
        }
        if (QyWeixinRobotUtil.isNotEmpty(getMentionedMobileList())) {
            resultMap.put("mentioned_mobile_list",getMentionedMobileList());
        }
        return resultMap;
    }
}
