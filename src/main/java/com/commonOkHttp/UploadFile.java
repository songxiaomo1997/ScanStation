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

import java.io.File;

/**
 * @ClassName: UploadFile
 * @Description: 通用OKHttp封装--基于 java.io.File 的文件上传bean
 * @author klw
 * @date 2018年4月4日 下午4:19:46
 */
public class UploadFile extends UploadFileBase {
    
    /**
     * @Fields file : 文件
     */
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
}
