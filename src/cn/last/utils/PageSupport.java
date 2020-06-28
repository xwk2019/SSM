package cn.last.utils;

import java.util.List;

/**
 * 分页工具类
 */
public class PageSupport<T> {

    private Integer pageIndex = 1;     //当前页码
    private Integer pageSize = 5;   //每一页的条数
    private Integer totalCount; //总记录数
    private Integer pageCount;  //总页数

    private List<T> list;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        if (pageIndex <= 0) {
            this.pageIndex = 1;
        } else if (pageIndex > pageCount) {
            this.pageIndex = pageCount;
        } else {
            this.pageIndex = pageIndex;
        }
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        setPageCount();
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount() {
        this.pageCount = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getStartRow() {
        System.out.println(pageIndex + "---------------" + (pageIndex - 1) * pageSize + "------------------------------------------");
        return (pageIndex - 1) * pageSize;
    }
}
