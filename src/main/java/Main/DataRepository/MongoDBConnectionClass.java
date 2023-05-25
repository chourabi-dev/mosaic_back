package Main.DataRepository;

import Main.Config.AppConfig;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import static Main.Config.AppConfig.MONGODB_SERVER_URI;

public class MongoDBConnectionClass {

    public static boolean isAllowedToConnect = true;
    private static MongoClient mongoClient;

    static {
        try {
            mongoClient = new MongoClient(new MongoClientURI(MONGODB_SERVER_URI));
        } catch (Exception ex) {
            System.err.println("Can't connect to MongoDB Server. " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static DB getDatabase(String dbname) {
        if (isAllowedToConnect)
            return mongoClient.getDB(dbname);
        else
            return null;
    }

    public static DB getWorkDatabase() {
        return getDatabase(AppConfig.DATABASE_NAME);
    }

    public static DBCollection getCollection(DB database, String collectionName) {
        boolean isfound = false;
        for (String colname : database.getCollectionNames()) {
            if (colname.equals(collectionName)) {
                isfound = true;
                break;
            }
        }
        if (!isfound) {
            database.createCollection(collectionName, null);
        }
        return database.getCollection(collectionName);
    }

    public static DBCollection getConfigCollection() {
        return getCollection(getWorkDatabase(), AppConfig.CONFIG_COLLECTION_NAME);
    }

    public static Boolean isConnected() {
        try {
            mongoClient.getAddress();
            return true;
        } catch (Exception e) {
            System.out.println("Database unavailable!");
            mongoClient.close();
            return false;
        }
    }
}