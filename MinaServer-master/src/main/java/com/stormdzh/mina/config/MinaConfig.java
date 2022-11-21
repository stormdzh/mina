package com.stormdzh.mina.config;

import com.stormdzh.mina.handler.ServerHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

/**
 * 类描述：MINA配置相关信息
 * https://gitee.com/wanghaifenghome/mina/tree/master/src/main/java/com/wanghaifeng/mina
 */
@Slf4j
@Configuration
public class MinaConfig {

	@Value("${mina.port1}")
	private int port1;
	@Value("${mina.port2}")
	private int port2;
	@Value("${mina.host}")
	private String host;

	@Bean
	public LoggingFilter loggingFilter() {
		return new LoggingFilter();
	}

	@Bean
	public IoHandler ioHandler() {
		return new ServerHandler();
	}

	@Bean
	public IoAcceptor ioAcceptor() throws Exception {
		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("logger", loggingFilter());
		// 使用自定义编码解码工厂类
//		acceptor.getFilterChain().addLast("coderc", new ProtocolCodecFilter(new SocketFactory(Charset.forName("utf-8"))));
		acceptor.getFilterChain().addLast("coderc",   // 使用自定义编码解码工厂类
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("GBK"))));//设置编码过滤器

		acceptor.setHandler(ioHandler());
		acceptor.getSessionConfig().setReadBufferSize(1024);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		SocketAddress addresses = new InetSocketAddress(host,port1);
		SocketAddress addresses2 = new InetSocketAddress(host,port2);
		acceptor.bind(new SocketAddress[] {addresses,addresses2});
		log.info("=====================> Mina服务器在端口：" + port1+","+port2 + "已经启动!");
		return acceptor;
	}

}
