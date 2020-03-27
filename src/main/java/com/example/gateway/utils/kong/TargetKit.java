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

public class TargetKit {

    private final static Logger logger = LoggerFactory.getLogger(TargetKit.class);

    /**
     * 删除 upstrea的 所有 target
     * @param url
     * @param upstreamId
     * @param targetId
     * @return
     * @throws Exception
     */
    public static Boolean deleteTargets( String url , String upstreamId ,String targetId ) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        url = url + ConstUtil.UPSTREAMS+"/"+upstreamId+ConstUtil.TARGETS+"/"+targetId;
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


    /***
     * 之家云专用 - 添加 targets
     * @param kongAdmingUrl kong集群 admin 接口地址
     * @param upstreamId 所属upstream Id
     * @param target 目标 ip:port
     * @param weight 目标 权重
     * @return
     * @throws Exception
     */
    public static JSONObject addTargets(String kongAdmingUrl,String upstreamId,String target,String weight) throws Exception {
        logger.info("addTargets参数{}={}={}={}", kongAdmingUrl,upstreamId,target,weight );
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdmingUrl + ConstUtil.UPSTREAMS + "/" + upstreamId + ConstUtil.TARGETS;

        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        arg.put("target",target);
        arg.put("weight", Integer.parseInt(weight));

        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        logger.info("addTargets返回结果{}:", response );
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
     * 之家云专用 - 查询 targets列表
     * @param kongAdminUrl kongAdmingUrl kong集群 admin 接口地址
     * @param upstreamId 所属upstream Id
     * @return
     * @throws Exception
     */
    public static JSONObject getTargets(String kongAdminUrl,String upstreamId) throws Exception {
        String url = kongAdminUrl + ConstUtil.UPSTREAMS+"/"+upstreamId + ConstUtil.TARGETS;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        logger.info("getTargets返回结果{}:", jsonObject );
        if(jsonObject.getString("data")!=null){
            return jsonObject;
        }else{
            return null;
        }
    }
}
