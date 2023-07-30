package com.lrpc;

import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class LRPCBootstrap {

	private LRPCBootstrap() {
	}

	private String applicationName = "defaule";

	private RegistryConfig registryConfig;

	private ProtocolConfig protocolConfig;

	/**
	 * 此处使用饿汉式单例
	 */
	private static final LRPCBootstrap instance = new LRPCBootstrap();

	/**
	 * 获取引导类对象
	 *
	 * @return
	 */
	public static LRPCBootstrap getInstance() {
		return instance;
	}

	/**
	 * 注册服务
	 *
	 * @param registry
	 * @return
	 */
	public LRPCBootstrap registry(RegistryConfig registry) {
		this.registryConfig = registry;
		return this;
	}

	/**
	 * 指定协议
	 *
	 * @param protocol
	 * @return
	 */
	public LRPCBootstrap protocol(ProtocolConfig protocol) {
		if (log.isDebugEnabled()) {
			log.debug("protocol config {} for serializable", protocol.toString());
		}
		this.protocolConfig = protocol;
		return this;
	}

	/**
	 * 封装需要发布的服务,将发布的服务接口注册到注册中心
	 *
	 * @param service
	 * @return
	 */
	public LRPCBootstrap publish(ServiceConfig service) {
		if (log.isDebugEnabled()) {
			log.debug("service {} is be register", service.getInterfaceProvider().getName());
		}

		return this;
	}

	/**
	 * 批量发布
	 *
	 * @param services
	 * @return
	 */
	public LRPCBootstrap publish(List<ServiceConfig> services) {
		return this;
	}

	/**
	 * 启动netty服务
	 */
	public void start() {

	}

	public LRPCBootstrap application(String name) {
		applicationName = name;
		return this;
	}

	public LRPCBootstrap reference(ReferenceConfig reference) {

		return this;
	}
}
