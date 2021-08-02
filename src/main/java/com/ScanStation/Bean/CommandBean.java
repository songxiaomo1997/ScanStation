package com.ScanStation.Bean;

import com.beust.jcommander.Parameter;

public class CommandBean {
    @Parameter(names = {"-active"},description = "主动扫描")
    private Boolean active = false;

    @Parameter(names = {"-passive"},description = "被动扫描扫描")
    private Boolean passive = false;

    @Parameter(names = {"-u", "--url"}, description = "url")
    private String url;

    @Parameter(names = {"--target","-target"}, description = "target")
    private String target;

    @Parameter(names = {"--pocPath"}, description = "pocPath")
    private String pocPath = ""; //后期内置poc

    @Parameter(names = {"-c", "--cookie"}, description = "cookie")
    private String cookie = "";

    @Parameter(names = {"-gP", "--globalParam"}, description = "globalParam")
    private String globalParam = "";

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = {"-hC", "--headerConfig"}, description = "headerConfig")
    private String headerConfig;

    @Parameter(names = {"-t", "--threads"}, description = "threads")
    private int threads = 10;

    @Parameter(names = {"-p","--proxy"}, description = "proxy")
    private int proxyProt;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getPassive() {
        return passive;
    }

    public void setPassive(Boolean passive) {
        this.passive = passive;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPocPath() {
        return pocPath;
    }

    public void setPocPath(String pocPath) {
        this.pocPath = pocPath;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getGlobalParam() {
        return globalParam;
    }

    public void setGlobalParam(String globalParam) {
        this.globalParam = globalParam;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getHeaderConfig() {
        return headerConfig;
    }

    public void setHeaderConfig(String headerConfig) {
        this.headerConfig = headerConfig;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getProxyProt() {
        return proxyProt;
    }

    public void setProxyProt(int proxyProt) {
        this.proxyProt = proxyProt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"active\":")
                .append(active);
        sb.append(",\"passive\":")
                .append(passive);
        sb.append(",\"url\":\"")
                .append(url).append('\"');
        sb.append(",\"target\":\"")
                .append(target).append('\"');
        sb.append(",\"pocPath\":\"")
                .append(pocPath).append('\"');
        sb.append(",\"cookie\":\"")
                .append(cookie).append('\"');
        sb.append(",\"globalParam\":\"")
                .append(globalParam).append('\"');
        sb.append(",\"debug\":")
                .append(debug);
        sb.append(",\"headerConfig\":\"")
                .append(headerConfig).append('\"');
        sb.append(",\"threads\":")
                .append(threads);
        sb.append(",\"proxyProt\":")
                .append(proxyProt);
        sb.append('}');
        return sb.toString();
    }
}
