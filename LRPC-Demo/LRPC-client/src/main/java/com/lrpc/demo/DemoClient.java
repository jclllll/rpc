package com.lrpc.demo;

import com.lrpc.IdGenerator;
import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.loadbalance.RoundRobinLoadBalance;

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

    public static void main(String[] args) throws InterruptedException {
        ReferenceConfig<DemoApiHello> reference = new ReferenceConfig<>();
        reference.setInterfaceConsumer(DemoApiHello.class);
        Queue<String> queue=new LinkedBlockingQueue<>(1000);
        Map<String,Integer>map=new HashMap<>(1024);
        LRPCBootstrap.getInstance()
            .application("LRPC-consumer")
            .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
            .serialize("hessian")
            .compress("snappY")
            .reference(reference);
        CyclicBarrier barrier=new CyclicBarrier(10);
        DemoApiHello demoApiHello = reference.get();
        Thread [] ts=new Thread[10];
        for(int i=0;i<10;i++){
            ts[i]=new Thread(()->{
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                for(int j=0;j<100;j++) {
                    String port = demoApiHello.sayHello("asd");
                    queue.add(port);
                }
            });
            ts[i].start();
        }
        for(int i=0;i<ts.length;i++){
            ts[i].join();
        }
        while(!queue.isEmpty()){
            if(map.get(queue.peek())==null){
                map.put(queue.poll(),1);
            }else{
                map.put(queue.peek(),map.get(queue.poll())+1);
            }
        }

        System.out.println(map);
        System.out.println(RoundRobinLoadBalance.queue.size());
        while(!RoundRobinLoadBalance.queue.isEmpty()){
            System.out.println(RoundRobinLoadBalance.queue.poll());
            System.out.println(RoundRobinLoadBalance.queue.size());
        }

        System.out.println("CACHE_INTERFACE_LOADBALANCE size : "+LRPCBootstrap.getInstance().CACHE_INTERFACE_LOADBALANCE.size());
        System.out.println(map);
    }
}
