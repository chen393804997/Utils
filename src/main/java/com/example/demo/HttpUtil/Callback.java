package com.example.demo.HttpUtil;

import okhttp3.Call;

import java.io.InputStream;

public interface Callback {
    void onFail(Call call);

    void onSuccess(Call call, String errCode, String errMsg, String responseBodyString, InputStream responseBodyInputStream, byte[] responseBodyBytes);
}
