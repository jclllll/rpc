package com.lrpc.manager;

import com.lrpc.common.utils.zookeeper.ZookeeperNode;
import com.lrpc.common.utils.zookeeper.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.List;

@Slf4j
public class Application {
	public static void main(String[] args) {
		//创建基础目录
		int timeout = 10000;
		try {
			//创建zookeeper实例，建立连接
			ZooKeeper zooKeeper = ZookeeperUtil.createZookeeper("127.0.0.1", 2181);
			//定义节点和数据
			ZookeeperNode baseNode = new ZookeeperNode("/lrpc-metadata", null);
			ZookeeperNode producerNode = new ZookeeperNode("/lrpc-metadata/producers", null);
			ZookeeperNode consumerNode = new ZookeeperNode("/lrpc-metadata/consumers", null);


			List.of(baseNode, producerNode, consumerNode).forEach(
				node -> {
					ZookeeperUtil.createZookeeperNode(zooKeeper,node,null,CreateMode.PERSISTENT);
					log.info("Node {} is create success",node.getNodePath());
				}
			);
		} catch (RuntimeException e) {
			log.error("create basic node fail,error message is {}", e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("All zookeeper node is created");
	}
}
