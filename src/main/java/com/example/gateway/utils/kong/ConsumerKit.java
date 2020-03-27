package com.example.gateway.utils.kong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

public class ConsumerKit {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerKit.class);

    public static JSONObject addConsumer(String kongAdminUrl, Map<String,String> map) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongAdminUrl + ConstUtil.CONSUMERS;
        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        if(map.get("username")!=null){
            arg.put("username", map.get("username"));
        }
        if(map.get("custom_id")!=null){
            arg.put("custom_id", map.get("custom_id"));
        }
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        return jsonObject;
    }

    /**
     * 获得所有的消费者
     * @param kongAdminUrl
     * @return
     * @throws Exception
     */
    public static JSONObject getAllConsumer(String kongAdminUrl ) throws Exception {

        String url = kongAdminUrl + ConstUtil.CONSUMERS;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        return jsonObject;
    }


    /**
     * 为消费者添加
     * @param kongUrl
     * @param consumer  Consumer实体的id或username属性
     * @param group 组
     * @return
     * @throws Exception
     */
    public static JSONObject addConsumerAcl(String kongUrl ,String consumer,String group ) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = kongUrl+ ConstUtil.CONSUMERS+"/"+consumer+ ConstUtil.ACLS;
        HttpPost post = new HttpPost(url);
        JSONObject arg = new JSONObject();
        arg.put("group", group );
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        boolean flag = false;
        logger.info( "addConsumerAcl返回值:{}",jsonObject );
        if (response.getStatusLine().getStatusCode() == 201) {
            flag = true;
        }
        jsonObject.put("result", flag);
        return jsonObject;
    }

    /**
     *  添加basic auth认证的用户名和密码
     * @param kongUrl
     * @param consumer
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static JSONObject addConsumerBasicAuth(String kongUrl ,String consumer ,String username,String password ) throws Exception {
        String url = kongUrl+ ConstUtil.CONSUMERS+"/"+consumer+ ConstUtil.BASIC_AUTH;
        JSONObject arg = new JSONObject();
        arg.put("username", username );
        arg.put("password", password );
        return addConsumerAuth(url , arg );
    }

    /**
     * 添加hmac 权限的 用户信息
     * @param kongUrl
     * @param consumer
     * @param username
     * @param secret
     * @return
     * @throws Exception
     */
    public static JSONObject addConsumerHmac(String kongUrl ,String consumer ,String username,String secret ) throws Exception {
        String url = kongUrl+ ConstUtil.CONSUMERS+"/"+consumer+ ConstUtil.HMAC_AUTH;
        JSONObject arg = new JSONObject();
        arg.put("username", username );
        arg.put("secret", secret );
        return addConsumerAuth(url , arg );
    }

    /**
     * 添加消费者的jwt认证账号
     * @param kongUrl
     * @param consumer
     * @param key
     * @param secret
     * @return
     * @throws Exception
     */
    public static JSONObject addConsumerJwt(String kongUrl ,String consumer ,String key,String secret ) throws Exception {
        String url = kongUrl+ ConstUtil.CONSUMERS+"/"+consumer+ ConstUtil.JWT;
        JSONObject arg = new JSONObject();
        arg.put("key", key );
        arg.put("secret", secret );
        return addConsumerAuth(url , arg );
    }

    /**
     * 添加消费者的oauth2认证账号
     * @param kongUrl
     * @param consumer
     * @param key
     * @param secret
     * @return
     * @throws Exception
     */
    public static JSONObject addConsumerOauth2(String kongUrl ,String consumer ,String key,String secret) throws Exception {
        String url = kongUrl+ ConstUtil.CONSUMERS+"/"+consumer+ ConstUtil.OAUTH2;
        JSONObject arg = new JSONObject();
        arg.put("name", key );
        arg.put("client_id", key );
        arg.put("client_secret", secret );
        String[] tmp = {"http://www.oauth2.com"};
        arg.put("redirect_uris",tmp);
        return addConsumerAuth(url , arg );
    }


    /**
     * 发送权限的请求
     * @param url
     * @param arg
     * @return
     * @throws Exception
     */
    public static JSONObject addConsumerAuth(String url ,JSONObject arg ) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        boolean flag = false;
        logger.info("addConsumerAuth返回:{}",jsonObject );
        if (response.getStatusLine().getStatusCode() == 201) {
            flag = true;
        }
        jsonObject.put("result", flag);
        return jsonObject;
    }

    /**
     * 根据消费者名称，查询消费者id
     * @param adminUrl
     * @param consumer
     * @return
     * @throws Exception
     */
    public static String getConsumerId(String adminUrl , String consumer) throws Exception {
        JSONObject jsonObject = ConsumerKit.getConsumer(adminUrl,consumer);
        return jsonObject.getString("id");
    }

    /**
     * 获取消费者
     *consumer  id或者名称
     */
    public static JSONObject getConsumer(String adminUrl , String consumer) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = adminUrl+ ConstUtil.CONSUMERS+"/"+consumer;
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        return jsonObject;
    }

    /**
     * 查询hmac的插件
     * @param adminUrl
     * @param consumer
     * @return
     * @throws Exception
     */
    public static JSONObject getConsumerHmac(String adminUrl , String consumer  ) throws Exception {
        return getConsumerPlugin(adminUrl,consumer,ConstUtil.HMAC_AUTH);
    }

    /**
     * 查询basic auth权限
     * @param adminUrl
     * @param consumer
     * @return
     * @throws Exception
     */
    public static JSONObject getConsumerBasicAuth(String adminUrl , String consumer  ) throws Exception {
        return getConsumerPlugin(adminUrl,consumer,ConstUtil.BASIC_AUTH);
    }

    /**
     * 查询jwt信息
     * @param adminUrl
     * @param consumer
     * @return
     * @throws Exception
     */
    public static JSONObject getConsumeJwt(String adminUrl, String consumer  ) throws Exception {
        return getConsumerPlugin(adminUrl,consumer,ConstUtil.JWT);
    }
    /**
     *
     * @param adminUrl
     * @param consumer  消费者id  或者 username
     * @return
     * @throws Exception
     */
    public static JSONObject getConsumerPlugin(String adminUrl , String consumer ,String pluginType) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = adminUrl+ ConstUtil.CONSUMERS+"/"+consumer + pluginType;
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(message);
        logger.info( "getConsumerPlugin返回值：{}",jsonObject );
        return jsonObject;
    }

    /**
     * 删除插件-删除消费者的对应组的 acl插件
     * @param adminUrl
     * @param consumerId 消费者 主键 id
     * @param consumerAclFlagId  消费者和acl关联关系的json中的id，可以根据消费者获取这个数据列表
     * @return
     * @throws Exception
     */
    public static Boolean delConsumerAcl(String adminUrl, String consumerId,String consumerAclFlagId) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url =  adminUrl+ ConstUtil.CONSUMERS +"/"+consumerId+ConstUtil.ACLS+"/"+ consumerAclFlagId ;
        HttpKit.HttpDel httpDel =new HttpKit.HttpDel(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(httpDel);
        logger.info("删除消费者返回值:{}",response );
        if( response.getStatusLine().getStatusCode()== 204) {
            return true;
        }
        return false;
    }

    /**
     * 删除消费者对应的basicauth权限用户名和密码
     * @param adminUrl
     * @param consumer
     * @return
     * @throws Exception
     */
    public static Boolean delConsumerBasicAuthByConsumer( String adminUrl, String consumer) throws Exception {
        JSONObject jsonObject = ConsumerKit.getConsumerBasicAuth( adminUrl ,consumer );
        if( jsonObject==null ){
            logger.error( " 删除消费者basicAuth没有相应的账号1{} {}", adminUrl , consumer);
            return true;
        }
        JSONArray dataJsonObject =  jsonObject.getJSONArray("data");
        if(dataJsonObject==null || dataJsonObject.isEmpty() ){
            logger.error( " 删除消费者basicAuth没有相应的账号2{} {}", adminUrl , consumer);
            return true;
        }
        for(int i = 0;i< dataJsonObject.size(); i++) {
            JSONObject  basicJson = dataJsonObject.getJSONObject(i);
            String consumerCredentialFlagId = basicJson.getString("id");
            delConsumerBasicAuth(adminUrl,consumer  ,consumerCredentialFlagId );
        }
        return true;
    }

    /**
     *
     * @param adminUrl
     * @param consumer  消费者username 或者 消费者id
     * @param consumerCredentialFlagId
     * @return
     * @throws Exception
     */
    private static Boolean delConsumerBasicAuth(String adminUrl , String consumer   ,String consumerCredentialFlagId) throws Exception {
        return delConsumerPlugin(adminUrl,consumer , ConstUtil.BASIC_AUTH,consumerCredentialFlagId );
    }


    /**
     * 删除消费者对应的basicauth权限用户名和密码
     * @param adminUrl
     * @param consumer  消费者username 或者 消费者id
     * @return
     * @throws Exception
     */
    public static Boolean delConsumerJwtByConsumer( String adminUrl , String consumer   ) throws Exception {
        JSONObject jsonObject = ConsumerKit.getConsumeJwt( adminUrl ,consumer );
        if( jsonObject==null ){
            logger.error( " 删除消费者delConsumerJwt没有相应的账号1{} {}", adminUrl , consumer);
            return true;
        }
        JSONArray dataJsonObject =  jsonObject.getJSONArray("data");
        if(dataJsonObject==null || dataJsonObject.isEmpty() ){
            logger.error( " 删除消费者delConsumerJwt没有相应的账号2{} {}", adminUrl , consumer);
            return true;
        }
        for(int i = 0;i< dataJsonObject.size(); i++) {
            JSONObject  basicJson = dataJsonObject.getJSONObject(i);
            String consumerCredentialFlagId = basicJson.getString("id");
            delConsumerJwt(adminUrl,consumer  ,consumerCredentialFlagId );
        }
        return true;
    }


    /**
     * 删除消费者jwt 的账号
     * @param adminUrl
     * @param consumer   消费者username 或者 消费者id
     * @param consumerCredentialFlagId  授权的中间表的id
     * @return
     * @throws Exception
     */
    private static Boolean delConsumerJwt(String adminUrl , String consumer   ,String consumerCredentialFlagId) throws Exception {
        return delConsumerPlugin(adminUrl,consumer , ConstUtil.JWT,consumerCredentialFlagId );
    }


    /**
     * 删除hmac 根据消费者账号
     * @param adminUrl
     * @param consumer
     * @return
     * @throws Exception
     */
    public static Boolean delConsumerHmacByConsumer( String adminUrl , String consumer   ) throws Exception {
        JSONObject jsonObject = ConsumerKit.getConsumerHmac( adminUrl ,consumer );
        if( jsonObject==null ){
            logger.error( " delConsumerHmacByConsumer没有相应的账号1{} {}", adminUrl , consumer);
            return true;
        }
        JSONArray dataJsonObject =  jsonObject.getJSONArray("data");
        if(dataJsonObject==null || dataJsonObject.isEmpty() ){
            logger.error( " delConsumerHmacByConsumer没有相应的账号2{} {}", adminUrl , consumer);
            return true;
        }
        for(int i = 0;i< dataJsonObject.size(); i++) {
            JSONObject  basicJson = dataJsonObject.getJSONObject(i);
            String consumerCredentialFlagId = basicJson.getString("id");
            delConsumerHmac(adminUrl,consumer  ,consumerCredentialFlagId );
        }
        return true;
    }

    /**
     * 删除消费者jwt 的账号
     * @param adminUrl
     * @param consumer   消费者username 或者 消费者id
     * @param consumerCredentialFlagId  授权的中间表的id
     * @return
     * @throws Exception
     */
    private static Boolean delConsumerHmac(String adminUrl , String consumer   ,String consumerCredentialFlagId) throws Exception {
        return delConsumerPlugin(adminUrl,consumer , ConstUtil.HMAC_AUTH,consumerCredentialFlagId );
    }

    /**
     * 如： /consumers/:consumers/basic-auth/:basicauth_credentials
     * @param adminUrl
     * @param consumer 消费者username 或者 消费者id
     * @param pluginType 插件类型
     * @return
     * @throws Exception
     */
    private static Boolean delConsumerPlugin(String adminUrl , String consumer ,String pluginType, String consumerCredentialFlagId) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url =  adminUrl+ ConstUtil.CONSUMERS +"/"+consumer+pluginType+"/"+ consumerCredentialFlagId ;
        HttpKit.HttpDel httpDel =new HttpKit.HttpDel(url);
        JSONObject arg = new JSONObject();
        StringEntity reqEntity = new StringEntity(arg.toString());
        reqEntity.setContentType("application/json");
        HttpResponse response = client.execute(httpDel);
        logger.info("delConsumerPlugin删除消费者插件返回值:{}",response );
        if( response.getStatusLine().getStatusCode()== 204)
        {
            return true;
        }
        return false;
    }

    /**
     * 删除  消费者 对应路由上的 acl 权限信息
     * @param kongAdmainAddr
     * @param consumerName
     * @param groupId
     * @return
     */
