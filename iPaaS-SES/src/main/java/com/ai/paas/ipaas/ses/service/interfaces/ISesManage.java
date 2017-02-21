package com.ai.paas.ipaas.ses.service.interfaces;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.rpc.api.vo.ApplyInfo;
import com.ai.paas.ipaas.ses.service.vo.SesMappingApply;
import com.ai.paas.ipaas.ses.service.vo.SesSrvApply;

/**
 * 
 * ClassName: ISesSrv 
 * date: 2015年8月18日 下午3:23:09 
 *
 * @author jianhua.ma
 * @version 
 *
 */
public interface ISesManage {
	/**
	 * 搜索服务是否已经创建(相同用户每个serviceid不能重复创建搜索服务)
	 * 
	 * @param SesSrvApply
	 * @throws PaasException
	 */
	public boolean isCreated(SesSrvApply sesSrvApply) throws PaasException;

	/**
	 * 创建索引
	 * 
	 * @param mappingApply.indexname    userid  serviceid
	 * @throws PaasException
	 */
	public void createIndex(SesMappingApply mappingApply) throws PaasException;
	/**
	 * 开通搜索服务
	 * 
	 * @param msgSrvApply
	 * @throws PaasException
	 */
	public void createSesService(SesSrvApply sesSrvApply) throws PaasException;
	/**
	 * 获得开通搜索服务地址
	 * 
	 * @param msgSrvApply
	 * @throws PaasException
	 */
	public String getSesServiceAdress(SesSrvApply sesSrvApply) throws PaasException;
	
	/**
	 * 创建mapping(数据模型)
	 * 
	 * @param index 	索引名称
	 * @param type  	索引类型
	 * @param mapping   模型定义
	 * @throws PaasException
	 */
	public void putMapping(SesMappingApply mappingApply) throws PaasException;
	/**
	 * 启动服务
	 * @param 
	 * @throws PaasException
	 */
	public void start(ApplyInfo info) throws PaasException;
	/**
	 * 停止服务
	 * @param 
	 * @throws PaasException
	 */
	public void stop(ApplyInfo info) throws PaasException;
	/**
	 * 注销服务
	 * @param 
	 * @throws PaasException
	 */
	public void recycle(ApplyInfo info) throws PaasException;
	/**
	 * 
	 * qryAvlSesHosts:(根据用户输入的节点个数，去数据库查询可用的主机列表). 
	 *
	 * @author jianhua.ma
	 * @param nodeNum
	 * @return
	 */
//	public List<SesHostInfo> qryAvlSesHosts(Integer nodeNum, HashMap<String, String> leftSesHosts) throws PaasException;
}
