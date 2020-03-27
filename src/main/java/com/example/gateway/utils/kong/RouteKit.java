package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.utils.ConstUtil;
import com.google.common.collect.Maps;
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

public class RouteKit {
    private final static Logger logger = LoggerFactory.getLogger(RouteKit.class);


    /**
     * 新增kong route实体
     * @param kongAdminUrl kong集群控制接口地址
     * @param map route body参数
     * @return
     * @throws Exception
     */
    public static JSONObject addRoutes(String kongAdminUrl,Map<String,Object> map) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.ROUTES;
        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();

        if(map.get("serviceId")!=null){
            Map serviceIdMap = Maps.newConcurrentMap();
            serviceIdMap.put("id",map.get("serviceId"));
            arg.put("service",serviceIdMap );
        }
        if(map.get("strip_path")!=null){
            arg.put("strip_path", (boolean)map.get("strip_path"));
        }
        if(map.get("preserve_host")!=null){
            arg.put("preserve_host", (boolean)map.get("preserve_host"));
        }
        if(map.get("protocols")!=null){
            String stringArray[] = map.get("protocols").toString().split(",");
            arg.put("protocols",stringArray);
        }
        if(map.get("hosts")!=null){
            String stringArray[] = {map.get("hosts").toString()};
            arg.put("hosts", stringArray);
        }
        if(map.get("paths")!=null){
            String stringArray[] = {map.get("paths").toString()};
            arg.put("paths", stringArray);
        }
        if(map.get("methods")!=null){
            String stringArray[] = map.get("methods").toString().split(",");
            arg.put("methods", stringArray);
        }

        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        logger.info("addRoutes返回结果{}:", message );
        if (response.getStatusLine().getStatusCode() == 201) {

            JSONObject jsonObject = JSON.parseObject(message);
            return jsonObject;
        }else{
            logger.error("返回异常:{}", message  );
            return null;
        }

    }

    /***
     * 更新 kong route实体
     * @param kongAdminUrl kong集群 接口地址
     * @param routeId route Id
     * @param map route 参数
     * @return
     * @throws Exception
     */
    public static JSONObject updateRoutes( String kongAdminUrl,String routeId , Map<String,String> map) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.ROUTES + "/" + routeId;
        HttpPatch patch = new HttpPatch(url);
        JSONObject arg = new JSONObject();
        if(map.get("serviceId")!=null){
            Map serviceIdMap = Maps.newConcurrentMap();
            serviceIdMap.put("id",map.get("serviceId"));
            arg.put("service",serviceIdMap );
        }

        if(map.get("hosts")!=null){
            String stringArray[] = {map.get("hosts")};
            arg.put("hosts", stringArray);
        }
        if(map.get("protocols")!=null){
            String stringArray[] = map.get("protocols").split(",");
            arg.put("protocols",stringArray);
        }
        if(map.get("paths")!=null){
            String stringArray[] = {map.get("paths")};
            arg.put("paths", stringArray);
        }
        if(map.get("preserve_host")!=null){
            arg.put("preserve_host", Boolean.parseBoolean( map.get("preserve_host")));
        }

        if(map.get("strip_path")!=null){
            arg.put("strip_path", Boolean.parseBoolean( map.get("strip_path")));
        }

        if (map.get("methods") != null) {
            String stringArray[] = map.get("methods").split(",");
            arg.put("methods", stringArray);
        }
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        patch.setEntity(reqEntity);
        HttpResponse response = client.execute(patch);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        logger.info("updateRoutes返回结果{}:", jsonObject );
        return jsonObject;
    }

    /***
     *删除route实体
     * @param kongAdminUrl kong集群接口地址
     * @param routeID route Id
     * @return
     * @throws Exception
     */
    public static Boolean deleteRoutes(String kongAdminUrl, String routeID) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.ROUTES+"/"+ routeID;
        HttpDelete delete = new HttpDelete(url);
        HttpResponse response = client.execute(delete);
        logger.info("deleteRoutes返回结果{}:", response );
        if (response.getStatusLine().getStatusCode() == 204) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 查询route实体详细配置参数
     * @param kongAdminUrl kong集群接口地址
     * @param routeId route id
     * @return
     * @throws Exception
     */
    public static JSONObject getRoute(String kongAdminUrl,String routeId) throws Exception {
        String url = kongAdminUrl + ConstUtil.ROUTES+"/"+routeId;
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info( "getRoute返回值:",jsonObject);
        if(jsonObject.getString("id")!=null){
            return jsonObject;
        } else{
            return null;
        }
    }

    /**
     * 获得服务下面的所有 路由
     * @param kongAdminUrl
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static JSONObject getRouteByService(String kongAdminUrl, String serviceId) throws Exception {
        String url = kongAdminUrl+ ConstUtil.SERVICES  +"/"+serviceId + ConstUtil.ROUTES ;
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        return jsonObject ;
    }

}
