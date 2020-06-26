package me.meczka.utils;

import okhttp3.*;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebUtils {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/83.0.4103.116 Safari/537.36";
    public enum FormType {
        XWWWFORMURLENCODED
    }
    public enum ReturnType{
        STRING,
        RESPONSE_OBJECT
    }
    public static Map<String,String> getHeaders()
    {
        return new HashMap<String, String>()
        {{
            put("User-Agent",USER_AGENT);
            put("Cache-Control","no-cache");
            put("Connection","keep-alive");
        }};
    }
    public static  Map<String,String> postFormHeaders()
    {
        return new HashMap<String, String>(){{
            put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,;q=0.8,application/signed-exchange;v=b3");
            put("Accept-Language","pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7");
            put("Content-Type","application/x-www-form-urlencoded");
            put("User-Agent",USER_AGENT);
            put("Cache-Control","max-age=0");
            put("Connection","keep-alive");
        }};
    }
    public static String cookieMapToString(Map<String,String> cookies)
    {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> cookie : cookies.entrySet())
        {
            sb.append(cookie.getKey()+"="+cookie.getValue()+";");
        }
        String retVal = sb.toString();
        retVal = retVal.substring(0,retVal.length()-1);
        return retVal;
    }
    public static String extractCookieValueByName(String name, List<HttpCookie> httpCookies)
    {
        for(int i = 0; i < httpCookies.size(); i++)
        {
            HttpCookie cookie = httpCookies.get(i);
            if(name.equalsIgnoreCase(cookie.getName()))
            {
                return cookie.getValue();
            }
        }
        return null;
    }
    public static String addParamsToUrl(String url, Map<String,String> params)
    {
        StringBuilder sb = new StringBuilder(url + "?");
        for(Map.Entry<String,String> entry : params.entrySet())
        {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        String retVal = sb.toString();
        return retVal.substring(0,retVal.length()-1);
    }
    public static String removeParamsFromUrl(String url)
    {
        int index = url.indexOf("?");
        url=url.substring(0,index);
        return url;
    }
    public static String sendGetRequest(String url, Map<String,String> headers, OkHttpClient httpClient)
    {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();
        addHeaders(headers,requestBuilder);
        Request request = requestBuilder.build();
        while(true) {
            try {
                Response response = httpClient.newCall(request).execute();

                if(response.code()==500||response.code()==504)
                {
                    System.out.println("CODE "+response.code()+" " + url);
                    continue;
                }
                return response.body().string();
            } catch (Exception e) {
             //   System.out.println(e.getMessage());
            //    System.out.println("RETRYING: " + url);
            }
        }
    }
    public static String sendJsonPost(String url, Map<String,String> headers, JSONObject payload, OkHttpClient httpClient)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),payload.toString());
        Request.Builder requestBuilder = new Request.Builder()
                .post(requestBody)
                .url(url);
        addHeaders(headers,requestBuilder);
        Request request = requestBuilder.build();
        try{
            Response response = httpClient.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
    public static String sendPostRequest(String url, FormType formType,Map<String,String> headers, Map<String,String> formParams, OkHttpClient httpClient)
    {
       return (String) sendPostRequest(url,formType,headers,formParams,httpClient, ReturnType.STRING);
    }
    public static String generateUrlWithParams(Map<String,String> params)
    {
        StringBuilder sb = new StringBuilder("?");
        for(Map.Entry<String,String> entry : params.entrySet())
        {
            sb.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        String retVal = sb.toString();
        retVal = retVal.substring(0,retVal.length()-1);
        return retVal;
    }
    public static Object sendPostRequest(String url, FormType formType,Map<String,String> headers, Map<String,String> formParams, OkHttpClient httpClient, ReturnType returnType)
    {
        RequestBody requestBody;
        switch (formType)
        {
            case XWWWFORMURLENCODED: {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, String> param : formParams.entrySet()) {
                    builder.add(param.getKey(), param.getValue());
                }
                requestBody = builder.build();

                Request.Builder requestBuilder = new Request.Builder()
                        .post(requestBody)
                        .url(url);
                addHeaders(headers, requestBuilder);
                Request request = requestBuilder.build();
                while (true) {
                    try {
                        Response response = httpClient.newCall(request).execute();
                        if(response.code()==500||response.code()==50||response.code()==502)
                        {
                            System.out.println("CODE "+response.code()+" " + url);
                            continue;
                        }
                        if (returnType == ReturnType.STRING) {
                            return response.body().string();
                        }
                        if (returnType == ReturnType.RESPONSE_OBJECT) {
                            return response;
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("RETRYING: "+ url);
                    }

                }
            }
        }
        return null;
    }
    private static void addHeaders(Map<String,String> headers,Request.Builder requestBuilder)
    {
        for(Map.Entry<String,String> header : headers.entrySet())
        {
            requestBuilder.header(header.getKey(),header.getValue());
        }

    }
}
