package com.lrpc.manager;

import com.lrpc.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class Application {
	public static void main(String[] args) {
		//创建基础目录
		ZooKeeper zooKeeper;
		CountDownLatch countDownLatch = new CountDownLatch(1);
		String connectString = Constant.DEFAULT_ZK_CONNECT;
		int timeout = 10000;
		try {
			//创建zookeeper实例，建立连接
			zooKeeper = new ZooKeeper(connectString, timeout, event -> {
				if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
					countDownLatch.countDown();
				}
			});
			countDownLatch.await();
			//定义节点和数据
			final String basePath = "/lrpc-metadata";

			final String producerPath = basePath + "/producers";
			final String consumerPath = basePath + "/consumers";
			if(zooKeeper.exists(basePath,null)==null) {
				String result = zooKeeper.create(basePath, null,
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				log.info("create [{}] success",result);
			}
			if(zooKeeper.exists(producerPath,null)==null) {
				String result = zooKeeper.create(producerPath, null,
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				log.info("create [{}] success",result);
			}
			if(zooKeeper.exists(consumerPath,null)==null) {
				String result = zooKeeper.create(consumerPath, null,
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				log.info("create [{}] success",result);
			}
		} catch (IOException | InterruptedException | KeeperException e) {
			log.error("create basic node fail,error message is {}",e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("zookeeper node is created");
	}
}
