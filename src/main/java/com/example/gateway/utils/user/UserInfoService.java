package com.example.gateway.utils.user;

import com.alibaba.fastjson.JSONObject;
import com.autohome.openapi.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("UserInfoService")
public class UserInfoService {
    @Value("${urls.user.login}")
    private String loginUrl;

    public JSONObject loginAuthentication(String tokenId) {
        //如果tokenId为空则拒绝访问
        if (tokenId == null || tokenId.equals("")) {
            return null;
        }
        return JSONObject.parseObject(HttpClientUtil.get(loginUrl + "?tokenId=" + tokenId + "&systemKey=system-fb78e621-55a8-4459-bfbf-be678d82fd35"));
    }
}
