package com.tendy;

public abstract class Phone {

    private Integer pageSize = null;
    private Integer pageStart = null;
    private Integer pageEnd = null;
    private Integer successNum = null;
    private Integer updateNum = null;
    private Integer failNum = null;
    private Integer cityId = null;
    private Integer businessId = null;

    public Phone(Integer pageSize, Integer pageStart, Integer pageEnd, Integer cityId, Integer businessId) {
        this.pageSize = pageSize;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.cityId = cityId;
        this.businessId = businessId;
        this.successNum = 0;
        this.updateNum = 0;
        this.failNum = 0;
    }

    public abstract void execute(Integer pageStart) throws Exception;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public void setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
    }

    public Integer getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(Integer pageEnd) {
        this.pageEnd = pageEnd;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getUpdateNum() {
        return updateNum;
    }

    public void setUpdateNum(Integer updateNum) {
        this.updateNum = updateNum;
    }

    public Integer getFailNum() {
        return failNum;
    }

    public void setFailNum(Integer failNum) {
        this.failNum = failNum;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }
}