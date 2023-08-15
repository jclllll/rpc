package com.lrpc.transport.message.serialize.impl;

import com.caucho.hessian.io.*;
import com.lrpc.transport.message.serialize.Serialize;
import com.lrpc.transport.message.serialize.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
final public class SerializeFactory {
    final private static Map<Integer, Serialize> CACHE_SERIALIZE = new ConcurrentHashMap<>(8);
    final public static Map<String, Integer> CACHE_NUM = new HashMap<>(8);

    static {
        CACHE_NUM.put("jdk", 0);
        CACHE_NUM.put("hessian", 1);
        CACHE_SERIALIZE.put(0, new JDKSerialize());
        CACHE_SERIALIZE.put(1, new HessianSerialize());
    }

    public static Serialize getSerialize(Integer num) {
        if (num == null || !CACHE_SERIALIZE.containsKey(num)) {
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
                log.error("序列化失败:{}", obj, e);
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
                    log.error("关闭流失败:", e);
                    throw new RuntimeException();
                }
            }
        }

        @Override
        public <T> T deSerialize(byte[] bytes) {
            ObjectInputStream ois = null;
            ByteArrayInputStream bais = null;
            try {
                bais = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bais);
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                log.error("反序列化失败:{}", e.getMessage());
                throw new SerializeException(e);
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流失败", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected static class HessianSerialize implements Serialize {

        @Override
        public byte[] serialize(Object obj) {
            ByteArrayOutputStream baos = null;
            HessianOutput ho = null;
            try {
                baos = new ByteArrayOutputStream();
                ho = new HessianOutput(baos);
                ho.writeObject(obj);
                ho.flush();
                return baos.toByteArray();
            } catch (IOException e) {
                log.error("序列化失败:{}", e.getMessage());
                throw new SerializeException(e);
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }
                    if (ho != null) {
                        ho.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流失败:{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public <T> T deSerialize(byte[] bytes) {
            ByteArrayInputStream bais = null;
            HessianInput hi = null;
            try {
                bais = new ByteArrayInputStream(bytes);
                hi = new HessianInput(bais);
                return (T) hi.readObject();
            } catch (IOException e) {
                log.error("反序列化失败:{}", e.getMessage());
                throw new RuntimeException(e);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (hi != null) {
                        hi.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流失败:{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }


    public static void registerSerialize(String name, int num, Serialize serialize) {
        if (num > 0b111) {
            throw new SerializeException("序列化器编号最大不能超过" + 0b111);
        }
        if (num == 0 || num == 1) {
            throw new SerializeException("默认序列化器不可覆盖");
        }
        CACHE_NUM.put(name.toLowerCase(),num);
        CACHE_SERIALIZE.put(num,serialize);
    }
}
