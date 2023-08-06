# rpc

LRPCRequest
1、服务调用方
    1、请求id
    2、压缩类型
    3、序列化方式（1byte）
    4、消息类型（普通请求、心跳请求、自定义请求）(2bit)
    5、负载 payload（接口名、方法名、参数列表、返回值类型）

报文 
```java
    魔术值（8）
    版本version(1)
    头部长度head_length(2)
    总长度 full_length(4)
    压缩、序列化、消息类型（1）
    请求id（8）
    body(未知)
    
```