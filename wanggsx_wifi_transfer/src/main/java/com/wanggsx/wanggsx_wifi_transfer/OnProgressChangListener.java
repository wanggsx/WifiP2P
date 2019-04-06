package com.wanggsx.wanggsx_wifi_transfer;

import java.io.File;

public interface OnProgressChangListener {

    //当传输进度发生变化时
    void onProgressChanged(FileTransfer fileTransfer, int progress);

    //当传输结束时
    void onTransferFinished(File file);

}