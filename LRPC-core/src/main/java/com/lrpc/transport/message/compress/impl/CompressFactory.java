package com.lrpc.transport.message.compress.impl;

import com.lrpc.transport.message.compress.Compress;
import com.lrpc.transport.message.compress.exception.CompressException;
import com.lrpc.transport.message.serialize.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.xerial.snappy.Snappy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
final public class CompressFactory {
    final private static Map<Integer, Compress> CACHE_COMPRESS = new ConcurrentHashMap<>(8);
    final public static Map<String, Integer> CACHE_NUM = new HashMap<>(8);

    static {
        CACHE_NUM.put("gzip", 0);
        CACHE_NUM.put("snappy", 1);
        CACHE_COMPRESS.put(0,new GzipCompress());
        CACHE_COMPRESS.put(1,new SnappyCompress());
    }

    public static Compress getCompress(Integer num) {
        if (num == null || !CACHE_COMPRESS.containsKey(num)) {
            log.error("压缩方式不被支持");
            throw new CompressException("压缩方式不支持");
        }
        return CACHE_COMPRESS.get(num);
    }

    protected static class GzipCompress implements Compress {
        @Override
        public byte[] compress(byte[] arr) {
            ByteArrayOutputStream baos = null;
            GZIPOutputStream gos = null;
            try {
                baos = new ByteArrayOutputStream();
                gos = new GZIPOutputStream(baos);
                gos.write(arr);
                gos.finish();
                byte[] buf = baos.toByteArray();
                log.info("压缩前:{},压缩后:{}", arr.length, buf.length);
                return buf;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }
                    if (gos != null) {
                        gos.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流失败：{}", e.getMessage());
                }
            }
        }

        @Override
        public byte[] deCompress(byte[] arr) {
            ByteArrayInputStream bais=null;
            GZIPInputStream gis=null;
            try{
                bais=new ByteArrayInputStream(arr);
                gis=new GZIPInputStream(bais);
                byte[]buf = gis.readAllBytes();
                log.info("解压前:{},解压后:{}",arr.length,buf.length);
                return buf;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try{
                    if(bais!=null){
                        bais.close();
                    }
                    if(gis!=null){
                        gis.close();
                    }
                }catch (IOException e){
                    log.error("关闭流失败:{}",e.getMessage());
                }
            }
        }
    }

    protected static class SnappyCompress implements Compress {
        @Override
        public byte[] compress(byte[] arr) {
            try {
                byte [] body = Snappy.compress(arr);
                log.info("压缩前:{},压缩后:{}",arr.length,body.length);
                return body;
            } catch (IOException e) {
                throw new CompressException(e);
            }
        }

        @Override
        public byte[] deCompress(byte[] arr) {
            try {
                byte [] body = Snappy.uncompress(arr);
                log.info("解压前:{},解压后:{}",arr.length,body.length);
                return body;
            } catch (IOException e) {
                throw new CompressException(e);
            }
        }
    }

    public static void registerCompress(String name,int num,Compress compress){
        if (num > 0b111) {
            throw new SerializeException("压缩器编号最大不能超过" + 0b111);
        }
        if (num == 0 || num == 1) {
            throw new SerializeException("默认压缩器不可覆盖");
        }
        CACHE_NUM.put(name.toLowerCase(),num);
        CACHE_COMPRESS.put(num,compress);
    }

}
