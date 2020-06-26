package me.meczka.utils;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CaptchaUtils {
    final static Logger LOGGER = Logger.getLogger("CaptchaUtils");
    public static final int CAPTCHAV2=0,CAPTCHAV3=1,TEXTCAPTCHA=2,HCAPTCHA=3;
    private static Map<String,String> tokenIdMap = new HashMap<>();
    public static String solveCaptcha(String sitekey,String domain)
    {
        return solveCaptcha(sitekey,domain,CAPTCHAV2);
    }
    public static String solveCaptcha(String sitekeyOrImage,String domain,int captcha_version)
    {
        return solveCaptcha(sitekeyOrImage,domain,captcha_version,null);
    }
    public static String solveCaptcha(String sitekeyOrImage,String domain,int captcha_version,String optionalProxy)
    {
        try {
            boolean isError=false;
            String token;
            do {
                String id=null;
                if(captcha_version==CAPTCHAV2) {
                    do {
                        id = CaptchaTokenGenerator.request(sitekeyOrImage, domain);
                        Thread.sleep(3000);
                    }while (id==null);
                }else if(captcha_version==CAPTCHAV3)
                {
                    id = CaptchaTokenGenerator.requestV3(sitekeyOrImage, domain);
                }
                else if (captcha_version==TEXTCAPTCHA)
                {
                    id = CaptchaTokenGenerator.requestTextCaptcha(sitekeyOrImage);
                }
                else if(captcha_version==HCAPTCHA)
                {
                    id = CaptchaTokenGenerator.requestHCaptcha(sitekeyOrImage,domain,optionalProxy);
                }
                Thread.sleep(5000);
                boolean found = false;
                do {
                    CaptchaTokenGenerator.updateCaptchaTokens();
                    token = CaptchaTokenGenerator.getCaptchaToken(id);
                    if (!token.equalsIgnoreCase("CAPTCHA_NOT_READY")) {
                        found = true;
                    }
                    if (token.equalsIgnoreCase("ERROR_CAPTCHA_UNSOLVABLE"))
                    {
                        isError=true;
                        CaptchaTokenGenerator.report(id, CaptchaTokenGenerator.Status.BAD);
                    }
                    if(token.startsWith("OK"))
                    {
                        isError=false;
                        tokenIdMap.put(token.substring(3),id);
                        break;
                    }
                    Thread.sleep(5000);
                } while (!found);
            }while (isError);
            LOGGER.info("Generated token is: " + token);
            return token.substring(3);
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
    public static void reportByToken(String token, CaptchaTokenGenerator.Status status)
    {
        String id = tokenIdMap.get(token);
        CaptchaTokenGenerator.report(id,status);
    }
    private String extractSitekeyFromHtml(String html)
    {
        int index = html.indexOf("sitekey=");
        String temp = html.substring(index+9);
        index = temp.indexOf("\"");
        temp=temp.substring(0,index);
        System.out.println(temp);
        return temp;
    }
}
