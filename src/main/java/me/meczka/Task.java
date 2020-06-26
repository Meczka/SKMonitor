package me.meczka;

import me.meczka.utils.CloudflareBypass;
import me.meczka.utils.WebUtils;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Task {
    private OkHttpClient httpClient;
    private CookieManager cookieManager;
    private App app;
    final Logger LOGGER;
    private String proxy;
    private boolean isAvailable = true;
    public Task(String proxy, App app, int taskID)
    {
        LOGGER = Logger.getLogger("Task[" + taskID + "]");
        String[] spltted = proxy.split(":");
        cookieManager = new CookieManager();
        httpClient = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .readTimeout(15, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(spltted[0],Integer.parseInt(spltted[1]))))
                .callTimeout(15,TimeUnit.SECONDS)
                .connectTimeout(15,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        this.app = app;
        this.proxy = proxy;

    }
    public void monitorLink(String link)
    {
        isAvailable = false;
        new Thread(){
            @Override
            public void run() {
                try {
                    if (CloudflareBypass.checkForCloudflare(httpClient, "https://sklepkoszykarza.pl", "buty")) {
                        CloudflareBypass.solveJSChallenge("https://sklepkoszykarza.pl", cookieManager, proxy);
                    }
                    LOGGER.info("Monitoring: " + link);
                    String response = WebUtils.sendGetRequest(link, WebUtils.getHeaders(), httpClient);
                    Document document = Jsoup.parse(response);
                    Elements products = document.getElementsByAttribute("data-product");
                    LOGGER.info("FOUND: " + products.size() + " PRODUCTS");
                    List<Item> items = new ArrayList<>();
                    for(int i = 0; i < products.size(); i++)
                    {
                        Element product = products.get(i);
                        String imageUrl = product.getElementsByTag("img").first().attr("data-echo");
                        String productLink = product.getElementsByTag("a").first().attr("href");
                        String name = product.getElementsByTag("p").first().text();
                        String pid = product.attr("data-product");
                        String price = product.getElementsByClass("ps pricebox").first().text();

                        Elements sizesElements = product.getElementsByClass("sizes").first().getElementsByTag("li");
                        List<String> sizes = new ArrayList<>();
                        for(int j = 0; j < sizesElements.size(); j++)
                        {
                            sizes.add(sizesElements.get(j).text());
                        }
                        Item item = new Item(name,price,productLink,sizes,imageUrl,pid);
                        items.add(item);
                    }
                    app.resolveItems(items);
                }catch (Exception e){e.printStackTrace();}
                try {
                    Thread.sleep(500);
                }catch (Exception e){e.printStackTrace();}
                isAvailable = true;
            }
        }.start();
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
