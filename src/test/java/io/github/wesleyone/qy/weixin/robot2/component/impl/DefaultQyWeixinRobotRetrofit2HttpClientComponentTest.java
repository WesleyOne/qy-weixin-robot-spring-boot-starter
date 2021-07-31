package io.github.wesleyone.qy.weixin.robot2.component.impl;

import io.github.wesleyone.qy.weixin.robot2.BasicMock;
import io.github.wesleyone.qy.weixin.robot2.Constant;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotResponse;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotTextMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotRetrofit2HttpClientComponentTest {

    public static class Mock extends BasicMock {
    }

    private DefaultQyWeixinRobotRetrofit2HttpClientComponent httpClientComponent;

    @Before
    public void setUp() {
        httpClientComponent = new DefaultQyWeixinRobotRetrofit2HttpClientComponent();
    }

    @Test
    public void send() {
        QyWeixinRobotTextMessage textMessage = new QyWeixinRobotTextMessage("hello,httpClientComponent#send");
        QyWeixinRobotResponse response = httpClientComponent.send(Constant.KEY_LIST.get(0), textMessage);
        Assert.assertNotNull(response);
    }

    @Test
    public void sendAsync() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        QyWeixinRobotTextMessage textMessage = new QyWeixinRobotTextMessage("hello,httpClientComponent#sendAsync");
        CallbackBuilder callbackBuilder = new CallbackBuilder(countDownLatch);
        httpClientComponent.sendAsync(Constant.KEY_LIST.get(0),
                textMessage,
                callbackBuilder::build);
        Assert.assertTrue(true);
    }

    @Test
    public void uploadMedia() {
        try {
            String filePath = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
            byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
            QyWeixinRobotResponse response = httpClientComponent.uploadMedia(Constant.KEY_LIST.get(0), "qrcode.png", byteArray);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());
            Assert.assertNotNull(response.getMedia_id());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    public static class CallbackBuilder {

        private CountDownLatch countDownLatch;

        public CallbackBuilder(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public Callback<QyWeixinRobotResponse> build() {
            return new Callback<QyWeixinRobotResponse>() {
                @Override
                public void onResponse(Call<QyWeixinRobotResponse> call, Response<QyWeixinRobotResponse> response) {
                    Assert.assertNotNull(response);
                    Assert.assertTrue(response.isSuccessful());
                    Assert.assertNotNull(response.body());
                    Assert.assertTrue(response.body().isSuccess());
                    countDownLatch.countDown();
                }
                @Override
                public void onFailure(Call<QyWeixinRobotResponse> call, Throwable throwable) {
                    Assert.fail(throwable.getMessage());
                    countDownLatch.countDown();
                }
            };
        }
    }
}