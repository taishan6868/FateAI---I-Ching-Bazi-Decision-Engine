package com.msld.wujibazi;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.github.kevinsawicki.http.HttpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.DigestUtils;

import java.util.*;

@SpringBootTest
class WujibaziApplicationTests {

    @Autowired
    private JavaMailSender javaMailSender;

    //发件人邮箱
    @Value("${spring.mail.username}")
    private String from;

    @Test
    void contextLoads() {

    }

    /**
     * 国内验证码
     */
    @Test
    void send() {
        Map<String, Object> map = new HashMap<>();
        map.put("account", "YZM4773702");// API账号
        map.put("password", "76PwJurVil7f0e");// API密码
        map.put("msg", "【无极天下】您的短信验证码为895689，5分钟内有效，若非本人操作请忽略。");//短信内容
        map.put("phone", "13530770611");// 手机号
        map.put("report", "true");// 是否需要状态报告
        map.put("extend", "275780");// 自定义扩展码
        System.out.println(JSON.toJSONString(map));
        String body = HttpRequest.post("http://smssh1.253.com/msg/v1/send/json").contentType("application/json", "UTF-8").send(JSON.toJSONString(map)).body();
        System.out.println(body);
    }

    /**
     * 国际普通短信
     */
    @Test
    void sendInternational() {
        Map<String, Object> map = new HashMap<>();
        map.put("account", "I0750347");// API账号
        map.put("password", "3ZYKc4G1jHd30c");// API密码
        map.put("msg", "【无极天下】您的短信验证码为895689，5分钟内有效，若非本人操作请忽略。");//短信内容
        map.put("mobile", "8613530770611");// 手机号
        System.out.println(JSON.toJSONString(map));
        String body = HttpRequest.post("http://intapi.253.com/send/json").contentType("application/json", "UTF-8").send(JSON.toJSONString(map)).body();
        System.out.println(body);
    }

    /**
     * 国际验证码
     */
    @Test
    void sendInternationalApi() {
        Map<String, String> map = new HashMap<>();
        map.put("account", "I0750347");// API账号
        map.put("msg", "【无极天下】您的短信验证码为895689，5分钟内有效，若非本人操作请忽略。");//短信内容
        map.put("mobile", "8613530770611");// 手机号
        Map<String, String> headers = new HashMap<>();
        headers.put("nonce", new Date().getTime() + "");

        Map<String, String> allMap = new HashMap<>();
        allMap.putAll(map);
        allMap.putAll(headers);

        String sign = getSign(allMap);
        headers.put("sign", sign);
        System.out.println(sign);
        String body = HttpRequest.post("https://intapi.253.com/send/sms").contentType("application/json", "UTF-8").headers(headers).send(JSON.toJSONString(map)).body();
        System.out.println(body);
    }

    /**
     * 计算签名
     */
    public static String getSign(Map<String, String> params) {
        // 参数进行字典排序
        String sortStr = getFormatParams(params);
        sortStr += "3ZYKc4G1jHd30c";
        System.out.println(sortStr);
        return DigestUtils.md5DigestAsHex(sortStr.getBytes()).toLowerCase(Locale.ROOT);
    }

    /**
     * 参数按ASCII排序
     */
    private static String getFormatParams(Map<String, String> params) {
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(params.entrySet());
        infoIds.sort(Map.Entry.comparingByKey());
        StringBuilder ret = new StringBuilder();
        for (Map.Entry<String, String> entry : infoIds) {
            ret.append(entry.getKey());
            // ret.append("=");
            ret.append(entry.getValue());
            // ret.append("&");
        }
        return ret.toString();
    }


    @Test
    void sendMail() {
        sendTextMail("验证码", "您的短信验证码为123456，5分钟内有效，若非本人操作请忽略。", "qinzyang@163.com");
    }

