package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpKit {

    public static class HttpPatch extends HttpPut {
        public HttpPatch(String url) {
            super(url);
        }
        @Override
        public String getMethod(){
            return "PATCH";
        }
    }
    public static class HttpDel extends HttpDelete {
        public HttpDel(String url) {
            super(url);
        }
        @Override
        public String getMethod(){
            return "DELETE";
        }
    }

    public static JSONObject DoHttpGet(String _url, JSONObject _arg) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(_url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(_arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        return jsonObject;
    }

    public static JSONObject DoHttpPost(String _url, JSONObject _arg) throws Exception {
        return null;
    }
}
