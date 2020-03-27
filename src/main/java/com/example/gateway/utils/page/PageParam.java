package com.example.gateway.utils.page;

import java.util.List;

public class PageParam<T> {

    private int page = 1; // 当前页

    private int pageSize = 10; // 每页的数量

    private int firstResult = 0; //起始条数

    private int total = 0;//总条数

    private List<T> results;// 结果集

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFirstResult() {
        return (this.getPage() - 1) * this.getPageSize();
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
