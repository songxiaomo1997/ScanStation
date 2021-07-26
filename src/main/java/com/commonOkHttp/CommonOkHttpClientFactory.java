/*
 * Copyright 2018 klw(213539@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.commonOkHttp;


import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;


/**
 * @ClassName: CommonOkHttpClientFactory
 * @Description: CommonOkHttpClient spring 工厂
 * @author klw
 * @date 2018年4月3日 下午5:11:20
 */
public class CommonOkHttpClientFactory extends AbstractFactoryBean<CommonOkHttpClient> {

    /**
     * @Fields readTimeoutMilliSeconds : 读超时
     */
    private long readTimeoutMilliSeconds = 100000;
    
    /**
     * @Fields writeTimeout : 写超时
     */
    private long writeTimeout = 10000;
    
    /**
     * @Fields connectTimeout : 连接超时
     */
    private long connectTimeout = 15000;
    
    
    //=========以下为https相关参数,如果不请求https,或者要使用默认CA方式,可以不用设置==============
    
    /**
     * @Fields isUnSafe : 是否使用不安全的方式(不对证书做任何效验), 如果此参数为默认值,并且没有添加信人证书,则使用默认CA方式验证
     */
    boolean isUnSafe = false;
    
    /**
     * @Fields isCheckHostname : 是否验证域名/IP, 仅对添加自签证书为信任时生效
     */
    boolean isCheckHostname = true;
    
    /**
     * @Fields certificateFilePaths : 用含有服务端公钥的证书校验服务端证书(添加自签证书为信任证书)
     */
    private Resource[] certificateFilePaths;
    
    
    /**
     * @Fields pkcsFile : 使用 指定 PKCS12 证书加密解密数据(应对支付宝,微信支付等)
     */
    private String pkcsFile = null;
    
    /**
     * @Fields pkcsFilePwd : PKCS12 证书的密码
     */
    private String pkcsFilePwd = null;
    
    @Override
    protected CommonOkHttpClient createInstance() throws Exception {
	return new CommonOkHttpClientBuilder(readTimeoutMilliSeconds, writeTimeout, connectTimeout, isUnSafe, 
		    isCheckHostname, certificateFilePaths, pkcsFile, pkcsFilePwd).build();
    }

    @Override
    public Class<?> getObjectType() {
	return CommonOkHttpClient.class;
    }

    public void setReadTimeoutMilliSeconds(long readTimeoutMilliSeconds) {
        this.readTimeoutMilliSeconds = readTimeoutMilliSeconds;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setUnSafe(boolean isUnSafe) {
        this.isUnSafe = isUnSafe;
    }

    public void setCertificateFilePaths(Resource... certificateFilePaths) {
        this.certificateFilePaths = certificateFilePaths;
    }

    public void setPkcsFile(String pkcsFile) {
        this.pkcsFile = pkcsFile;
    }

    public void setPkcsFilePwd(String pkcsFilePwd) {
        this.pkcsFilePwd = pkcsFilePwd;
    }

    public void setCheckHostname(boolean isCheckHostname) {
        this.isCheckHostname = isCheckHostname;
    }

}
