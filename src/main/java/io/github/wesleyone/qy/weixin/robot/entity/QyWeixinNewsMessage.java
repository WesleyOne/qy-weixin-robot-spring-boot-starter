package io.github.wesleyone.qy.weixin.robot.entity;

import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotMessageType;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图文类型
 * <p>数据格式：
 * <pre><code>
 * {
 *     "msgtype": "news",
 *     "news": {
 *        "articles" : [
 *            {
 *                "title" : "火字旁的炜",
 *                "description" : "扫码关注公众号可撩",
 *                "url" : "https://wesleyone.github.io/",
 *                "picurl" : "https://raw.githubusercontent.com/WesleyOne/wesleyone.github.io/master/docs/assert/images/qrcode.png"
 *            }
 *         ]
 *     }
 * }
 * </code></pre>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinNewsMessage extends QyWeixinBaseMessage {

    private static final long serialVersionUID = 3421196075120141550L;
    /**
     * 图文消息，一个图文消息支持1到8条图文
     */
    private List<Article> articles;

    public QyWeixinNewsMessage(List<Article> articles) {
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public QyWeixinRobotMessageType getMsgType() {
        return QyWeixinRobotMessageType.news;
    }

    @Override
    public Map<String, Object> getMsgBody() {
        if (QyWeixinRobotUtil.isEmpty(getArticles())) {
            throw new IllegalArgumentException("articles is null");
        }
        Map<String, Object> resultMap = new HashMap<>(1);
        resultMap.put("articles",getArticles());
        return resultMap;
    }

    public static class Article {
        /**
         * 标题，
         * 不超过128个字节，超过会自动截断
         * 必填
         */
        private String title;
        /**
         * 描述，
         * 不超过512个字节，超过会自动截断
         * 必填
         */
        private String description;
        /**
         * 点击后跳转的链接。
         * 必填
         */
        private String url;
        /**
         * 图文消息的图片链接，
         * 支持JPG、PNG格式，较好的效果为大图 1068*455，小图150*150。
         */
        private String picurl;

        public Article(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
