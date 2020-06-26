package me.meczka.webhook;


import me.meczka.Item;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class WebhookUtils {
    private final static String WEBHOOK_URL = "https://discordapp.com/api/webhooks/723977270267478077/382Z7XUjQo6gPYDA9E8xhiYNDkrMh5L_CdTwy0OUbEKDEiyebZ4eu3EsWUz8q2BfTzf6",
    SK_IMG = "https://sklepkoszykarza.pl/img/koszykarz/favicon.png";
    public static void newItemWebhook(Item item)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DiscordWebhook discordWebhook = new DiscordWebhook(WEBHOOK_URL);
        discordWebhook.setAvatarUrl(SK_IMG);
        discordWebhook.setUsername("SK Monitor");
        System.out.println(item.getImageUrl());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setUrl(item.getItemLink())
                .setTitle(item.getName())
                .setAuthor("NEW PRODUCT",null,null)
                .setThumbnail(item.getImageUrl())
                .setFooter("Hypemonitor " + dtf.format(now),"https://res.cloudinary.com/dklrin11o/image/twitter_name/w_600/Hypemonitorpl.jpg")
                .addField("Price",item.getPrice(),false)
                .addField("Sizes",serializeList(item.getSizes()),false)
                .setColor(Color.GREEN);
        discordWebhook.addEmbed(embed);
        try{
            discordWebhook.execute();
        }catch (Exception e){e.printStackTrace();}
    }
    public static void priceChangeWebhook(Item newItem, Item previousItem)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DiscordWebhook discordWebhook = new DiscordWebhook(WEBHOOK_URL);
        discordWebhook.setAvatarUrl(SK_IMG);
        discordWebhook.setUsername("CL20 Monitor");
        System.out.println(newItem.getImageUrl());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setUrl(newItem.getItemLink())
                .setTitle(newItem.getName())
                .setAuthor("PRICE CHANGE",null,null)
                .setThumbnail(newItem.getImageUrl())
                .setFooter("Hypemonitor " + dtf.format(now),"https://res.cloudinary.com/dklrin11o/image/twitter_name/w_600/Hypemonitorpl.jpg")
                .addField("Previous Price",previousItem.getPrice(),false)
                .addField("New Price",newItem.getPrice(),false)
                .addField("Sizes",serializeList(newItem.getSizes()),false)
                .setColor(Color.BLUE);
        discordWebhook.addEmbed(embed);
        try{
            discordWebhook.execute();
        }catch (Exception e){e.printStackTrace();}
    }
    public static void restockWebhook(Item newItem, Item previousItem)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DiscordWebhook discordWebhook = new DiscordWebhook(WEBHOOK_URL);
        discordWebhook.setAvatarUrl(SK_IMG);
        discordWebhook.setUsername("CL20 Monitor");
        System.out.println(newItem.getImageUrl());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setUrl(newItem.getItemLink())
                .setTitle(newItem.getName())
                .setAuthor("RESTOCK",null,null)
                .setThumbnail(newItem.getImageUrl())
                .setFooter("Hypemonitor " + dtf.format(now),"https://res.cloudinary.com/dklrin11o/image/twitter_name/w_600/Hypemonitorpl.jpg")
                .addField("Price",newItem.getPrice(),false)
                .addField("New Sizes",serializeList(newItem.getSizes()),false)
                .addField("Previous Sizes",serializeList(previousItem.getSizes()),false)
                .setColor(Color.RED);
        discordWebhook.addEmbed(embed);
        try{
            discordWebhook.execute();
        }catch (Exception e){e.printStackTrace();}
    }
    public static String serializeList(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        for(String l : list)
        {
            sb.append(l+"\\n");
        }
        return sb.toString();
    }
}
