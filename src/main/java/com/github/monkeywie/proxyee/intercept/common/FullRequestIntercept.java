package com.github.monkeywie.proxyee.intercept.common;

import com.ScanStation.Decomposer.Decomposer;
import com.ScanStation.Decomposer.HttpDecomposerImp;
import com.github.monkeywie.proxyee.intercept.HttpProxyIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

public abstract class FullRequestIntercept extends HttpProxyIntercept {

    /**
     * default max content length size is 8MB
     */
    private static final int DEFAULT_MAX_CONTENT_LENGTH = 1024 * 1024 * 1024;

    private int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;

    public Decomposer getDecomposer() {
        return decomposer;
    }

    public void setDecomposer(Decomposer decomposer) {
        this.decomposer = decomposer;
    }

    public Decomposer decomposer;


    public FullRequestIntercept(Decomposer decomposer) {
        this.decomposer = decomposer;
    }


    @Override
    public final void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) throws Exception {
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
            handleRequest(clientChannel,fullHttpRequest, pipeline);
            fullHttpRequest.content().markReaderIndex();
            fullHttpRequest.content().retain();
            if (fullHttpRequest.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            }
        } else if (match(httpRequest, pipeline)) {
            //重置拦截器
            pipeline.resetBeforeHead();
            //添加gzip解压处理
            clientChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
            //添加Full request解码器
            clientChannel.pipeline().addAfter("decompress", "aggregator", new HttpObjectAggregator(maxContentLength));
            //重新过一遍处理器链
            clientChannel.pipeline().fireChannelRead(httpRequest);

            return;
        }
        pipeline.beforeRequest(clientChannel, httpRequest);
    }

    @Override
    public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
        //如果是FullHttpRequest
        if (pipeline.getHttpRequest() instanceof FullHttpRequest) {
            if (clientChannel.pipeline().get("decompress") != null) {
                clientChannel.pipeline().remove("decompress");
            }
            if (clientChannel.pipeline().get("aggregator") != null) {
                clientChannel.pipeline().remove("aggregator");
            }
            FullHttpRequest httpRequest = (FullHttpRequest) pipeline.getHttpRequest();
            httpRequest.content().resetReaderIndex();
        }
        pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
    }

    /**
     * 匹配到的请求会解码成FullRequest
     */
    public boolean match(HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
        return true;
    }

    /**
     * 拦截并处理响应
     */
    public void handleRequest(Channel clientChannel,FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
    }

}
