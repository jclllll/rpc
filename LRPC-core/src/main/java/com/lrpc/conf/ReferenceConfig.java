package com.lrpc.conf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ReferenceConfig<T> {
	private Class<T> interfaceConsumer;

	public Class<T> getInterfaceConsumer() {
		return interfaceConsumer;
	}

	public void setInterfaceConsumer(Class<T> interfaceConsumer) {
		this.interfaceConsumer = interfaceConsumer;
	}

	/**
	 *
	 * @return 生成api接口的代理对象
	 */
	public T get() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class[] classes = new Class[]{interfaceConsumer};
		//动态代理生成代理对象

		Object o = Proxy.newProxyInstance(classLoader, classes, (proxy, method, args) -> {
			System.out.println("LRPC proxy");
			return null;
		});
		return (T) o;
	}

	public ReferenceConfig() {
	}
}