//    public static boolean delConsumerAclByConsumerAndGroupId(String kongAdmainAddr, String consumerName, String groupId) throws Exception {
//        String consumerId = getConsumerId( kongAdmainAddr ,consumerName );
//        String consumerRouterAclId = getConsumerRouterAclId(kongAdmainAddr, consumerId ,groupId );
//        return  delConsumerAcl(kongAdmainAddr ,consumerId,  consumerRouterAclId ) ;
//    }

    /**
     * 查询 消费者 对应路由的中间表的id
     * @param kongAdmainAddr
     * @param consumerId
     * @param groupId
     * @return
     */
//    private static String getConsumerRouterAclId(String kongAdmainAddr, String consumerId, String groupId) throws Exception {
//        JSONObject jsonObject =  PluginApiKit.listPluginAclForCustomer( kongAdmainAddr, consumerId );
//        JSONArray aclPluginList = jsonObject.getJSONArray("data");
//        if(aclPluginList==null || aclPluginList.isEmpty() ){
//            logger.info("查询消费者的插件列表为空:{}{}",kongAdmainAddr,consumerId );
//            return "";
//        }
//        for(int i=0;i< aclPluginList.size();i++ ){
//            JSONObject routerAclObject =  aclPluginList.getJSONObject( i);
//            String group = routerAclObject.getString("group");
//            if(group.equals( groupId ) ){
//                logger.info("返回消费者对应的插件为:{}",routerAclObject );
//                return routerAclObject.getString("id");
//            }
//        }
//        logger.info("没有查询到acl的插件信息:{}{}",kongAdmainAddr,consumerId );
//        return "";
//    }

}
