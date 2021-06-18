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
package com.scanStation.commonOkHttp;

/**
 * @ClassName: UploadFileBase
 * @Description: 通用OKHttp封装--文件上传bean基类
 * @author klw
 * @date 2018年4月4日 下午4:19:29
 */
abstract class UploadFileBase {

    /**
     * @Fields prarmName : 文件参数名称
     */
    private String prarmName;
    
    /**
     * @Fields mediaType : 文件类型的 MediaType
     */
    private String mediaType;

    public String getPrarmName() {
        return prarmName;
    }

    public void setPrarmName(String prarmName) {
        this.prarmName = prarmName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    
}
