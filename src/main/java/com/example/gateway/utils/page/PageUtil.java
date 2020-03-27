package com.example.gateway.utils.page;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

public class PageUtil {
    private final static Logger logger = LoggerFactory.getLogger(PageUtil.class);

    public static String pottingQuery(String queryStr) {
        String hqlStr = "";
        if (!Strings.isNullOrEmpty(queryStr)) {
            JSONObject jsonObject = JSONObject.parseObject(queryStr.trim());

            String isCriteria = String.valueOf(jsonObject.get("iscriteria"));
            String keyword = String.valueOf(jsonObject.get("keyword"));
            JSONObject condition = jsonObject.getJSONObject("condition");

            for (Map.Entry<String, Object> entry : condition.entrySet()) {
                if ("0".equals(isCriteria)) {
                    //if ("1".equals(iscriteria)) {
                    if (!Strings.isNullOrEmpty(keyword)) {
                        hqlStr += " or tabs." + entry.getKey() + " like '%" + keyword.trim() + "%'";
                    }
                } else {
                    if (!Strings.isNullOrEmpty(String.valueOf(entry.getValue()))) {
                        if ("createtime".equals(entry.getKey())) {//时间段查询
                            if (dataCheck(String.valueOf(entry.getValue()))) {
                                String[] time = String.valueOf(entry.getValue()).replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                                hqlStr += " and tabs.createtime between '" + time[0] + "' and '" + time[1] + "'";
                            }
                        } else if ("update_date".equals(entry.getKey())) {
                            if (dataCheck(String.valueOf(entry.getValue()))) {
                                String[] time = String.valueOf(entry.getValue()).replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                                hqlStr += " and tabs.update_date between '" + time[0] + "' and '" + time[1] + "'";
                            }
                        } else if ("putTime".equals(entry.getKey())) {
                            if (dataCheck(String.valueOf(entry.getValue()))) {
                                String[] time = String.valueOf(entry.getValue()).replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                                hqlStr += " and tabs.putTime between '" + time[0] + "' and '" + time[1] + "'";
                            }
                        } else {
                            hqlStr += " and tabs." + entry.getKey() + " like '%" + entry.getValue().toString().trim() + "%'";
                        }

                    }
                }
            }

            if (!Strings.isNullOrEmpty(hqlStr)) {
                if ("0".equals(isCriteria)) {
                    //if ("1".equals(iscriteria)) {
                    hqlStr = hqlStr.replaceFirst("or", "and (") + ")";
                }
                hqlStr = " where 1=1 " + hqlStr;
            } else {
                hqlStr = " where 1=1 ";
            }

            //sql注入拦截 放在最后处理，保留 where1=1 不改变原来业务逻辑的处理
            if (!sql_inj(queryStr)) {
                hqlStr = " where 1=1 and 1=2";
            }


        }
        return hqlStr;
    }

