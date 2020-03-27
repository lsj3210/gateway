package com.example.gateway.utils;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static CloseableHttpClient httpClient;

    //private final static String basicAuthStr = DESEncryptHelper.encodeBase64("auto_cloud:qaz@#$<>?075");
    private final static String basicAuthStr = "12345";

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(20);
        cm.setDefaultMaxPerRoute(50);
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }


    /**
     * 非openapi认证的get请求
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, false, false, "");
    }

    /**
     * openapi认证的get请求
     *
     * @param url
     * @return
     */
    public static String getAuth(String url) {

        return get(url, true, false, "");
    }

    public static String getJwtAuth(String url, String jwtAuthStr) {

        return get(url, false, true, jwtAuthStr);
    }

    private static String get(String url, boolean isAuth, boolean isJwt, String jwtAuthStr) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpGet.setConfig(requestConfig);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("Accept", "application/json");
            if (isAuth) {
                httpGet.setHeader("Authorization", "Basic " + basicAuthStr);
            }
            if (isJwt) {
                httpGet.setHeader("Authorization", "Bearer " + jwtAuthStr);
            }
            response = httpClient.execute(httpGet);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * 非openapi认证的post请求
     *
     * @param url
     * @return
     */
    public static String post(String url, String jsonString) {
        return post(url, jsonString, false);
    }


    /**
     * openapi认证的post请求
     *
     * @param url
     * @return
     */
    public static String postAuth(String url, String jsonString) {

        return post(url, jsonString, true);
    }

    private static String post(String url, String jsonString, boolean isAuth) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            if (isAuth) {
                httpPost.addHeader("Authorization", "Basic " + basicAuthStr);
            }
            httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
        } catch (SocketTimeoutException e) {
            result = "{\"status\":false,\"message\":\"请求超时\"}";
            logger.error(e.getMessage());
        } catch (IOException e) {
            result = "{\"status\":false,\"message\":\"请求异常\"}";
            logger.error(e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }


    /**
     * 非openapi认证的post请求
     *
     * @param url
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        return post(url, params, false);
    }


    /**
     * openapi认证的post请求
     *
     * @param url
     * @return
     */
    public static String postAuth(String url, Map<String, String> params) {
        return post(url, params, true);
    }


    private static String post(String url, Map<String, String> params, boolean isAuth) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            List<NameValuePair> form = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                form.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Accept", "application/json");
            if (isAuth) {
                httpPost.addHeader("Authorization", "Basic " + basicAuthStr);
            }
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
        } catch (SocketTimeoutException e) {
            result = "{\"status\":false,\"message\":\"请求超时\"}";
            logger.error(e.getMessage());
        } catch (IOException e) {
            result = "{\"status\":false,\"message\":\"请求异常\"}";
            logger.error(e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * openapi认证的put请求
     *
     * @param url
     * @return
     */
    public static String putAuth(String url, String jsonString) {

        return put(url, jsonString, true);
    }

    private static String put(String url, String jsonString, boolean isAuth) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpPut httpPut = new HttpPut(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpPut.setConfig(requestConfig);
            httpPut.addHeader("Content-type", "application/json; charset=utf-8");
            httpPut.setHeader("Accept", "application/json");
            if (isAuth) {
                httpPut.addHeader("Authorization", "Basic " + basicAuthStr);
            }
            httpPut.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPut);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
        } catch (SocketTimeoutException e) {
            result = "{\"status\":false,\"message\":\"请求超时\"}";
            logger.error(e.getMessage());
        } catch (IOException e) {
            result = "{\"status\":false,\"message\":\"请求异常\"}";
            logger.error(e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

}
