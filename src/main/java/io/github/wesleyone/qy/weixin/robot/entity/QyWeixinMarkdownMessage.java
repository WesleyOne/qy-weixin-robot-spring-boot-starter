package io.github.wesleyone.qy.weixin.robot.entity;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * markdown类型消息
 * <p>数据格式：</p>
 * <pre>{@code
 * {
 *     "msgtype": "markdown",
 *     "markdown": {
 *         "content": "> TALK IS SO CHEAP,SHOW ME YOUR CODE."
 *     }
 * }
 * }</pre>
 *
 * 目前支持的markdown语法是如下的子集：
 * <pre>
 * 1. 标题 （支持1至6级标题，注意#与文字中间要有空格）
 * # 标题一
 * ## 标题二
 * ### 标题三
 * #### 标题四
 * ##### 标题五
 * ###### 标题六
 * </pre>
 * <pre>
 * 2. 加粗
 * **bold**
 * </pre>
 * <pre>
 * 3. 链接
 * [这是一个链接](http://work.weixin.qq.com/api/doc)
 * </pre>
 * <pre>
 * 4.行内代码段（暂不支持跨行）
 * {@code `code`}
 * </pre>
 * <pre>
 * 5. 引用
 * {@code > 引用文字}
 * </pre>
 * <pre>
 * 6. 字体颜色(只支持3种内置颜色)
 * {@code
 *  <font color="info">绿色</font>
 *  <font color="comment">灰色</font>
 *  <font color="warning">橙红色</font>
 * }
 * </pre>
 *
 * @author http://wesleyone.github.io/
 */
public class QyWeixinMarkdownMessage extends QyWeixinBaseAsyncMessage {

    private static final long serialVersionUID = -7154208917565668922L;
    public static final int MAX_MARKDOWN_CONTENT_LEN = 4096;
    /**
     * <code>@所有人</code><br>
     * <code>text/markdown类型消息支持在content中使用<@all>扩展语法来@群所有人</code>
     */
    public static final String AT_ALL_C = "<@all>";
    /**
     * markdown内容，
     * <p>最长不超过4096个字节，必须是utf8编码</p>
     */
    private String content;

    public QyWeixinMarkdownMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public QyWeixinRobotMessageType getMsgType() {
        return QyWeixinRobotMessageType.markdown;
    }

    @Override
    public Map<String, Object> getMsgBody() {
        if (getContent() == null) {
            throw new IllegalArgumentException("content is null");
        }
        Map<String, Object> resultMap = new HashMap<>(1);
        resultMap.put("content", getContent());
        return resultMap;
    }
}
