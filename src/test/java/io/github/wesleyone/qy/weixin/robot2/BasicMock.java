package io.github.wesleyone.qy.weixin.robot2;

import com.alibaba.testable.core.annotation.MockMethod;
import io.github.wesleyone.qy.weixin.robot2.entity.QyWeixinRobotResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MOCK
 * @author http://wesleyone.github.io/
 */
public class BasicMock {
    @MockMethod(targetClass= Call.class,targetMethod = "execute")
    private Response<QyWeixinRobotResponse> execute() {
        System.out.println("MOCK Call.execute");
        QyWeixinRobotResponse ok = QyWeixinRobotResponse.ok();
        ok.setMedia_id("mock");
        ok.setType("file");
        ok.setCreated_at(""+System.currentTimeMillis()/1000);
        return Response.success(ok);
    }
    @MockMethod(targetClass= Call.class,targetMethod = "enqueue")
    private void enqueue(final Callback<QyWeixinRobotResponse> callback) {
        System.out.println("MOCK Call.enqueue");
        QyWeixinRobotResponse ok = QyWeixinRobotResponse.ok();
        ok.setMedia_id("mock");
        ok.setType("file");
        ok.setCreated_at(""+System.currentTimeMillis()/1000);
        callback.onResponse(null, Response.success(ok));
    }
}
