package com.ai.paas.ipaas.ses.manage.rest.interfaces;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.vo.ses.SesUserInstance;

@Path("/ses/userInst")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
public interface IRPCSesUserInst {
	/**
	 * 根据userid srvId查询主机实例
	 * 
	 * @param userId
	 * @param srvId
	 * @return
	 * @throws PaasException
	 */
	@Path("queryInst")
	@POST
	public SesUserInstance queryInst(@FormParam("userId") String userId, @FormParam("srvId") String srvId)
			throws PaasException;
	
	/**
	 * 获取内外网地址
	 * 
	 * @return hostIps
	 * @throws PaasException
	 */
	@Path("getHostIp")
	@POST
	public String getHostIp()throws PaasException;

}
