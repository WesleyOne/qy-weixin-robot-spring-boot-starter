package io.github.wesleyone.qy.weixin.robot2.client;

import io.github.wesleyone.qy.weixin.robot2.BasicMock;
import io.github.wesleyone.qy.weixin.robot2.Constant;
import io.github.wesleyone.qy.weixin.robot2.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot2.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot2.component.impl.DefaultQyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot2.entity.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotGroupClientTest {

    public static class Mock extends BasicMock {
    }

    private QyWeixinRobotGroupClient qyWeixinRobotClient;

    @Before
    public void setUp() {
        QyWeixinRobotScheduledManagerComponent scheduledExecutorService
                = new DefaultQyWeixinRobotScheduledManagerComponent(0,1, TimeUnit.SECONDS,true
                , Executors.newSingleThreadScheduledExecutor(new QyWeixinRobotThreadFactory("qy-weixin-test-")));
        qyWeixinRobotClient = new QyWeixinRobotGroupClient(Constant.KEY_LIST,null,null,null,scheduledExecutorService);
        qyWeixinRobotClient.init();
    }

    @After
    public void tearDown() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void init() {
        try {
            qyWeixinRobotClient.init();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void destroy() {
        try {
            qyWeixinRobotClient.destroy();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void send() {
        sendText(qyWeixinRobotClient::send);
        sendMarkdown(qyWeixinRobotClient::send);
        sendImg(qyWeixinRobotClient::send);
        sendNew(qyWeixinRobotClient::send);
    }

    @Test
    public void sendDirect() {
        sendText(qyWeixinRobotClient::sendDirect);
        sendMarkdown(qyWeixinRobotClient::sendDirect);
        sendImg(qyWeixinRobotClient::sendDirect);
        sendNew(qyWeixinRobotClient::sendDirect);
    }

    @Test
    public void uploadMedia() {
        try {
            String filePath = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
            byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
            qyWeixinRobotClient.uploadMedia("qrcode.png", byteArray);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void sendText(Function<QyWeixinRobotBaseMessage, QyWeixinRobotResponse> func) {
        // 文本
        final QyWeixinRobotTextMessage textMessage = new QyWeixinRobotTextMessage("文本类型同步发送测试，关注公众号【火字旁的炜】");
        List<String> mentionedList = new ArrayList<>();
        mentionedList.add("@all");
        textMessage.setMentionedList(mentionedList);
        QyWeixinRobotResponse send = func.apply(textMessage);
        Assert.assertNotNull(send);
        Assert.assertTrue(send.isSuccess());
    }

    public void sendMarkdown(Function<QyWeixinRobotBaseMessage, QyWeixinRobotResponse> func) {
        // markdown
        final QyWeixinRobotMarkdownMessage markdownMessage = new QyWeixinRobotMarkdownMessage(
                "目前支持的markdown语法是如下的子集：\n" +
                "<@all> \n " +
                "# 标题一\n " +
                "## 标题二\n " +
                "### 标题三\n " +
                "#### 标题四\n " +
                "##### 标题五\n " +
                "###### 标题六\n " +
                "**加粗**\n " +
                "[这是一个链接](https://work.weixin.qq.com/api/doc/90000/90136/91770)\n " +
                "`code`\n " +
                "> 引用文字:" +
                "> talk is so cheap" +
                "> show me code\n" +
                "\n " +
                "<font color=\"info\">绿色</font>\n" +
                "<font color=\"comment\">灰色</font>\n" +
                "<font color=\"warning\">橙红色</font>"
        );
        QyWeixinRobotResponse send = func.apply(markdownMessage);
        Assert.assertNotNull(send);
        Assert.assertTrue(send.isSuccess());
    }

    public void sendImg(Function<QyWeixinRobotBaseMessage, QyWeixinRobotResponse> func) {
        // 图片
        try {
            String path = Constant.IMG_PATH;
            final String base64 = Constant.base64(path);
            final String md5 = Constant.md5(path);
            Assert.assertEquals(Constant.BASE64,base64);
            Assert.assertEquals(Constant.MD5,md5);
            final QyWeixinRobotImageMessage imageMessage = new QyWeixinRobotImageMessage(base64, md5);
            QyWeixinRobotResponse send = func.apply(imageMessage);
            Assert.assertNotNull(send);
            Assert.assertTrue(send.isSuccess());
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    public void sendNew(Function<QyWeixinRobotBaseMessage, QyWeixinRobotResponse> func) {
        // 图文
        List<QyWeixinRobotNewsMessage.Article> articles = new ArrayList<>();
        for (int i=0;i<7;i++) {
            final QyWeixinRobotNewsMessage.Article article = new QyWeixinRobotNewsMessage.Article(i+"火字旁的炜", "https://wesleyone.github.io/");
            article.setDescription(i+"关注公众号【火字旁的炜】");
            article.setPicurl("https://raw.githubusercontent.com/WesleyOne/wesleyone.github.io/master/docs/assert/images/qrcode.png");
            articles.add(article);
        }
        final QyWeixinRobotNewsMessage newsMessage = new QyWeixinRobotNewsMessage(articles);
        QyWeixinRobotResponse send = func.apply(newsMessage);
        Assert.assertNotNull(send);
        Assert.assertTrue(send.isSuccess());
    }
}