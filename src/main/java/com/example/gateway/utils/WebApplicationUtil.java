package com.example.gateway.utils;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class WebApplicationUtil {
    private static WebApplicationContext springContext;

    public static WebApplicationContext getApplicationContext(ServletContext sc) {
        if (springContext == null) {
            springContext = WebApplicationContextUtils.getWebApplicationContext(sc);
        }
        return springContext;
    }
}
