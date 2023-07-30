package com.lrpc.discovery.impl;

import com.lrpc.common.Constant;
import com.lrpc.common.utils.net.NetUtils;
import com.lrpc.common.utils.zookeeper.ZookeeperNode;
import com.lrpc.common.utils.zookeeper.ZookeeperUtil;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.discovery.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {
    public ZookeeperRegistry(){zooKeeper = ZookeeperUtil.createZookeeper();}
    private ZooKeeper zooKeeper;

    public ZookeeperRegistry(String connectString,int timeout) {
        this.zooKeeper = ZookeeperUtil.createZookeeper(connectString,timeout);
    }

    @Override
    public void registry(ServiceConfig<?>service) {
        String parentPath = Constant.DEFAULT_PROVIDER_PATH + "/" + service.getInterfaceProvider().getName();
        //持久节点
        ZookeeperUtil.createZookeeperNode(
            zooKeeper,
            new ZookeeperNode(parentPath, null),
            null,
            CreateMode.PERSISTENT
        );
        //创建本机临时节点
        String childNode = parentPath + "/" + NetUtils.getLocalIp() + ":" + 8080;
        String childName = ZookeeperUtil.createZookeeperNode(
            zooKeeper,
            new ZookeeperNode(childNode,null),
            null,
            CreateMode.EPHEMERAL
        );
        log.info("child create {}",childName);
        if (log.isDebugEnabled()) {
            log.debug("service {} is be register", service.getInterfaceProvider().getName());
        }
    }
}
