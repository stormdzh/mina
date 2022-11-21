# MINA

## 一、概述

​	Apache MINA是一个网络通信应用框架，可帮助用户轻松开发高性能和高可扩展性的网络应用程序。 它通过Java NIO在各种传输（如TCP / IP和UDP / IP）上提供抽象的，事件驱动的异步API。MINA 不仅处理TCP和UDP，还通过VmpPipe或APR 在串行通信（RSC232）之上提供一层，现有支持的协议MINA附带了各种实现的协议：HTTP，XML，TCP，LDAP，DHCP，NTP，DNS，XMPP，SSH，FTP ......在某些时候，MINA不仅可以看作是NIO框架，而且可以看作是网络层与一些协议实现。在不久的将来，MINA可能会提供更广泛的协议供您使用。

当前稳定版[2.0.19](http://mina.apache.org/mina-project/downloads.html)（Java 7+）

## 二、文档

### 2.1 MINA 基础

#### 2.1.1 NIO

​	NIO (New IO) 非阻塞IO流。MINA是在NIO 1之上编写的。在Java 7，NIO-2中设计了一个新版本，我们还没有从这个版本所带来的附加功能中受益。同样重要的是要知道NIO中的N意味着New，但我们将在许多地方使用Non-Blocking术语。

​	**java.nio.*包中包含以下键结构：**

- Buffers  - 数据容器

- Chartsets  - 用于字节和Unicode的容器转换器 

- Channels - 表示与能够进行I / O操作的实体的连接 

- Selectors  - 提供可选择的多路复用非阻塞IO 

- Regexps  - 提供一些工具来操作正则表达式

  **NIO vs BIO :**	

  BIO或Blocking IO依赖于阻塞模式中使用的普通套接字：当您在套接字上读取，写入或执行任何操作时，被调用的操作将使调用者通知直到操作完成。

  BIO（阻塞IO）和NIO（非阻塞IO）之间的最大区别在于，在BIO中，您发送请求，并等到获得响应。在服务器端，这意味着一个线程将与任何传入连接相关联，因此您不必处理多路复用连接的复杂性。另一方面，在NIO中，您必须处理非阻塞系统的同步特性，这意味着在发生某些事件时将调用您的应用程序。在NIO中，您不会调用并等待结果，您发送命令并在结果准备好时通知您。

#### 2.1.2 MINA 架构

![img](http://mina.apache.org/staticresources/images/mina/mina_app_arch.png)

- MINA IoService ：

  它将处理与您的应用程序以及远程对等方的所有交互，发送和接收消息，管理会话，连接等。它是一个接口，实现为服务器端的IoAcceptor和客户端的IoConnector。

  会话管理：创建和删除会话，检测空闲。
  过滤链管理：处理过滤链，允许用户即时更改链
  handler invocation：在收到一些新消息时调用处理程序等
  统计管理：更新发送的消息数，发送的字节数以及许多其他消息
  听众管理：管理听众可以设置的听众
  通信管理：处理双方的数据传输

- Session:

  会话是MINA的核心：每次客户端连接到服务器时，都会在服务器上创建一个新会话，并将保留在内存中，直到客户端断开连接。 如果您在客户端使用MINA，则每次连接到服务器时，也会在客户端上创建会话。

  ![img](http://mina.apache.org/staticresources/images/mina/session-state.png)

- Filters:

  它过滤IoService和IoHandler之间的所有I / O事件和请求。如果您有Web应用程序编程经验，可以放心地认为它是Servlet过滤器的表兄弟。提供了许多开箱即用的过滤器，通过使用开箱即用的过滤器简化典型的横切关注点来加速网络应用程序开发速度。

  LoggingFilter记录所有事件和请求。
  ProtocolCodecFilter将传入的ByteBuffer转换为消息POJO，反之亦然。
  CompressionFilter压缩所有数据。

  | Filter                       | class                                                        | Description                                                  |
  | ---------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
  | Blacklist                    | [BlacklistFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/firewall/BlacklistFilter.html) | Blocks connections from blacklisted remote addresses         |
  | Buffered Write               | [BufferedWriteFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/buffer/BufferedWriteFilter.html) | Buffers outgoing requests like the BufferedOutputStream does |
  | Compression                  | [CompressionFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/compression/CompressionFilter.html) |                                                              |
  | ConnectionThrottle           | [ConnectionThrottleFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/firewall/ConnectionThrottleFilter.html) |                                                              |
  | ErrorGenerating              | [ErrorGeneratingFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/errorgenerating/ErrorGeneratingFilter.html) |                                                              |
  | Executor                     | [ExecutorFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/executor/ExecutorFilter.html) |                                                              |
  | FileRegionWrite              | [FileRegionWriteFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/stream/FileRegionWriteFilter.html) |                                                              |
  | KeepAlive                    | [KeepAliveFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/keepalive/KeepAliveFilter.html) |                                                              |
  | Logging                      | [LoggingFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/logging/LoggingFilter.html) | Logs event messages, like MessageReceived, MessageSent, SessionOpened, ... |
  | MDC Injection                | [MdcInjectionFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/logging/MdcInjectionFilter.html) | Inject key IoSession properties into the MDC                 |
  | Noop                         | [NoopFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/util/NoopFilter.html) | A filter that does nothing. Useful for tests.                |
  | Profiler                     | [ProfilerTimerFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/statistic/ProfilerTimerFilter.html) | Profile event messages, like MessageReceived, MessageSent, SessionOpened, ... |
  | ProtocolCodec                | [ProtocolCodecFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/codec/ProtocolCodecFilter.html) | A filter in charge of encoding and decoding messages         |
  | Proxy                        | [ProxyFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/proxy/filter/ProxyFilter.html) |                                                              |
  | Reference counting           | [ReferenceCountingFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/util/ReferenceCountingFilter.html) | Keeps track of the number of usages of this filter           |
  | RequestResponse              | [RequestResponseFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/reqres/RequestResponseFilter.html) |                                                              |
  | SessionAttributeInitializing | [SessionAttributeInitializingFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/util/SessionAttributeInitializingFilter.html) |                                                              |
  | StreamWrite                  | [StreamWriteFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/stream/StreamWriteFilter.html) |                                                              |
  | SslFilter                    | [SslFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/ssl/SslFilter.html) |                                                              |
  | WriteRequest                 | [WriteRequestFilter](http://mina.apache.org/mina-project/xref/org/apache/mina/filter/util/WriteRequestFilter.html) |                                                              |

- Handler：

  处理MINA发出的所有I / O事件。 界面是过滤器链末端完成的所有活动的中心。

  IoHandler具有以下功能

  sessionCreated
  sessionOpened
  sessionClosed
  sessionIdle
  exceptionCaught
  messageReceived
  messageSent

### 2.2 MINA 核心

#### 2.2.1 IoBuffer

这是ByteBuffer的替代品。 MINA不直接使用NIO ByteBuffer有两个原因：

- 它不提供有用的getter和putter，如fill，get / putString和get / putAsciiInt（）

- 由于其固定容量，很难编写可变长度数据

IoBuffer引入了autoExpand属性。 它会自动扩展其容量并限制价值。

#### 2.2.2 Codec Filter

没有ProtocolCodecFilter，一次调用IoSession.write（Object message）发送方可以在接收方上产生多个messageReceived（IoSession会话，对象消息）事件;多次调用IoSession.write（Object message）可能会导致单个messageReceived事件。当客户端和服务器在同一主机（或本地网络）上运行时，您可能不会遇到此行为

#### 2.2.3 Executor Filter

ExecutorFilter类
这个类正在实现IoFilter接口，基本上，它包含一个Executor，用于将传入的事件传播到线程池。 如果某些任务是CPU密集型的，这将允许应用程序更有效地使用处理器。

假设大多数处理将在您的应用程序中完成，或者在某些CPU密集型过滤器（例如，CodecFilter）之前的某个地方，可以在处理程序之前使用此过滤器。

####  2.2.4 SSL Filter

SslFilter是负责管理通过安全连接发送的数据的加密和解密的过滤器。 每当您需要建立安全连接或转换现有连接以使其安全时，您必须在过滤器链中添加SslFilter。

由于任何会话都可以随意修改它的消息过滤器链，因此它允许在打开的连接上使用startTLS等协议

#### 2.2.5 Logging Filter

为了指定触发日志记录的IoHandler事件以及执行日志记录的级别，LoggingFilter中有一个名为setLogLevel（IoEventType，LogLevel）的方法。 以下是此方法的选项：

| IoEventType      | Description                                      |
| ---------------- | ------------------------------------------------ |
| SESSION_CREATED  | Called when a new session has been created       |
| SESSION_OPENED   | Called when a new session has been opened        |
| SESSION_CLOSED   | Called when a session has been closed            |
| MESSAGE_RECEIVED | Called when data has been received               |
| MESSAGE_SENT     | Called when a message has been sent              |
| SESSION_IDLE     | Called when a session idle time has been reached |
| EXCEPTION_CAUGHT | Called when an exception has been thrown         |

### 2.3 MINA 高级

#### 2.3.1 Spring集成

略，见应用，直接启动单元测试