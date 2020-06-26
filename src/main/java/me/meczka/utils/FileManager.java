package me.meczka.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<String> readFileAsList(File file)
    {
        List<String> list = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            is.close();
            br.close();
        }catch (IOException e){e.printStackTrace();}
        return list;
    }

        public static void addLineToFile(File file,String line)
        {
            try {
                FileWriter fr = new FileWriter(file,true);
                BufferedWriter bw = new BufferedWriter(fr);
                bw.write("\n"+line);
                bw.close();
                fr.close();
            }catch (IOException e){e.printStackTrace();}

        }
    public static String readFileAsString(File file)
    {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            br.close();
        }catch (IOException e){e.printStackTrace();}
        return sb.toString();
    }
    public static void saveListAsFile(File file,List<String> lines)
    {
        try {
            FileWriter fr = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fr);
            for(String line : lines) {
                bw.write("\n" + line);
            }
            bw.close();
            fr.close();
        }catch (IOException e){e.printStackTrace();}

    }
    public static void saveStringAsFile(File file, String string)
    {
        try {
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(string);
            bw.close();
            fw.close();
        }catch (IOException e){e.printStackTrace();}
    }
}
