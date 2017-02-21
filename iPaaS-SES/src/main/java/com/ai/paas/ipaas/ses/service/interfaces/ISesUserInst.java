package com.ai.paas.ipaas.ses.service.interfaces;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesUserInstance;

public interface ISesUserInst {
	/**
	 * 根据userid srvId查询主机实例
	 * 
	 * @param userId
	 * @param srvId
	 * @return
	 * @throws PaasException
	 */
	public SesUserInstance queryInst(String userId, String srvId)
			throws PaasException;
	
	/**
	 * 获得内外网的对应地址
	 * 
	 * @param 
	 * @return hostIps
	 * @throws PaasException
	 */
	public String getHostIp() throws PaasException;

}
