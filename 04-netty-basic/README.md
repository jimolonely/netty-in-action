# 请解释下面的TCP参数

```java
    public static final ChannelOption<Boolean> SO_BROADCAST = valueOf("SO_BROADCAST");
    public static final ChannelOption<Boolean> SO_KEEPALIVE = valueOf("SO_KEEPALIVE");
    public static final ChannelOption<Integer> SO_SNDBUF = valueOf("SO_SNDBUF");
    public static final ChannelOption<Integer> SO_RCVBUF = valueOf("SO_RCVBUF");
    public static final ChannelOption<Boolean> SO_REUSEADDR = valueOf("SO_REUSEADDR");
    public static final ChannelOption<Integer> SO_LINGER = valueOf("SO_LINGER");
    public static final ChannelOption<Integer> SO_BACKLOG = valueOf("SO_BACKLOG");
    public static final ChannelOption<Integer> SO_TIMEOUT = valueOf("SO_TIMEOUT");
```

# AbstractChannel的常用方法

* connect
* bind
* read
* write
* flush
* close

# EmbeddedChannel是干嘛的

模拟测试

他的主要方法是：

* writeInbound
* readInbound
* writeOutbound
* readOutbound
* close

# ChannelInboundHandler的主要方法

以及 `ChannelInboundHandlerAdapter`的默认实现

* channelRegistered
* channelActive
* channelRead
* channelReadComplete
* channelInactive
* exceptionCaught

# ChannelOutboundHandler的主要方法

以及 `ChannelOutboundHandlerAdapter`的默认实现

* bind: 服务端
* connect: 客户端
* write
* read
* flush
* disConnect: 客户端
* close

# Pipeline

Handler在流水线中的执行顺序

## ChannelHandlerContext的作用

获取组件实例，入站出站方法

* 截断流水线：仅限入站
* 手动触发下一站

## ChannelPipeline的方法

* 增加Handler
* 删除Handler

## ChannelInitializer的作用

 方便组装流水线

* 为什么要在执行ChannelInitializer之后就移除？--- 因为只执行一次


