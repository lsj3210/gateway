package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.utils.ConstUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CertKit {
    private final static Logger logger = LoggerFactory.getLogger(CertKit.class);

    public static boolean addCert(String adminUrl  , Map<String,Object> map) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url =  adminUrl+ ConstUtil.CERT;

        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        if(map.get("key")!=null){
            arg.put("key", map.get("key"));
        }
        if(map.get("cert")!=null){
            arg.put("cert", map.get("cert"));
        }
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        int code = response.getStatusLine().getStatusCode();
        if (code == 201) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 查询证书列表
     * @param kong_admin_url
     * @return
     * @exception Exception
     */
    public static JSONObject queryCerts(String kong_admin_url) throws Exception {
        String url = kong_admin_url+ConstUtil.CERT;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        if(jsonObject.getString("data")!=null){
            return jsonObject;
        }else{
            return null;
        }
    }

    /**
     * 之家云专用 - 为域名绑定证书
     * @param kong_admin_url
     * @param cert_id
     * @param domain
     * @return
     * @exception Exception
     */
    public static boolean addSNI(String kong_admin_url,String cert_id,String domain) throws Exception {
        String url = kong_admin_url + ConstUtil.CERT + "/"+ cert_id +"/snis";
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        arg.put("name", domain);
        arg.put("certificate", "{\"id\":\""+cert_id+"\"}");

        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        int code = response.getStatusLine().getStatusCode();
        if (code == 201) {
            return true;
        }else{
            return false;
        }
    }

    /***
     * 之家云专用 - 通过域名查询证书
     * @param kong_admin_url
     * @param domain
     * @return
     * @throws Exception
     */
    public static boolean queryCertBySNI(String kong_admin_url,String domain) throws Exception{
        String url = kong_admin_url+ ConstUtil.SNIS+"/" + domain;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        if(jsonObject.getString("id")!=null){
            return true;
        }else{
            return false;
        }
    }
}
