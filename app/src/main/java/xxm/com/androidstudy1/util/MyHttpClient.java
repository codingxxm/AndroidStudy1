package xxm.com.androidstudy1.util;


import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyHttpClient {

    private static OkHttpClient client = null;

    private final static MyX509TrustManager trustManager = new MyX509TrustManager();

    private final static MyHostnameVerifier hostnameVerifier = new MyHostnameVerifier();

    private final static MediaType defaultMediatype = MediaType.get("application/json; charset=utf-8");

    private MyHttpClient() {

    }

    public static OkHttpClient getClient() {
        if(client==null){
            synchronized (MyHttpClient.class){
                if(client==null){
                    try {
                        final TrustManager[] trustAllCerts = new TrustManager[]{trustManager};
                        final SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                        final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                        client = new OkHttpClient.Builder()
                                .sslSocketFactory(sslSocketFactory,trustManager)
                                .hostnameVerifier(hostnameVerifier)
                                .connectTimeout(30,TimeUnit.SECONDS)
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return client;
    }

    public static ResponseBody post(String url, Map<String, String> params, MediaType mediaType){
        Request request = null;
        String paramsStr = "{}";
        if (params != null && !params.isEmpty()) {
            paramsStr = JSON.toJSONString(params);
        }
        RequestBody body = RequestBody.create(mediaType == null ? defaultMediatype : mediaType, paramsStr);
        request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try{
            Response response = client.newCall(request).execute();
            return response.body();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public static ResponseBody get(String url, Map<String, String> params){
        Request request = null;
        if (params != null && !params.isEmpty()) {
            HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            request = new Request.Builder()
                    .url(builder.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }
        try{
            Response response = client.newCall(request).execute();
            return response.body();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public static void asyncPost(String url, Map<String, String> params, MediaType mediaType, Callback callback) {
        Request request = null;
        String paramsStr = "{}";
        if (params != null && !params.isEmpty()) {
            paramsStr = JSON.toJSONString(params);
        }

        RequestBody body = RequestBody.create(mediaType == null ? defaultMediatype : mediaType, paramsStr);
        request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void asyncGet(String url, Map<String, String> params, Callback callback) {
        Request request = null;
        if (params != null && !params.isEmpty()) {
            HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            request = new Request.Builder()
                    .url(builder.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        client.newCall(request).enqueue(callback);
    }

    private static class MyX509TrustManager implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }

    private static class MyHostnameVerifier implements HostnameVerifier{
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        final Map<String, String> params = new HashMap<>();
        params.put("currentGroupId", "1");
        MyHttpClient.asyncGet(Const.GET_MEETING_LIST, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("asyncGet" + response.body().string());
            }
        });
        System.out.println("------------------------------------------------------");
        MyHttpClient.asyncPost(Const.GET_MEETING_LIST, params, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("asyncPost" + response.body().string());
            }
        });

        System.out.println("------------------------------------------------------");
        ResponseBody body1 = MyHttpClient.get(Const.GET_MEETING_LIST, params);
        System.out.println("get:"+body1.string());

        System.out.println("------------------------------------------------------");
        ResponseBody body2 = MyHttpClient.post(Const.GET_MEETING_LIST, params,null);
        System.out.println("post:"+body2.string());


    }
}

