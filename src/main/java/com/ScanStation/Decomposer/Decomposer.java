package com.ScanStation.Decomposer;

import com.ScanStation.Bean.HttpBean;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.concurrent.LinkedBlockingQueue;

public interface Decomposer<T> {

    HttpBean ProduceHttp(FullHttpRequest request, Channel clientChannel);

    Boolean isProduce(FullHttpRequest request, Channel clientChannel);

    LinkedBlockingQueue<T> getQueue();
}
