package me.meczka;

import me.meczka.mongo.MongoConnection;
import me.meczka.utils.Authenticator;
import me.meczka.utils.CloudflareBypass;
import me.meczka.utils.FileManager;
import me.meczka.utils.WebUtils;
import me.meczka.webhook.WebhookUtils;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    private List<String> proxy;
    private List<String> links;
    final Logger LOGGER = Logger.getLogger("Monitor");
    private MongoConnection mongoConnection;
    private Task[] tasks;
    private int nextHttpClient = 0;
    public static void main( String[] args )
    {
        new App().start();
    }
    public App()
    {
        proxy = FileManager.readFileAsList(new File("proxy.txt"));
        links = FileManager.readFileAsList(new File("links.txt"));
        mongoConnection = new MongoConnection();
        tasks = new Task[proxy.size()];
        for(int i = 0; i < proxy.size(); i++)
        {
            tasks[i] = new Task(proxy.get(i),this,i);
        }
    }
    public void start()
    {
        while (true) {
            for (String link : links) {
                boolean found = false;
                while(!found) {
                    for (Task task : tasks) {
                        if (task.isAvailable()) {
                            task.monitorLink(link);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    public void monitorLink(String link, OkHttpClient httpClient)
    {
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
        resolveItems(items);
    }
    public synchronized void resolveItems(List<Item> items)
    {
        for(Item item : items)
        {
            String pid = item.getPid();
            Item databaseItem = mongoConnection.getItemByPid(pid);
            if(databaseItem==null)
            {
                WebhookUtils.newItemWebhook(item);
                mongoConnection.addNewItem(item.toJSON());
            }
            else if(!item.equals(databaseItem))
            {
                mongoConnection.updateItem(item);
            }
        }
    }
}

