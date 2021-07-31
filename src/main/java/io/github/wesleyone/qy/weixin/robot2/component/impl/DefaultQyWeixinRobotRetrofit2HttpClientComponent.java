package io.github.wesleyone.qy.weixin.robot2.component.impl;

import io.github.wesleyone.qy.weixin.robot2.client.QyWeixinRobotGroupClient;
import io.github.wesleyone.qy.weixin.robot2.component.QyWeixinRobotHttpClientComponent;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotBaseMessage;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Http请求组件
 * @author http://wesleyone.github.io/
 */
public class DefaultQyWeixinRobotRetrofit2HttpClientComponent implements QyWeixinRobotHttpClientComponent<Callback<QyWeixinRobotResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(QyWeixinRobotHttpClientComponent.class.getName());
    private static final String DEFAULT_HOST = "https://qyapi.weixin.qq.com/";
    private static final int MIN_FILE_SIZE = 5;
    private static final int MAX_FILE_SIZE = 20 * 1024 *1024;

    /**
     * Webhook接口
     */
    interface QyWeixinRobotWebhook {
        /**
         * 发送消息
         * @param key       KEY
         * @param message   消息内容
         * @return 响应结果
         */
        @POST("/cgi-bin/webhook/send")
        Call<QyWeixinRobotResponse> send(@Query("key") String key, @Body Map<String,Object> message);

        /**
         * 文件上传接口
         * <p>素材上传得到media_id，该media_id仅三天内有效</p>
         * <p>media_id在同一企业内应用之间可以共享</p>
         * <p>要求文件大小在5B~20M之间</p>
         * @param key   KEY
         * @param type  固定"file"
         * @param file  文件
         * @return  响应结果
         */
        @POST("/cgi-bin/webhook/upload_media")
        @Multipart
        Call<QyWeixinRobotResponse> uploadMedia(@Query("key") String key,@Query("type") String type, @Part MultipartBody.Part file);
    }

    private final QyWeixinRobotWebhook qyWeixinRobotWebhook;

    public DefaultQyWeixinRobotRetrofit2HttpClientComponent() {
        this(new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5,5L,TimeUnit.MINUTES))
                .build());
    }

    public DefaultQyWeixinRobotRetrofit2HttpClientComponent(OkHttpClient httpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        this.qyWeixinRobotWebhook = retrofit.create(QyWeixinRobotWebhook.class);
    }


    @Override
    public QyWeixinRobotResponse send(String key, QyWeixinRobotBaseMessage message) {
        if (logger.isDebugEnabled()) {
            logger.debug("send_request.key:{},message:{}", key, message);
        }
        QyWeixinRobotResponse response = innerSend(key, message);
        if (logger.isDebugEnabled()) {
            logger.debug("send_response key:{},message:{},response:{}", key, message,response);
        }
        return response;
    }

    private QyWeixinRobotResponse innerSend(String key, QyWeixinRobotBaseMessage message) {
        if (key == null || message == null) {
            return QyWeixinRobotResponse.err("Illegal param");
        }
        try {
            Call<QyWeixinRobotResponse> responseCall = qyWeixinRobotWebhook.send(key, message.toMap());
            Response<QyWeixinRobotResponse> execute = responseCall.execute();
            if (execute.isSuccessful()) {
                return execute.body();
            }
            return QyWeixinRobotResponse.err(execute.toString());
        } catch (IOException e) {
            return QyWeixinRobotResponse.err(e.getMessage());
        }
    }

    @Override
    public void sendAsync(String key, QyWeixinRobotBaseMessage message, Supplier<Callback<QyWeixinRobotResponse>> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug("sendAsync_request.key:{},message:{}", key, message);
        }
        if (supplier == null) {
            CallbackBuilder callbackBuilder = new CallbackBuilder(key, message);
            supplier = callbackBuilder::build;
        }
        Call<QyWeixinRobotResponse> responseCall = qyWeixinRobotWebhook.send(key ,message.toMap());
        responseCall.enqueue(supplier.get());
    }

    @Override
    public QyWeixinRobotResponse uploadMedia(String key, String filename, final byte[] content) {
        if (content.length < MIN_FILE_SIZE || content.length > MAX_FILE_SIZE) {
            return QyWeixinRobotResponse.err("要求文件大小在5B~20M之间");
        }
        try {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), content);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("media", filename, fileBody);
            Call<QyWeixinRobotResponse> uploadMediaCall = qyWeixinRobotWebhook.uploadMedia(key, "file", filePart);
            Response<QyWeixinRobotResponse> execute = uploadMediaCall.execute();
            if (execute.isSuccessful()) {
                return execute.body();
            }
            return QyWeixinRobotResponse.err(execute.toString());
        } catch (IOException e) {
            return QyWeixinRobotResponse.err(e.getMessage());
        }
    }

    public static class CallbackBuilder {
        private final String key;
        private final QyWeixinRobotBaseMessage message;
        public CallbackBuilder(String key, QyWeixinRobotBaseMessage message) {
            this.key = key;
            this.message = message;
        }

        public Callback<QyWeixinRobotResponse> build() {
            return new Callback<QyWeixinRobotResponse>() {
                @Override
                public void onResponse(Call<QyWeixinRobotResponse> call, Response<QyWeixinRobotResponse> response) {
                    if (!response.isSuccessful()) {
                        logger.error("sendAsync_onResponse key:{},message:{},response:{}", key, message, response);
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("sendAsync_onResponse key:{},message:{},response:{}", key, message,response.body());
                        }
                    }
                }
                @Override
                public void onFailure(Call<QyWeixinRobotResponse> call, Throwable throwable) {
                    logger.error("sendAsync_onFailure key:{},message:{}", key, message,throwable);
                }
            };
        }
    }
}