    /**
     * @param @param  queryStr
     * @param @param  sqlCheck 多传一个参数不进行sql拦截操作
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: pottingQuery
     * @Description: 有些子查询不需要进行sql注入拦截
     */
    public static String pottingQuery(String queryStr, String sqlCheck) {
        String hqlStr = "";
        if (!Strings.isNullOrEmpty(queryStr)) {
            JSONObject jsonObject = JSONObject.parseObject(queryStr.trim());

            String iscriteria = String.valueOf(jsonObject.get("iscriteria"));
            String keyword = String.valueOf(jsonObject.get("keyword"));

            JSONObject condition = jsonObject.getJSONObject("condition");

            for (Map.Entry<String, Object> entry : condition.entrySet()) {
                if ("0".equals(iscriteria)) {
                    if (!Strings.isNullOrEmpty(keyword)) {
                        hqlStr += " or tabs." + entry.getKey() + " like '%" + keyword.trim() + "%'";
                    }
                } else {
                    if (!Strings.isNullOrEmpty(String.valueOf(entry.getValue()))) {
                        if ("createtime".equals(entry.getKey())) {//时间段查询
                            if (dataCheck(String.valueOf(entry.getValue()))) {
                                String[] time = String.valueOf(entry.getValue()).replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                                hqlStr += " and tabs.createtime between '" + time[0] + "' and '" + time[1] + "'";
                            }
                        } else if ("update_date".equals(entry.getKey())) {
                            if (dataCheck(String.valueOf(entry.getValue()))) {
                                String[] time = String.valueOf(entry.getValue()).replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                                hqlStr += " and tabs.update_date between '" + time[0] + "' and '" + time[1] + "'";
                            }
                        } else if ("putTime".equals(entry.getKey())) {
                            if (dataCheck(String.valueOf(entry.getValue()))) {
                                String[] time = String.valueOf(entry.getValue()).replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                                hqlStr += " and tabs.putTime between '" + time[0] + "' and '" + time[1] + "'";
                            }
                        } else if ("run_status".equals(entry.getKey())) {//处理运行状态为其它
                            if ("other".equals(entry.getValue().toString().trim())) {
                                hqlStr += " and trim(tabs.run_status)!='up' and trim(tabs.run_status)!='down' and trim(tabs.run_status)!='2' or tabs.run_status is null";
                            } else {
                                hqlStr += " and trim(tabs.run_status)='" + entry.getValue().toString().trim() + "'";
                            }
                        } else if ("publish_status".equals(entry.getKey())) {//处理发布状态
                            String publish_status = "";
                            if ("1".equals(entry.getValue().toString().trim())) {//待发布
                                publish_status = "('1')";
                            } else if ("2".equals(entry.getValue().toString().trim())) {//发布中
                                publish_status = "('2','4')";
                            } else if ("3".equals(entry.getValue().toString().trim())) {//发布失败
                                publish_status = "('3','5')";
                            } else if ("4".equals(entry.getValue().toString().trim())) {//发布成功
                                publish_status = "('6')";
                            }
                            hqlStr += " and tabs.publish_status in " + publish_status;
                        } else {
                            hqlStr += " and tabs." + entry.getKey() + " like '%" + entry.getValue().toString().trim() + "%'";
                        }

                    }
                }
            }

            if (!Strings.isNullOrEmpty(hqlStr)) {

                if ("0".equals(iscriteria)) {
                    hqlStr = hqlStr.replaceFirst("or", "and (") + ")";
                }

                hqlStr = " where 1=1 " + hqlStr;
            } else {
                hqlStr = " where 1=1 ";
            }
        }
        return hqlStr;
    }

    public static long dateFormat(String timeStr) {
        timeStr = timeStr.replace("Z", " UTC");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        try {
            Date date = formatter.parse(timeStr);
            return date.getTime();
        } catch (Exception e) {
            logger.error("格式日期", e);
        }
        return 0;
    }

    public static boolean dataCheck(String data) {
        if (Strings.isNullOrEmpty(data) || ",".equals(data) || "[null,null]".equals(data) || "[0,0]".equals(data) || "[\"\",\"\"]".equals(data) || "[null]".equals(data) || "0,0".equals(data)
                || "[\"\"]".equals(data)) {
            return false;
        }
        return true;
    }


    //匹配sql注入正则
    private final static String REG = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
            + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    private final static Pattern sqlPattern = Pattern.compile(REG, Pattern.CASE_INSENSITIVE);

    public static boolean sql_inj(String str) {
        if (sqlPattern.matcher(str).find()) {
            logger.error("未能通过过滤器：str=" + str);
            return false;
        }
        return true;
    }

//	public static void main(String[] args) {
//		String str = "{\"iscriteria\":\"0\",\"keyword\":\"\",\"condition\":{\"entity_name\":\"\",\"server_group_name\":\"\",\"version_name\":\"\",\"publish_explain\":\"\",\"update_name\":\"\",\"update_date\":[\"\"]} update1 aora OR}";
//		System.out.println(sql_inj(str));
//	}
}
