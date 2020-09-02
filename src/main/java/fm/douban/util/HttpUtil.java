package fm.douban.util;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static Logger LOG = LoggerFactory.getLogger(HttpUtil.class);
    private static  OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(4, TimeUnit.MINUTES).build();

    @PostConstruct
    public void init() {
        LOG.info("okHttpClient init successful");
    }

    /**
     * 构建必要的http header 也许爬虫有用
     * @param referer
     * @param host
     * @return
     */
    public Map<String, String> buildHeaderData(String referer, String host) {
        Map<String, String> headers = new HashMap<>();

        // 比较通用的
        headers.put("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");

        // 不同的爬取目标，有不同的值
        if (referer != null) {
            headers.put("Referer", referer);
        }
        if (host != null) {
            headers.put("Host", host);
        }

        return headers;
    }

    /**
     * 根据输入的url,读取页面内容并返回
     * @param url
     * @param headers
     * @return
     */
    public String getContent(String url, Map<String, String> headers) {

        Builder reqBuilder = new Request.Builder().url(url);
        // 如果传入 http header ，则放入 Request 中
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                reqBuilder.addHeader(key, headers.get(key));
            }
        }

        Request request = reqBuilder.build();
        // 使用client去请求
        Call call = okHttpClient.newCall(request);
        // 返回结果字符串
        String result = null;
        try {
            // 获得返回结果
            LOG.error("request " + url + " begin . ");
            result = call.execute().body().string();
        } catch (IOException e) {
            LOG.error("request " + url + " exception . ", e);
        }
        return result;
    }

}
