package com.example.gateway.utils.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserInfo {
    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录用户工号
     */
    private String usercode;

    /**
     * 登录用户账户
     */
    private String account;

    /**
     * 登录用户tokenID
     */
    private String tokenid;

    /**
     * build
     */
    private String buid;

    /**
     * deptname
     */
    private String deptname;

    /**
     * alldeptname
     */
    private String allDeptname;

    /**
     * 已逗号分隔的业务线ID
     */
    private String productLineID = "";
    /**
     * 业务线ID列表
     */
    private List<Integer> productLineIDList;


    public UserInfo() {
    }

    public UserInfo(JSONObject userLoginInfo) {
        if (userLoginInfo != null) {
            this.tokenid = userLoginInfo.getString("tokenid");
            this.username = userLoginInfo.getString("username");
            this.account = userLoginInfo.getString("account");
            this.usercode = userLoginInfo.getString("employeeid");
            this.buid = userLoginInfo.getString("buid");
            this.deptname = userLoginInfo.getString("deptname");
            this.allDeptname = userLoginInfo.getString("allDeptname");

            JSONArray teams = userLoginInfo.getJSONArray("teams");
            Set<Integer> tmp = new HashSet<Integer>();
            for (int i = 0; i < teams.size(); i++) {
                JSONObject team = teams.getJSONObject(i);
                Integer productId = team.getInteger("businessId");
                tmp.add(productId);
            }

            List<Integer> idList = new ArrayList<Integer>(tmp);
            this.productLineIDList = idList;

            for (int j = 0; j < idList.size(); j++) {
                if (j == idList.size() - 1) {
                    this.productLineID += idList.get(j).toString();
                } else {
                    this.productLineID += idList.get(j).toString() + ",";
                }
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getBuid() {
        return buid;
    }

    public void setBuid(String buid) {
        this.buid = buid;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getAllDeptname() {
        return allDeptname;
    }

    public void setAllDeptname(String allDeptname) {
        this.allDeptname = allDeptname;
    }

    public List<Integer> getProductLineIDList() {
        return productLineIDList;
    }

    public void setProductLineIDList(List<Integer> productLineIDList) {
        this.productLineIDList = productLineIDList;
    }
}
