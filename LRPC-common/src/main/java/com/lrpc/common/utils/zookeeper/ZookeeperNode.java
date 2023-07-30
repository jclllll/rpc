package com.lrpc.common.utils.zookeeper;

public class ZookeeperNode {
	private String nodePath;

	private byte [] data;
	public String getNodePath() {
		return nodePath;
	}

	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}

	public ZookeeperNode(String nodePath, byte[] data) {
		this.nodePath = nodePath;
		this.data = data;
	}
	public ZookeeperNode(){

	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