    private void sendTextMail(String subject, String content, String... to) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        mailMessage.setSentDate(new Date());
        javaMailSender.send(mailMessage);
    }

    @Test
    void testALiPay() throws AlipayApiException {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC2WEUAfm6kDOYHH0i6bg+Kqv4LZDeWCis5ZSx5m1vjwS4IdWqbFTgZrfPg1ZpFJRYiZ7SvUwOzWYApW+pXP7phSvrKYWA1kv7/yp2MzAKMGuiyxJqyGfB75zJNzq33TXNh9Hbzz7vHHgYUdrFkj1ZDq3QY522ymKdfK8u4RcXGEah2SatAEQWZ4VlvomFUbfkr9pStLFzkyS+lJTQXmBwf7jsLg6YmBAvxxWjZ8Y1ZxSUPJscGi09qGJUR6m2j5sKwDaWAAwlmlN+TkdtAQ7l03fpcecOJXS/KK4CfCZIY3+Omv7vmsJSq4Ep9e/tPxMD2+dGZkk8o1drdhVJy1MgZAgMBAAECggEAOlymOTI+V2OJfF4W+RShBNEV3q+E2xEGJKitQ37mOk9qXGq4ZEq2WgHDPQMhDWCBZSGsQzFezdinnJU+vA45qUpGWuAv2vXhiB1hr97cMduq/wzONpL21bF5+E1egPjZJVEJgZ/Lij+h0orq3DTEd4Q49H+eLLuPVna+DYzHcE1Kxy4JDkIUZYilqGLywa2/GkOR27jThsE8L9X+tJhaF9FEjVBFsz0mryUzcg+OHArUnqQTQV9x2cqQvrl2T9qoBxIowY6U1RIH1+b+IDdRXitZnPQvAvbj1VWN7fqqEya14uzrsfTuqUPU/UArILzJXF6O4CWyXsKSx6cvlizkwQKBgQD59FPyt963jKz/ySSGoOMrDryrVk86lUaeGhdBg5rLYzG32UPY0xPnylQit3y/ypWru77UOLVGtNEYgLAosAkmom2hqHwecTXp3iY1W0YI98Y+qS7yp8h0RjOXXiBJxQVSEhMaszK0pmvMiUw771dICMIaZ/rDP0PNj90uZ9XdZwKBgQC6wVC20d96SP5RobM49e2aRQdiOHdoOP7rjMkI56yvPwniT3sgqWiAj/ZqwcK2xDCjMYShT0SEl6ROkLZmbOv2o0X/TimAGc2FTobpkMNIu56SAZ9q3NKM5FFOKlmpA2SDy1Deh6VP7ffuOKnig6zP6NlDN/ronJLQcwb0O9s+fwKBgQDYIeIZJc7qjjTldKsl0qa8C6Eu0pHtyThn642ic8b9CYfrPm5BZbt3mWeJIXo4aVdkSRKodWSigCWx90zb0eQHyTndKFAzDpec4x/M0WDMzltYwXg8EMLvb+iagaSPxVJldgWY98znkmPyV+JYjQu+2PvmU3TtMZ5SeIy73MN8DwKBgA6qA0Z3sE6P0LhkNAhy9xc3+F50BJxAbXPdeCOk3WBdWM9WYlqsMrJ1m2nCkSF8wjzuNHXeHYkzxwkMP+ZHAzg4ldR4NLYEUKj5A4rvfQYHBWp9iiD/+eBqRSAYHzYwOAJJQq5WhFqEWwVvmZdZs4YXyIeIq8D8GmMXxSIEWTDNAoGAKkxKUXdt0MWe5cSXCSPJf6xusshB+aLYQoik+4erfM/XoQrAtWXoTjru3alZ2/BB4di6xtnQF8WA/H8Q+xmN4zIlRvQ9GaOHIEpEAdYfTBtrHhs+Xz0C+xU+XbANVpssn09J2Q8hx3APJBY899Lpi4alQmzBLaGuTZXlfOjHXAA=";
        String aLiPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmdygCQ4GKuOT+WEh1dPS+Z7m2iTaJFtI5kdVHB97V4HQY9HJYPfd6QrNTye5k2iEqwWErtG2/fkRIxmm93ao4VsQ4D4+xF5aZ/qPQwvMNw0tYGDm9S18H9LyRem6u10T0mOsYPa1aake5HrKg4ZvsFH3z3peXqErFMf36gCNL4WRfXCO+wWaIgnkUG6fTFls4LZbMqvzwpJHOUN9XSzuCOXI/hP/AVmMZeWHMGCLcrlX297i6h9Sp/yLg/TVbz/F6RIjeLU/c8b66rYqnnNG+GFHJUpK5seog4peWvVTGqcmwz69VVj0GLMZ7qf0oRk8L8hRLAffrtTvFTrOlq3yiwIDAQAB";
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2021004104624098", privateKey, "json", "GBK", aLiPayPublicKey, "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl("");
        request.setReturnUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101004");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //bizContent.put("time_expire", "2022-08-01 22:00:00");

        //// 商品明细信息，按需传入
        //JSONArray goodsDetail = new JSONArray();
        //JSONObject goods1 = new JSONObject();
        //goods1.put("goods_id", "goodsNo1");
        //goods1.put("goods_name", "子商品1");
        //goods1.put("quantity", 1);
        //goods1.put("price", 0.01);
        //goodsDetail.add(goods1);
        //bizContent.put("goods_detail", goodsDetail);

        //// 扩展信息，按需传入
        //JSONObject extendParams = new JSONObject();
        //extendParams.put("sys_service_provider_id", "2088511833207846");
        //bizContent.put("extend_params", extendParams);

        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }

    @Test
    void getZoneByLngAndLat() {
        // String s = HttpRequest.get("https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=" + (new Date().getTime() / 1000) + "&key=AIzaSyB4PBAJe8iciAcl1dhrhmSdRNAl77vYS4g").body();
        // System.out.println(s);

        String s = HttpRequest.get("https://maps.googleapis.com/maps/api/geocode/json?latlng=39.6034810,-119.6822510&location_type=ROOFTOP&result_type=street_address&key=AIzaSyB4PBAJe8iciAcl1dhrhmSdRNAl77vYS4g").body();
        JSONObject object = JSONObject.parseObject(s);
        JSONObject plus_code = object.getJSONObject("plus_code");
        if (plus_code != null && plus_code.getString("compound_code") != null) {
            String compound_code = plus_code.getString("compound_code");
            if (compound_code.contains(" ")) {
                System.out.println(compound_code.substring(compound_code.indexOf(" "), compound_code.length()));
            } else {
                System.out.println(compound_code);
            }

        }
    }


}
