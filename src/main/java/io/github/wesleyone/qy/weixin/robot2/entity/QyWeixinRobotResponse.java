package io.github.wesleyone.qy.weixin.robot2.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 响应结构体
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotResponse implements Serializable {

    private static final long serialVersionUID = 403271527938819109L;
    private static final int SUCCESS_CODE = 0;
    private static final int SYS_ERROR_CODE = 99999;

    /**
     * 响应码
     */
    private Integer errcode;
    /**
     * 异常信息
     */
    private String errmsg;
    /**
     * 文件上传接口返回值
     * <p>媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件(file)</p>
     */
    private String type;
    /**
     * 文件上传接口返回值
     * <p>媒体文件上传后获取的唯一标识，3天内有效</p>
     */
    private String media_id;
    /**
     * 文件上传接口返回值
     * <p>媒体文件上传时间戳</p>
     */
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

    public static QyWeixinRobotResponse err(String message) {
        final QyWeixinRobotResponse qyWeixinRobotResponse = new QyWeixinRobotResponse();
        qyWeixinRobotResponse.setErrcode(SYS_ERROR_CODE);
        qyWeixinRobotResponse.setErrmsg(message);
        return qyWeixinRobotResponse;
    }

    public static QyWeixinRobotResponse ok() {
        final QyWeixinRobotResponse qyWeixinRobotResponse = new QyWeixinRobotResponse();
        qyWeixinRobotResponse.setErrcode(SUCCESS_CODE);
        qyWeixinRobotResponse.setErrmsg("ok");
        return qyWeixinRobotResponse;
    }

    public boolean isSuccess() {
        return Objects.equals(errcode, SUCCESS_CODE);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QyWeixinRobotResponse.class.getSimpleName() + "[", "]")
                .add("errcode=" + errcode)
                .add("errmsg='" + errmsg + "'")
                .add("type='" + type + "'")
                .add("media_id='" + media_id + "'")
                .add("created_at='" + created_at + "'")
                .toString();
    }
}
