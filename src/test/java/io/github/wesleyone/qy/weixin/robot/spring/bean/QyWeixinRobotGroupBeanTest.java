package io.github.wesleyone.qy.weixin.robot.spring.bean;

import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotKeyManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotQueueManagerComponent;
import io.github.wesleyone.qy.weixin.robot.component.QyWeixinRobotScheduledManagerComponent;
import io.github.wesleyone.qy.weixin.robot.entity.*;
import io.github.wesleyone.qy.weixin.robot.spring.autoconfiguration.QyWeixinRobotAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * QyWeixinRobotGroupBean测试用例
 * @author http://wesleyone.github.io/
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(
        classes = {MyQyWeixinRobotConfiguration.class, QyWeixinRobotAutoConfiguration.class},
        webEnvironment=SpringBootTest.WebEnvironment.NONE)
public class QyWeixinRobotGroupBeanTest {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private QyWeixinRobotGroupBean groupABean;

    @Test
    public void checkMyConfig() {
        try {
            final QyWeixinRobotHttpClientComponent httpClient = context.getBean("myQyWeixinRobotHttpClient", QyWeixinRobotHttpClientComponent.class);
            Assert.assertNotNull(httpClient);
            QyWeixinRobotHttpClientComponent httpClientMain = context.getBean(QyWeixinRobotHttpClientComponent.class);
            Assert.assertEquals(httpClientMain, httpClient);

            final QyWeixinRobotQueueManagerFactoryBean queueManagerFactoryBean = context.getBean(BeanFactory.FACTORY_BEAN_PREFIX+"myQueueManagerFactoryBean", QyWeixinRobotQueueManagerFactoryBean.class);
            Assert.assertNotNull(queueManagerFactoryBean);
            QyWeixinRobotQueueManagerFactoryBean queueManagerFactoryBeanMain = context.getBean(QyWeixinRobotQueueManagerFactoryBean.class);
            Assert.assertEquals(queueManagerFactoryBeanMain, queueManagerFactoryBean);

            final QyWeixinRobotKeyManagerFactoryBean keyManagerFactoryBean = context.getBean(BeanFactory.FACTORY_BEAN_PREFIX+"myKeyManagerFactoryBean", QyWeixinRobotKeyManagerFactoryBean.class);
            Assert.assertNotNull(queueManagerFactoryBean);
            QyWeixinRobotKeyManagerFactoryBean keyManagerFactoryBeanMain = context.getBean(QyWeixinRobotKeyManagerFactoryBean.class);
            Assert.assertEquals(keyManagerFactoryBeanMain, keyManagerFactoryBean);

            final QyWeixinRobotScheduledManagerComponent executorService = context.getBean("myQyWeixinRobotScheduledExecutorService", QyWeixinRobotScheduledManagerComponent.class);
            Assert.assertNotNull(executorService);
            QyWeixinRobotScheduledManagerComponent executorServiceMain = context.getBean(QyWeixinRobotScheduledManagerComponent.class);
            Assert.assertEquals(executorServiceMain, executorService);

        } catch (NoSuchBeanDefinitionException e) {
            Assert.fail(e.getMessage());
        }

        try {
            QyWeixinRobotHttpClientComponent defaultQyWeixinRobotHttpClient = context.getBean("defaultQyWeixinRobotHttpClient", QyWeixinRobotHttpClientComponent.class);
            Assert.assertNull(defaultQyWeixinRobotHttpClient);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
            System.out.println("[ignore] defaultQyWeixinRobotHttpClient is null");
        }

        try {
            QyWeixinRobotScheduledManagerComponent defaultQyWeixinRobotScheduledManager = context.getBean("defaultQyWeixinRobotScheduledManager", QyWeixinRobotScheduledManagerComponent.class);
            Assert.assertNull(defaultQyWeixinRobotScheduledManager);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
            System.out.println("[ignore] defaultQyWeixinRobotScheduledManager is null");
        }

        // 必须多例
        QyWeixinRobotQueueManagerComponent queueManagerComponent1 = context.getBean(QyWeixinRobotQueueManagerComponent.class);
        QyWeixinRobotQueueManagerComponent queueManagerComponent2 = context.getBean(QyWeixinRobotQueueManagerComponent.class);
        Assert.assertNotEquals(queueManagerComponent1,queueManagerComponent2);

        QyWeixinRobotKeyManagerComponent keyManagerComponent1 = context.getBean(QyWeixinRobotKeyManagerComponent.class);
        QyWeixinRobotKeyManagerComponent keyManagerComponent2 = context.getBean(QyWeixinRobotKeyManagerComponent.class);
        Assert.assertNotEquals(keyManagerComponent1,keyManagerComponent2);

    }

    @Test
    public void send() {
        sendText(groupABean::send);
        sendMarkdown(groupABean::send);
        sendImg(groupABean::send);
        sendNew(groupABean::send);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Test
    public void sendDirect() {
        sendText(groupABean::sendDirect);
        sendMarkdown(groupABean::sendDirect);
        sendImg(groupABean::sendDirect);
        sendNew(groupABean::sendDirect);
    }

    @Test
    public void uploadMedia() {
        try {
            String filePath = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
            byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
            groupABean.uploadMedia("qrcode.png", byteArray);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            // ignore
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