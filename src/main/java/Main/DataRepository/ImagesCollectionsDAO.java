package Main.DataRepository;

import Main.Beans.CollageItem;
import Main.Beans.ConfigItem;
import Main.Beans.ImageLocation;
import Main.Beans.Levels;
import Main.Config.AppConfig;
import Main.Utils.ImageUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static Main.Utils.CollageUtils.awaitTerminationAfterShutdown;
import static Main.Utils.CollageUtils.getRandomNumber;

public class ImagesCollectionsDAO {

    static ConfigDAO configDAO = new ConfigDAO();
    private ObjectMapper mapper;

    public ImagesCollectionsDAO() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private DBCollection getCollection(String CollectionName) {
        return MongoDBConnectionClass.getCollection(MongoDBConnectionClass.getWorkDatabase(), CollectionName);
    }

    public CollageItem save(CollageItem item) {
        try {
            ConfigItem config = configDAO.findItemBy1Properties("COLLECTION_NAME", item.CollectionName);
            if (countCollectionItems(item.CollectionName) < (config.COLUMNS * config.ROWS)) {
                ImageLocation newCoordinates = null;
                Boolean FoundLocation = false;
                /**
                 * Collage COL & ROW Added Here
                 */
                while (!FoundLocation) {
                    newCoordinates = new ImageLocation(getRandomNumber(1, config.ROWS), getRandomNumber(1, config.COLUMNS));
                    FoundLocation = isItemExists(item.CollectionName, newCoordinates.COLUMN_POS, newCoordinates.LINE_POS);
                }
                item.insertTimeDate = Instant.now().getEpochSecond();
                item.COLUMN_POS = newCoordinates.COLUMN_POS;
                item.ROW_POS = newCoordinates.LINE_POS;
                Object o = com.mongodb.util.JSON.parse(mapper.writeValueAsString(item));
                DBObject newObject = (DBObject) o;
                getCollection(item.CollectionName).insert(newObject);
                return item;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<CollageItem> getResults(DBCursor cursor) {
        List<CollageItem> resList = new ArrayList<CollageItem>();
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

    public List<CollageItem> findAll(String CollectionName) {
        return getResults(getCollection(CollectionName).find());
    }

    public List<CollageItem> findAllBy1Properties(String CollectionName, String propertyName1, Object value1) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        return getResults(getCollection(CollectionName).find(whereQuery));
    }

    public List<CollageItem> findAllBy1Properties(String CollectionName, BasicDBObject whereQuery) {
        return getResults(getCollection(CollectionName).find(whereQuery));
    }

    public List<CollageItem> findAllBy2Properties(String CollectionName, String propertyName1, Object value1, String propertyName2, Object value2) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        whereQuery.put(propertyName2, value2);
        return getResults(getCollection(CollectionName).find(whereQuery));
    }

    public List<CollageItem> findAllBy3Properties(String CollectionName, String propertyName1, Object value1, String propertyName2, Object value2, String propertyName3, Object value3) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        whereQuery.put(propertyName2, value2);
        whereQuery.put(propertyName3, value3);
        return getResults(getCollection(CollectionName).find(whereQuery));
    }

    public List<CollageItem> findAllBy4Properties(String CollectionName, String propertyName1, Object value1, String propertyName2, Object value2, String propertyName3, Object value3, String propertyName4, Object value4) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(propertyName1, value1);
        whereQuery.put(propertyName2, value2);
        whereQuery.put(propertyName3, value3);
        whereQuery.put(propertyName4, value4);
        return getResults(getCollection(CollectionName).find(whereQuery));
    }

