package com.example.gateway.utils.user;

import com.alibaba.fastjson.JSONObject;
import com.autohome.openapi.utils.WebApplicationUtil;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class UserInfoFilter extends AdviceFilter {
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        String tokenId = request.getParameter("tokenId");
        //通过request获得上下文根
        WebApplicationContext wac = WebApplicationUtil.getApplicationContext(request.getServletContext());
        UserInfoService userInfoService = (UserInfoService) wac.getBean("UserInfoService");
        //通过集中权限服务验证tokenId是否有效
        JSONObject userLoginInfo = userInfoService.loginAuthentication(tokenId);
        //如果登录信息不存在则拒绝访问
        if (userLoginInfo == null || !userLoginInfo.getBooleanValue("status")) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.getOutputStream().write("AccessDenied".getBytes());
            //http正常的响应码
            httpResponse.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
            return false;
        }
        UserInfo user = new UserInfo(userLoginInfo);
        user.setTokenid(tokenId);
        ParamsContext.setUserInfo(user);

        UserInfo userInfo = new UserInfo(userLoginInfo);
        userInfo.setTokenid(tokenId);
        ParamsContext.setUserInfo(userInfo);

        return true;

    }

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        //System.out.println("====后处理/后置返回处理");
    }

    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
        ParamsContext.remove();
        //System.out.println("====完成处理/后置最终处理");
    }
}
