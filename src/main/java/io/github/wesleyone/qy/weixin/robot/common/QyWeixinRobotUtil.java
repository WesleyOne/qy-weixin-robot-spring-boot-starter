package io.github.wesleyone.qy.weixin.robot.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

/**
 * @author http://wesleyone.github.io/
 */
public class QyWeixinRobotUtil {


    /**
     * 集合判非空
     * @param collection    集合
     * @return  true非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 集合判空
     * @param collection    集合
     * @return  true空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return !isNotEmpty(collection);
    }

    /**
     * 判断是否全部是空字符串
     * @param source    字符串
     * @return  true全空
     */
    public static boolean isBlank(String source) {
        if (source == null) {
            return true;
        }
        return source.trim().length() == 0;
    }

    /**
     * 截取byte数组
     * <p>不改变原数组
     * @param source 原数组
     * @param offset 偏差值
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByteArray(byte[] source,int offset,int length) {
        byte[] target = new byte[length];
        System.arraycopy(source, offset, target, 0, length);
        return target;
    }

    public static String subStringByByteArrayLength(String source,int length) {
        int sourceByteLen = source.getBytes(StandardCharsets.UTF_8).length;
        if (sourceByteLen <= length) {
            return source;
        }
        final byte[] targetByteArray = subByteArray(
                source.getBytes(StandardCharsets.UTF_8)
                , 0, length);
        return new String(targetByteArray,StandardCharsets.UTF_8);
    }

    /**
     * 文件转成Base64
     * <p>可用于图片类型消息
     *
     * @param filePath  文件路径
     * @return          Base64编码
     * @throws IOException  IO异常
     */
    public static String base64(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath is null");
        }
        byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(byteArray);
    }


    /**
     * 获取文件的md5值
     * <p>可用于图片类型消息</p>
     *
     * @param filePath  文件路径
     * @return  MD5编码
     * @throws IOException  IO异常
     * @throws NoSuchAlgorithmException 算法不存在异常
     */
    public static String md5(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5Algorithm = MessageDigest.getInstance("MD5");
        byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
        md5Algorithm.update(byteArray, 0, byteArray.length);
        byte[] digest = md5Algorithm.digest();
        StringBuilder md5Sb = new StringBuilder();
        for (byte b : digest) {
            String tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                md5Sb.append("0").append(tmp);
            } else {
                md5Sb.append(tmp);
            }
        }
        return md5Sb.toString();
    }

}
