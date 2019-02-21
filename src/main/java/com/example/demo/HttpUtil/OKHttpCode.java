package com.example.demo.HttpUtil;

public class OKHttpCode {

    public static final String ERR_CODE = "errCode";
    public static final String ERR_MSG = "errMsg";

    //默认状态码
    public static final String DEFAULT_CODE = "0";

    //同步请求，初始化Response异常
    public static final String INIT_RESPONSE_CODE = "-10000";

    //异步请求，onResponse异常
    public static final String ON_RESPONSE_CODE = "-10001";

    //上传文件组装File异常
    public static final String FILE_REQUEST_CODE = "-10002";

    //从ResponseBody里去返回数据时异常
    public static final String GET_DATA_RESPONSEBODY = "-10003";

    // 通过URL方式 初始化 MultipartBody 异常
    public static final String INIT_MULTIPARTBODY_BY_URL = "-10004";

}
