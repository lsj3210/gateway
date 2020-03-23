package com.example.gateway.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataBaseConfig implements EnvironmentAware {
    @Value("${spring.datasource.url}")
    private String confUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String confDriver;

    @Value("${spring.datasource.username}")
    private String confUser;

    @Value("${spring.datasource.password}")
    private String confPwd;

    @Value("${spring.datasource.initialSize}")
    private int confInitialSize;

    @Value("${spring.datasource.minIdle}")
    private int confMinIdle;

    @Value("${spring.datasource.maxActive}")
    private int confMaxActive;

    @Value("${spring.datasource.maxWait}")
    private int confMaxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int confTimeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int confMinEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}")
    private String confValidationQuery;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean confTestWhileIdle;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean confTestOnBorrow;

    @Value("${spring.datasource.testOnReturn}")
    private boolean confTestOnReturn;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean confPoolPreparedStatements;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private int confMaxPoolPreparedStatementPerConnectionSize;

    @Override
    public void setEnvironment(Environment env) {
        //this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean
    public DataSource writeDataSource() {

        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(confUrl);
        datasource.setDriverClassName(confDriver);
        datasource.setUsername(confUser);
        datasource.setPassword(confPwd);

        datasource.setInitialSize(confInitialSize);
        datasource.setMinIdle(confMinIdle);
        datasource.setMaxWait(Long.valueOf(confMaxWait));
        datasource.setMaxActive(confMaxActive);

        datasource.setTimeBetweenEvictionRunsMillis(confTimeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(Long.valueOf(confMinEvictableIdleTimeMillis));
        datasource.setValidationQuery(confValidationQuery);

        datasource.setTestWhileIdle(confTestWhileIdle);
        datasource.setTestOnBorrow(confTestOnBorrow);
        datasource.setTestOnReturn(confTestOnReturn);

        datasource.setPoolPreparedStatements(confPoolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(confMaxPoolPreparedStatementPerConnectionSize);

        return datasource;
    }
}
