package me.meczka;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class Item {
    private String name;
    private String price;
    private String itemLink;
    private List<String> sizes;
    private String imageUrl;
    private String pid;

    public Item(String name, String price, String itemLink, List<String> sizes, String imageUrl, String pid) {
        this.name = name;
        this.price = price;
        this.itemLink = itemLink;
        this.sizes = sizes;
        this.imageUrl = imageUrl;
        this.pid = pid;
    }
    public String toJSON()
    {
        JSONObject retVal = new JSONObject();
        retVal.put("name",name);
        retVal.put("itemLink", itemLink);
        retVal.put("price",price);
        retVal.put("imageUrl",imageUrl);
        retVal.put("pid",pid);
        JSONArray sizesJson = new JSONArray();
        for(int i = 0; i < sizes.size(); i++)
        {
            sizesJson.put(sizes.get(i));
        }
        retVal.put("sizes",sizesJson);
        return retVal.toString();
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getItemLink() {
        return itemLink;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return getName().equals(item.getName()) &&
                getPrice().equals(item.getPrice()) &&
                getItemLink().equals(item.getItemLink()) &&
                getSizes().equals(item.getSizes()) &&
                getImageUrl().equals(item.getImageUrl()) &&
                getPid().equals(item.getPid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPrice(), getItemLink(), getSizes(), getImageUrl(),getPid());
    }
}
