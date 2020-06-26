package me.meczka.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class Finder {
    public static String findSitekey(String page)
    {
        int index = page.indexOf("sitekey");
        page=page.substring(index);
        page=extractStringFromClosestQuotes(page);
        return page;
    }
    public static String findAuthenticityToken(String page)
    {
        int index = page.indexOf("authenticity_token");
        page=page.substring(index);
        page=page.replaceFirst("\""," ");
        page=extractStringFromClosestQuotes(page);
        return page;
    }
    public static String extractFromHtmlRemovingFirstQuote(String page,String keyword)
    {
        int index = page.indexOf(keyword);
        page=page.substring(index);
        page=page.replaceFirst("\""," ");
        page=extractStringFromClosestQuotes(page);
        return page;
    }
    public static String extractFromHtmlRemovingFirstSmallQuote(String page,String keyword)
    {
        int index = page.indexOf(keyword);
        page=page.substring(index);
        page=page.replaceFirst("'"," ");
        page=extractStringFromClosestSmallQuotes(page);
        return page;
    }
    public static String extractStringFromClosestSmallQuotes(String toExtract)
    {
        int index = toExtract.indexOf("'");
        toExtract=toExtract.substring(index+1);
        index = toExtract.indexOf("'");
        toExtract=toExtract.substring(0,index);
        return toExtract;
    }

    public static String extractStringFromClosestQuotes(String toExtract)
    {
        int index = toExtract.indexOf("\"");
        toExtract=toExtract.substring(index+1);
        index = toExtract.indexOf("\"");
        toExtract=toExtract.substring(0,index);
        return toExtract;
    }
    public static String extractFromHtml(String page,String keyword)
    {
        int index = page.indexOf(keyword);
        page=page.substring(index);
        page=extractStringFromClosestQuotes(page);
        return page;
    }
    public static Map<String,String> extractFromForm(String html,String formClass)
    {
        Map<String,String> returnMap = new HashMap<>();
        Document document = Jsoup.parse(html);
        Element form = document.getElementsByClass(formClass).first();
        Elements  inputs = form.getElementsByTag("input");
        for(int i = 0; i < inputs.size(); i++)
        {
            Element element = inputs.get(i);
            returnMap.put(element.attributes().get("name"),element.attributes().get("value"));
        }
        return returnMap;
    }
    public static Map<String,String> extractFromFormById(String html,String id)
    {
        Map<String,String> returnMap = new HashMap<>();
        Document document = Jsoup.parse(html);
        Element form = document.getElementById(id);
        Elements  inputs = form.getElementsByTag("input");
        for(int i = 0; i < inputs.size(); i++)
        {
            Element element = inputs.get(i);
            returnMap.put(element.attributes().get("name"),element.attributes().get("value"));
        }
        return returnMap;
    }
}
