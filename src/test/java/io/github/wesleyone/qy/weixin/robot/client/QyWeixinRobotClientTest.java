package io.github.wesleyone.qy.weixin.robot.client;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotThreadFactoryImpl;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import io.github.wesleyone.qy.weixin.robot.entity.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * QyWeixinRobotClient测试用例
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotClientTest {

    private QyWeixinRobotClient qyWeixinRobotClient;

    public static class Mock extends BasicMock {
    }

    @Before
    public void setUp() {
        qyWeixinRobotClient = new QyWeixinRobotClient(Constant.WEBHOOK_URL_KEY);
        QyWeixinRobotScheduledExecutorService scheduledExecutorService
                = new QyWeixinRobotScheduledExecutorService(0,1,TimeUnit.SECONDS,true
                , Executors.newSingleThreadScheduledExecutor(new QyWeixinRobotThreadFactoryImpl("qy-weixin-test-")));
        qyWeixinRobotClient.setScheduledExecutorService(scheduledExecutorService);
        qyWeixinRobotClient.init();
    }

    @After
    public void tearDown() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(200);
    }


    @Test
    public void postMsgSync_text() {
        // 文本
        final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("文本类型同步发送测试，关注公众号【火字旁的炜】");
        List<String> mentionedList = new ArrayList<>();
        mentionedList.add(QyWeixinTextMessage.AT_ALL);
        qyWeixinTextMessage.setMentionedList(mentionedList);
        boolean result = qyWeixinRobotClient.postMsgSync(qyWeixinTextMessage);
        Assert.assertTrue(result);
    }

    @Test
    public void postMsgSync_mk() {
        // markdown
        final QyWeixinMarkdownMessage qyWeixinMarkdownMessage = new QyWeixinMarkdownMessage(QyWeixinMarkdownMessage.AT_ALL_C+"\n " +
                "目前支持的markdown语法是如下的子集：\n" +
                "\n " +
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
        qyWeixinRobotClient.postMsgSync(qyWeixinMarkdownMessage);
    }

    @Test
    public void postMsgSync_img() {
        // 图片
        try {
            String path = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
            final String base64 = QyWeixinRobotUtil.base64(path);
            final String md5 = QyWeixinRobotUtil.md5(path);
            Assert.assertEquals(Constant.BASE64,base64);
            Assert.assertEquals(Constant.MD5,md5);
            final QyWeixinImageMessage qyWeixinImageMessage = new QyWeixinImageMessage(base64, md5);
            qyWeixinRobotClient.postMsgSync(qyWeixinImageMessage);
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void postMsgSync_news() {
        // 图文
        List<QyWeixinNewsMessage.Article> articles = new ArrayList<>();
        for (int i=0;i<7;i++) {
            final QyWeixinNewsMessage.Article article = new QyWeixinNewsMessage.Article(i+"火字旁的炜", "https://wesleyone.github.io/");
            article.setDescription(i+"关注公众号【火字旁的炜】");
            article.setPicurl("https://raw.githubusercontent.com/WesleyOne/wesleyone.github.io/master/docs/assert/images/qrcode.png");
            articles.add(article);
        }
        final QyWeixinNewsMessage qyWeixinNewsMessage = new QyWeixinNewsMessage(articles);
        qyWeixinRobotClient.postMsgSync(qyWeixinNewsMessage);
    }

    @Ignore
    @Test
    public void postMsgSync_file() {
        // 文件
        QyWeixinFileMessage qyWeixinFileMessage = new QyWeixinFileMessage("xxx");
        boolean msgSync = qyWeixinRobotClient.postMsgSync(qyWeixinFileMessage);
        Assert.assertTrue(msgSync);
    }

    @Test
    public void postMsgAsyncQueue_text() throws InterruptedException {
        // 文本
        final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("文本类型同步发送测试，关注公众号【火字旁的炜】");
        List<String> mentionedList = new ArrayList<>();
        mentionedList.add(QyWeixinTextMessage.AT_ALL);
        qyWeixinTextMessage.setMentionedList(mentionedList);
        boolean result = qyWeixinRobotClient.postMsgAsyncQueue(qyWeixinTextMessage);
        Assert.assertTrue(result);
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void postMsgAsyncQueue_mk() throws InterruptedException {
        // markdown
        final QyWeixinMarkdownMessage qyWeixinMarkdownMessage = new QyWeixinMarkdownMessage(QyWeixinMarkdownMessage.AT_ALL_C+"\n " +
                "目前支持的markdown语法是如下的子集：\n" +
                "\n " +
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
        boolean result = qyWeixinRobotClient.postMsgAsyncQueue(qyWeixinMarkdownMessage);
        Assert.assertTrue(result);
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void postMsgAsync_batch_test() throws InterruptedException {

        final Runnable textTask = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage(i + "关注公众号【火字旁的炜】");
                    qyWeixinRobotClient.postMsgAsyncQueue(qyWeixinTextMessage);
                    try {
                        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 500));
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        };
        final Runnable mkTask = new Runnable() {
            @Override
            public void run() {
                // markdown
                for (int i=0;i<10;i++) {
                    final QyWeixinMarkdownMessage qyWeixinMarkdownMessage = new QyWeixinMarkdownMessage(
                            "# 标题一\n " +
                                    "## 标题二\n " +
                                    "### 标题三\n " +
                                    "#### 标题四\n " +
                                    "##### 标题五\n " +
                                    "###### 标题六\n " +
                                    "**加粗**\n " +
                                    "[这是一个链接](https://work.weixin.qq.com/api/doc/90000/90136/91770)\n " +
                                    "`code`\n " +
                                    "> 引用文字:\n " +
                                    "> talk is so cheap\n " +
                                    "> show me code\n" +
                                    "\n " +
                                    "<font color=\"info\">绿色</font>\n" +
                                    "<font color=\"comment\">灰色</font>\n" +
                                    "<font color=\"warning\">橙红色</font>"
                    );
                    qyWeixinRobotClient.postMsgAsyncQueue(qyWeixinMarkdownMessage);
                    try {
                        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 500));
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        };
        // 文本
        final Thread textThread = new Thread(textTask);
        textThread.start();
        final Thread mkThread = new Thread(mkTask);
        mkThread.start();

        mkThread.join();
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void postMsgAsyncDirect_text() {
        // 文本
        final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("文本类型同步发送测试，关注公众号【火字旁的炜】");
        List<String> mentionedList = new ArrayList<>();
        mentionedList.add(QyWeixinTextMessage.AT_ALL);
        qyWeixinTextMessage.setMentionedList(mentionedList);
        qyWeixinRobotClient.postMsgAsyncDirect(qyWeixinTextMessage);

    }

    @Test
    public void postMsgAsyncDirect_mk() {
        // markdown
        final QyWeixinMarkdownMessage qyWeixinMarkdownMessage = new QyWeixinMarkdownMessage(QyWeixinMarkdownMessage.AT_ALL_C+"\n " +
                "目前支持的markdown语法是如下的子集：\n" +
                "\n " +
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
        qyWeixinRobotClient.postMsgAsyncDirect(qyWeixinMarkdownMessage);
    }

    @Test
    public void postMsgAsyncDirect_img() {
        // 图片
        try {
            String path = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
            final String base64 = QyWeixinRobotUtil.base64(path);
            final String md5 = QyWeixinRobotUtil.md5(path);
            Assert.assertEquals(Constant.BASE64,base64);
            Assert.assertEquals(Constant.MD5,md5);
            final QyWeixinImageMessage qyWeixinImageMessage = new QyWeixinImageMessage(base64, md5);
            qyWeixinRobotClient.postMsgAsyncDirect(qyWeixinImageMessage);
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void postMsgAsyncDirect_news() {
        // 图文
        List<QyWeixinNewsMessage.Article> articles = new ArrayList<>();
        for (int i=0;i<7;i++) {
            final QyWeixinNewsMessage.Article article = new QyWeixinNewsMessage.Article(i+"火字旁的炜", "https://wesleyone.github.io/");
            article.setDescription(i+"关注公众号【火字旁的炜】");
            article.setPicurl("https://raw.githubusercontent.com/WesleyOne/wesleyone.github.io/master/docs/assert/images/qrcode.png");
            articles.add(article);
        }
        final QyWeixinNewsMessage qyWeixinNewsMessage = new QyWeixinNewsMessage(articles);
        qyWeixinRobotClient.postMsgAsyncDirect(qyWeixinNewsMessage);
    }

    @Test
    public void postMsgAsyncDirect_file() {
        // 文件
        QyWeixinFileMessage qyWeixinFileMessage = new QyWeixinFileMessage("xxx");
        qyWeixinRobotClient.postMsgAsyncDirect(qyWeixinFileMessage);
    }

    @Test
    public void uploadMedia() throws InterruptedException {
        qyWeixinRobotClient.uploadMedia(Constant.IMG_PATH);
        TimeUnit.SECONDS.sleep(5);
    }

}