package com.lrpc.demo;

import com.lrpc.IdGenerator;
import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class DemoClient {
	static {
		IdGenerator.init(1, 1);
	}

	//	public static void main(String[] args) {
//		ReferenceConfig<DemoApiHello> reference = new ReferenceConfig<>();
//		reference.setInterfaceConsumer(DemoApiHello.class);
//		LRPCBootstrap.getInstance()
//			.application("LRPC-consumer")
//			.registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//			.serialize("hessian")
//			.compress("snappY")
//			.reference(reference);
//        val demoApiHello = reference.get();
//        System.out.println(demoApiHello.sayHello("hello"));
//		return ;
//    }
	public static void main(String[] args) throws InterruptedException {
		ReferenceConfig<DemoApiHello> reference = new ReferenceConfig<>();
		reference.setInterfaceConsumer(DemoApiHello.class);
		Queue<String> queue = new LinkedBlockingQueue<>();
		Map<String, Integer> map = new HashMap<>(1024);
		LRPCBootstrap.getInstance()
			.application("LRPC-consumer")
			.registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
			.serialize("hessian")
			.compress("snappY")
			.reference(reference);
		CyclicBarrier barrier = new CyclicBarrier(10);
		DemoApiHello demoApiHello = reference.get();
		Thread[] ts = new Thread[20];
		for (int i = 0; i < ts.length; i++) {
			ts[i] = new Thread(() -> {
				try {
					barrier.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} catch (BrokenBarrierException e) {
					throw new RuntimeException(e);
				}
				for (int j = 0; j < 1000; j++) {
					String port = demoApiHello.sayHello("asd");
					queue.add(port);
				}
			});
			ts[i].start();
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].join();
		}
		while (!queue.isEmpty()) {
			if (map.get(queue.peek()) == null) {
				map.put(queue.poll(), 1);
			} else {
				map.put(queue.peek(), map.get(queue.poll()) + 1);
			}
		}
		System.out.println(map);
		Integer[] num = new Integer[1];
		num[0]=0;
		Collection<Integer> values = map.values();
		values.forEach(it->num[0]+=it.intValue());
		System.out.println(num[0]);
	}
}
