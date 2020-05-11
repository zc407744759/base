package com.wutos.base.common.util;

import java.util.Map;

/**
 * @author jiyang
 * @Title: RequestPage
 * @Description:   翻页参数对象，用于接收页面传输参数
 * @date 2019/4/29
 */
public class RequestPage {
	
	 /**
     * 页码，从1开始
     */
    private int pageNum = 1;
    /**
     * 页面大小
     */
    private int pageSize = 10;
    
    /**
     * 页面参数,key-value
     */
    private Map<String, Object> param;
    
    /**
     * 排序
     */
    private String sort = "createdOn desc";
    
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
}
