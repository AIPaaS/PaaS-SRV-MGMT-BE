package com.ai.paas.ipaas.ses.service.constant;

import com.ai.paas.ipaas.PaaSConstant;

public class SesConstants extends PaaSConstant {

	public static final int SES_SERVICE_START = 1;
	public static final int SES_SERVICE_RECYCLE = 1;
	public static final String SPLITER_VERTICAL_LINE = "\\|";
	public static final String SPLITER_COLON = ":";
	public static final String SPLITER_COMMA = ",";
	public static final String SES_ZK_PATH = "/SES/";
	public static final String SES_MAPPING_ZK_PATH = "/SES/MAPPING/";
	public static final int SES_RESOURCE_AVIL = 1;

	public static final String MODE_SINGLE = "single";
	public static final String MODE_CLUSTER = "cluster";

	public static final String FILE_PATH = "/commonconfig/";

	public static final String CLUSTER_FILE_PATH = "/redis/cluster/";
	public static final String AGENT_URL_BASE = "http://";

	public static final String PWD_KEY = "BaryTukyTukyBary";

	public static final String USER_ID = "userId";
	public static final String SERVICE_ID = "serviceId";
	public static final String SERVICE_NAME = "serviceName";
	public static final String APPLY_TYPE = "applyType";
	public static final String CAPACITY = "capacity";
	public static final String HA_MODE = "haMode";

	public static final String SUCCESS_FLAG = "true";
	public static final String FAIL_FLAG = "false";

	public static final String RESULT_CODE = "resultCode";
	public static final String RESULT_MSG = "resultMsg";
	public static final String SUCCESS_CODE = "000000";
	public static final String FAIL_CODE = "999999";
	public static final String SUCCESS_INFO = "Apply mcs successfully created!";
	public static final String FAIL_INFO = "Failed to apply the cache";

	public static final String APPLY_TYPE_C = "create";
	public static final String APPLY_TYPE_M = "modify";
	public static final String APPLY_TYPE_R = "remove";
	public static final String APPLY_TYPE_START = "start";
	public static final String APPLY_TYPE_STOP = "stop";
	public static final String APPLY_TYPE_RESTART = "restart";
	public static final String FUNCTION_PRE = "/mcs/manage/";

	public static final String MCS_ZK_COMMON_PATH = "/MCS/COMMON";
	public static final String MCS_ZK_COMMON = "{'maxActive':'500','maxIdle':'100','maxWait':'1000',"
			+ "'testOnBorrow':'false','testOnReturn':'true'}";

	public static final int CACHE_NUM = 6;
	public static final int VALIDATE_STATUS = 1;
	public static final int INVALIDATE_STATUS = 2;

	public static final String DDL_KEY = "key";
	public static final String DDL_VALUE = "value";

	public static final String APPLY_TYPE_ERROR = "com.ai.paas.ipaas.mcs.apply_type_error";
	public static final String CREATE_FILE_ERROR = "com.ai.paas.ipaas.mcs.create_file_error";
	public static final String UPLOAD_FILE_ERROR = "com.ai.paas.ipaas.mcs.upload_file_error";
	public static final String START_ERROR = "com.ai.paas.ipaas.mcs.start_error";
	public static final String CLUSTER_START_ERROR = "com.ai.paas.ipaas.mcs.cluster_start_error";
	public static final String MODIFY_MCS_RESOURCE_ERROR = "com.ai.paas.ipaas.mcs.modify_mcs_resource_error";
	public static final String ADD_MCS_USER_INS_ERROR = "com.ai.paas.ipaas.mcs.add_mcs_user_ins_error";

	public static final String EXPECT_ONE_RECORD_FAIL = "Expect just one record but got nothing or too many results...";
	public static final String RECORD_EXISTS = "record already exists...";
	public static final String SAVE_TO_DB_ERROR = "save to db error...";
	/** 1 单库 2 多库、DBS */
	public static final int GROUP_ID_1 = 1;
	public static final int GROUP_ID_2 = 2;
	/** 普通数据库 */
	public static final int COMMON_DB_TYPE = 1;
	/** DBS数据库 */
	public static final int DBS_DB_TYPE = 2;
	/** mysql数据库 */
	public static final int MYSQL_DB = 1;
	/** oracle数据库 */
	public static final int ORACLE_DB = 2;

	/** 1主表 */
	public static final int PRIMARY = 1;
	/** 非主表 */
	public static final int IN_PRIMARY = 2;

	public static final String SERVICE_CODE = "ses";

	public static final String IMAGE_CODE = "ses";
	
	public static final String SSH_USER_CODE = "ses_ssh_user";
	
	public static final String SSH_USER_PWD_CODE = "ses_ssh_user_pwd";
	 
	public static final String HOST_CODE = "host";
	
	public static final String IP_CODE = "ip";
	

	/**
	 * 认证返回结果
	 */
	public static class ExecResult {
		public static final String SUCCESS = "000000"; // 成功
		public static final String FAIL = "999999"; // 失败

		private ExecResult() {

		}
	}
}
