package com.lrpc.conf;

public class ReferenceConfig <T>{
  private Class<T> interfaceConsumer;

  public Class<T> getInterfaceConsumer() {
    return interfaceConsumer;
  }

  public void setInterfaceConsumer(Class<T> interfaceConsumer) {
    this.interfaceConsumer = interfaceConsumer;
  }

  public T get(){
    return null;
  }
  public ReferenceConfig(){}


}
