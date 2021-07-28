package com.ScanStation;

import com.ScanStation.Bean.HttpBean;
import com.ScanStation.Decomposer.Decomposer;
import com.ScanStation.Decomposer.HttpDecomposerImp;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullRequestIntercept;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.TestOnly;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class Proxy {
    /**
     * LinkedBlockingQueue<HttpBean> httpQueue = new LinkedBlockingQueue<HttpBean>();
     * HttpDecomposerImp decomposer = new HttpDecomposerImp(httpQueue);
     * decomposer.getQueue(); 获取生成的Httpbean队列
     * Proxy proxy = new Proxy(decomposer);
     * proxy.startProxy(9999);
     **/
    public Decomposer decomposer;

    Proxy(Decomposer decomposer) {
        this.decomposer = decomposer;
    }

    void startProxy(int port) {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        log.info("++++++++++++代理启动++++++++++++");
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new CertDownIntercept());
                        pipeline.addLast(new FullRequestIntercept(decomposer) {
                            @Override
                            public void handleRequest(Channel clientChannel, FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
//                                log.debug(httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri());
                                new Thread(() -> {
                                    if (this.decomposer.isProduce(httpRequest,clientChannel)) {
                                        this.decomposer.ProduceHttp(httpRequest, clientChannel);
                                    }
                                }).start();
                            }
                        });
                    }
                }).start(port);
    }

    @TestOnly
    public static void main(String[] args) {
        LinkedBlockingQueue<HttpBean> httpQueue = new LinkedBlockingQueue<HttpBean>();
        HttpDecomposerImp decomposer = new HttpDecomposerImp(httpQueue);
        int prot = 1111;
        new Thread(()-> {
            Proxy proxy = new Proxy(decomposer);
            proxy.startProxy(prot);
        }).start();

        AtomicInteger i = new AtomicInteger();
        new Thread(() -> {
            while (true) {
                if (!decomposer.getQueue().isEmpty()) {
                    log.error(decomposer.getQueue().size() + "---------------------------------------------" + !decomposer.getQueue().isEmpty());
                    try {
                        String http = decomposer.getQueue().take().toString();
                        i.getAndIncrement();
                        log.warn(i + "-----" + http);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
