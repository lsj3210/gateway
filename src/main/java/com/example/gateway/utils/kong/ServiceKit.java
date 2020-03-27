package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.utils.ConstUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServiceKit {
    private final static Logger logger = LoggerFactory.getLogger(ServiceKit.class);

    /***
     * 添加service实体
     * @param kongAdminUrl kong集群管理接口地址
     * @param map service参数
     * @return
     * @throws Exception
     */
    public static JSONObject addServices(String kongAdminUrl, Map<String,Object> map) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.SERVICES;
        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        if(map.get("name")!=null){
            arg.put("name", map.get("name"));
        }
        if(map.get("host")!=null){
            arg.put("host", map.get("host"));
        }
        if(map.get("protocol")!=null){
            arg.put("protocol", map.get("protocol"));
        }
        if(map.get("path")!=null){
            arg.put("path", map.get("path"));
        }
        if(map.get("write_timeout")!=null){
            arg.put("write_timeout", map.get("write_timeout"));
        }
        if(map.get("read_timeout")!=null){
            arg.put("read_timeout", map.get("read_timeout"));
        }
        if(map.containsKey("retries")){
            arg.put("retries", map.get("retries"));
        }
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        logger.info("addServices返回结果{}:", response );
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
     * 更新服务
     * @param kongAdminUrl kong集群管理API
     * @param serviceId 服务ID
     * @param map 服务参数
     * @return
     * @throws Exception
     */
    public static JSONObject updateServices(String kongAdminUrl,String serviceId, Map<String,Object> map) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.SERVICES + "/" + serviceId;
        HttpPatch patch = new HttpPatch(url);
        JSONObject arg = new JSONObject();
        if(map.get("write_timeout")!=null){
            arg.put("write_timeout", map.get("write_timeout"));
        }
        if(map.get("read_timeout")!=null){
            arg.put("read_timeout", map.get("read_timeout"));
        }
        if(map.get("protocol")!=null){
            arg.put("protocol", map.get("protocol"));
        }
        if(map.get("path")!=null){
            arg.put("path", map.get("path"));
        }
        if(map.get("name")!=null){
            arg.put("name", map.get("name"));
        }
        if(map.containsKey("retries")){
            arg.put("retries", map.get("retries"));
        }
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        patch.setEntity(reqEntity);
        HttpResponse response = client.execute(patch);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        return jsonObject;
    }

    /**
     * 查询service实体详情
     * @param kongAdminUrl kong集群管理接口地址
     * @param name kong service name 或者kong id
     * @return
     * @throws Exception
     */
    public static JSONObject getServices(String kongAdminUrl,String name) throws Exception {
        String url = kongAdminUrl + ConstUtil.SERVICES+"/"+name;
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        if(jsonObject.getString("id")!=null){
            return jsonObject;
        }else{
            return null;
        }
    }

    /**
     * 之家云专用 - 获取所有 kong service 实体数据
     * @param kongAdminUrl kong集群实例地址
     * @return
     * @throws Exception
     */
    public static JSONObject getAllClusterServices(String kongAdminUrl ) throws Exception {
        String url = kongAdminUrl + ConstUtil.SERVICES;
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        return jsonObject;
    }

    /***
     * 删除服务
     * @param kongAdminUrl kong集群管理接口
     * @param serviceName 服务名称
     * @return
     * @throws Exception
     */
    public static Boolean deleteServicesByName(String kongAdminUrl, String serviceName) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl+ConstUtil.SERVICES+"/"+serviceName;
        HttpDelete delete = new HttpDelete(url);
        HttpResponse response = client.execute(delete);
        logger.info("deleteServicesByName返回结果{}:", response );
        if (response.getStatusLine().getStatusCode() == 204) {
            return true;
        }else {
            return false;
        }
    }
}
