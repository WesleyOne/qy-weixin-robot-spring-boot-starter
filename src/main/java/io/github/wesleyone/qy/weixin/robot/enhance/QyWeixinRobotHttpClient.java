package io.github.wesleyone.qy.weixin.robot.enhance;

import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinResponse;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Http请求客户端
 * <p>注意：目前不考虑更换域名、更换接口的场景
 * <p>核心方法：
 * <ul>
 *     <li>sendSync同步请求</li>
 *     <li>sendAsync异步请求</li>
 * </ul>
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotHttpClient implements EnhanceInterface {

    public static final String DEFAULT_HOST = "https://qyapi.weixin.qq.com/";
    /**
     * Webhook接口
     */
    interface QyWeixinRobotWebhook {
        /**
         * 发送消息
         * @param key KEY
         * @param message   消息内容
         * @return 响应结果
         */
        @POST("/cgi-bin/webhook/send")
        Call<QyWeixinResponse> send(@Query("key") String key, @Body Map<String,Object> message);

        /**
         * 上传文件
         * @param key   KEY
         * @param type  固定"file"
         * @param file  文件
         * @return  响应结果
         */
        @POST("/cgi-bin/webhook/upload_media")
        @Multipart
        Call<QyWeixinResponse> uploadMedia(@Query("key") String key,@Query("type") String type, @Part MultipartBody.Part file);
    }

    private final OkHttpClient client;

    private final QyWeixinRobotWebhook webhookApi;

    public QyWeixinRobotHttpClient() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5,5L,TimeUnit.MINUTES))
                .build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(DEFAULT_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        this.webhookApi = retrofit.create(QyWeixinRobotWebhook.class);
    }

    public QyWeixinRobotHttpClient(OkHttpClient client) {
        this.client = client;
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(DEFAULT_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        this.webhookApi = retrofit.create(QyWeixinRobotWebhook.class);
    }

    /**
     * 同步请求
     * @param key       KEY
     * @param message   消息
     * @return true发送成功
     */
    public QyWeixinResponse sendSync(String key, Map<String,Object> message) {
        if (webhookApi == null) {
            return QyWeixinResponse.err("webhookApi null");
        }
        Call<QyWeixinResponse> send = webhookApi.send(key ,message);
        Response<QyWeixinResponse> execute;
        try {
            execute = send.execute();
        } catch (IOException e) {
            return QyWeixinResponse.err(e.getMessage());
        }
        if (!execute.isSuccessful()) {
            String errMsg = "";
            try {
                if (execute.errorBody() != null) {
                    errMsg = execute.errorBody().string();
                }
            } catch (IOException e) {
                // ignore
            }
            return QyWeixinResponse.err(errMsg);
        }
        QyWeixinResponse body = execute.body();
        if (body == null) {
            return QyWeixinResponse.err("QyWeixinResponse null");
        }
        return body;
    }

    /**
     * 异步请求
     * @param key       KEY
     * @param message   消息
     * @param callback  回调
     */
    public void sendAsync(String key, Map<String,Object> message, Callback<QyWeixinResponse> callback) {
        if (webhookApi == null) {
            return;
        }
        Call<QyWeixinResponse> send = webhookApi.send(key ,message);
        send.enqueue(callback);
    }

    public void uploadMedia(String key, String filePath, Callback<QyWeixinResponse> callback) {
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("media", file.getName(), fileBody);
        Call<QyWeixinResponse> uploadMediaCall = webhookApi.uploadMedia(key, "file", filePart);
        uploadMediaCall.enqueue(callback);
    }

}
