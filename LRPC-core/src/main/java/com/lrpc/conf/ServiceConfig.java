package com.lrpc.conf;

public class ServiceConfig<T> {
  private Class<T> interfaceProvider;

  public Class<T> getInterfaceProvider() {
    return interfaceProvider;
  }

  public void setInterfaceProvider(Class<T> interfaceProvider) {
    this.interfaceProvider = interfaceProvider;
  }

  public Object getRes() {
    return res;
  }

  public void setRes(Object res) {
    this.res = res;
  }

  private Object res;
  public ServiceConfig(){}


}
