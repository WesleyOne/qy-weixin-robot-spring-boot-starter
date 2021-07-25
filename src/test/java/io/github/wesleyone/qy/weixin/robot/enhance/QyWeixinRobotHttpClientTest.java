package io.github.wesleyone.qy.weixin.robot.enhance;

import io.github.wesleyone.qy.weixin.robot.BasicMock;
import io.github.wesleyone.qy.weixin.robot.Constant;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinResponse;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinTextMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotHttpClientTest {

    public static class Mock extends BasicMock {
    }

    private QyWeixinRobotHttpClient qyWeixinRobotHttpClient;
    @Before
    public void setUp() {
        qyWeixinRobotHttpClient = new QyWeixinRobotHttpClient();
        qyWeixinRobotHttpClient.init();
    }

    @After
    public void tearDown() throws InterruptedException {

    }

    @Test
    public void sendSync() {
        final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("文本类型同步发送测试");
        QyWeixinResponse response = qyWeixinRobotHttpClient.sendSync(Constant.WEBHOOK_URL_KEY[0], qyWeixinTextMessage.toMap());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void sendAsync() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final QyWeixinTextMessage qyWeixinTextMessage = new QyWeixinTextMessage("文本类型异步发送测试");
        qyWeixinRobotHttpClient.sendAsync(Constant.WEBHOOK_URL_KEY[0], qyWeixinTextMessage.toMap(), new Callback<QyWeixinResponse>() {
            @Override
            public void onResponse(Call<QyWeixinResponse> call, Response<QyWeixinResponse> response) {
                Assert.assertTrue(response.isSuccessful());
                Assert.assertNotNull(response.body());
                Assert.assertTrue(response.body().isSuccess());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<QyWeixinResponse> call, Throwable throwable) {
                Assert.fail(throwable.getMessage());
                countDownLatch.countDown();
            }
        });
        boolean await = countDownLatch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(await);
    }

    @Test
    public void uploadMedia() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        qyWeixinRobotHttpClient.uploadMedia(Constant.WEBHOOK_URL_KEY[0], Constant.IMG_PATH, new Callback<QyWeixinResponse>() {
            @Override
            public void onResponse(Call<QyWeixinResponse> call, Response<QyWeixinResponse> response) {
                Assert.assertTrue(response.isSuccessful());
                Assert.assertNotNull(response.body());
                Assert.assertTrue(response.body().isSuccess());
                Assert.assertNotNull(response.body().getMedia_id());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<QyWeixinResponse> call, Throwable throwable) {
                Assert.fail(throwable.getMessage());
                countDownLatch.countDown();
            }
        });
        boolean await = countDownLatch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(await);
    }
}