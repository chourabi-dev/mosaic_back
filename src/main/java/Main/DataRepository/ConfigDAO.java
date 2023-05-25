package Main.DataRepository;

import Main.Beans.ConfigItem;
import Main.Config.AppConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static Main.DataRepository.MongoDBConnectionClass.getConfigCollection;
import static Main.DataRepository.MongoDBConnectionClass.getWorkDatabase;
import static Main.Utils.CollageUtils.awaitTerminationAfterShutdown;

public class ConfigDAO {

    private ObjectMapper mapper;

    public ConfigDAO() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private DBCollection getCollection() {
        return getConfigCollection();
    }

    public boolean save(ConfigItem item) {
        try {
            Object o = com.mongodb.util.JSON.parse(mapper.writeValueAsString(item));
            DBObject newObject = (DBObject) o;
            getCollection().insert(newObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String collectionname) {
        try {
            Object o = com.mongodb.util.JSON.parse(mapper.writeValueAsString(findItemBy1Properties("COLLECTION_NAME", collectionname)));
            DBObject newObject = (DBObject) o;
            getCollection().remove(newObject);
            getWorkDatabase().getCollection(collectionname).drop();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ConfigItem item) {
        try {
            Object o = com.mongodb.util.JSON.parse(mapper.writeValueAsString(item));
            DBObject updObject = (DBObject) o;
            getCollection().update(new BasicDBObject("COLLECTION_NAME", item.COLLECTION_NAME), updObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<ConfigItem> getResults(DBCursor cursor) {
        List<ConfigItem> resList = new ArrayList<ConfigItem>();
        ExecutorService executor = AppConfig.executorType();
        while (cursor.hasNext()) {
            DBObject current = cursor.next();
            executor.submit(() -> {
                try {
                    DBObject object = current;
                    resList.add(initiateFromDBObject(object));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        awaitTerminationAfterShutdown(executor);
        return resList;
    }

    public List<ConfigItem> findAll() {
        return getResults(getCollection().find());
    }

    public List<ConfigItem> findAllBy1Properties(String propertyName1, Object value1) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        return getResults(getCollection().find(whereQuery));
    }

    public List<ConfigItem> findAllBy2Properties(String propertyName1, Object value1, String propertyName2, Object value2) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        whereQuery.put(propertyName2, value2);
        return getResults(getCollection().find(whereQuery));
    }

    public List<ConfigItem> findAllBy3Properties(String propertyName1, Object value1, String propertyName2, Object value2, String propertyName3, Object value3) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        whereQuery.put(propertyName2, value2);
        whereQuery.put(propertyName3, value3);
        return getResults(getCollection().find(whereQuery));
    }

    public List<ConfigItem> findAllBy4Properties(String propertyName1, Object value1, String propertyName2, Object value2, String propertyName3, Object value3, String propertyName4, Object value4) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        whereQuery.put(propertyName2, value2);
        whereQuery.put(propertyName3, value3);
        whereQuery.put(propertyName4, value4);
        return getResults(getCollection().find(whereQuery));
    }

    private ConfigItem initiateFromDBObject(DBObject object) {
        ConfigItem item = new ConfigItem();
        item.id = (String) object.get("id");
        item.COLLECTION_NAME = (String) object.get("COLLECTION_NAME");
        item.CLIENT_NAME = (String) object.get("CLIENT_NAME");
        item.COLUMNS = (int) object.get("COLUMNS");
        item.ROWS = (int) object.get("ROWS");
        item.IMAGE_OFFSET = (int) object.get("IMAGE_OFFSET");
        item.BASE64_BANNER = (String) object.get("BASE64_BANNER");
        item.BASE64_LOGO = (String) object.get("BASE64_LOGO");
        item.BASE64_HEADERIMAGE = (String) object.get("BASE64_HEADERIMAGE");
        item.THUMBNAIL_SIZE = (int) object.get("THUMBNAIL_SIZE");
        item.COLOR = (String) object.get("COLOR");
        return item;
    }

    public ConfigItem findItemBy1Properties(String propertyName1, Object value1) {
        DBObject object = getCollection().findOne(new BasicDBObject().append(propertyName1, value1));
        if (object != null) {
            return initiateFromDBObject(object);
        } else
            return null;
    }
}
