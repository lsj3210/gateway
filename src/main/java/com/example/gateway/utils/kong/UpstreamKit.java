package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.utils.ConstUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UpstreamKit {
    private final static Logger logger = LoggerFactory.getLogger(UpstreamKit.class);

    /***
     * 添加upstream接口
     * @param kongAdminUrl
     * @param map
     * @return
     * @throws Exception
     */
    public static JSONObject addUpstreams(String kongAdminUrl,Map<String,Object> map) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.UPSTREAMS;
        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        if(map.get("name")!=null){
            arg.put("name", map.get("name").toString());
        }
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 201) {
            HttpEntity entity = response.getEntity();
            String message = EntityUtils.toString(entity, "UTF-8");
            JSONObject jsonObject = JSON.parseObject(message);
            return jsonObject;
        }else{
            return null;
        }
    }

    /***
     * 更新upstream接口
     * @param kongAdminUrl
     * @param map
     * @return
     * @throws Exception
     */
    public static JSONObject modifyUpstreams(String kongAdminUrl,String upstreamId ,Map<String,Object> map) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.UPSTREAMS+"/"+upstreamId;
        HttpPatch patch = new HttpPatch(url);
        JSONObject arg = new JSONObject();
        arg.putAll(map );

        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        patch.setEntity(reqEntity);
        HttpResponse response = client.execute(patch);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);

        return jsonObject;
    }

    /***
     * 之家云专用 - 查询upstream接口
     * @param kongAdminUrl
     * @param upstreamId
     * @return
     * @throws Exception
     */
    public static JSONObject getUpstreams(String kongAdminUrl,String upstreamId) throws Exception {
        String url = kongAdminUrl + ConstUtil.UPSTREAMS+"/"+upstreamId;
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
            return jsonObject;
        }else{
            return null;
        }
    }

    /***
     * 删除upstream接口
     * @param kongAdminUrl
     * @param upstreamId
     * @return
     * @throws Exception
     */
    public static Boolean deleteUpstreams(String kongAdminUrl,String upstreamId) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.UPSTREAMS+"/" + upstreamId;
        HttpKit.HttpDel httpdel = new HttpKit.HttpDel(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(httpdel);
        if (response.getStatusLine().getStatusCode() == 204) {
            return true;
        }else{
            return false;
        }
    }

}
