package com.lrpc.conf;

public class RegistryConfig {
	public String getUrl() {
		return url;
	}

	private final String url;

	public RegistryConfig(String connectUrl) {
		url = connectUrl;
	}
}
