package me.meczka;

import static org.junit.Assert.assertTrue;

import me.meczka.webhook.WebhookUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        List<String> sizes = new ArrayList<>();
        sizes.add("twst");
        Item item = new Item("Test","100","https://sklepkoszykarza.pl/buty-air-jordan-1-low-553558-611.html",sizes,"https://szopex.blob.core.windows.net/shops/media/f1000/2020/jordan/169368/buty-air-jordan-1-low-553558-611-5eaa97c09b773.jpg","123");
        WebhookUtils.newItemWebhook(item);
    }
}
