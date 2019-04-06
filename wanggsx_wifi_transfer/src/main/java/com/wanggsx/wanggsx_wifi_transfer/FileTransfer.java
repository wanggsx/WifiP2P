package com.wanggsx.wanggsx_wifi_transfer;

import java.io.Serializable;

public class FileTransfer implements Serializable {

    //文件路径
    private String filePath;

    //文件大小
    private long fileLength;

    //MD5码
    private String md5;

}