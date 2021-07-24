package io.github.wesleyone.qy.weixin.robot;

import com.alibaba.testable.core.annotation.MockMethod;
import io.github.wesleyone.qy.weixin.robot.entity.QyWeixinResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MOCK
 * @author http://wesleyone.github.io/
 */
public class BasicMock {
    @MockMethod(targetClass= Call.class,targetMethod = "execute")
    private Response<QyWeixinResponse> execute() {
        System.out.println("MOCK Call.execute");
        return Response.success(QyWeixinResponse.ok());
    }
    @MockMethod(targetClass= Call.class,targetMethod = "enqueue")
    private void enqueue(final Callback<QyWeixinResponse> callback) {
        System.out.println("MOCK Call.enqueue");
        QyWeixinResponse ok = QyWeixinResponse.ok();
        ok.setMedia_id("mock");
        ok.setType("file");
        ok.setCreated_at(""+System.currentTimeMillis()/1000);
        callback.onResponse(null, Response.success(ok));
    }
}
