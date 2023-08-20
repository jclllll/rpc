package com.lrpc.loadbalance;

import com.lrpc.IdGenerator;
import com.lrpc.LRPCBootstrap;
import com.sun.source.tree.Tree;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ConsistentHashLoadBalance extends AbstractLoadBalancer {
	private int virtualNodes = 0;
	private final MessageDigest md5;

	{
		try {
			md5 = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private final SortedMap<Integer, InetSocketAddress> circle = new TreeMap<>();

	public ConsistentHashLoadBalance(int virtualNodes, List<InetSocketAddress> nodes) {
		this.virtualNodes = virtualNodes;
		loadBalance(nodes);
	}

	@Override
	public InetSocketAddress choiceNode() {
		String requestId = String.valueOf(LRPCBootstrap.getInstance().requestCache.get().getRequestId());
		int hash = hash(requestId);
		return getNext(hash);
	}

	private InetSocketAddress getNext(int hash) {
		if (circle.containsKey(hash)) {
			return circle.get(hash);
		}
		SortedMap<Integer, InetSocketAddress> tailMap = circle.tailMap(hash);
		return circle.get(tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey());
	}

	@Override
	protected void loadBalance(List<InetSocketAddress> addresses) {
		for (InetSocketAddress address : addresses) {
			putCircle(address);
		}
		if (circle.size() != virtualNodes * addresses.size()) {
			log.error("虚拟节点数量不正确:{},{}", circle.size(), virtualNodes * addresses.size());
		}
	}

	private void putCircle(InetSocketAddress address) {
		for (int i = 0; i < virtualNodes; i++) {
			int hash = hash(address.toString() + "_" + i);
			while (circle.containsKey(hash)) {
				hash = hash(address.toString() + "_" + System.nanoTime());
			}
			log.info("node哈希值为:{}", hash);
			circle.put(hash, address);
		}
	}

	private int hash(String str) {
		//16位byte
		byte[] row = md5(str);
		return new String(row).hashCode();
	}

	private synchronized byte[] md5(String str) {
		return md5.digest(str.getBytes(StandardCharsets.UTF_8));
	}

}