package xxm.com.androidstudy1.util;

import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.io.OutputStreamWriter;  
import java.net.HttpURLConnection;  
import java.net.InetSocketAddress;  
import java.net.Proxy;  
import java.net.URL;  
import java.net.URLConnection;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
import java.util.HashMap;
import java.util.Iterator;  
import java.util.Map;  
  



import javax.net.ssl.HostnameVerifier;  
import javax.net.ssl.HttpsURLConnection;  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.SSLSession;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  
  



import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
  
public class HttpRequestor {  
    private String charset = "utf-8";
    private String contentType = "application/x-www-form-urlencoded";  
  
    private String cookie = "";  
  
    private String userAgent = "";  
    private String Referer = "";  
    private Integer connectTimeout = null;  
    private Integer socketTimeout = null;  
    private String proxyHost = "";  
    private Integer proxyPort;  
    private String encode = "UTF-8";  
    private boolean ssl = false;  
  
    public int repeats = 3; // request重复次数  
    public int delay = 2000; // request请求失败后的延时时间  
    public int timeout = 30000; // request请求超时时间  
  
    public HttpRequestor() {  
  
    }  
  
    private static class TrustAnyTrustManager implements X509TrustManager {  
  
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
        }  
  
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
        }  
  
        public X509Certificate[] getAcceptedIssuers() {  
            return new X509Certificate[] {};  
        }  
    }  
  
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {  
        public boolean verify(String hostname, SSLSession session) {  
            return true;  
        }  
    }  
  
    private HttpURLConnection CreateConnection(String url, String method) throws Exception {  
        URL localURL = new URL(url);  
        URLConnection connection = openConnection(localURL);  
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;  
  
        if (localURL.getProtocol().equals("https")) {  
            SSLContext sc = SSLContext.getInstance("SSL");  
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());  
  
            httpURLConnection = (HttpsURLConnection) connection;  
  
            ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sc.getSocketFactory());  
            ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(new TrustAnyHostnameVerifier());  
        }  
  
        httpURLConnection.setConnectTimeout(this.timeout);  
        httpURLConnection.setReadTimeout(this.timeout);  
  
        if (charset != "")  
            httpURLConnection.setRequestProperty("Accept-Charset", charset);  
  
        if (cookie != ""){  
            httpURLConnection.setRequestProperty("Cookie", cookie);  
              
        }  
  
        if (userAgent != "")  
            httpURLConnection.setRequestProperty("User-Agent", userAgent);  
  
        if (Referer != "")  
            httpURLConnection.setRequestProperty("Referer", Referer);  
  
        if (method.equals("POST") || method.equals("UPLOAD")) {  
            httpURLConnection.setDoOutput(true);  
            httpURLConnection.setRequestMethod("POST");  
  
            if (contentType != "") {  
                if (method.equals("POST"))  
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
                else  
                    httpURLConnection.setRequestProperty("Content-Type",  
                            "multipart/form-data; boundary=---------------------------2525779185408");  
            }  
        }  
  
        return httpURLConnection;  
    }  
  
    private void CheckResponseCode(HttpURLConnection httpURLConnection) throws Exception {  
        if (httpURLConnection.getResponseCode() == 404) {
            System.out.println("请求连接不存在,退出请求！URL:" + httpURLConnection.getURL());
        }
  
        if (httpURLConnection.getResponseCode() >= 300) {  
            throw new IOException();  
        }  
    }  
  
    /** 
     * Do GET request 
     *  
     * @param url 
     * @return 
     * @throws Exception 
     * @throws IOException 
     */  
    public HttpURLConnection doGetResponse(String url) throws Exception {  
        HttpURLConnection httpURLConnection = this.CreateConnection(url, "GET");  
  
        int repeat = 0;  
        while (repeat < this.repeats) {  
            try {  
                this.CheckResponseCode(httpURLConnection);  
                break;  
            } catch (IOException exception) {
                System.out.println("【" + repeat + "】请求连接发生HTTP错误,尝试重新请求！错误码:" + httpURLConnection.getResponseCode() + "URL:"
                        + url);
                repeat++;
                Thread.sleep(this.delay);  
            } catch (Exception ex) {  
                repeat++;  
                ex.printStackTrace();  
            } finally {  
            }  
        }  
        return httpURLConnection;  
    }  
  
    /** 
     * Do GET request 
     *  
     * @param url 
     * @return 
     * @throws Exception 
     * @throws IOException 
     */  
    public String doGet(String url) throws Exception {  
        HttpURLConnection httpURLConnection = this.doGetResponse(url);  
  
        if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() < 300) {  
            InputStream inputStream = httpURLConnection.getInputStream();  
            String result = IOUtils.toString(inputStream, this.encode);  
            inputStream.close();  
            return result;  
        }  
        return "";  
    }  
  
    /** 
     * Do POST request(FORM) 
     *  
     * @param url 
     * @param parameterMap 
     * @return 
     * @throws Exception 
     */  
    @SuppressWarnings("rawtypes")  
    public HttpURLConnection doPostResponse(String url, Map parameterMap) throws Exception {  
        StringBuffer parameterBuffer = new StringBuffer();  
        if (parameterMap != null) {  
            Iterator iterator = parameterMap.keySet().iterator();  
            String key = null;  
            String value = null;  
            while (iterator.hasNext()) {  
                key = (String) iterator.next();  
                if (parameterMap.get(key) != null) {  
                    value = (String) parameterMap.get(key);  
                } else {  
                    value = "";  
                }  
  
                parameterBuffer.append(key).append("=").append(value);  
                if (iterator.hasNext()) {  
                    parameterBuffer.append("&");  
                }  
            }  
        }  
  
        HttpURLConnection httpURLConnection = this.CreateConnection(url, "POST");  
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterBuffer.length()));  
  
        OutputStream outputStream = null;  
        OutputStreamWriter outputStreamWriter = null;  
  
        int repeat = 0;  
        while (repeat < this.repeats) {  
            try {  
                outputStream = httpURLConnection.getOutputStream();  
                outputStreamWriter = new OutputStreamWriter(outputStream, charset);  
  
                outputStreamWriter.write(parameterBuffer.toString());  
                outputStreamWriter.flush();  
  
                this.CheckResponseCode(httpURLConnection);  
                break;  
            } catch (IOException exception) {
                System.out.println("【" + repeat + "】请求连接发生HTTP错误,尝试重新请求！错误码:" + httpURLConnection.getResponseCode() + "URL:"
                        + url);

                repeat++;  
                Thread.sleep(this.delay);  
            } catch (Exception ex) {  
                repeat++;  
                ex.printStackTrace();  
            } finally {  
                if (outputStreamWriter != null) {  
                    outputStreamWriter.close();  
                }  
  
                if (outputStream != null) {  
                    outputStream.close();  
                }  
  
            }  
        }  
        return httpURLConnection;  
    }  
  
    @SuppressWarnings("rawtypes")  
    public String doPost(String url, Map parameterMap) throws Exception {  
        HttpURLConnection httpURLConnection = this.doPostResponse(url, parameterMap);  
        if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() < 300) {  
            InputStream inputStream = httpURLConnection.getInputStream();  
            String result = IOUtils.toString(inputStream, this.encode);  
            inputStream.close();  
            return result;  
        }  
        return "";  
    }  
  
    /** 
     * Do POST request(JSON) 
     *  
     * @param url 
     * @param jsonDatas 
     * @return 
     * @throws Exception 
     */  
    public HttpURLConnection doPostResponse(String url, JSONObject jsonDatas) throws Exception {  
        if (jsonDatas.get("cookie") != null)  
            this.cookie = jsonDatas.getString("cookie");  
        HttpURLConnection httpURLConnection = this.CreateConnection(url, "POST");  
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(jsonDatas.toString().length()));  
  
        OutputStream outputStream = null;  
        OutputStreamWriter outputStreamWriter = null;  
  
        int repeat = 0;  
        while (repeat < this.repeats) {  
            try {  
                outputStream = httpURLConnection.getOutputStream();  
                outputStreamWriter = new OutputStreamWriter(outputStream, charset);  
  
                outputStreamWriter.write(jsonDatas.toString());  
                outputStreamWriter.flush();  
  
                this.CheckResponseCode(httpURLConnection);  
                break;  
            } catch (IOException exception) {
                System.out.println("【" + repeat + "】请求连接发生HTTP错误,尝试重新请求！错误码:" + httpURLConnection.getResponseCode() + "URL:"
                        + url);
                repeat++;
                Thread.sleep(this.delay);  
            } catch (Exception ex) {  
                repeat++;  
                ex.printStackTrace();  
            } finally {  
                if (outputStreamWriter != null) {  
                    outputStreamWriter.close();  
                }  
  
                if (outputStream != null) {  
                    outputStream.close();  
                }  
            }  
        }  
        return httpURLConnection;  
    }  
  
    public String doPost(String url, JSONObject jsonDatas) throws Exception {  
        HttpURLConnection httpURLConnection = this.doPostResponse(url, jsonDatas);  
        if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() < 300) {  
            InputStream inputStream = httpURLConnection.getInputStream();  
            String result = IOUtils.toString(inputStream, this.encode);  
            inputStream.close();  
            return result;  
        }  
        return "";  
    }  
  
    public String doPostSSL(String url, JSONObject jsonDatas) throws Exception {  
        URL localURL = new URL(url);  
  
        SSLContext sc = SSLContext.getInstance("SSL");  
        sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());  
  
        URLConnection connection = openConnection(localURL);  
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;  
  
        httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());  
        httpsURLConnection.setDoOutput(true);  
        httpsURLConnection.setRequestMethod("POST");  
        httpsURLConnection.setConnectTimeout(this.timeout);  
        httpsURLConnection.setReadTimeout(this.timeout);  
        httpsURLConnection.setHostnameVerifier(new TrustAnyHostnameVerifier());  
  
        if (charset != "")  
            httpsURLConnection.setRequestProperty("Accept-Charset", charset);  
  
        if (contentType != "")  
            httpsURLConnection.setRequestProperty("Content-Type", "application/json");  
  
        if (cookie != "")  
            httpsURLConnection.setRequestProperty("Cookie", cookie);  
  
        if (userAgent != "")  
            httpsURLConnection.setRequestProperty("User-Agent", userAgent);  
  
        if (Referer != "")  
            httpsURLConnection.setRequestProperty("Referer", Referer);  
  
        httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(jsonDatas.toString().length()));  
  
        OutputStream outputStream = null;  
        OutputStreamWriter outputStreamWriter = null;  
        InputStream inputStream = null;  
        String result = "";  
  
        int repeat = 0;  
        while (repeat < this.repeats) {  
            try {  
                outputStream = httpsURLConnection.getOutputStream();  
                outputStreamWriter = new OutputStreamWriter(outputStream, charset);  
  
                outputStreamWriter.write(jsonDatas.toString());  
                outputStreamWriter.flush();  
                if (httpsURLConnection.getResponseCode() == 404) {
                    System.out.println("请求连接不存在,退出请求！URL:" + localURL);
                    break;
                }  
  
                if (httpsURLConnection.getResponseCode() >= 300) {  
                    throw new IOException();  
                }  
  
                inputStream = httpsURLConnection.getInputStream();  
                result = IOUtils.toString(inputStream, this.encode);  
                if (StringUtils.isEmpty(result))
                    repeat++;  
                break;  
            } catch (IOException exception) {
                System.out.println("【" + repeat + "】请求连接发生HTTP错误,尝试重新请求！错误码:" + httpsURLConnection.getResponseCode() + "URL:"
                        + localURL);
                repeat++;
                Thread.sleep(this.delay);  
            } catch (Exception ex) {  
                repeat++;  
                ex.printStackTrace();  
            } finally {  
                if (outputStreamWriter != null) {  
                    outputStreamWriter.close();  
                }  
  
                if (outputStream != null) {  
                    outputStream.close();  
                }  
  
                if (inputStream != null) {  
                    inputStream.close();  
                }  
            }  
        }  
        return result;  
    }  
  
    @SuppressWarnings("rawtypes")  
    public String doUpload(String url, Map parameterMap, String fileName) throws Exception {  
        // StringBuffer parameterBuffer = new StringBuffer();  
        ByteArrayOutputStream parameterBuffer = new ByteArrayOutputStream();  
  
        if (parameterMap != null) {  
            Iterator iterator = parameterMap.keySet().iterator();  
  
            while (iterator.hasNext()) {  
                parameterBuffer.write("-----------------------------2525779185408\r\n".getBytes());  
                String key = null;  
                String value = null;  
                key = (String) iterator.next();  
  
                if (iterator.hasNext()) {  
  
                    if (parameterMap.get(key) != null) {  
                        value = (String) parameterMap.get(key);  
                    } else {  
                        value = "";  
                    }  
  
                    parameterBuffer.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());  
                    parameterBuffer.write((value + "\r\n").getBytes());  
                } else {  
                    byte[] value1 = null;  
                    if (parameterMap.get(key) != null) {  
                        value1 = (byte[]) parameterMap.get(key);  
                    } else {  
                        value1 = new byte[0];  
                    }  
                    parameterBuffer.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\""  
                            + fileName + "\"\r\n\r\n").getBytes());  
                    parameterBuffer.write(value1);  
                    parameterBuffer.write("\r\n".getBytes());  
                    parameterBuffer.write(("-----------------------------2525779185408--").getBytes());  
                }  
            }  
        }  
  
        parameterBuffer.close();  
        HttpURLConnection httpURLConnection = this.CreateConnection(url, "UPLOAD");  
  
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterBuffer.size()));  
  
        OutputStream outputStream = null;  
        OutputStreamWriter outputStreamWriter = null;  
        InputStream inputStream = null;  
        String result = "";  
  
        int repeat = 0;  
        while (repeat < this.repeats) {  
            try {  
                outputStream = httpURLConnection.getOutputStream();  
                // outputStreamWriter = new OutputStreamWriter(outputStream);  
  
                // outputStreamWriter.write(parameterBuffer.toString());  
                outputStream.write(parameterBuffer.toByteArray());  
                outputStream.flush();  
  
                if (httpURLConnection.getResponseCode() == 404) {
                    System.out.println("请求连接不存在,退出请求！URL:" + url);
                    break;
                }  
  
                if (httpURLConnection.getResponseCode() >= 300) {  
                    throw new IOException();  
                }  
  
                inputStream = httpURLConnection.getInputStream();  
                result = IOUtils.toString(inputStream, this.encode);  
                if (StringUtils.isNotEmpty(result))  
                    return result;  
  
            } catch (IOException exception) {
                System.out.println("【" + repeat + "】请求连接发生HTTP错误,尝试重新请求！错误码:" + httpURLConnection.getResponseCode() + "URL:"
                        + url);

                exception.printStackTrace();  
                repeat++;  
                Thread.sleep(this.delay);  
            } catch (Exception ex) {  
                repeat++;  
                ex.printStackTrace();  
            } finally {  
  
                if (outputStreamWriter != null) {  
                    outputStreamWriter.close();  
                }  
  
                if (outputStream != null) {  
                    outputStream.close();  
                }  
  
                if (inputStream != null) {  
                    inputStream.close();  
                }  
  
            }  
        }  
        return result;  
    }  
  
    private URLConnection openConnection(URL localURL) throws IOException {  
        URLConnection connection;  
        if (proxyHost != null && proxyPort != null) {  
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));  
            connection = localURL.openConnection(proxy);  
        } else {  
            connection = localURL.openConnection();  
        }  
        return connection;  
    }  
  
    /** 
     * Render request according setting 
     *  
     * @param request 
     */  
    @SuppressWarnings("unused")  
    private void renderRequest(URLConnection connection) {  
  
        if (connectTimeout != null) {  
            connection.setConnectTimeout(connectTimeout);  
        }  
  
        if (socketTimeout != null) {  
            connection.setReadTimeout(socketTimeout);  
        }  
  
    }  
  
    /* 
     * Getter & Setter 
     */  
    public Integer getConnectTimeout() {  
        return connectTimeout;  
    }  
  
    public void setConnectTimeout(Integer connectTimeout) {  
        this.connectTimeout = connectTimeout;  
    }  
  
    public Integer getSocketTimeout() {  
        return socketTimeout;  
    }  
  
    public void setSocketTimeout(Integer socketTimeout) {  
        this.socketTimeout = socketTimeout;  
    }  
  
    public String getProxyHost() {  
        return proxyHost;  
    }  
  
    public void setProxyHost(String proxyHost) {  
        this.proxyHost = proxyHost;  
    }  
  
    public Integer getProxyPort() {  
        return proxyPort;  
    }  
  
    public void setProxyPort(Integer proxyPort) {  
        this.proxyPort = proxyPort;  
    }  
  
    public String getReferer() {  
        return Referer;  
    }  
  
    public void setReferer(String Referer) {  
        this.Referer = Referer;  
    }  
  
    public String getCharset() {  
        return charset;  
    }  
  
    public void setCharset(String charset) {  
        if (charset.isEmpty())  
            charset = "UTF-8";  
        this.charset = charset;  
    }  
  
    public String getCookie() {  
        return cookie;  
    }  
  
    public void setCookie(String cookie) {  
        this.cookie = cookie;  
    }  
  
    public String getUserAgent() {  
        return cookie;  
    }  
  
    public void setUserAgent(String userAgent) {  
        this.userAgent = userAgent;  
    }  
  
    public String getContentType() {  
        return contentType;  
    }  
  
    public void setContentType(String contentType) {  
        this.contentType = contentType;  
    }  
  
    public String getEncode() {  
        return encode;  
    }  
  
    public void setEncode(String encode) {  
        this.encode = encode;  
    }  
  
    public boolean getSSL() {  
        return this.ssl;  
    }  
  
    public void setSSL(boolean ssl) {  
        this.ssl = ssl;  
    }
    
}