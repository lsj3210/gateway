package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.utils.ConstUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PluginKit {
    private final static Logger logger = LoggerFactory.getLogger(PluginKit.class);

    /**
     * 原始插件进行 过滤 1 插件名称 2 消费者名称
     * @param pluginName 过滤条件插件名称
     * @param consumerName  - 过滤条件-消费者名称
     * @param pluginsList 原始插件列表
     * @return
     */
    public static Map<String, Object> parsePluginsData(String pluginName,String consumerName, JSONObject pluginsList ){

        if (pluginsList != null && pluginsList.size()>1) {
            JSONArray json = pluginsList.getJSONArray("data");
            if (json!=null & !json.isEmpty() ) {
                List<Map<String, Object>> list  = new ArrayList<>();
                for (int i = 0; i < json.size(); i++) {
                    JSONObject job = json.getJSONObject(i);
                    Iterator keys = job.keySet().iterator();
                    Map<String, Object> kongPluginsInfo = new HashMap<>();
                    while (keys.hasNext()) {
                        String key = keys.next().toString();
                        Object value = job.getString(key);
                        kongPluginsInfo.put(key, value);
                    }
                    if (kongPluginsInfo.get("name").equals(pluginName)) {
                        list.add(kongPluginsInfo);
                    }
                }
                if(consumerName!=null && consumerName.length()>0){
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> currentPlugins = list.get(i);
                        if(currentPlugins.get("consumer_id")!=null && currentPlugins.get("consumer_id").toString().equals(consumerName)){
                            return currentPlugins;
                        }
                    }
                }else{
                    if(list.size()==1){
                        return list.get(0);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 转换 f服务关联的插件信息
     * @param pluginName
     * @param pluginsList -用服务id查询的 pluginsList 里面有路由对应的插件，所以需要单独过滤
     * @return
     */
    public static Map<String, Object> parseServicePluginsData(String pluginName, JSONObject pluginsList ){

        if (pluginsList != null && !pluginsList.isEmpty()) {
            JSONArray dataJson = pluginsList.getJSONArray("data");
            if (dataJson!=null & !dataJson.isEmpty() ) {
                List<Map<String, Object>>  list  = new ArrayList<>();
                for (int i = 0; i < dataJson.size(); i++) {
                    JSONObject pluginJson = dataJson.getJSONObject(i);
                    JSONObject routJson = pluginJson.getJSONObject("route");
                    //过滤掉 路由插件
                    if(routJson==null ||  routJson.isEmpty()){
                        Iterator keys = pluginJson.keySet().iterator();
                        Map<String, Object> kongPluginsInfo = new HashMap<>();

                        Map<String, Object> kongPluginsInfo2 = new HashMap<>();
                        kongPluginsInfo2.putAll(pluginJson );

                        while (keys.hasNext()) {
                            String key = keys.next().toString();
                            Object value = pluginJson.getString(key);
                            kongPluginsInfo.put(key, value);
                        }
                        if (kongPluginsInfo.get("name").equals(pluginName)) {
                            list.add(kongPluginsInfo);
                        }
                    }
                }
                if( list.size()>0 ){
                    return list.get(0);
                }
            }
        }
        return null;
    }

    public static JSONObject getPluginListByRouteId(String kongAdminUrl,String routeId) throws Exception {
        String url = kongAdminUrl + ConstUtil.ROUTES+"/"+routeId+ ConstUtil.PLUGINS;;
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info( "getPluginListByRouteId返回结果:{}",jsonObject );
        return jsonObject;
    }

    /**
     * 查询所有消费者的插件
     * @param kongAdminUrl
     * @param consumerId
     * @return
     * @throws Exception
     */
    public static JSONObject getPluginListByConsumersId(String kongAdminUrl,String consumerId) throws Exception {
        String url = kongAdminUrl + ConstUtil.CONSUMERS+"/"+consumerId+ ConstUtil.PLUGINS;;
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info( "getPluginListByConsumersId返回结果:{}",jsonObject );
        return jsonObject;
    }


    /**
     * 删除插件
     */
    public static Boolean delPlugin( String adminUrl ,String pluginsId ) throws Exception {
        logger.info( "删除数据参数：{}={}",adminUrl,pluginsId );
        String url = adminUrl+ ConstUtil.PLUGINS+"/"+pluginsId;;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpKit.HttpDel httpDel = new HttpKit.HttpDel(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(httpDel);
        logger.info( "delPlugin返回结果:{}",response );
        if( response.getStatusLine().getStatusCode()== 204) {
            return true;
        }
        return false;
    }

    /***
     * 之家云专用 - 添加插件
     * @param kongAdminUrl
     * @param map
     * @return
     * @throws Exception
     */
    public static JSONObject addPlugin(String kongAdminUrl, Map map) throws Exception {
        logger.info( "向kong添加插件 参数:{} {}",kongAdminUrl,map);
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl+ConstUtil.PLUGINS;
        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            arg.put(entry.getKey(), entry.getValue());
        }
        StringEntity reqEntity = new StringEntity(arg.toString(),"UTF-8");
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        logger.info( "向kong添加插件 返回结果:{}",response );
        return jsonObject;
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        if(params==null){
            return pairs;
        }
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }

        return pairs;
    }

    public static JSONObject addPluginForm(String kongAdminUrl, Map map) throws Exception {
        logger.info("kongAdminUrl返回参数{}:", map );
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl+ConstUtil.PLUGINS;
        HttpPost post = new HttpPost(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(map);
        post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        logger.info("kongAdminUrl返回结果{}:", jsonObject );
        return jsonObject;
    }

    /***
     * 之家云专用 - 更新插件
     * @param kongAdminUrl
     * @param pluginsId
     * @param map
     * @return
     * @throws Exception
     */
    public static JSONObject updatePlugin(String kongAdminUrl,String pluginsId,Map map) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl+ ConstUtil.PLUGINS+"/"+pluginsId;;
        HttpKit.HttpPatch httpPatch =new HttpKit.HttpPatch(url);

        JSONObject arg = new JSONObject();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            arg.put(entry.getKey(), entry.getValue());
        }
        StringEntity reqEntity = new StringEntity(arg.toString(),"UTF-8");
        reqEntity.setContentType("application/json");
        httpPatch.setEntity(reqEntity);
        HttpResponse response = client.execute(httpPatch);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        logger.info("updatePlugin返回结果{}:", jsonObject );
        return jsonObject;
    }

    /***
     * 之家云专用 - 查询路由插件信息接口
     * @param kongAdminUrl
     * @param pluginName
     * @param consumerName
     * @param routeId
     * @return
     * @throws Exception
     */
    public static Map<String,Object> getPluginByRoutesId(String kongAdminUrl,String pluginName,String consumerName,String routeId) throws Exception{
        String url = kongAdminUrl+ConstUtil.ROUTES+"/"+routeId+ ConstUtil.PLUGINS +"?size=1000";
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info("getPluginByRoutesId返回结果{}:", jsonObject);
        return parsePluginsData(pluginName,consumerName,jsonObject);
    }


    /**
     * 获取路由下所有的插件信息
     * @param kongAdminUrl
     * @param routeId
     * @return
     * @throws Exception
     */
    public static JSONObject getAllPluginByRoutesId(String kongAdminUrl, String routeId) throws Exception{
        String url = kongAdminUrl+ConstUtil.ROUTES+"/"+routeId+ ConstUtil.PLUGINS +"?size=1000";
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info("getAllPluginByRoutesId返回结果{}:", jsonObject );
        return jsonObject ;
    }

    /***
     * 之家云专用 - 查询服务插件信息接口
     * @param kongAdminUrl
     * @param pluginName
     * @param consumerName
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static Map<String,Object> getPluginByServiceId(String kongAdminUrl,String pluginName,String consumerName,String serviceId) throws Exception{

        String url = kongAdminUrl+ConstUtil.SERVICES+"/"+serviceId+ ConstUtil.PLUGINS +"?size=1000";
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        return parsePluginsData(pluginName,consumerName,jsonObject);
    }

    /**
     * 查询获得所有的插件 根据 服务Id
     * @param kongAdminUrl
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static JSONObject getAllPluginByServiceId(String kongAdminUrl ,String serviceId) throws Exception{
        String url = kongAdminUrl+ConstUtil.SERVICES+"/"+serviceId+ ConstUtil.PLUGINS +"?size=1000";
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info("getPluginByServiceId返回结果{}:", jsonObject );
        return jsonObject;
    }

    /**
     * 根据服务id查询服务信息
     * @param kongAdminUrl
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static JSONObject getPluginByServiceId(String kongAdminUrl, String serviceId) throws Exception{
        String url = kongAdminUrl+ ConstUtil.SERVICES+"/"+serviceId+ ConstUtil.PLUGINS +"?size=1000";
        JSONObject arg = new JSONObject();
        JSONObject jsonObject = HttpKit.DoHttpGet(url,arg);
        logger.info("getPluginByServiceId2返回结果{}:", jsonObject );
        return jsonObject;
    }

}
