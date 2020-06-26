package me.meczka.utils;

import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Set;

public class CloudflareBypass {
    static final Logger LOGGER = Logger.getLogger("ClouflareBypass");
    public synchronized  static void solveJSChallenge(String url, CookieManager cookieManager, String proxy)
    {
        long startingTime = System.currentTimeMillis();
        try {
            System.setProperty("webdriver.chrome.driver", "chromedriver");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--proxy-server="+proxy,"--no-sandbox","--headless");

            ChromeDriver driver = new ChromeDriver(chromeOptions);
            driver.get(url);
            while (true) {
                System.out.println(driver.getTitle());
                if(System.currentTimeMillis()>startingTime+60*1000*3)
                {
                    driver.quit();
                    break;
                }
                if (driver.getTitle().contains("Attention Required")) {
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    //String sitekey = Finder.findSitekey(driver.getPageSource());
                   // LOGGER.info("SITEKEY: " + sitekey);
                    String token = CaptchaUtils.solveCaptcha("f9630567-8bfa-4fc9-8ee5-9c91c6276dff", driver.getCurrentUrl(), CaptchaUtils.HCAPTCHA,proxy);
                    LOGGER.info(token);
                    executor.executeScript("" +
                            "var eles = [];\n" +
                            "var inputs = document.getElementsByTagName(\"textarea\");\n" +
                            "for(var i = 0; i < inputs.length; i++) {\n" +
                            "    if(inputs[i].name.startsWith(\"h-captcha-response\")) {\n" +
                            "        eles.push(inputs[i]);\n" +
                            "    }\n" +
                            "}" +
                            "" +
                            "" +
                            "" +
                            "eles[0].innerHTML=\"" + token + "\";");
                    executor.executeScript("" +
                            "var eles = [];\n" +
                            "var inputs = document.getElementsByTagName(\"textarea\");\n" +
                            "for(var i = 0; i < inputs.length; i++) {\n" +
                            "    if(inputs[i].name.startsWith(\"g-recaptcha-response\")) {\n" +
                            "        eles.push(inputs[i]);\n" +
                            "    }\n" +
                            "}" +
                            "" +
                            "" +
                            "" +
                            "eles[0].innerHTML=\"" + token + "\";");
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    executor.executeScript("document.getElementById(\"challenge-form\").submit();");
                } else if (hasCookie(driver, "cf_clearance")) {
                    cookieManager.getCookieStore().removeAll();
                    Cookie from = getCookieByName(driver, "cf_clearance");
                    LOGGER.info("SOLVED: " + from.getValue());
                    HttpCookie to = new HttpCookie(from.getName(), from.getValue());
                    to.setPath("/");
                    to.setVersion(0);
                    Cookie cfduidOrg = getCookieByName(driver, "__cfduid");
                    System.out.println("DOMAIN: " + cfduidOrg.getDomain());
                    System.out.println("PATH: " +cfduidOrg.getPath() );
                    HttpCookie cfduidCpy = new HttpCookie(cfduidOrg.getName(), cfduidOrg.getValue());
                    cfduidCpy.setPath("/");
                    cfduidCpy.setVersion(0);
                    try {
                        cookieManager.getCookieStore().add(new URI(url), to);
                        cookieManager.getCookieStore().add(new URI(url), cfduidCpy);
                        driver.quit();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    public static boolean checkForCloudflare(OkHttpClient httpClient, String url, String keyword)
    {
        if(WebUtils.sendGetRequest(url,WebUtils.getHeaders(),httpClient).toLowerCase().contains(keyword.toLowerCase()))
        {
            return false;
        }
        return true;
    }
    private static boolean hasCookie(WebDriver driver, String cookieName)
    {
        Set<Cookie> cookies = driver.manage().getCookies();
        for(Cookie cookie : cookies)
        {
            if(cookie.getName().equalsIgnoreCase(cookieName))
            {
                return true;
            }
        }
        return false;
    }
    private static Cookie getCookieByName(WebDriver driver, String cookieName)
    {
        Set<Cookie> cookies = driver.manage().getCookies();
        for(Cookie cookie : cookies)
        {
            if(cookie.getName().equalsIgnoreCase(cookieName))
            {
                return cookie;
            }
        }
        return null;
    }
    /*public static void solveJSChallangeRequest()
    {
        CookieManager cookieManager = new CookieManager();
        OkHttpClient httpClient = new OkHttpClient.Builder()
            //    .cookieJar(new JavaNetCookieJar(cookieManager))
                //.proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("195.66.120.50",8000)))
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("localhost",8888)))
                .addNetworkInterceptor(new HeaderFixInterceptor())
                //.proxyAuthenticator(new Authenticator("U5oAtZ","RFvVYr"))
                .build();

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0"));
        headers.add(new Header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/*;q=0.8"));
        headers.add(new Header("accept-language","pl,en-US;q=0.7,en;q=0.3"));
        headers.add(new Header("accept-encoding","gzip, deflate, br"));
        headers.add(new Header("upgrade-insecure-requests","1"));
        headers.add(new Header("te","trailers"));


        String response = (String) RequestSender.sendGetRequest("https://chmielna20.pl",headers,httpClient, RequestSender.ResponseType.STRING);

        String cRay = Finder.extractFromHtml(response,"cRay");
        String cHash = Finder.extractFromHtml(response,"cHash");
        LOGGER.info("CRAY: " + cRay);
        LOGGER.info("CHASH: " + cHash);
        RequestSender.sendGetRequest("https://chmielna20.pl/cdn-cgi/images/trace/jschal/js/nocookie/transparent.gif?ray="+cRay,headers,httpClient, RequestSender.ResponseType.STRING);
        JSONObject jsonObject = new JSONObject();
        JSONObject chlog = new JSONObject();
        chlog.put("c",0);
        chlog.put("1",new JSONObject().put("start",System.currentTimeMillis()));
        jsonObject.put("chLog",chlog);
        jsonObject.put("chReq","non-interactive");
        jsonObject.put("chC",0);
        jsonObject.put("chCAS",0);
        jsonObject.put("cvId","1");
        jsonObject.put("cHash",cHash);
        jsonObject.put("oV",1);
        JSONObject cRq = new JSONObject();
        cRq.put("d",Finder.extractFromHtml(response," d:"));
        cRq.put("t",Finder.extractFromHtml(response," t:"));
        cRq.put("m",Finder.extractFromHtml(response," m:"));
        cRq.put("i1",Finder.extractFromHtml(response," i1:"));
        cRq.put("i2",Finder.extractFromHtml(response," i2:"));
        jsonObject.put("cRq",cRq);
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        headers = new ArrayList<>();
        headers.add(new Header("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0"));
        headers.add(new Header("accept",""));
        headers.add(new Header("accept-language","pl,en-US;q=0.7,en;q=0.3"));
        headers.add(new Header("accept-encoding","gzip"));
        headers.add(new Header("content-type","application/x-www-form-urlencoded"));
        headers.add(new Header("cf-challenge",cHash));
        headers.add(new Header("origin","https://chmielna20.pl"));
        headers.add(new Header("referer","https://chmielna20.pl/"));
        headers.add(new Header("te","trailers"));
        headers.add(new Header("cookie","cf_chl_1="+cRay));
        try {
            String cf_chl_opt = response.substring(response.indexOf("window._cf_chl_opt"),response.indexOf("window._cf_chl_enter"));
            engine.eval("var window = []");
            engine.eval(cf_chl_opt);
            engine.eval(Constants.LZString);
            engine.eval(Constants.generate);
            engine.eval(Constants.deobf);
            Object n = engine.get("m");
            Invocable inv = (Invocable) engine;

            String payload = (String) inv.invokeFunction("generate");
            List<Param> params = new ArrayList<>();
            params.add(new Param("v_"+cRay,payload));
            String postBody = "v_"+cRay+"=" + payload;
            HttpCookie cookie = new HttpCookie("cf_chl_1",cRay);
            cookie.setPath("/");
            cookie.setVersion(0);
            cookieManager.getCookieStore().add(new URI("https://chmielna20.pl"),cookie);
            Response responseObj = (Response) RequestSender.sendPostRequest("https://chmielna20.pl/cdn-cgi/challenge-platform/generate/ov1/"+cRay+"/" +cHash, RequestSender.PostType.STRINGURLENCODED,
                    headers,postBody,httpClient, RequestSender.ResponseType.RESPONSEOBJ);
            response = GZIPEncoding.decode(responseObj.body().byteStream());
            System.out.println(response);
            String deobfuscated = (String) inv.invokeFunction("deobf",response);
            System.out.println("DEOBF: " + deobfuscated);
           // response = WebUtils.sendPostRequest("https://chmielna20.pl/cdn-cgi/challenge-platform/generate/ov1/"+cRay+"/" +cHash, WebUtils.FormType.XWWWFORMURLENCODED,
           //         headers,params,httpClient);

        }catch (Exception e){e.printStackTrace();}

    }*/
}
