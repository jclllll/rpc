package com.lrpc.transport.message.compress.impl;

import com.lrpc.transport.message.compress.Compress;
import com.lrpc.transport.message.serialize.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;

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
        CACHE_NUM.put("***", 1);
        CACHE_NUM.put("****", 2);
    }

    public static Compress getCompress(Integer num) {
        if (num == null) {
            log.error("压缩方式不被支持");
            throw new SerializeException("压缩方式不支持");
        }
        if (CACHE_COMPRESS.get(num) != null) {
            return CACHE_COMPRESS.get(num);
        }
        if (num == 0) {
            CACHE_COMPRESS.put(num, new CompressFactory.GzipCompress());
        } else if (num == 1) {
            CACHE_COMPRESS.put(num, new CompressFactory.aCompress());
        } else if (num == 2) {
            CACHE_COMPRESS.put(num, new CompressFactory.bCompress());
        } else {
            log.error("压缩方式不被支持");
            throw new SerializeException("压缩方式不支持");
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

    protected static class aCompress implements Compress {
        @Override
        public byte[] compress(byte[] arr) {
            return new byte[0];
        }

        @Override
        public byte[] deCompress(byte[] arr) {
            return new byte[0];
        }
    }

    protected static class bCompress implements Compress {
        @Override
        public byte[] compress(byte[] arr) {
            return new byte[0];
        }

        @Override
        public byte[] deCompress(byte[] arr) {
            return new byte[0];
        }
    }
}
