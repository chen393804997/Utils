package com.example.demo.HttpUtil;


import com.example.demo.utils.JSONUtils;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

    //    private static final MediaType TYPE_OCTET = MediaType.parse("application/octet-stream; charset=utf-8");
    private static final MediaType TYPE_FORM_DATA = MediaType.parse("multipart/form-data; charset=utf-8");

    private static OkHttpClient client = null;

    //定义异步请求返回数据的类型
    public static final int RETURN_SYNC_STRING = 0;
    public static final int RETURN_SYNC_INPUTSTREAM = 1;
    public static final int RETURN_SYNC_BYTE = 2;

    public OkHttpUtils() {
        init(new OKHttpProperty());
    }

    public OkHttpUtils(OKHttpProperty property) {
        init(property);
    }

//=======================get请求(String)==================================

    /**
     * 同步请求
     *
     * @param url 请求接口的url
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doGetReturnString(String url) throws Exception {
        return doResponseBodyByString(doGet(url, new HashMap<String, String>()));
    }

    /**
     * 同步请求
     *
     * @param url 请求接口的url
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doGetReturnInputStream(String url) throws Exception {
        return doGet(url, new HashMap<String, String>()).byteStream();
    }

    /**
     * 同步请求
     *
     * @param url 请求接口的url
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doGetReturnByte(String url) throws Exception {
        return doResponseBodyByBytes(doGet(url, new HashMap<String, String>()));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doGetReturnString(String url, Map<String, String> requestParam) throws Exception {
        return doResponseBodyByString(doGet(url, requestParam, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doGetReturnInputStream(String url, Map<String, String> requestParam) throws Exception {
        return doGet(url, requestParam, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doGetReturnByte(String url, Map<String, String> requestParam) throws Exception {
        return doResponseBodyByBytes(doGet(url, requestParam, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return ResponseBody
     *
     * @throws Exception
     *
     * **/
    private ResponseBody doGet(String url, Map<String, String> requestParam) throws Exception {
        return doGet(url, requestParam, null, null);
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doGetReturnString(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(doUrlByGet(url, requestParam), null, false, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doGetReturnInputStream(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doGetReturnResponseBody(url, requestParam, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public ResponseBody doGetReturnResponseBody(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(doUrlByGet(url, requestParam), null, false, headerMap, addHeaderMap);
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doGetReturnByte(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(doUrlByGet(url, requestParam), null, false, headerMap, addHeaderMap));
    }


    private ResponseBody doGet(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(doUrlByGet(url, requestParam), null, false, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doGetSync(String url, Map<String, String> requestParam, int returnSyncFlag, Callback callback) {
        doGetSync(url, requestParam, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doGetSync(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) {
        doSync(doUrlByGet(url, requestParam), null, false, headerMap, addHeaderMap, returnSyncFlag, callback);
    }


//=======================post请求(String)==================================

    /**
     * 同步请求
     *
     * @param url 请求接口的url
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url) throws Exception {
        return doResponseBodyByString(doPost(url, new HashMap<String, String>()));
    }

    /**
     * 同步请求
     *
     * @param url 请求接口的url
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url) throws Exception {
        return doPost(url, new HashMap<String, String>()).byteStream();
    }

    /**
     * 同步请求
     *
     * @param url 请求接口的url
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url) throws Exception {
        return doResponseBodyByBytes(doPost(url, new HashMap<String, String>()));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam) throws Exception {
        return doResponseBodyByString(doPost(url, requestParam, null, null));
    }

    public String doPostFromBodyReturnString(String url, Map<String, String> requestParam) throws Exception {
        return doResponseBodyByString(doPostFormBody(url, requestParam, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public String doPostReturnString(String url, String json) throws Exception {
        return doResponseBodyByString(doPost(url, json, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam) throws Exception {
        return doPost(url, requestParam, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, String json) throws Exception {
        return doPost(url, json, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam) throws Exception {
        return doResponseBodyByBytes(doPost(url, requestParam, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, String json) throws Exception {
        return doResponseBodyByBytes(doPost(url, json, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     *
     * @return ResponseBody
     *
     * @throws Exception
     *
     * **/
    private ResponseBody doPost(String url, Map<String, String> requestParam) throws Exception {
        return doPost(url, requestParam, null, null);
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     *
     * @return ResponseBody
     *
     * @throws
     *
     * **/
    private ResponseBody doPost(String url, String json) throws Exception {
        return doPost(url, json, null, null);
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doMultipartBody(requestParam, null), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, String json, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doJSONBody(json), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doPostReturnResponseBody(url, requestParam, headerMap, addHeaderMap).byteStream();
    }

    public ResponseBody doPostReturnResponseBody(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, null), true, headerMap, addHeaderMap);
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, String json, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doJSONBody(json), true, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doMultipartBody(requestParam, null), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, String json, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doJSONBody(json), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * @throws Exception
     *
     *
     * **/
    private ResponseBody doPost(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, null), true, headerMap, addHeaderMap);
    }

    private ResponseBody doPostFormBody(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartFormBody(requestParam), true, headerMap, addHeaderMap);
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * @throws Exception
     *
     *
     * **/
    private ResponseBody doPost(String url, String json, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doJSONBody(json), true, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, int returnSyncFlag, Callback callback) {
        doPostSync(url, requestParam, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doPostSync(String url, String json, int returnSyncFlag, Callback callback) {
        doPostSync(url, json, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) {

        try {
            doSync(url, doMultipartBody(requestParam, null), true, headerMap, addHeaderMap, returnSyncFlag, callback);
        }catch (Exception e) {}
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param json 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doPostSync(String url, String json, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) {

        try {
            doSync(url, doJSONBody(json), true, headerMap, addHeaderMap, returnSyncFlag, callback);
        }catch (Exception e) {}
    }


//=======================post请求(byte)==================================

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, byte[] bytes) throws Exception {
        return doResponseBodyByString(doPost(url, bytes, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, byte[] bytes) throws Exception {
        return doPost(url, bytes, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, byte[] bytes) throws Exception {
        return doResponseBodyByBytes(doPost(url, bytes, null, null));
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, byte[] bytes, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doMultipartBody(null, bytes, false), true, headerMap, addHeaderMap));
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, byte[] bytes, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(null, bytes, false), true, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, byte[] bytes, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doMultipartBody(null, bytes, false), true, headerMap, addHeaderMap));
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * **/
    private ResponseBody doPost(String url, byte[] bytes, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(null, bytes, false), true, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doPostSync(String url, byte[] bytes, int returnSyncFlag, Callback callback) {
        doPostSync(url, bytes, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param bytes 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * **/
    public void doPostSync(String url, byte[] bytes, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) {
        doSync(url, doMultipartBody(null, bytes, false), true, headerMap, addHeaderMap, returnSyncFlag, callback);
    }



//=======================post请求(File)==================================

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, File file) throws Exception {
        return doResponseBodyByString(doPost(url, file, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, File file) throws Exception {
        return doPost(url, file, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, File file) throws Exception {
        return doResponseBodyByBytes(doPost(url, file, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doMultipartBody(null, file, false), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(null, file, false), true, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public byte[] doPostReturnByte(String url, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doMultipartBody(null, file, false), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    private ResponseBody doPost(String url, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(null, file, false), true, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public void doPostSync(String url, File file, int returnSyncFlag, Callback callback) throws Exception {
        doPostSync(url, file, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public void doPostSync(String url, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) throws Exception {
        doSync(url, doMultipartBody(null, file, false), true, headerMap, addHeaderMap, returnSyncFlag, callback);
    }



//=======================post请求(Map<String, String> requestParam, File file)==================================

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, File file) throws Exception {
        return doResponseBodyByString(doPost(url, requestParam, file, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, File file) throws Exception {
        return doPost(url, requestParam, file, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, File file) throws Exception {
        return doResponseBodyByBytes(doPost(url, requestParam, file, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doMultipartBody(requestParam, file, false), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, file, false), true, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doMultipartBody(requestParam, file, false), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * **/
    private ResponseBody doPost(String url, Map<String, String> requestParam, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, file, false), true, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, File file, int returnSyncFlag, Callback callback) throws Exception {
        doPostSync(url, requestParam, file, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param file 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, File file, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) throws Exception {
        doSync(url, doMultipartBody(requestParam, file, false), true, headerMap, addHeaderMap, returnSyncFlag, callback);
    }



//=======================post请求(Map<String, String> requestParam, List<File> files)====================

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     *
     * @return String
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, List<File> files) throws Exception {
        return doResponseBodyByString(doPost(url, requestParam, files, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, List<File> files) throws Exception {
        return doPost(url, requestParam, files, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     *
     * @return byte[]
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, List<File> files) throws Exception {
        return doResponseBodyByBytes(doPost(url, requestParam, files, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, List<File> files, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doMultipartBody(requestParam, files), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, List<File> files, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, files), true, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, List<File> files, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doMultipartBody(requestParam, files), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return ResponseBody
     *
     * **/
    private ResponseBody doPost(String url, Map<String, String> requestParam, List<File> files, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, files), true, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, List<File> files, int returnSyncFlag, Callback callback) throws Exception {
        doPostSync(url, requestParam, files, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param files 请求参数
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, List<File> files, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) throws Exception {
        doSync(url, doMultipartBody(requestParam, files), true, headerMap, addHeaderMap, returnSyncFlag, callback);
    }


//=======================post请求(Map<String, String> requestParam, List<File> files)===============

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, String uriFilPath) throws Exception {
        return doResponseBodyByString(doPost(url, requestParam, uriFilPath, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, String uriFilPath) throws Exception {
        return doPost(url, requestParam, uriFilPath, null, null).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, String uriFilPath) throws Exception {
        return doResponseBodyByBytes(doPost(url, requestParam, uriFilPath, null, null));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return String
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public String doPostReturnString(String url, Map<String, String> requestParam, String uriFilPath, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByString(doNotSync(url, doMultipartBody(requestParam, uriFilPath, false), true, headerMap, addHeaderMap));
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return InputStream  注意：InputStream使用完之后一定要关闭(close)
     * @throws Exception errCode:xxx errMsg:xxx
     * **/
    public InputStream doPostReturnInputStream(String url, Map<String, String> requestParam, String uriFilPath, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, uriFilPath, false), true, headerMap, addHeaderMap).byteStream();
    }

    /**
     *  同步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     *
     * @return byte[]
     *
     * @throws Exception errCode:xxx errMsg:xxx
     *
     * **/
    public byte[] doPostReturnByte(String url, Map<String, String> requestParam, String uriFilPath, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doResponseBodyByBytes(doNotSync(url, doMultipartBody(requestParam, uriFilPath, false), true, headerMap, addHeaderMap));
    }

    private ResponseBody doPost(String url, Map<String, String> requestParam, String uriFilPath, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {
        return doNotSync(url, doMultipartBody(requestParam, uriFilPath, false), true, headerMap, addHeaderMap);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     * @throws IOException errCode:xxx errMsg:xxx
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, String uriFilPath, int returnSyncFlag, Callback callback) throws IOException {
        doPostSync(url, requestParam, uriFilPath, null, null, returnSyncFlag, callback);
    }

    /**
     *  异步请求
     *
     * @param url 请求接口的url
     * @param requestParam 请求参数
     * @param uriFilPath 网络文件uri路径
     * @param headerMap 添加请求的header，header不能参数不能重复
     * @param addHeaderMap key必须 new String("xxx") , value随便
     * @param returnSyncFlag 定义异步请求String、InputStream还是byte[]
     * @param callback 回调函数
     *
     * @throws IOException errCode:xxx errMsg:xxx
     *
     * **/
    public void doPostSync(String url, Map<String, String> requestParam, String uriFilPath, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) throws IOException {
        doSync(url, doMultipartBody(requestParam, uriFilPath, false), true, headerMap, addHeaderMap, returnSyncFlag, callback);
    }

    /**
     * 同步请求 获取 Response
     * @param url
     * @param json
     * @param headerMap
     * @param addHeaderMap
     * @return
     */

    public Response doPostReturnResponse(String url, String json, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap)  throws Exception {

        return doNotSyncReturnResponse(url, doJSONBody(json), true, headerMap, addHeaderMap);
    }



//=======================私有方法===================================

    private void init(OKHttpProperty property) {
        client = new OkHttpClient()
                .newBuilder()
                .readTimeout(property.getReadTimeOut(), TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(property.getWriteTimeout(),TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(property.getConnectTimeout(),TimeUnit.SECONDS)//设置连接超时时间
                .retryOnConnectionFailure(property.isRetryOnConnectionFailure()) //请求失败是否自动重连
                .connectionPool(
                        new ConnectionPool(property.getMaxIdleConnections(), property.getKeepAliveDuration(), TimeUnit.MINUTES)
                ).build();
    }

    private Request getRequest(String url, RequestBody body, boolean isPost, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) {
        Request.Builder b = new Request.Builder();
        if (isPost) {
            b.post(body);
        }

        if (headerMap != null) {
            for (String headerKey : headerMap.keySet()) {
                b.header(headerKey, headerMap.get(headerKey));
            }
        }

        if (addHeaderMap != null) {
            for (String headerKey : addHeaderMap.keySet()) {
                b.addHeader(headerKey, addHeaderMap.get(headerKey));
            }
        }

        return b.url(url).build();
    }


    private ResponseBody doNotSync(String url, RequestBody body, boolean isPost, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {

        Request request = getRequest(url, body, isPost, headerMap, addHeaderMap);

        ResponseBody dBody = null;
        Map<String, String> map = new HashMap<>();

        Response response = null;

        try {
            response = client.newCall(request).execute();
        }catch (IOException e) {

            map.put(OKHttpCode.ERR_CODE, OKHttpCode.INIT_RESPONSE_CODE);
            map.put(OKHttpCode.ERR_MSG, e.getMessage());

            throw new IOException(JSONUtils.toJson(map));
        }


        if (response.isSuccessful()) {
            dBody = response.body();
            return dBody;
        }else {
            if (dBody != null) {
                dBody.close();
            }

            map.put(OKHttpCode.ERR_CODE, response.code()+"");
            map.put(OKHttpCode.ERR_MSG, response.message());

            throw new IOException(JSONUtils.toJson(map));
        }

    }

    private void doSync(String url, RequestBody body, boolean isPost, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap, int returnSyncFlag, Callback callback) {
        Request request = getRequest(url, body, isPost, headerMap, addHeaderMap);
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFail(call);
            }

            @Override
            public void onResponse(Call call, Response response) {

                ResponseBody dBody = null;

                String str = null;
                InputStream in = null;
                byte[] bytes = null;

                String errCode = OKHttpCode.DEFAULT_CODE, errMsg = "";

                try {

                    if (response.isSuccessful()) {
                        dBody = response.body();
                        switch (returnSyncFlag) {
                            case OkHttpUtils.RETURN_SYNC_STRING: str = dBody.string(); break;
                            case OkHttpUtils.RETURN_SYNC_INPUTSTREAM: in = dBody.byteStream(); break;
                            case OkHttpUtils.RETURN_SYNC_BYTE: bytes = dBody.bytes(); break;
                        }
                    } else {
                        if (dBody != null) {
                            dBody.close();
                        }

                        errCode = response.code()+"";
                        errMsg = response.message();
                    }

                }catch (IOException e) {
                    errCode = OKHttpCode.ON_RESPONSE_CODE;
                    errMsg = e.getMessage();
                }

                callback.onSuccess(call, errCode, errMsg, str, in, bytes);
            }
        });
    }


    private String doUrlByGet(String url, Map<String, String> requestParam) {

        if (requestParam != null && !requestParam.isEmpty()) {

            StringBuffer buffer = new StringBuffer();

            int index = url.indexOf("?");
            if (index != -1) {
                buffer.append(url.substring(0, index));
            }else {
                buffer.append(url);
            }

            buffer.append("?");

            int i = 1, size = requestParam.size();
            for (String key : requestParam.keySet()) {
                buffer.append(key).append("=").append(requestParam.get(key));
                if (i < size) {
                    buffer.append("&");
                }
                i++;
            }

            url = new String(buffer);
        }

        return url;
    }

    private RequestBody doJSONBody(String json) {

        return RequestBody.create(null, json);
    }

    private RequestBody doMultipartBody(Map<String, String> requestParam, File file, boolean b) throws Exception {

        List<File> files = null;
        if (file != null) {
            files = new ArrayList<File>();
            files.add(file);
        }

        return doMultipartBody(requestParam, files);
    }

    private RequestBody doMultipartBody(Map<String, String> requestParam, List<File> files) throws Exception {

        RequestBody body = null;

        if (files != null || (requestParam != null && !requestParam.isEmpty())) {

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            if (files != null) {
                for (File file : files) {
                    if (file.exists() && file.isFile()) {

                        String fileName = file.getName();

                        int index = fileName.indexOf(".");

                        if (index != -1) {

                            String n1 = fileName.substring(0, index);
                            String n2 = fileName.substring(index + 1);
                            String n3 = n1 + n2.substring(0, 1).toUpperCase() + n2.substring(1);

                            builder.addFormDataPart(n3, fileName, RequestBody.create(TYPE_FORM_DATA, file));
                        }
                    }else {

                        Map<String, String> map = new HashMap<>();
                        map.put(OKHttpCode.ERR_CODE, OKHttpCode.FILE_REQUEST_CODE);
                        map.put(OKHttpCode.ERR_MSG, "文件不能为空");

                        throw new Exception(JSONUtils.toJson(map));
                    }
                }
            }

            if (requestParam != null && !requestParam.isEmpty()) {
                for (String key : requestParam.keySet()) {
                    builder.addFormDataPart(key, requestParam.get(key));
                }
            }

            body = builder.build();
        }else {
            body = RequestBody.create(null, "");
        }

        return body;
    }


    private RequestBody doMultipartFormBody(Map<String, String> requestParam) throws Exception {

        RequestBody body = null;

        if (requestParam != null && !requestParam.isEmpty()) {

            FormBody.Builder builder = new FormBody.Builder();
            for (String key : requestParam.keySet()) {
                builder.add(key, requestParam.get(key));
            }

            body = builder.build();
        }else {
            body = RequestBody.create(null, "");
        }

        return body;
    }


    private RequestBody doMultipartBody(Map<String, String> requestParam, String uriFilePath, boolean b) throws IOException {

        RequestBody fileBody = null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (!StringUtils.isEmpty(uriFilePath)) {

            String dUriFilePath = "";
            int index = uriFilePath.indexOf("?");
            if (index != -1) {
                dUriFilePath = uriFilePath.substring(0, index);
            }else {
                dUriFilePath = uriFilePath;
            }

            int index1 = dUriFilePath.lastIndexOf("/");
            String fileName = "";
            if (index1 != -1) {
                fileName = dUriFilePath.substring(index1+1);
            }

            int index2 = fileName.indexOf(".");
            if (index2 != -1) {
                String n1 = fileName.substring(0, index2);
                String n2 = fileName.substring(index2 + 1);
                String n3 = n1 + n2.substring(0, 1).toUpperCase() + n2.substring(1);

                InputStream in = null;
                try {

                    URL url = new URL(uriFilePath);
                    // 打开连接
                    URLConnection con = url.openConnection();
                    //设置请求超时为5s
                    con.setConnectTimeout(5*1000);
                    // 输入流
                    in = con.getInputStream();
                    fileBody = RequestBody.create(TYPE_FORM_DATA, IOUtils.toByteArray(in));
                    builder.addFormDataPart(n3, fileName, fileBody);

                }catch (IOException e) {

                    Map<String, String> map = new HashMap<>();
                    map.put(OKHttpCode.ERR_CODE, OKHttpCode.INIT_MULTIPARTBODY_BY_URL);
                    map.put(OKHttpCode.ERR_MSG, e.getMessage());

                    throw new IOException(JSONUtils.toJson(map));
                }finally {
                    if (in != null) {

                        try {
                            in.close();
                        }catch (IOException e) {
                            Map<String, String> map = new HashMap<>();
                            map.put(OKHttpCode.ERR_CODE, OKHttpCode.INIT_MULTIPARTBODY_BY_URL);
                            map.put(OKHttpCode.ERR_MSG, e.getMessage());

                            throw new IOException(JSONUtils.toJson(map));
                        }
                    }
                }
            }
        }

        if (requestParam != null && !requestParam.isEmpty()) {
            for (String key : requestParam.keySet()) {
                builder.addFormDataPart(key, requestParam.get(key));
            }
        }

        return builder.build();
    }


    //处理字节方法
    private RequestBody doMultipartBody(Map<String, String> requestParam, byte[] bytes, boolean b) {

        RequestBody fileBody = null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (bytes != null && bytes.length > 0) {
            fileBody = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), bytes);
            builder.addPart(fileBody);
        }

        if (requestParam != null && !requestParam.isEmpty()) {
            for (String key : requestParam.keySet()) {
                builder.addFormDataPart(key, requestParam.get(key));
            }
        }

        return builder.build();
    }


    private String doResponseBodyByString(ResponseBody body) throws IOException {

        try {
            return body.string();
        }catch (IOException e) {

            Map<String, String> map = new HashMap<>();
            map.put(OKHttpCode.ERR_CODE, OKHttpCode.GET_DATA_RESPONSEBODY);
            map.put(OKHttpCode.ERR_MSG, e.getMessage());

            throw new IOException(JSONUtils.toJson(map));
        }
    }

    private byte[] doResponseBodyByBytes(ResponseBody body) throws IOException {

        try {
            return body.bytes();
        }catch (IOException e) {

            Map<String, String> map = new HashMap<>();
            map.put(OKHttpCode.ERR_CODE, OKHttpCode.GET_DATA_RESPONSEBODY);
            map.put(OKHttpCode.ERR_MSG, e.getMessage());

            throw new IOException(JSONUtils.toJson(map));
        }
    }


    private Response doNotSyncReturnResponse(String url, RequestBody body, boolean isPost, Map<String, String> headerMap, IdentityHashMap<String, String> addHeaderMap) throws Exception {

        Request request = getRequest(url, body, isPost, headerMap, addHeaderMap);

        Map<String, String> map = new HashMap<>();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        }catch (IOException e) {
            map.put(OKHttpCode.ERR_CODE, OKHttpCode.INIT_RESPONSE_CODE);
            map.put(OKHttpCode.ERR_MSG, e.getMessage());
            throw new IOException(JSONUtils.toJson(map));
        }
        return response;
    }

//======================测试方法===============================================











    private static void testDoGet() throws Exception {

        String url = "http://newranktest.oss-cn-hangzhou.aliyuncs.com/accelerator/ar/2016/11/09/1161036227f6410bbb6f28d1d1ad5a12.png";
        url += "?" + System.currentTimeMillis();

        OkHttpUtils okHttpUtils = new OkHttpUtils();

        Map<String, String> map = new HashMap<String, String>();
        map.put("Referer", "http://www.newrank.cn/");
        InputStream inputStream = okHttpUtils.doGetReturnInputStream(url, new HashMap<String, String>(), map, null);

        FileOutputStream out = new FileOutputStream("D:/kk.png");
        IOUtils.copy(inputStream, out);
        inputStream.close();
    }

    private static void testDoGetAndRequestParam() throws Exception {

        String url = "http://localhost:8118/xdnphb/v1/iplist/test2";
        OkHttpUtils okHttpUtils = new OkHttpUtils();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "name");
        map.put("name1", "name1");

        InputStream inputStream = okHttpUtils.doGetReturnInputStream(url, map);

        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();

        System.out.println(new String(bytes));
    }

    private static void testDoGetSync() throws Exception {

        String url = "http://localhost:8118/xdnphb/v1/iplist/test2";
        OkHttpUtils okHttpUtils = new OkHttpUtils();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "name");
        map.put("name1", "name1");

        okHttpUtils.doGetSync(url, map, OkHttpUtils.RETURN_SYNC_STRING, new Callback() {
            @Override
            public void onFail(Call call) {
                System.out.println(111);
            }

            @Override
            public void onSuccess(Call call, String errCode, String errMsg, String responseBodyString, InputStream responseBodyInputStream, byte[] responseBodyBytes) {

                System.out.println("1");

                System.out.println(responseBodyString);

                System.out.println("2");
            }
        });
    }

    private static void testDoPostAndRequestParam() throws Exception {

        OkHttpUtils okHttpUtils = new OkHttpUtils();
        String url = "http://localhost:8118/xdnphb/v1/iplist/test3";

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "tom");
        map.put("name1", "name1");
        ResponseBody body = okHttpUtils.doPost(url, map);
        System.out.println(body.string());
    }

    private static void uploadFile() throws Exception {


        /**
         *
         * 提供一个Controller，包含如下
         *
         *
         public void upload(HttpServletRequest request) throws IOException {
         StandardMultipartHttpServletRequest r = (StandardMultipartHttpServletRequest) request;
         MultiValueMap<String, MultipartFile> map = r.getMultiFileMap();
         for (String key : map.keySet()) {
         MultipartFile file = r.getFile(key);
         System.out.println(file.getSize());

         System.out.println(file.getName());

         System.out.println(file.getContentType());

         System.out.println(file.getOriginalFilename());

         OutputStream out = new FileOutputStream(new File("D:/xx" + file.getOriginalFilename()));
         IOUtils.copy(file.getInputStream(), out);
         out.flush();
         out.close();
         }
         }
         *
         * **/

        OkHttpUtils okHttpUtils = new OkHttpUtils();
        String url = "http://localhost:8118/xdnphb/v1/iplist/file";

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "tom");

        File file = new File("D:/a.xlsx");

        System.out.println(file.getName());

        System.out.println("file.length() = " + file.length());

        String str = okHttpUtils.doPostReturnString(url, file);
        System.out.println(str);
    }

    private static void testUri() throws Exception {

        OkHttpUtils okHttpUtils = new OkHttpUtils();
        String url = "http://localhost:8118/xdnphb/v1/iplist/file";

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "tom");

        String uriFilePath = "http://newrank.oss-cn-hangzhou.aliyuncs.com/mainBanner/index/2017/05/05/42129e5a92bb405882637ac165a9f32c.jpg?uuid=1493980413017.4778";

        String str = okHttpUtils.doPostReturnString(url, null, uriFilePath);
        System.out.println(str);

    }

    private static void testP() throws Exception {

        OkHttpUtils okHttpUtils = new OkHttpUtils();

        String token = "SwYIzHBfsDA_OinaYOzb8ZeCUmu5-Ty0eHn38ILi3RIFx2Zsn5UDkL3pyM0d39NcvWKP_MMf-phvXns7WbzWv0SRrlg3U7zLvylJxxGe_SQjmwxMXIuD1hkfAS-YIcApMXQjAJABMF";
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+token;
        //{"expire_seconds": 604800, "action_name": "QR_SCENE", "action_info": {"scene": {"scene_id": 123}}}

        Map<String, Object> ddMap = new HashMap<String, Object>();
        ddMap.put("scene_id", 123);
        Map<String, Object> dMap = new HashMap<String, Object>();
        dMap.put("scene", ddMap);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("expire_seconds", 604800);
        map.put("action_name", "QR_SCENE");
        map.put("action_info", dMap);



        String str = okHttpUtils.doPostReturnString(url, JSONUtils.toJson(map));
        System.out.println(str);

    }

    public static void test() {

        OkHttpUtils okHttpUtils = new OkHttpUtils();

//        String url = "https://tinsight.newrank.cn/api/v1/message/ground/v1/hot?pagNumber=1&version=1";
        String url = "https://tinsight.newrank.cn/api/v1/message/ground/v1/hot?pagNumber=1&version=1";
//        String url = "https://tinsight.newrank.cn/api/v1/account/auth/test2?uuid=4de14a5dcfeb4d99b5e3806d4b35172a";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "21bb701a717e4fbab97922635f252113");

        Map<String, String> requestParam = null;
        IdentityHashMap<String, String> addHeaderMap = null;

       /* try {
           String a = okHttpUtils.doGetReturnString(url, requestParam, headerMap, addHeaderMap);
            System.out.println(a);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        okHttpUtils.doGetSync(url, requestParam, headerMap, null, OkHttpUtils.RETURN_SYNC_STRING, new Callback() {

            @Override
            public void onFail(Call call) {

            }

            @Override
            public void onSuccess(Call call, String errCode, String errMsg, String responseBodyString, InputStream responseBodyInputStream, byte[] responseBodyBytes) {
                System.out.println(errCode);
                System.out.println(responseBodyString);
            }
        });

        /*for (int i=0;i<1; i++) {
            new Thread( () -> {




            }).start();
        }*/
    }

    public static void main(String[] args) throws Exception {
        //uploadFile();
//        testDoPostAndRequestParam();
        //testUri();
//        testDoGetAndRequestParam();
//        testP();

//        test();


        OkHttpUtils okHttpUtils = new OkHttpUtils();

        String url = "https://mp.weixin.qq.com/s?__biz=MjAzNzMzNTkyMQ==&mid=2653768661&idx=2&sn=68c15d8a7bda061f29d499edc13efe4a&scene=0";
        String a = okHttpUtils.doGetReturnString(url);
        System.out.println(a);

    }
}
