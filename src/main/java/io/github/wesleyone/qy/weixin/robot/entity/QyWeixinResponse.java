package io.github.wesleyone.qy.weixin.robot.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 响应结构体
 * @author http://wesleyone.github.io/
 */
public class QyWeixinResponse implements Serializable {

    private static final long serialVersionUID = 5294486414127538864L;

    private static final int SUCCESS_CODE = 0;
    private static final int SYS_ERROR_CODE = 99998;
    private static final int UNKNOW_CODE = 99999;

    private Integer errcode;
    private String errmsg;
    private String type;
    private String media_id;
    private String created_at;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static QyWeixinResponse unknow() {
        final QyWeixinResponse qyWeixinResponse = new QyWeixinResponse();
        qyWeixinResponse.setErrcode(UNKNOW_CODE);
        qyWeixinResponse.setErrmsg("UNKNOW_CODE");
        return qyWeixinResponse;
    }

    public static QyWeixinResponse err(String message) {
        final QyWeixinResponse qyWeixinResponse = new QyWeixinResponse();
        qyWeixinResponse.setErrcode(SYS_ERROR_CODE);
        qyWeixinResponse.setErrmsg(message);
        return qyWeixinResponse;
    }

    public static QyWeixinResponse ok() {
        final QyWeixinResponse qyWeixinResponse = new QyWeixinResponse();
        qyWeixinResponse.setErrcode(SUCCESS_CODE);
        qyWeixinResponse.setErrmsg("ok");
        return qyWeixinResponse;
    }

    public boolean isSuccess() {
        return Objects.equals(errcode, SUCCESS_CODE);
    }

    @Override
    public String toString() {
        return "QyWeixinResponse{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", type='" + type + '\'' +
                ", media_id='" + media_id + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
