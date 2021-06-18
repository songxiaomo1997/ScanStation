package com.scanStation.commonOkHttp;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

class RequestTimeEventListener extends EventListener {


    private long Starttime;

    public long getRequestTime() {
        return requestTime;
    }

    public long getStarttime() {
        return Starttime;
    }

    private long requestTime;

    @Override
    public void connectionAcquired(@NotNull Call call, @NotNull Connection connection) {
        this.Starttime=System.currentTimeMillis();
    }

    @Override
    public void connectionReleased(@NotNull Call call, @NotNull Connection connection) {
        long endTime= System.currentTimeMillis();
        this.requestTime = endTime-Starttime;
    }

}
