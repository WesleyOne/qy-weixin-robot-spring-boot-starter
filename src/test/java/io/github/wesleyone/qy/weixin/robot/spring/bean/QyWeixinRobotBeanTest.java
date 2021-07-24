package io.github.wesleyone.qy.weixin.robot.spring.bean;

import com.alibaba.testable.core.annotation.MockMethod;
import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinQueueProcessStrategy;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotScheduledExecutorService;
import io.github.wesleyone.qy.weixin.robot.common.QyWeixinRobotUtil;
import io.github.wesleyone.qy.weixin.robot.demo2.MyQyWeixinRobotConfiguration;
import io.github.wesleyone.qy.weixin.robot.entity.*;
import io.github.wesleyone.qy.weixin.robot.enhance.QyWeixinRobotHttpClient;
import io.github.wesleyone.qy.weixin.robot.spring.autoconfiguration.QyWeixinRobotAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * QyWeixinRobotBean测试用例
 * @author http://wesleyone.github.io/
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {MyQyWeixinRobotConfiguration.class,QyWeixinRobotAutoConfiguration.class},
        webEnvironment=SpringBootTest.WebEnvironment.NONE)
public class QyWeixinRobotBeanTest {

    public static class Mock {
        @MockMethod(targetClass= QyWeixinRobotHttpClient.class,targetMethod = "sendSync")
        private QyWeixinResponse sendSync(String key, Map<String,Object> message) {
            System.out.println("MOCK sendSync");
            return QyWeixinResponse.ok();
        }

        @MockMethod(targetClass= QyWeixinRobotHttpClient.class,targetMethod = "sendAsync")
        private void sendAsync(String key, Map<String,Object> message, Callback<QyWeixinResponse> callback) {
            System.out.println("MOCK sendAsync");
            callback.onResponse(null, Response.success(QyWeixinResponse.ok()));
        }
    }

    @Autowired
    private ApplicationContext context;
    @Autowired
    private QyWeixinRobotBean robotA;
    @Autowired
    private QyWeixinRobotBean robotB;

    /**
     * 检测手动扩展是否成功
     */
    @Test
    public void springBoot_manual_extend_test() {
        try {
            final QyWeixinRobotHttpClient httpClient = context.getBean("myHttpClient",QyWeixinRobotHttpClient.class);
            Assert.assertNotNull(httpClient);
            QyWeixinRobotHttpClient httpClientMain = context.getBean(QyWeixinRobotHttpClient.class);
            Assert.assertEquals(httpClientMain, httpClient);

            final QyWeixinQueueProcessStrategy strategy = context.getBean("myQueueProcessStrategy", QyWeixinQueueProcessStrategy.class);
            Assert.assertNotNull(strategy);
            QyWeixinQueueProcessStrategy strategyMain = context.getBean(QyWeixinQueueProcessStrategy.class);
            Assert.assertEquals(strategyMain, strategy);

            final QyWeixinRobotScheduledExecutorService executorService = context.getBean("myScheduledExecutorService",QyWeixinRobotScheduledExecutorService.class);
            Assert.assertNotNull(executorService);
            QyWeixinRobotScheduledExecutorService executorServiceMain = context.getBean(QyWeixinRobotScheduledExecutorService.class);
            Assert.assertEquals(executorServiceMain, executorService);

        } catch (NoSuchBeanDefinitionException e) {
            Assert.fail();
        }

        try {
            QyWeixinRobotHttpClient defaultQyWeixinRobotApiClient = context.getBean("defaultQyWeixinRobotApiClient", QyWeixinRobotHttpClient.class);
            Assert.assertNull(defaultQyWeixinRobotApiClient);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }
        try {
            QyWeixinQueueProcessStrategy defaultQyWeixinQueueProcessStrategy = context.getBean("defaultQyWeixinQueueProcessStrategy", QyWeixinQueueProcessStrategy.class);
            Assert.assertNull(defaultQyWeixinQueueProcessStrategy);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }
        try {
            QyWeixinRobotScheduledExecutorService defaultQyWeixinRobotScheduledExecutorService = context.getBean("defaultQyWeixinRobotScheduledExecutorService", QyWeixinRobotScheduledExecutorService.class);
            Assert.assertNull(defaultQyWeixinRobotScheduledExecutorService);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }

    }

    @Test
    public void postMsgSync_text() {
        // 文本
        final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("文本类型同步发送测试，关注公众号【火字旁的炜】");
        List<String> mentionedList = new ArrayList<>();
        mentionedList.add(QyWeixinTextMessage.AT_ALL);
        qyWeixinTextMessage.setMentionedList(mentionedList);
        robotA.postMsgSync(qyWeixinTextMessage);

        final QyWeixinTextMessage qyWeixinTextMessageB = new QyWeixinTextMessage("文本类型同步发送测试B，关注公众号【火字旁的炜】");
        qyWeixinTextMessage.setMentionedList(mentionedList);
        robotB.postMsgSync(qyWeixinTextMessageB);
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
        robotA.postMsgSync(qyWeixinMarkdownMessage);
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
            robotA.postMsgSync(qyWeixinImageMessage);
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void postMsgSync_news() throws InterruptedException {
        // 图文
        List<QyWeixinNewsMessage.Article> articles = new ArrayList<>();
        for (int i=0;i<7;i++) {
            final QyWeixinNewsMessage.Article article = new QyWeixinNewsMessage.Article(i+"火字旁的炜", "https://wesleyone.github.io/");
            article.setDescription(i+"关注公众号【火字旁的炜】");
            article.setPicurl("https://raw.githubusercontent.com/WesleyOne/wesleyone.github.io/master/docs/assert/images/qrcode.png");
            articles.add(article);
        }
        final QyWeixinNewsMessage qyWeixinNewsMessage = new QyWeixinNewsMessage(articles);
        robotA.postMsgSync(qyWeixinNewsMessage);
    }

    @Test
    public void postMsgAsync_test() throws InterruptedException {

        final Runnable textTask = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage(i + "关注公众号【火字旁的炜】");
                    robotA.postMsgAsyncQueue(qyWeixinTextMessage);
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
                for (int i=0;i<20;i++) {
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
                    robotA.postMsgAsyncQueue(qyWeixinMarkdownMessage);
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
    }

    @Test
    public void postMsgSync_file() {
        // 文件
        QyWeixinFileMessage qyWeixinFileMessage = new QyWeixinFileMessage("xxx");
        boolean msgSync = robotA.postMsgSync(qyWeixinFileMessage);
        Assert.assertTrue(msgSync);
    }
}