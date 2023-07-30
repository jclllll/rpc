package com.lrpc.common.utils.zookeeper;

import com.lrpc.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZookeeperUtil {
	public static ZooKeeper createZookeeper(String connectString, int timeout) {
		if (StringUtils.isEmpty(connectString) || timeout < 0) {
			throw new RuntimeException("param is can not empty or timeout can not be -");
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		try {
			//创建zookeeper实例，建立连接
			final ZooKeeper zooKeeper = new ZooKeeper(connectString, timeout, event -> {
				if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
					countDownLatch.countDown();
				}
			});
			countDownLatch.await();
			return zooKeeper;
		} catch (InterruptedException | IOException e) {
			log.error("create zookeeper is fail,error mssage is {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static ZooKeeper createZookeeper() {
		return createZookeeper(Constant.DEFAULT_ZK_CONNECT, Constant.TIME_OUT);
	}

	public static String createZookeeperNode(ZooKeeper zooKeeper, ZookeeperNode node, Watcher watcher, CreateMode createMode) {
		try {
			if (zooKeeper.exists(node.getNodePath(), watcher) == null) {
				return zooKeeper.create(node.getNodePath(), node.getData(),
					ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
			}
			return "";
		} catch (InterruptedException | KeeperException e) {
			log.error("{} node is create fail , error message is {}", node.getNodePath(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static void closeZookeeper(ZooKeeper zooKeeper){
		try {
			zooKeeper.close();
		} catch (InterruptedException e) {
			log.error("zookeeper : {} close fail , error mssage is {}",e.getMessage());
			throw new RuntimeException("zookeeper close fail");
		}
	}
}
