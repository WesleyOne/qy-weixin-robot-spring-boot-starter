package io.github.wesleyone.qy.weixin.robot.client;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactory;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.impl.DefaultQyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.entity.*;
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
        // ??????
        final QyWeixinRobotTextMessage textMessage = new QyWeixinRobotTextMessage("?????????????????????????????????????????????????????????????????????");
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
                "???????????????markdown???????????????????????????\n" +
                "<@all> \n " +
                "# ?????????\n " +
                "## ?????????\n " +
                "### ?????????\n " +
                "#### ?????????\n " +
                "##### ?????????\n " +
                "###### ?????????\n " +
                "**??????**\n " +
                "[??????????????????](https://work.weixin.qq.com/api/doc/90000/90136/91770)\n " +
                "`code`\n " +
                "> ????????????:" +
                "> talk is so cheap" +
                "> show me code\n" +
                "\n " +
                "<font color=\"info\">??????</font>\n" +
                "<font color=\"comment\">??????</font>\n" +
                "<font color=\"warning\">?????????</font>"
        );
        QyWeixinRobotResponse send = func.apply(markdownMessage);
        Assert.assertNotNull(send);
        Assert.assertTrue(send.isSuccess());
    }

    public void sendImg(Function<QyWeixinRobotBaseMessage, QyWeixinRobotResponse> func) {
        // ??????
        try {
            final QyWeixinRobotImageMessage imageMessage = new QyWeixinRobotImageMessage(Constant.BASE64, Constant.MD5);
            QyWeixinRobotResponse send = func.apply(imageMessage);
            Assert.assertNotNull(send);
            Assert.assertTrue(send.isSuccess());
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    public void sendNew(Function<QyWeixinRobotBaseMessage, QyWeixinRobotResponse> func) {
        // ??????
        List<QyWeixinRobotNewsMessage.Article> articles = new ArrayList<>();
        for (int i=0;i<7;i++) {
            final QyWeixinRobotNewsMessage.Article article = new QyWeixinRobotNewsMessage.Article(i+"???????????????", "https://wesleyone.github.io/");
            article.setDescription(i+"????????????????????????????????????");
            article.setPicurl("https://raw.githubusercontent.com/WesleyOne/wesleyone.github.io/master/docs/assert/images/qrcode.png");
            articles.add(article);
        }
        final QyWeixinRobotNewsMessage newsMessage = new QyWeixinRobotNewsMessage(articles);
        QyWeixinRobotResponse send = func.apply(newsMessage);
        Assert.assertNotNull(send);
        Assert.assertTrue(send.isSuccess());
    }
}