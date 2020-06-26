package me.meczka.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import me.meczka.Item;
import org.bson.Document;

import java.util.List;

public class MongoConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection items;
    private Object String;

    public MongoConnection()
    {
        mongoClient = new MongoClient();
        database = mongoClient.getDatabase("monitor");
        items = database.getCollection("sklepkoszykarza");
    }
    public void addNewItem(String json)
    {
        items.insertOne(Document.parse(json));
    }
    public Item getItemByPid(String pid)
    {

        Document document  = (Document) items.find(Filters.eq("pid",pid)).first();
        if(document == null)
        {
            return null;
        }
        List<String> sizes = (List<String>) document.get("sizes");
        return new Item(document.getString("name"),document.getString("price"),document.getString("itemLink"),sizes,document.getString("imageUrl")
        ,document.getString("pid"));
    }
    public void updateItem(Item item)
    {
        items.replaceOne(Filters.eq("pid",item.getPid()),
                Document.parse(item.toJSON())
                );
    }


}
