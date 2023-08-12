package com.lrpc.transport.message.serialize.impl;

import com.lrpc.transport.message.serialize.Serialize;
import com.lrpc.transport.message.serialize.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
final public class SerializeFactory {
    final private static Map<Integer, Serialize> CACHE_SERIALIZE = new ConcurrentHashMap<>(8);
    final public static Map<String, Integer> CACHE_NUM = new HashMap<>(8);

    static {
        CACHE_NUM.put("jdk", 0);
        CACHE_NUM.put("json", 1);
        CACHE_NUM.put("hessian", 2);
    }

    public static Serialize getSerialize(Integer num) {
        if (num == null) {
            log.error("序列化方式不被支持");
            throw new SerializeException("序列化方式不支持");
        }
        if (CACHE_SERIALIZE.get(num) != null) {
            return CACHE_SERIALIZE.get(num);
        }
        if (num == 0) {
            CACHE_SERIALIZE.put(num, new JDKSerialize());
        } else if (num == 1) {
            CACHE_SERIALIZE.put(num, new JSONSerialize());
        } else if (num == 2) {
            CACHE_SERIALIZE.put(num, new HessianSerialize());
        }
        else{
            log.error("序列化方式不被支持");
            throw new SerializeException("序列化方式不支持");
        }
        return CACHE_SERIALIZE.get(num);
    }

    protected static class JDKSerialize implements Serialize {

        @Override
        public byte[] serialize(Object obj) {
            ObjectOutputStream oos = null;
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                return baos.toByteArray();
            } catch (IOException e) {
                log.error("序列化失败:{}",obj,e);
                throw new SerializeException(e);
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流失败:",e);
                    throw new RuntimeException();
                }
            }
        }

        @Override
        public<T> T deSerialize(byte[] bytes) {
            ObjectInputStream ois=null;
            ByteArrayInputStream bais=null;
            try{
                bais=new ByteArrayInputStream(bytes);
                ois=new ObjectInputStream(bais);
                return (T)ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                log.error("反序列化失败:{}",e.getMessage());
                throw new SerializeException(e);
            } finally {
                try {
                    if (bais != null){
                        bais.close();
                    }
                    if(ois!=null){
                        ois.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流失败",e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected static class JSONSerialize implements Serialize {

        @Override
        public byte[] serialize(Object obj) {
            return new byte[0];
        }

        @Override
        public Object deSerialize(byte[] bytes) {
            return null;
        }
    }

    protected static class HessianSerialize implements Serialize {

        @Override
        public byte[] serialize(Object obj) {
            return new byte[0];
        }

        @Override
        public Object deSerialize(byte[] bytes) {
            return null;
        }
    }
}
