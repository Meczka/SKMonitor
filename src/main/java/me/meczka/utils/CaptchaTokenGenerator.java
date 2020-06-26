package me.meczka.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CaptchaTokenGenerator {
    public static Map<String,String> awaitingCaptchas = new HashMap<>();
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS).build();
    private static final String API_KEY = "b321b79fc002bec545f183bbf3dc21cb";
    final static Logger LOGGER = Logger.getLogger("CaptchaTokenGenerator");
    final static String CAPTCHA_URL_IN = "https://2captcha.com/in.php", CAPTCHA_URL_RES="https://2captcha.com/res.php";
    public enum Status {
        GOOD,
        BAD
    }
    public synchronized static String request(String sitekey,String domain)
    {
        Map<String,String> params = new HashMap<>();
        params.put("key",API_KEY);
        params.put("method","userrecaptcha");
        params.put("googlekey",sitekey);
        params.put("pageurl",domain);
        params.put("json","1");
        String url = "https://2captcha.com/in.php"+ WebUtils.generateUrlWithParams(params);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            String id = jsonObject.getString("request");
            LOGGER.info("Starting request with id: " + id);
            awaitingCaptchas.put(id,null);
            return id;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public synchronized static String requestV3(String sitekey,String domain)
    {
        Map<String,String> params = new HashMap<>();
        params.put("key",API_KEY);
        params.put("method","userrecaptcha");
        params.put("version","v3");
        params.put("action","register");
        params.put("min_score","0.3");
        params.put("json","1");
        params.put("googlekey",sitekey);
        params.put("pageurl",domain);
        String url = "https://2captcha.com/in.php" + WebUtils.generateUrlWithParams(params);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            String id = jsonObject.getString("request");
            LOGGER.info("Starting request with id: " + id);
            awaitingCaptchas.put(id,null);
            return id;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
    public synchronized static String requestHCaptcha(String sitekey, String domain, String proxy)
    {
        Map<String,String> params = new HashMap<>();
        params.put("key",API_KEY);
        params.put("method","hcaptcha");
        params.put("sitekey",sitekey);
        params.put("pageurl",domain);
        params.put("json","1");
        params.put("proxy","ULr7vE:9KmoKb@"+proxy);
        params.put("proxytype","HTTP");
        String url = "https://2captcha.com/in.php" + WebUtils.generateUrlWithParams(params);
        String resp = WebUtils.sendGetRequest(url, WebUtils.getHeaders(),httpClient);
        JSONObject jsonObject = new JSONObject(resp);
        String id = jsonObject.getString("request");
        LOGGER.info("Starting request with id: " + id);
        awaitingCaptchas.put(id,null);
        return id;
    }
    public synchronized static String requestTextCaptcha(String base64Image)
    {
        Map<String,String> params = new HashMap<>();
        params.put("method","base64");
        params.put("key",API_KEY);
        params.put("body",base64Image);
        params.put("submit","Upload and get the ID");
        params.put("json","1");
        params.put("regsense","1");
        params.put("textinstructions","Please be case sensitive");
        String response = WebUtils.sendPostRequest(CAPTCHA_URL_IN, WebUtils.FormType.XWWWFORMURLENCODED, WebUtils.getHeaders(),params,httpClient);
        System.out.println(response);
        JSONObject jsonObject = new JSONObject(response);
        String id = jsonObject.getString("request");
        LOGGER.info("Starting request with id: " + id);
        awaitingCaptchas.put(id,null);
        return id;
    }
    public synchronized static void updateCaptchaTokens()
    {

        for(Map.Entry<String,String> entry : awaitingCaptchas.entrySet()) {
            if(entry.getValue()==null) {
                Map<String, String> params = new HashMap<>();
                params.put("key", API_KEY);
                params.put("action", "get");
                params.put("id", entry.getKey());
                String url = "https://2captcha.com/res.php"+ WebUtils.generateUrlWithParams(params);
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                try {
                    Response response = httpClient.newCall(request).execute();
                    String responseBody = response.body().string();
                    if(!responseBody.equalsIgnoreCase("CAPCHA_NOT_READY"))
                    {
                        awaitingCaptchas.replace(entry.getKey(),responseBody);

                    }
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }
    public synchronized static String getCaptchaToken(String id)
    {
        if(awaitingCaptchas.get(id)==null)
        {
            return "CAPTCHA_NOT_READY";
        }
        else
        {
            return awaitingCaptchas.get(id);
        }
    }
    public synchronized static void report(String id, Status status)
    {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("key", API_KEY);
            if (status == Status.GOOD) {
                params.put("action", "reportgood");
                LOGGER.info("REPORTING GOOD ID: " + id);
            } else {
                params.put("action", "reportbad");
                LOGGER.info("REPORTING BAD ID: " + id);
            }
            params.put("id", id);

            String url = CAPTCHA_URL_RES + WebUtils.generateUrlWithParams(params);
            System.out.println("URLLL: " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();
            System.out.println("REPORT RESPONSE: " + response.body().string());
        }catch (Exception e){e.printStackTrace();}
    }
}