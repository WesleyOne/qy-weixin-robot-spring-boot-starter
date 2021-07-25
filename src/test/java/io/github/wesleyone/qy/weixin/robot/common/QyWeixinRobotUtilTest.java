package io.github.wesleyone.qy.weixin.robot.common;

import io.github.wesleyone.qy.weixin.robot.Constant;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    @Test
    public void isNotEmpty() {
        Assert.assertFalse(QyWeixinRobotUtil.isNotEmpty(new ArrayList<>()));
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new Object());
        Assert.assertTrue(QyWeixinRobotUtil.isNotEmpty(objects));
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(QyWeixinRobotUtil.isEmpty(new ArrayList<>()));
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new Object());
        Assert.assertFalse(QyWeixinRobotUtil.isEmpty(objects));
    }

    @Test
    public void isBlank() {
        Assert.assertTrue(QyWeixinRobotUtil.isBlank(null));
        Assert.assertTrue(QyWeixinRobotUtil.isBlank(""));
        Assert.assertTrue(QyWeixinRobotUtil.isBlank("   "));
        Assert.assertFalse(QyWeixinRobotUtil.isBlank("  a "));
        Assert.assertFalse(QyWeixinRobotUtil.isBlank("a"));
    }

    @Test
    public void subByteArray() {
        byte[] bytes = new byte[5];
        bytes[0] = 0;
        bytes[1] = 1;
        bytes[2] = 2;
        bytes[3] = 3;
        bytes[4] = 4;
        int len = 1;
        byte[] bytes0 = QyWeixinRobotUtil.subByteArray(bytes, 0, len);
        Assert.assertEquals(len,bytes0.length);
        Assert.assertEquals(0,bytes0[0]);
        int len1 = 2;
        byte[] bytes1 = QyWeixinRobotUtil.subByteArray(bytes, 1, len1);
        Assert.assertEquals(len1,bytes1.length);
        Assert.assertEquals(1,bytes1[0]);
        Assert.assertEquals(2,bytes1[1]);
        int len2 = 5;
        byte[] bytes2 = QyWeixinRobotUtil.subByteArray(bytes, 0, len2);
        Assert.assertEquals(len2,bytes2.length);
        Assert.assertEquals(0,bytes2[0]);
        Assert.assertEquals(1,bytes2[1]);
        Assert.assertEquals(2,bytes2[2]);
        Assert.assertEquals(3,bytes2[3]);
        Assert.assertEquals(4,bytes2[4]);
    }

    @Test
    public void subStringByByteArrayLength() {
        String source = "Hello";
        String target = QyWeixinRobotUtil.subStringByByteArrayLength(source, 4);
        Assert.assertEquals("Hell",target);
        String target2 = QyWeixinRobotUtil.subStringByByteArrayLength(source, 14);
        Assert.assertEquals(source,target2);
    }
}