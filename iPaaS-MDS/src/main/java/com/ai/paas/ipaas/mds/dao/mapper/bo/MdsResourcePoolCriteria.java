package com.ai.paas.ipaas.mds.dao.mapper.bo;

import java.util.ArrayList;
import java.util.List;

public class MdsResourcePoolCriteria {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    public MdsResourcePoolCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart=limitStart;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public void setLimitEnd(Integer limitEnd) {
        this.limitEnd=limitEnd;
    }

    public Integer getLimitEnd() {
        return limitEnd;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andClusterIdIsNull() {
            addCriterion("cluster_id is null");
            return (Criteria) this;
        }

        public Criteria andClusterIdIsNotNull() {
            addCriterion("cluster_id is not null");
            return (Criteria) this;
        }

        public Criteria andClusterIdEqualTo(Integer value) {
            addCriterion("cluster_id =", value, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdNotEqualTo(Integer value) {
            addCriterion("cluster_id <>", value, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdGreaterThan(Integer value) {
            addCriterion("cluster_id >", value, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("cluster_id >=", value, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdLessThan(Integer value) {
            addCriterion("cluster_id <", value, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdLessThanOrEqualTo(Integer value) {
            addCriterion("cluster_id <=", value, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdIn(List<Integer> values) {
            addCriterion("cluster_id in", values, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdNotIn(List<Integer> values) {
            addCriterion("cluster_id not in", values, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdBetween(Integer value1, Integer value2) {
            addCriterion("cluster_id between", value1, value2, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterIdNotBetween(Integer value1, Integer value2) {
            addCriterion("cluster_id not between", value1, value2, "clusterId");
            return (Criteria) this;
        }

        public Criteria andClusterNameIsNull() {
            addCriterion("cluster_name is null");
            return (Criteria) this;
        }

        public Criteria andClusterNameIsNotNull() {
            addCriterion("cluster_name is not null");
            return (Criteria) this;
        }

        public Criteria andClusterNameEqualTo(String value) {
            addCriterion("cluster_name =", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameNotEqualTo(String value) {
            addCriterion("cluster_name <>", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameGreaterThan(String value) {
            addCriterion("cluster_name >", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameGreaterThanOrEqualTo(String value) {
            addCriterion("cluster_name >=", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameLessThan(String value) {
            addCriterion("cluster_name <", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameLessThanOrEqualTo(String value) {
            addCriterion("cluster_name <=", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameLike(String value) {
            addCriterion("cluster_name like", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameNotLike(String value) {
            addCriterion("cluster_name not like", value, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameIn(List<String> values) {
            addCriterion("cluster_name in", values, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameNotIn(List<String> values) {
            addCriterion("cluster_name not in", values, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameBetween(String value1, String value2) {
            addCriterion("cluster_name between", value1, value2, "clusterName");
            return (Criteria) this;
        }

        public Criteria andClusterNameNotBetween(String value1, String value2) {
            addCriterion("cluster_name not between", value1, value2, "clusterName");
            return (Criteria) this;
        }

        public Criteria andZkAddressIsNull() {
            addCriterion("zk_address is null");
            return (Criteria) this;
        }

        public Criteria andZkAddressIsNotNull() {
            addCriterion("zk_address is not null");
            return (Criteria) this;
        }

        public Criteria andZkAddressEqualTo(String value) {
            addCriterion("zk_address =", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressNotEqualTo(String value) {
            addCriterion("zk_address <>", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressGreaterThan(String value) {
            addCriterion("zk_address >", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressGreaterThanOrEqualTo(String value) {
            addCriterion("zk_address >=", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressLessThan(String value) {
            addCriterion("zk_address <", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressLessThanOrEqualTo(String value) {
            addCriterion("zk_address <=", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressLike(String value) {
            addCriterion("zk_address like", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressNotLike(String value) {
            addCriterion("zk_address not like", value, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressIn(List<String> values) {
            addCriterion("zk_address in", values, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressNotIn(List<String> values) {
            addCriterion("zk_address not in", values, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressBetween(String value1, String value2) {
            addCriterion("zk_address between", value1, value2, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andZkAddressNotBetween(String value1, String value2) {
            addCriterion("zk_address not between", value1, value2, "zkAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressIsNull() {
            addCriterion("kafka_address is null");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressIsNotNull() {
            addCriterion("kafka_address is not null");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressEqualTo(String value) {
            addCriterion("kafka_address =", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressNotEqualTo(String value) {
            addCriterion("kafka_address <>", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressGreaterThan(String value) {
            addCriterion("kafka_address >", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressGreaterThanOrEqualTo(String value) {
            addCriterion("kafka_address >=", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressLessThan(String value) {
            addCriterion("kafka_address <", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressLessThanOrEqualTo(String value) {
            addCriterion("kafka_address <=", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressLike(String value) {
            addCriterion("kafka_address like", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressNotLike(String value) {
            addCriterion("kafka_address not like", value, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressIn(List<String> values) {
            addCriterion("kafka_address in", values, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressNotIn(List<String> values) {
            addCriterion("kafka_address not in", values, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressBetween(String value1, String value2) {
            addCriterion("kafka_address between", value1, value2, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andKafkaAddressNotBetween(String value1, String value2) {
            addCriterion("kafka_address not between", value1, value2, "kafkaAddress");
            return (Criteria) this;
        }

        public Criteria andClusterStateIsNull() {
            addCriterion("cluster_state is null");
            return (Criteria) this;
        }

        public Criteria andClusterStateIsNotNull() {
            addCriterion("cluster_state is not null");
            return (Criteria) this;
        }

        public Criteria andClusterStateEqualTo(Integer value) {
            addCriterion("cluster_state =", value, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateNotEqualTo(Integer value) {
            addCriterion("cluster_state <>", value, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateGreaterThan(Integer value) {
            addCriterion("cluster_state >", value, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateGreaterThanOrEqualTo(Integer value) {
            addCriterion("cluster_state >=", value, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateLessThan(Integer value) {
            addCriterion("cluster_state <", value, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateLessThanOrEqualTo(Integer value) {
            addCriterion("cluster_state <=", value, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateIn(List<Integer> values) {
            addCriterion("cluster_state in", values, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateNotIn(List<Integer> values) {
            addCriterion("cluster_state not in", values, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateBetween(Integer value1, Integer value2) {
            addCriterion("cluster_state between", value1, value2, "clusterState");
            return (Criteria) this;
        }

        public Criteria andClusterStateNotBetween(Integer value1, Integer value2) {
            addCriterion("cluster_state not between", value1, value2, "clusterState");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserIsNull() {
            addCriterion("zk_auth_user is null");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserIsNotNull() {
            addCriterion("zk_auth_user is not null");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserEqualTo(String value) {
            addCriterion("zk_auth_user =", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserNotEqualTo(String value) {
            addCriterion("zk_auth_user <>", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserGreaterThan(String value) {
            addCriterion("zk_auth_user >", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserGreaterThanOrEqualTo(String value) {
            addCriterion("zk_auth_user >=", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserLessThan(String value) {
            addCriterion("zk_auth_user <", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserLessThanOrEqualTo(String value) {
            addCriterion("zk_auth_user <=", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserLike(String value) {
            addCriterion("zk_auth_user like", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserNotLike(String value) {
            addCriterion("zk_auth_user not like", value, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserIn(List<String> values) {
            addCriterion("zk_auth_user in", values, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserNotIn(List<String> values) {
            addCriterion("zk_auth_user not in", values, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserBetween(String value1, String value2) {
            addCriterion("zk_auth_user between", value1, value2, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthUserNotBetween(String value1, String value2) {
            addCriterion("zk_auth_user not between", value1, value2, "zkAuthUser");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdIsNull() {
            addCriterion("zk_auth_passwd is null");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdIsNotNull() {
            addCriterion("zk_auth_passwd is not null");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdEqualTo(String value) {
            addCriterion("zk_auth_passwd =", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdNotEqualTo(String value) {
            addCriterion("zk_auth_passwd <>", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdGreaterThan(String value) {
            addCriterion("zk_auth_passwd >", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdGreaterThanOrEqualTo(String value) {
            addCriterion("zk_auth_passwd >=", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdLessThan(String value) {
            addCriterion("zk_auth_passwd <", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdLessThanOrEqualTo(String value) {
            addCriterion("zk_auth_passwd <=", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdLike(String value) {
            addCriterion("zk_auth_passwd like", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdNotLike(String value) {
            addCriterion("zk_auth_passwd not like", value, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdIn(List<String> values) {
            addCriterion("zk_auth_passwd in", values, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdNotIn(List<String> values) {
            addCriterion("zk_auth_passwd not in", values, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdBetween(String value1, String value2) {
            addCriterion("zk_auth_passwd between", value1, value2, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andZkAuthPasswdNotBetween(String value1, String value2) {
            addCriterion("zk_auth_passwd not between", value1, value2, "zkAuthPasswd");
            return (Criteria) this;
        }

        public Criteria andOrgCodeIsNull() {
            addCriterion("org_code is null");
            return (Criteria) this;
        }

        public Criteria andOrgCodeIsNotNull() {
            addCriterion("org_code is not null");
            return (Criteria) this;
        }

        public Criteria andOrgCodeEqualTo(String value) {
            addCriterion("org_code =", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeNotEqualTo(String value) {
            addCriterion("org_code <>", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeGreaterThan(String value) {
            addCriterion("org_code >", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeGreaterThanOrEqualTo(String value) {
            addCriterion("org_code >=", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeLessThan(String value) {
            addCriterion("org_code <", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeLessThanOrEqualTo(String value) {
            addCriterion("org_code <=", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeLike(String value) {
            addCriterion("org_code like", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeNotLike(String value) {
            addCriterion("org_code not like", value, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeIn(List<String> values) {
            addCriterion("org_code in", values, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeNotIn(List<String> values) {
            addCriterion("org_code not in", values, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeBetween(String value1, String value2) {
            addCriterion("org_code between", value1, value2, "orgCode");
            return (Criteria) this;
        }

        public Criteria andOrgCodeNotBetween(String value1, String value2) {
            addCriterion("org_code not between", value1, value2, "orgCode");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}