    public List<CollageItem> findAllForThumbnail(String CollectionName, String imagename, Levels level) {
        int OFFSET = 0;
        switch (level) {
            case L17:
                OFFSET = 8;
                break;
            case Live:
                OFFSET = 8;
                break;
            case L18:
                OFFSET = 4;
                break;
            case L19:
                OFFSET = 2;
                break;
            case L20:
                OFFSET = 1;
                break;
        }
        int C = ImageUtils.imageNameToColumns(imagename);
        int L = ImageUtils.imageNameToRow(imagename);
        int BEGIN_COL = (OFFSET * C) - (OFFSET - 1);
        int BEGIN_ROW = (OFFSET * L) - (OFFSET - 1);
        int END_COL = BEGIN_COL + (OFFSET - 1);
        int END_ROW = BEGIN_ROW + (OFFSET - 1);
        return getResults(getCollection(CollectionName).find(
                new BasicDBObject()
                        .append("ROW_POS", new BasicDBObject("$gte", BEGIN_ROW))
                        .append("ROW_POS", new BasicDBObject("$lte", END_ROW))
                        .append("COLUMN_POS", new BasicDBObject("$gte", BEGIN_COL))
                        .append("COLUMN_POS", new BasicDBObject("$lte", END_COL))
        ));
    }

    public void deleteAllItems(String CollectionName) {
        try {
            getCollection(CollectionName).drop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNewCollection(String CollectionName) {
        MongoDBConnectionClass.getWorkDatabase().createCollection(CollectionName, null);
    }

    public void renameCollection(String OldCollectionName, String NewCollectionName) {
        getCollection(OldCollectionName).rename(NewCollectionName);
    }

    public void refreshCollection(String CollectionName) {
        deleteAllItems(CollectionName);
        createNewCollection(CollectionName);
    }

    public boolean update(CollageItem item) {
        try {
            Object o = com.mongodb.util.JSON.parse(mapper.writeValueAsString(item));
            DBObject updObject = (DBObject) o;
            getCollection(item.CollectionName).update(new BasicDBObject("uuid", item.uuid), updObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CollageItem getLastElement(String CollectionName) {
        return getResults(getCollection(CollectionName).find().sort(new BasicDBObject("insertTimeDate", -1)).limit(1)).get(0);
    }

    public CollageItem getFirstElement(String CollectionName) {
        return getResults(getCollection(CollectionName).find().sort(new BasicDBObject("insertTimeDate", 1)).limit(1)).get(0);
    }

    public long countCollectionItems(String CollectionName) {
        return getCollection(CollectionName).count();
    }

    public boolean isItemExists(String collectionname, int col, int row) {
        return getCollection(collectionname).findOne(new BasicDBObject().append("ROW_POS", row).append("COLUMN_POS", col)) == null;
    }

    public CollageItem findItemBy1Properties(String collectionname, String propertyName1, Object value1) {
        DBObject object = getCollection(collectionname).findOne(new BasicDBObject().append(propertyName1, value1));
        if (object != null) {
            return initiateFromDBObject(object);
        } else
            return null;
    }

    public CollageItem findItemBy2Properties(String collectionname, String propertyName1, Object value1, String propertyName2, Object value2) {
        DBObject object = getCollection(collectionname).findOne(new BasicDBObject().append(propertyName1, value1).append(propertyName2, value2));
        if (object != null) {
            return initiateFromDBObject(object);
        } else
            return null;
    }

    private CollageItem initiateFromDBObject(DBObject object) {
        CollageItem item = new CollageItem();
        item.uuid = (String) object.get("uuid");
        item.nom = (String) object.get("nom");
        item.message = (String) object.get("message");
        item.email = (String) object.get("email");
        item.CollectionName = (String) object.get("CollectionName");
        item.ImageName = (String) object.get("ImageName");
        item.NextUuid = (String) object.get("NextUuid");
        item.insertTimeDate = (int) object.get("insertTimeDate");
        item.ROW_POS = (int) object.get("ROW_POS");
        item.COLUMN_POS = (int) object.get("COLUMN_POS");
        return item;
    }

    public boolean delete(CollageItem item) {
        try {
            Object o = com.mongodb.util.JSON.parse(mapper.writeValueAsString(item));
            DBObject newObject = (DBObject) o;
            getCollection(item.CollectionName).remove(newObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
