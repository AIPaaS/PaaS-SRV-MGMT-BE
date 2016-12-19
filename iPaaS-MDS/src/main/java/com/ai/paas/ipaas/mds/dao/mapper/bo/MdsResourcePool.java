package com.ai.paas.ipaas.mds.dao.mapper.bo;

public class MdsResourcePool {
    private Integer clusterId;

    private String clusterName;

    private String zkAddress;

    private String kafkaAddress;

    private Integer clusterState;

    private String remark;

    private String zkAuthUser;

    private String zkAuthPasswd;

    private String orgCode;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName == null ? null : clusterName.trim();
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress == null ? null : zkAddress.trim();
    }

    public String getKafkaAddress() {
        return kafkaAddress;
    }

    public void setKafkaAddress(String kafkaAddress) {
        this.kafkaAddress = kafkaAddress == null ? null : kafkaAddress.trim();
    }

    public Integer getClusterState() {
        return clusterState;
    }

    public void setClusterState(Integer clusterState) {
        this.clusterState = clusterState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getZkAuthUser() {
        return zkAuthUser;
    }

    public void setZkAuthUser(String zkAuthUser) {
        this.zkAuthUser = zkAuthUser == null ? null : zkAuthUser.trim();
    }

    public String getZkAuthPasswd() {
        return zkAuthPasswd;
    }

    public void setZkAuthPasswd(String zkAuthPasswd) {
        this.zkAuthPasswd = zkAuthPasswd == null ? null : zkAuthPasswd.trim();
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }
}