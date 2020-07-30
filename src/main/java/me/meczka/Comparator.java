package me.meczka;

import me.meczka.utils.WebUtils;
import me.meczka.webhook.WebhookUtils;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Comparator {
    static final Logger LOGGER = Logger.getLogger("Comparator");
    public static boolean compare(Item webItem, Item databaseItem, OkHttpClient httpClient)
    {
        if(!WebhookUtils.serializeList(webItem.getSizes()).equals(WebhookUtils.serializeList(databaseItem.getSizes())))
        {
            LOGGER.info("CHECKING PRODUCT");
            List<String> sizes = new ArrayList<>();
            String response = WebUtils.sendGetRequest(webItem.getItemLink(),WebUtils.getHeaders(),httpClient);
            Document document = Jsoup.parse(response);
            Elements spans = document.getElementsByClass("size_box");
            for(Element span : spans)
            {
                sizes.add(span.text());
            }
            if(webItem.getSizes().size()==sizes.size())
            {
                if(webItem.getSizes().size()>databaseItem.getSizes().size())
                {
                    List<String> newSizes = findNewSizes(sizes,databaseItem.getSizes());
                    int actualNewSizes = 0;
                    for(String newSize : newSizes)
                    {
                        if(!App.recentlyRestocked.containsKey(databaseItem.getPid() + newSize) ||
                                App.recentlyRestocked.get(databaseItem.getPid() + newSize) + 1000 * 60 * 60 < System.currentTimeMillis())
                        {
                            actualNewSizes++;
                        }
                    }
                    if(actualNewSizes!=0) {
                        WebhookUtils.restockWebhook(webItem, databaseItem);
                        for(String newSize : newSizes)
                        {
                            App.recentlyRestocked.put(databaseItem.getPid() + newSize,System.currentTimeMillis());
                        }
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }
    private static List<String> findNewSizes(List<String> webSizes, List<String> databaseSizes)
    {
        List<String> retVal = new ArrayList<>();
        for(String webSize : webSizes)
        {
            boolean found = false;
            for(String databaseSize : databaseSizes)
            {
                if(webSize.equalsIgnoreCase(databaseSize))
                {
                    found = true;
                }

                if(!found)
                {
                    retVal.add(webSize);
                }
            }
        }
        return retVal;
    }
}
