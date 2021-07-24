package io.github.wesleyone.qy.weixin.robot.common;

import io.github.wesleyone.qy.weixin.robot.Constant;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * QyWeixinUtilTest测试用例
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotUtilTest {

    @Test
    public void testBase64() throws IOException {
        String path = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
        final String base64 = QyWeixinRobotUtil.base64(path);
        Assert.assertEquals(Constant.BASE64,base64);
    }

    @Test
    public void testMd5() throws IOException, NoSuchAlgorithmException {
        String path = Objects.requireNonNull(this.getClass().getResource("/")).getPath()+"qrcode.png";
        final String md5 = QyWeixinRobotUtil.md5(path);
        Assert.assertEquals(Constant.MD5,md5);
    }
}