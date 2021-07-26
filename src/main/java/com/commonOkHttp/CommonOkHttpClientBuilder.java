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

import com.commonOkHttp.utils.HttpsUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: CommonOkHttpClientBuilder
 * @Description: 通用OKHttp封装 创建器
 * @author klw
 * @date 2018年4月5日 下午11:23:56
 */
public class CommonOkHttpClientBuilder {
    
    /**
     * @Fields readTimeoutMilliSeconds : 读超时
     */
    private long readTimeoutMilliSeconds;
    
    /**
     * @Fields writeTimeout : 写超时
     */
    private long writeTimeout;
    
    /**
     * @Fields connectTimeout : 连接超时
     */
    private long connectTimeout;
    
    
    //=========以下为https相关参数,如果不请求https,或者要使用默认CA方式,可以不用设置==============
    
    /**
     * @Fields isUnSafe : 是否使用不安全的方式(不对证书做任何效验), 如果此参数为默认值,并且没有添加信人证书,则使用默认CA方式验证
     */
    boolean isUnSafe;
    
    /**
     * @Fields isCheckHostname : 是否验证域名/IP, 仅对添加自签证书为信任时生效
     */
    boolean isCheckHostname;
    
    /**
     * @Fields certificateFilePaths : 用含有服务端公钥的证书校验服务端证书(添加自签证书为信任证书)
     */
    private List<URL> certificateFilePaths;
    
    
    /**
     * @Fields pkcsFile : 使用 指定 PKCS12 证书加密解密数据(应对支付宝,微信支付等)
     */
    private String pkcsFile;
    
    /**
     * @Fields pkcsFilePwd : PKCS12 证书的密码
     */
    private String pkcsFilePwd;
    
    public CommonOkHttpClientBuilder() {
	readTimeoutMilliSeconds = 100000;//100000;
	writeTimeout = 10000;//10000;
	connectTimeout = 15000;//15000;
	isUnSafe = false;
	isCheckHostname = true;
	certificateFilePaths = null;
	pkcsFile = null;
	pkcsFilePwd = null;
    }
    
    public CommonOkHttpClientBuilder(long readTimeoutMilliSeconds, long writeTimeout, long connectTimeout, boolean isUnSafe, 
	    boolean isCheckHostname, List<URL> certificateFilePaths, String pkcsFile, String pkcsFilePwd) {
	this.readTimeoutMilliSeconds = readTimeoutMilliSeconds;
	this.writeTimeout = writeTimeout;
	this.connectTimeout = connectTimeout;
	this.isUnSafe = isUnSafe;
	this.isCheckHostname = isCheckHostname;
	this.certificateFilePaths = certificateFilePaths;
	this.pkcsFile = pkcsFile;
	this.pkcsFilePwd = pkcsFilePwd;
    }
    
    public CommonOkHttpClientBuilder(long readTimeoutMilliSeconds, long writeTimeout, long connectTimeout, boolean isUnSafe, 
	    boolean isCheckHostname, Resource[] certificateFilePaths, String pkcsFile, String pkcsFilePwd) {
	this.readTimeoutMilliSeconds = readTimeoutMilliSeconds;
	this.writeTimeout = writeTimeout;
	this.connectTimeout = connectTimeout;
	this.isUnSafe = isUnSafe;
	this.isCheckHostname = isCheckHostname;
	this.certificateFilePaths(certificateFilePaths);
	this.pkcsFile = pkcsFile;
	this.pkcsFilePwd = pkcsFilePwd;
    }
    
    public CommonOkHttpClientBuilder readTimeoutMilliSeconds(long readTimeoutMilliSeconds) {
	this.readTimeoutMilliSeconds = readTimeoutMilliSeconds;
	return this;
    }
    
    public CommonOkHttpClientBuilder writeTimeout(long writeTimeout) {
	this.writeTimeout = writeTimeout;
	return this;
    }
    
    public CommonOkHttpClientBuilder connectTimeout(long connectTimeout) {
	this.connectTimeout = connectTimeout;
	return this;
    }
    
    public CommonOkHttpClientBuilder unSafe(boolean isUnSafe) {
	this.isUnSafe = isUnSafe;
	return this;
    }
    
    public CommonOkHttpClientBuilder checkHostname(boolean isCheckHostname) {
	this.isCheckHostname = isCheckHostname;
	return this;
    }
    
    public CommonOkHttpClientBuilder certificateFilePaths(Resource[] certificateFilePathsArr) {
	if (certificateFilePathsArr != null) {
	    this.certificateFilePaths = new ArrayList<>(certificateFilePathsArr.length);
	    for (Resource certificateFilePath : certificateFilePathsArr) {
		try {
		    certificateFilePaths.add(certificateFilePath.getURL());
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return this;
    }
    
    public CommonOkHttpClientBuilder certificateFilePaths(List<URL> certificateFilePaths) {
	this.certificateFilePaths = certificateFilePaths;
	return this;
    }
    
    public CommonOkHttpClientBuilder pkcs(String pkcsFile, String pkcsFilePwd) {
	this.pkcsFile = pkcsFile;
	this.pkcsFilePwd = pkcsFilePwd;
	return this;
    }
    
    public CommonOkHttpClient build() {
	HttpsUtils.SSLParams sslParams = null;
	if(isUnSafe) {
	    // 使用不安全的方式创建SSL环境--不验证证书,不验证域名, 100% 会被中间人攻击
	    sslParams = HttpsUtils.getSslSocketFactory(isUnSafe);
	} else {
	    if(certificateFilePaths != null && certificateFilePaths.size() > 0) {
		// 把指定证书添加为信任证书,可以防止中间人攻击
		List<InputStream> isList = new ArrayList<>();
		certificateFilePaths.stream().forEach((certificateFilePath) -> {
		    try {
			isList.add(new FileInputStream(certificateFilePath.getFile()));
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		});
		sslParams = HttpsUtils.getSslSocketFactory(isCheckHostname, isList.toArray(new InputStream[0]));
	    } else if(StringUtils.isNotBlank(pkcsFile) && StringUtils.isNotBlank(pkcsFilePwd)) {
		// 使用指定证书加密/解密数据(已知微信这样要求)
		sslParams = HttpsUtils.getSslSocketFactory(pkcsFile, pkcsFilePwd);
	    } else {
		// 使用默认方式创建SSL环境--使用JDK自带的CA证书或者安装到JDK中的CA证书验证, 容易被中间人攻击(中间人使用与目标服务器相同的CA证书和CA颁发的证书)
		sslParams = HttpsUtils.getSslSocketFactory(isUnSafe);
	    }
	}
	
	return new CommonOkHttpClient(readTimeoutMilliSeconds, writeTimeout, connectTimeout, sslParams);
    }
    
}
