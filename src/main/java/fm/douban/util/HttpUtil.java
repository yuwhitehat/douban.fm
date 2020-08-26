package fm.douban.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
    /**
     * 构建必要的http header 也许爬虫有用
     * @param referer
     * @param host
     * @return
     */
    public Map<String, String> buildHeaderData(String referer, String host) {
        Map<String, String> data = new HashMap<>();
        data.put(referer,"https://fm.douban.com/");
        data.put(host, "fm.douban.com");
        return data;
    }

    /**
     * 根据输入的url,读取页面内容并返回
     * @param url
     * @param headers
     * @return
     */
    public String getContent(String url, Map<String, String> headers) {
        headers = buildHeaderData("Referer","Host");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Referer",headers.get("Referer"))
                .addHeader("Host",headers.get("Host"))
                .build();

        String result = null;
        try {
            // 执行请求
            Response response = okHttpClient.newCall(request).execute();
            // 获取响应内容
            result = response.body().string();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return result;
    }

}
