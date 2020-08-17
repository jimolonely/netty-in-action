
# netty的核心组件和设计

* channel --- Socket
* EventLoop --- 控制流、多线程处理、并发
* ChannelFuture --- 异步通知

## Channel

基于IO操作，对Socket的封装，简化了使用。

* EmbeddedChannel
* LocalServerChannel
* NioDatagramChannel
* NioSctpChannel
* NioSocketChannel

## EventLoop

* EventLoopGroup包含多个EventLoop
* 一个EventLoop在他生命周期内只和一个线程绑定
* 所有EventLoop处理的IO事件都在他的线程上被处理
* 一个Channel在他的生命周期只注册一个EventLoop
* 一个EventLoop可能会被分配给多个Channel

## ChannelFuture

addListener()

## ChannelHandler

处理业务逻辑的地方

例如：ChannelInboundHandler

## ChannelPipeline

将一串ChannelHandler串起来，组成链式反应

## 编码器和解码器

* 解码：入站时将字节转成另一种格式，比如java对象
* 编码：出战时转成字节

例如

* ByteToMessageDecoder
* MessageToByteEncoder
* ProtobufEncoder

## SimpleChannelInboundHandler

重要的 channelRead0()方法

## Bootstrap、BootstrapServer

* 对于客户端的引导：Bootstrap，连接到远程主机和端口
* 对于服务端的引导：BootstrapServer，绑定到一个本地端口

他们其实是不同的网络行为：是监听传入的连接还是建立一个或多个连接

BootstrapServer一般需要2个EventLoopGroup（也可以是一个实例）：

* 一组只包含一个ServerChannel，代表服务本身绑定在某个本地端口的套接字
* 一组包含所有处理客户端连接的Channel，每个已建立连接的客户端都有一个

