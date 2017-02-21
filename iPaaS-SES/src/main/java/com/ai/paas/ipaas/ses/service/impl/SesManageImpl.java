package com.ai.paas.ipaas.ses.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.PaasRuntimeException;
import com.ai.paas.ipaas.ServiceUtil;
import com.ai.paas.ipaas.agent.util.AgentUtil;
import com.ai.paas.ipaas.agent.util.AidUtil;
import com.ai.paas.ipaas.agent.util.ParamUtil;
import com.ai.paas.ipaas.base.dao.interfaces.IpaasImageResourceMapper;
import com.ai.paas.ipaas.base.dao.interfaces.IpaasSysConfigMapper;
import com.ai.paas.ipaas.base.dao.mapper.bo.IpaasImageResource;
import com.ai.paas.ipaas.base.dao.mapper.bo.IpaasImageResourceCriteria;
import com.ai.paas.ipaas.base.dao.mapper.bo.IpaasSysConfig;
import com.ai.paas.ipaas.base.dao.mapper.bo.IpaasSysConfigCriteria;
import com.ai.paas.ipaas.ccs.constants.ConfigCenterDubboConstants.PathType;
import com.ai.paas.ipaas.ccs.service.ICCSComponentManageSv;
import com.ai.paas.ipaas.ccs.service.dto.CCSComponentOperationParam;
import com.ai.paas.ipaas.rpc.api.vo.ApplyInfo;
import com.ai.paas.ipaas.ses.dao.interfaces.SesResourcePoolMapper;
import com.ai.paas.ipaas.ses.dao.interfaces.SesUserInstanceMapper;
import com.ai.paas.ipaas.ses.dao.interfaces.SesUserMappingMapper;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesResourcePool;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesResourcePoolCriteria;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesUserInstance;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesUserInstanceCriteria;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesUserMappingCriteria;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesUserWeb;
import com.ai.paas.ipaas.ses.dao.mapper.bo.SesWebPool;
import com.ai.paas.ipaas.ses.service.constant.SesConstants;
import com.ai.paas.ipaas.ses.service.interfaces.ISesManage;
import com.ai.paas.ipaas.ses.service.interfaces.ISesUserWeb;
import com.ai.paas.ipaas.ses.service.vo.SesHostInfo;
import com.ai.paas.ipaas.ses.service.vo.SesMappingApply;
import com.ai.paas.ipaas.ses.service.vo.SesSrvApply;
import com.ai.paas.ipaas.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class SesManageImpl implements ISesManage {
	private static transient final Logger LOGGER = Logger.getLogger(SesManageImpl.class);
	
	@Autowired
	private ICCSComponentManageSv iCCSComponentManageSv;
	
	@Autowired
	private ISesUserWeb userWebSV;

	@Override
	public void createSesService(SesSrvApply sesSrvApply) throws PaasException {	
		String orgCode = sesSrvApply.getOrgCode();
		// 1.准备数据，包括从资源里面获取ses集群,实现获取算法
		List<SesHostInfo> sesHosts = qryAvlSesHosts(
				orgCode, sesSrvApply.getClusterNum(), sesSrvApply.getSesMem());
		String userId = sesSrvApply.getUserId();
		String serviceId = sesSrvApply.getServiceId();
		String clusterAddr = getClusterAddress(sesHosts);
		
		// 获取可用的web端
		SesWebPool webPool = userWebSV.getAvlWeb(orgCode, userId, serviceId);
		processSESServers(userId, serviceId, sesHosts, clusterAddr, webPool);
		
		// 更新ses_user_instance
		StringBuilder clusterString = new StringBuilder();
		for (SesHostInfo sesHostInfo : sesHosts) {
			SesUserInstance sesUser = new SesUserInstance();
			sesUser.setHostIp(sesHostInfo.getIp());
			sesUser.setSshPort(Integer.valueOf(sesHostInfo.getPort()));
			sesUser.setServiceId(serviceId);
			sesUser.setServiceName(sesSrvApply.getServiceName());
			sesUser.setSesPort(Integer.parseInt(sesHostInfo.getHttpPort()));
			sesUser.setTcpPort(Integer.parseInt(sesHostInfo.getTcpPort()));
			sesUser.setStatus(SesConstants.SES_SERVICE_START);
			sesUser.setUserId(userId);
			sesUser.setBeginTime(new Timestamp(new Date().getTime()));
			sesUser.setMemUse(sesSrvApply.getSesMem());
			ServiceUtil.getMapper(SesUserInstanceMapper.class).insert(sesUser);
			if (clusterString.length() > 0) {
				clusterString.append(SesConstants.SPLITER_COMMA);
			}
			clusterString.append(sesUser.getHostIp()
					+ SesConstants.SPLITER_COLON + sesUser.getTcpPort());
		}
		
		// 写入用户的web端地址
		SesUserWeb userWeb = new SesUserWeb();
		userWeb.setWebId("" + webPool.getId());
		userWeb.setServiceId(serviceId);
		userWeb.setUserId(userId);
		userWeb.setStatus(SesConstants.VALIDATE_STATUS);
		userWebSV.saveUserWeb(userWeb);
		LOGGER.info("write to zk..........");
		
		// 写入zk
		addZk(userId, serviceId, clusterString.toString());
	}

	public String getSesServiceAdress(SesSrvApply sesSrvApply) throws PaasException{
		String userId = sesSrvApply.getUserId();
		String serviceId = sesSrvApply.getServiceId();
		/** added orgCode column in 2016-10 **/
		String orgCode = sesSrvApply.getOrgCode();
		
		// 获取可用的web端
		SesWebPool webPool = userWebSV.getAvlWeb(orgCode, userId, serviceId);
		String sesAdress = webPool.getWebUrl();
		return sesAdress;
	}
	
	private void processSESServers(String userId, String serviceId,
			List<SesHostInfo> sesHosts, String clusterAddr, SesWebPool webPool)
			throws PaasException {

		String basePath = AgentUtil.getAgentFilePath(AidUtil.getAid());
		
		// 1.先将需要执行镜像命令的机器配置文件上传上去。
		InputStream in = null;
		try {
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/init_ansible_ssh_hosts.sh");
			String[] cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/init_ansible_ssh_hosts.sh", cnt, AidUtil.getAid());
			
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ses_run.yml");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ses_run.yml", cnt, AidUtil.getAid());
			
			// 还得上传文件
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ansible_run_ses.sh");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ansible_run_ses.sh", cnt, AidUtil.getAid());
			
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ses_exist.yml");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ses_exist.yml", cnt, AidUtil.getAid());
			
			// 还得上传文件
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ansible_exist_ses.sh");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ansible_exist_ses.sh", cnt, AidUtil.getAid());
			
			AgentUtil.executeCommand("chmod +x " + basePath
					+ "ses/init_ansible_ssh_hosts.sh && chmod +x " + basePath
					+ "ses/ansible_run_ses.sh &&" + " chmod +x " + basePath
					+ "ses/ansible_exist_ses.sh", AidUtil.getAid());
		} catch (Exception ex) {
			// 发生错误，回滚
			throw new PaasRuntimeException("", ex);
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
		}
		
		// 2.启动ses集群
		createSESServers(userId, serviceId, sesHosts, clusterAddr, webPool);
	}

	private void createSESServers(String userId, String serviceId,
			List<SesHostInfo> sesHosts, String clusterAddr, SesWebPool webPool)
			throws PaasException {
		String basePath = AgentUtil.getAgentFilePath(AidUtil.getAid());
		String sshUser = getSesSSHUser();
		String sshUserPwd = getSesSSHUserPwd();
		IpaasImageResource sesImage = getSesImage();
		
		// 2.启动ses集群
		for (SesHostInfo sesHostInfo : sesHosts) {
			String ip = sesHostInfo.getIp();
			String sshPort = sesHostInfo.getPort();
			// 生成每个主机列表
			String mkSshHosts = ParamUtil.replace(
					"ses/init_ansible_ssh_hosts.sh {0} {1} {2}", new String[] {
							basePath + "ses", ip.replace(".", ""), ip+":"+sshPort });
			LOGGER.info("---------mkSshHosts {}----------" + mkSshHosts);
			try {
				// 生成主机文件
				AgentUtil.executeCommand(basePath + mkSshHosts, AidUtil.getAid());
				
				// 先确定是否已经存在，如果已经有了镜像在运行，就不启动了
				// 开始执行
				String existImage = ParamUtil.replace(
						"ses/ansible_exist_ses.sh {0} {1} {2} "
								+ "{3} {4} {5} {6} {7} {8}", new String[] {
								basePath + "ses", ip.replace(".", ""), sshUser,
								sshUserPwd, ip, sshUser, userId, serviceId,
								sesHostInfo.getHttpPort() });

				String result = AgentUtil.executeCommandWithReturn(basePath
						+ existImage, AidUtil.getAid());
				
				if (result.indexOf(userId + "-" + serviceId + "-"
						+ sesHostInfo.getHttpPort()) >= 0) {
					continue;
				}
				
				// 开始执行
				String runImage = ParamUtil
						.replace(
								"ses/ansible_run_ses.sh {0} {1} {2} "
										+ "{3} {4} {5} {6} {7} {8} {9} {10} {11} {12} {13} {14}",
								new String[] {
										basePath + "ses",
										ip.replace(".", ""),
										sshUser,
										sshUserPwd,
										ip,
										sshUser,
										sesImage.getImageRepository() + "/"
												+ sesImage.getImageName(),
										userId, serviceId,
										sesHostInfo.getTcpPort(),
										sesHostInfo.getHttpPort(), clusterAddr,
										ip, sesHostInfo.getDataPath(),
										// 需要传入用户可以使用的web地址，去选择
										webPool.getWebUrl() });

				LOGGER.info("---------runImage {}----------" + runImage);
				AgentUtil.executeCommand(basePath + runImage, AidUtil.getAid());
			} catch (Exception ex) {
				// 发生错误，回滚
				throw new PaasRuntimeException("", ex);
			}
		}
	}

	private void stopSESServers(String userId, String serviceId,
			List<SesHostInfo> sesHosts) throws PaasException {
		String basePath = AgentUtil.getAgentFilePath(AidUtil.getAid());
		String sshUser = getSesSSHUser();
		String sshUserPwd = getSesSSHUserPwd();
		
		// 上传文件
		// 1.先将需要执行镜像命令的机器配置文件上传上去。
		InputStream in = null;
		try {
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/init_ansible_ssh_hosts.sh");
			String[] cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/init_ansible_ssh_hosts.sh", cnt, AidUtil.getAid());
			
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ses_stop.yml");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ses_stop.yml", cnt, AidUtil.getAid());
			
			// 还得上传文件
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ansible_stop_ses.sh");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ansible_stop_ses.sh", cnt, AidUtil.getAid());

			AgentUtil.executeCommand("chmod +x " + basePath
					+ "ses/init_ansible_ssh_hosts.sh && chmod +x " + basePath
					+ "ses/ansible_stop_ses.sh ", AidUtil.getAid());
		} catch (Exception ex) {
			// 发生错误，回滚
			throw new PaasRuntimeException("", ex);
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
		}
		
		// 2.停止ses集群
		for (SesHostInfo sesHostInfo : sesHosts) {
			String ip = sesHostInfo.getIp();
			String sshPort = sesHostInfo.getPort();
			// 生成每个主机列表
			String mkSshHosts = ParamUtil.replace(
					"ses/init_ansible_ssh_hosts.sh {0} {1} {2}", new String[] {
							basePath + "ses", ip.replace(".", ""), ip+":"+sshPort });
			LOGGER.info("---------mkSshHosts {}----------" + mkSshHosts);
			
			try {
				// 生成主机文件
				AgentUtil.executeCommand(basePath + mkSshHosts, AidUtil.getAid());
				// 开始执行
				String stopSes = ParamUtil.replace(
						"ses/ansible_stop_ses.sh {0} {1} {2} "
								+ "{3} {4} {5} {6} {7} {8}", new String[] {
								basePath + "ses", ip.replace(".", ""), sshUser,
								sshUserPwd, ip, sshUser, userId, serviceId, 
								sesHostInfo.getHttpPort() });

				String result = AgentUtil.executeCommandWithReturn(basePath
						+ stopSes, AidUtil.getAid());
				processResult(result);
			} catch (Exception ex) {
				// 发生错误，回滚
				throw new PaasRuntimeException("", ex);
			}
		}
	}

	private void startSESServers(String userId, String serviceId,
			List<SesHostInfo> sesHosts) throws PaasException {
		String basePath = AgentUtil.getAgentFilePath(AidUtil.getAid());
		String sshUser = getSesSSHUser();
		String sshUserPwd = getSesSSHUserPwd();
		// 上传文件
		// 1.先将需要执行镜像命令的机器配置文件上传上去。
		InputStream in = null;
		try {
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/init_ansible_ssh_hosts.sh");
			String[] cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/init_ansible_ssh_hosts.sh", cnt, AidUtil.getAid());
			
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ses_start.yml");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ses_start.yml", cnt, AidUtil.getAid());
			
			// 还得上传文件
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ansible_start_ses.sh");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ansible_start_ses.sh", cnt, AidUtil.getAid());

			AgentUtil.executeCommand("chmod +x " + basePath
					+ "ses/init_ansible_ssh_hosts.sh && chmod +x " + basePath
					+ "ses/ansible_start_ses.sh ", AidUtil.getAid());
		} catch (Exception ex) {
			// 发生错误，回滚
			throw new PaasRuntimeException("", ex);
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
		}
		
		// 2.停止ses集群
		for (SesHostInfo sesHostInfo : sesHosts) {
			String ip = sesHostInfo.getIp();
			String sshPort = sesHostInfo.getPort();
			// 生成每个主机列表
			String mkSshHosts = ParamUtil.replace(
					"ses/init_ansible_ssh_hosts.sh {0} {1} {2}", new String[] {
							basePath + "ses", ip.replace(".", ""), ip+":"+sshPort });
			LOGGER.info("---------mkSshHosts {}----------" + mkSshHosts);
			try {
				// 生成主机文件
				AgentUtil.executeCommand(basePath + mkSshHosts,
						AidUtil.getAid());
				// 开始执行
				String stopSes = ParamUtil.replace(
						"ses/ansible_start_ses.sh {0} {1} {2} "
								+ "{3} {4} {5} {6} {7} {8}", new String[] {
								basePath + "ses", ip.replace(".", ""), sshUser,
								sshUserPwd, ip, sshUser, userId, serviceId, 
								sesHostInfo.getHttpPort() });

				String result = AgentUtil.executeCommandWithReturn(basePath + stopSes, AidUtil.getAid());
				processResult(result);
			} catch (Exception ex) {
				// 发生错误，回滚
				throw new PaasRuntimeException("", ex);
			}
		}
	}

	private void deleteSESServers(String userId, String serviceId,
			List<SesHostInfo> sesHosts) throws PaasException {
		String basePath = AgentUtil.getAgentFilePath(AidUtil.getAid());
		String sshUser = getSesSSHUser();
		String sshUserPwd = getSesSSHUserPwd();
		// 上传文件
		// 1.先将需要执行镜像命令的机器配置文件上传上去。
		InputStream in = null;
		try {
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/init_ansible_ssh_hosts.sh");
			String[] cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/init_ansible_ssh_hosts.sh", cnt, AidUtil.getAid());
			
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ses_delete.yml");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ses_delete.yml", cnt, AidUtil.getAid());
			
			in = SesManageImpl.class.getResourceAsStream("/playbook/ses/ansible_delete_ses.sh");
			cnt = AgentUtil.readFileLines(in);
			in.close();
			AgentUtil.uploadFile("ses/ansible_delete_ses.sh", cnt, AidUtil.getAid());

			AgentUtil.executeCommand("chmod +x " + basePath
					+ "ses/init_ansible_ssh_hosts.sh && chmod +x " + basePath
					+ "ses/ansible_delete_ses.sh ", AidUtil.getAid());
		} catch (Exception ex) {
			// 发生错误，回滚
			throw new PaasRuntimeException("", ex);
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
		}
		
		// 2.停止ses集群
		for (SesHostInfo sesHostInfo : sesHosts) {
			String ip = sesHostInfo.getIp();
			String sshPort = sesHostInfo.getPort();
			// 生成每个主机列表
			String mkSshHosts = ParamUtil.replace(
					"ses/init_ansible_ssh_hosts.sh {0} {1} {2}", new String[] {
							basePath + "ses", ip.replace(".", ""), ip+":"+sshPort });
			LOGGER.info("---------mkSshHosts {}----------" + mkSshHosts);
			
			try {
				// 生成主机文件
				AgentUtil.executeCommand(basePath + mkSshHosts, AidUtil.getAid());
				// 开始执行
				String stopSes = ParamUtil.replace(
						"ses/ansible_delete_ses.sh {0} {1} {2} "
								+ "{3} {4} {5} {6} {7} {8}", new String[] {
								basePath + "ses", ip.replace(".", ""), sshUser,
								sshUserPwd, ip, sshUser, userId, serviceId, 
								sesHostInfo.getHttpPort() });

				String result = AgentUtil.executeCommandWithReturn(basePath + stopSes, AidUtil.getAid());
				processResult(result);
			} catch (Exception ex) {
				// 发生错误，回滚
				throw new PaasRuntimeException("", ex);
			}
		}
	}

	private void processResult(String result) {
		if (!StringUtil.isBlank(result)) {
			Gson gson = new Gson();
			JsonObject json = gson.fromJson(result, JsonObject.class);
			String code = json.get("code").getAsString();
			if (!"0".equals(code)) {
				throw new PaasRuntimeException(result);
			}
		}
	}

	private String getClusterAddress(List<SesHostInfo> sesHosts) {
		StringBuilder sb = new StringBuilder();
		for (SesHostInfo sesHostInfo : sesHosts) {
			sb.append("\\\\\\\"" + sesHostInfo.getIp() + ":"
					+ sesHostInfo.getTcpPort() + "\\\\\\\",");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	private void createIndex(TransportClient client,
			SesMappingApply mappingApply) {
		String userId = mappingApply.getUserId();
		String serviceId = mappingApply.getServiceId();
		String indexName = String.valueOf(Math.abs((userId + serviceId)
				.hashCode()));
		LOGGER.info("createIndex ..........userid:" + userId
				+ ".....serviceId:" + serviceId + ".....indexName:" + indexName);
		try {
			if (!doesIndexExist(indexName, client)) {
				String settings = " {\"client.transport.ping_timeout\":\"120s\","
						+ " \"analysis\": {"
						+ "         \"filter\": {"
						+ "            \"nGram_filter\": {"
						+ "               \"type\": \"nGram\","
						+ "               \"min_gram\": 1,"
						+ "               \"max_gram\": 10"
						+ "            }"
						+ "         },"
						+ "         \"analyzer\": {"
						+ "            \"nGram_analyzer\": {"
						+ "               \"type\": \"custom\","
						+ "               \"tokenizer\": \"ik_max_word\","
						+ "               \"filter\": ["
						+ "                  \"lowercase\","
						+ "                  \"nGram_filter\""
						+ "               ]"
						+ "            }"
						+ "         }"
						+ "      }" + "   " + "}";
				client.admin().indices().prepareCreate(indexName)
						.setSettings(settings).get();
			}
		} catch (Exception e) {
			LOGGER.error("createIndex error..........userid:" + userId
					+ ".....serviceId:" + serviceId + ".....indexName:"
					+ indexName, e);
		}
	}

	/**
	 * createIndex:(初始化集群后创建一个空索引).
	 * 
	 * @author jianhua.ma
	 * @param indexName
	 * @param ipAndPort
	 * @param client
	 */
	@Override
	public void createIndex(SesMappingApply mappingApply) {

		TransportClient client = null;
		String userId = mappingApply.getUserId();
		String serviceId = mappingApply.getServiceId();
		String ipAndPort = quryIpAndPort(userId, serviceId);
		String indexName = String.valueOf(Math.abs((userId + serviceId)
				.hashCode()));
		LOGGER.info("createIndex ..........userid:" + userId
				+ ".....serviceId:" + serviceId + ".....indexName:" + indexName);
		try {
			client = prepareSearchClient(ipAndPort);
			createIndex(client, mappingApply);
		} catch (Exception e) {
			LOGGER.error("createIndex error..........userid:" + userId
					+ ".....serviceId:" + serviceId + ".....indexName:"
					+ indexName, e);
		} finally {
			if (client != null) {
				client.close();
			}
		}

	}

	/*
	 * 创建搜索客户端 tcp连接搜索服务器 创建索引
	 */
	private TransportClient prepareSearchClient(String ipAndPort) {
		TransportClient searchClient = null;
		/* 如果10秒没有连接上搜索服务器，则超时 */
		Settings settings = Settings.settingsBuilder()
				.put("client.transport.ping_timeout", "100s")
				.put("client.transport.sniff", "true")
				.put("client.transport.ignore_cluster_name", "true").build();
		/* 创建搜索客户端 */
		searchClient = TransportClient.builder().settings(settings).build();
		List<String> clusterList = new ArrayList<String>();
		if (!StringUtil.isBlank(ipAndPort)) {
			clusterList = Arrays.asList(ipAndPort.split(","));
		}
		for (String item : clusterList) {
			if (!StringUtil.isBlank(item)) {
				String address = item.split(":")[0];
				int port = Integer.parseInt(item.split(":")[1]);
				/* 通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配 */
				searchClient
						.addTransportAddress(new InetSocketTransportAddress(
								new InetSocketAddress(address, port)));
			}
		}
		return searchClient;
	}

	/**
	 * 添加zk记录
	 * 
	 * @param userId
	 * @param serviceId
	 * @param mcsResourcePool
	 * @param requirepass
	 * @throws PaasException
	 */
	private void addZk(String userId, String serviceId, String hosts)
			throws PaasException {
		// 在zk中记录申请信息
		CCSComponentOperationParam op = new CCSComponentOperationParam();
		op.setUserId(userId);
		op.setPath(SesConstants.SES_ZK_PATH + serviceId);
		op.setPathType(PathType.READONLY);

		JsonObject dataJson = new JsonObject();

		dataJson.addProperty("hosts", hosts);
		iCCSComponentManageSv.add(op, dataJson.toString());
		LOGGER.info("write to zk su!");
	}

	/**
	 * 向zk添加mapping
	 */
	private void addMapping2Zk(String userId, String serviceId, String mapping)
			throws PaasException {
		// 在zk中记录申请信息
		CCSComponentOperationParam op = new CCSComponentOperationParam();
		op.setUserId(userId);
		op.setPath(SesConstants.SES_MAPPING_ZK_PATH + serviceId);
		op.setPathType(PathType.READONLY);

		JsonObject dataJson = new JsonObject();

		dataJson.addProperty("mapping", mapping);
		iCCSComponentManageSv.add(op, dataJson.toString());
		LOGGER.info("addMapping2Zk success!");
	}

	/**
	 * 获得最空闲的资源主机列表
	 * @param nodeNum
	 * @return
	 */
	private List<SesResourcePool> getBestHostsRes(String orgCode, int nodeNum) {
		SesResourcePoolCriteria sesRsrcPoolExample = new SesResourcePoolCriteria();
		sesRsrcPoolExample.setLimitStart(0);
		sesRsrcPoolExample.setLimitEnd(nodeNum);
		sesRsrcPoolExample.setOrderByClause("(ifnull(mem_total, 0) - ifnull(mem_used, 0)) desc");
		sesRsrcPoolExample.createCriteria().andStatusEqualTo(SesConstants.SES_RESOURCE_AVIL)
			.andOrgCodeEqualTo(orgCode);
		List<SesResourcePool> pool = ServiceUtil.getMapper(
				SesResourcePoolMapper.class).selectByExample(sesRsrcPoolExample);
		return pool;
	}

	/**
	 * 获得资源主机信息
	 * @return
	 */
	private SesResourcePool quryHostByIp(String ip) {
		SesResourcePoolCriteria sesRsrcPoolExample = new SesResourcePoolCriteria();
		sesRsrcPoolExample.createCriteria().andHostIpEqualTo(ip);
		List<SesResourcePool> pool = ServiceUtil.getMapper(
				SesResourcePoolMapper.class).selectByExample(sesRsrcPoolExample);
		return pool.get(0);
	}

	/**
	 * 根据集群节点数查询可用节点信息. 
	 * 返回结果为每一个端口号对应一个SesHostInfo
	 */
	private List<SesHostInfo> qryAvlSesHosts(String orgCode, Integer nodeNum, Integer esMem)
			throws PaasException {
		try {
			List<SesResourcePool> pool = getBestHostsRes(orgCode, nodeNum);
			List<SesHostInfo> result = new ArrayList<SesHostInfo>();

			int count = 0;
			int kill = 0;
			do {
				for (SesResourcePool res : pool) {
					if (count < nodeNum) {
						int mem_total = res.getMemTotal();
						int mem_used = res.getMemUsed();
						if (mem_total - mem_used < esMem) {
							pool.remove(res);
							continue;
						}
						String host_ip = res.getHostIp();
						String host_ssh_port = res.getSshPort()+"";
						int port_min = res.getPortMin();
						int port_max = res.getPortMax();
						if (port_min >= port_max) {
							throw new PaasException(
									"haven't got enough ports...");
						}
						SesHostInfo host = new SesHostInfo();
						host.setIp(host_ip);
						host.setPort(host_ssh_port);    /** set the value of ssh_port **/
						host.setMemUsed(host.getMemUsed() + esMem);
						host.setHttpPort(String.valueOf(port_min));
						host.setTcpPort(String.valueOf(port_min + 1));
						host.setDataPath(res.getDataPath());
						result.add(host);
						res.setPortMin(port_min + 2);
						res.setMemUsed(res.getMemUsed() + host.getMemUsed());
						count++;
					} else {
						break;
					}

				}
				kill++;
			} while (count < nodeNum && kill < (nodeNum + 1));
			if (kill > nodeNum) {
				throw new PaasException("ses resource not enough...");
			}
			for (SesResourcePool ses : pool) {
				ServiceUtil.getMapper(SesResourcePoolMapper.class)
						.updateByPrimaryKeySelective(ses);
			}
			return result;
		} catch (Exception e) {
			throw new PaasException("qryAvlSesHosts error...", e);
		}
	}

	@Override
	public boolean isCreated(SesSrvApply sesSrvApply) throws PaasException {
		return true;
	}

	/**
	 * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引
	 * ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	 * 
	 * @author jianhua.ma
	 * @param indices
	 *            索引名称；
	 * @param mappingType
	 *            索引类型
	 * @throws Exception
	 */
	public void createMapping(String indices, String mappingType,
			String mapping, String ipAndPort) throws Exception {
		TransportClient client = null;
		try {
			client = prepareSearchClient(ipAndPort);
			PutMappingResponse putMappingResponse = client.admin().indices()
					.preparePutMapping().setIndices(indices)
					.setType(mappingType).setSource(mapping).get();
			if (!putMappingResponse.isAcknowledged())
				throw new PaasRuntimeException(putMappingResponse.toString());
		} catch (Exception e) {
			LOGGER.error("createMapping error", e);
			throw new PaasException("createMapping error", e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	/**
	 * 根据userid serviceid查询集群任意一组ip＋port用于创建client
	 *
	 * @author jianhua.ma
	 * @param userId
	 * @param serviceId
	 * @return
	 */
	private String quryIpAndPort(String userId, String serviceId) {
		SesUserInstanceCriteria example = new SesUserInstanceCriteria();
		example.createCriteria().andUserIdEqualTo(userId)
				.andServiceIdEqualTo(serviceId).andStatusEqualTo(1);
		List<SesUserInstance> insts = ServiceUtil.getMapper(
				SesUserInstanceMapper.class).selectByExample(example);
		String ipAndPort = "";
		if (insts != null) {
			for (SesUserInstance inst : insts) {
				ipAndPort += inst.getHostIp() + SesConstants.SPLITER_COLON
						+ inst.getTcpPort() + ",";
			}
		}
		return ipAndPort;
	}

	/**
	 * 创建mapping
	 */
	@Override
	public void putMapping(SesMappingApply mappingApply) throws PaasException {
		TransportClient client = null;
		String userId = mappingApply.getUserId();
		String serviceId = mappingApply.getServiceId();
		String ipAndPort = quryIpAndPort(userId, serviceId);

		try {
			client = prepareSearchClient(ipAndPort);
			String indexName = mappingApply.getIndexName();
			String indexType = mappingApply.getIndexType();
			if (!doesIndexExist(indexName, client)) {
				createIndex(client, mappingApply);
			} else {
				// 先删除index，再创建，以保证删除mapping
				DeleteIndexResponse delete = client.admin().indices()
						.delete(new DeleteIndexRequest(indexName)).get();
				if (!delete.isAcknowledged()) {
					LOGGER.error("Index wasn't deleted" + indexName);
					throw new PaasRuntimeException("Index wasn't deleted"
							+ indexName);
				}
				createIndex(client, mappingApply);
			}
			String mapping = mappingApply.getMapping();
			LOGGER.info("putMapping begin ..........userid:" + userId
					+ ".....serviceId:" + serviceId + ".....indexName:"
					+ indexName + ".....mapping:" + mapping);
			client.admin().indices().preparePutMapping().setIndices(indexName)
					.setType(indexType).setSource(mapping).get();
			addMapping2Zk(userId, serviceId, mapping);
		} catch (Exception e) {
			LOGGER.error("createMapping error", e);
			throw new PaasException("createMapping error", e);
		} finally {
			if (client != null) {
				client.close();
			}
		}

	}

	/**
	 * 检察索引是否已经存在.
	 */
	private boolean doesIndexExist(String indexName, TransportClient client)
			throws PaasException {
		try {
			IndicesExistsRequest ier = new IndicesExistsRequest();
			ier.indices(new String[] { indexName.toLowerCase() });
			return client.admin().indices().exists(ier).get().isExists();
		} catch (Exception e) {
			LOGGER.error("doesIndexExist error", e);
			throw new PaasException("doesIndexExist error", e);
		}
	}

	@Override
	public void start(ApplyInfo info) throws PaasException {
		SesUserInstanceMapper mapper = ServiceUtil
				.getMapper(SesUserInstanceMapper.class);
		SesUserInstanceCriteria instanceCriteria = new SesUserInstanceCriteria();
		instanceCriteria.createCriteria().andUserIdEqualTo(info.getUserId())
				.andServiceIdEqualTo(info.getServiceId()).andStatusEqualTo(1);
		List<SesUserInstance> instances = mapper
				.selectByExample(instanceCriteria);
		// 应该先看看是否已经启动，没有就进行启动，有就不启动了
		List<SesHostInfo> sesHosts = new ArrayList<>();
		for (SesUserInstance instance : instances) {
			SesHostInfo sesHost = new SesHostInfo();
			sesHost.setIp(instance.getHostIp());
			sesHost.setPort(instance.getSshPort()+"");
			sesHost.setTcpPort("" + instance.getTcpPort());
			sesHost.setHttpPort("" + instance.getSesPort());
			sesHosts.add(sesHost);
		}
		startSESServers(info.getUserId(), info.getServiceId(), sesHosts);
	}

	@Override
	public void stop(ApplyInfo info) throws PaasException {
		SesUserInstanceMapper mapper = ServiceUtil
				.getMapper(SesUserInstanceMapper.class);
		SesUserInstanceCriteria instanceCriteria = new SesUserInstanceCriteria();
		instanceCriteria.createCriteria().andUserIdEqualTo(info.getUserId())
				.andServiceIdEqualTo(info.getServiceId()).andStatusEqualTo(1);
		List<SesUserInstance> instances = mapper
				.selectByExample(instanceCriteria);
		List<SesHostInfo> sesHosts = new ArrayList<>();
		for (SesUserInstance ins : instances) {
			SesHostInfo sesHost = new SesHostInfo();
			sesHost.setIp(ins.getHostIp());
			sesHost.setPort(ins.getSshPort()+"");
			sesHost.setTcpPort("" + ins.getTcpPort());
			sesHost.setHttpPort("" + ins.getSesPort());
			sesHosts.add(sesHost);
		}
		stopSESServers(info.getUserId(), info.getServiceId(), sesHosts);
	}

	@Override
	public void recycle(ApplyInfo info) throws PaasException {
		// 停服务
		stop(info);
		// 服务器回收资源
		SesUserInstanceMapper mapper = ServiceUtil
				.getMapper(SesUserInstanceMapper.class);
		SesUserInstanceCriteria instanceCriteria = new SesUserInstanceCriteria();
		instanceCriteria.createCriteria().andUserIdEqualTo(info.getUserId())
				.andServiceIdEqualTo(info.getServiceId()).andStatusEqualTo(1);
		List<SesUserInstance> instances = mapper
				.selectByExample(instanceCriteria);
		List<SesHostInfo> sesHosts = new ArrayList<>();

		for (SesUserInstance instance : instances) {
			String ip = instance.getHostIp();
			// 更新ses_resource_pool.mem_used
			SesResourcePool host = quryHostByIp(ip);
			host.setMemUsed(host.getMemUsed() - instance.getMemUse());
			ServiceUtil.getMapper(SesResourcePoolMapper.class)
				.updateByPrimaryKeySelective(host);
			SesHostInfo sesHost = new SesHostInfo();
			sesHost.setIp(instance.getHostIp());
			sesHost.setPort(instance.getSshPort()+"");
			sesHost.setTcpPort("" + instance.getTcpPort());
			sesHost.setHttpPort("" + instance.getSesPort());
			sesHosts.add(sesHost);
		}
		// 此处应停止并删除运行的容器
		deleteSESServers(info.getUserId(), info.getServiceId(), sesHosts);
		// ses_user_instance 删除记录
		instanceCriteria.createCriteria().andUserIdEqualTo(info.getUserId())
				.andServiceIdEqualTo(info.getServiceId());
		mapper.deleteByExample(instanceCriteria);
		// ses_user_mapping 删除记录
		SesUserMappingMapper mappingMapper = ServiceUtil
				.getMapper(SesUserMappingMapper.class);
		SesUserMappingCriteria sesUserMappingCriteria = new SesUserMappingCriteria();
		sesUserMappingCriteria.createCriteria()
				.andUserIdEqualTo(info.getUserId())
				.andServiceIdEqualTo(info.getServiceId());
		mappingMapper.deleteByExample(sesUserMappingCriteria);

	}

	private IpaasImageResource getSesImage() throws PaasException {
		IpaasImageResourceMapper rpm = ServiceUtil
				.getMapper(IpaasImageResourceMapper.class);
		IpaasImageResourceCriteria rpmc = new IpaasImageResourceCriteria();
		rpmc.createCriteria().andStatusEqualTo(SesConstants.VALIDATE_STATUS)
				.andServiceCodeEqualTo(SesConstants.SERVICE_CODE)
				.andImageCodeEqualTo(SesConstants.IMAGE_CODE);
		List<IpaasImageResource> res = rpm.selectByExample(rpmc);
		if (res == null || res.isEmpty())
			throw new PaasException("SES IMAGE not config.");
		return res.get(0);
	}

	private String getSesSSHUser() throws PaasException {
		IpaasSysConfigMapper sysconfigDao = ServiceUtil
				.getMapper(IpaasSysConfigMapper.class);
		IpaasSysConfigCriteria rpmc = new IpaasSysConfigCriteria();
		rpmc.createCriteria().andTableCodeEqualTo(SesConstants.SERVICE_CODE)
				.andFieldCodeEqualTo(SesConstants.SSH_USER_CODE);
		List<IpaasSysConfig> res = sysconfigDao.selectByExample(rpmc);
		if (res == null || res.isEmpty())
			throw new PaasException("SES ssh user not config.");
		return res.get(0).getFieldValue();
	}

	private String getSesSSHUserPwd() throws PaasException {
		IpaasSysConfigMapper sysconfigDao = ServiceUtil
				.getMapper(IpaasSysConfigMapper.class);
		IpaasSysConfigCriteria rpmc = new IpaasSysConfigCriteria();
		rpmc.createCriteria().andTableCodeEqualTo(SesConstants.SERVICE_CODE)
				.andFieldCodeEqualTo(SesConstants.SSH_USER_PWD_CODE);
		List<IpaasSysConfig> res = sysconfigDao.selectByExample(rpmc);
		if (res == null || res.isEmpty())
			throw new PaasException("SES ssh user not config.");
		return res.get(0).getFieldValue();
	}

	public static void main(String[] args) throws PaasException {
		String indexName = String.valueOf(Math
				.abs(("1321014990EB4DF79D893600A2F7CCA6SES003").hashCode()));
		System.out.println(indexName);
		StringBuilder sb = new StringBuilder();
		sb.append("1111,");
		sb.append("1111,");
		sb.append("1111,");
		sb.append("1111,");
		System.out.println(sb.deleteCharAt(sb.length() - 1).toString());
	}
}
