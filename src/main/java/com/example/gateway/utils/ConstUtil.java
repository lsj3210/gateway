package com.example.gateway.utils;

public class ConstUtil {
    // kong
    public static final String SERVICES="/services";
    public static final String UPSTREAMS="/upstreams";
    public static final String TARGETS="/targets";
    public static final String ROUTES="/routes";
    public static final String CONSUMERS="/consumers";
    public static final String PLUGINS="/plugins";
    public static final String CERT = "/certificates";
    public static final String ACLS = "/acls";
    public static final String BASIC_AUTH = "/basic-auth";
    public static final String HMAC_AUTH = "/hmac-auth";
    public static final String JWT = "/jwt";
    public static final String OAUTH2 = "/oauth2";
    public static final String SNIS = "/snis";

    // crontab
    public static final  String  CRON_S5= "*/5 * * * * ?"; //每隔5秒执行一次
    public static final  String  CRON_M1= "0 */1 * * * ?"; //每隔一分钟执行一次
    public static final  String  CRON_M1H1= "0 1 * * * ?"; //每小时 1分钟执行
    public static final  String  CRON_H1= "0 0 1 * * ?";   //每天凌晨1点执行
    public static final  String  CRON_H2= "0 0 2 * * ?";  //每天凌晨2点执行
    public static final  String  CRON_H5= "0 0 5 * * ?";  //每天凌晨5点执行
    public static final  String  CRON_D_H20= "0 0 1 20 * ?"; //每月20号凌晨1点执行
}
