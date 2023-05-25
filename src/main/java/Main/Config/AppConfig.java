package Main.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppConfig {
    public static String MONGODB_SERVER_URI = "mongodb://127.0.0.1:27017";
    public static String DATABASE_NAME = "Collage";
    public static String CONFIG_COLLECTION_NAME = "CONFIG";
    /**
     * Where to store generated images, must be owned by user "tomcat" in linux.
     */
    public static String THUMBNAILS_DIR = "/var/www/html/mosaics-images";
    /**
     * Actual Server URI, used as base for images link. the part https://mosaika.tn is the variable one, the rest must remain static.
     */
    public static String SERVERURI = "http://37.59.204.209:8080/mosaics-images";
    
    
    

    public static ExecutorService executorType() {
        return Executors.newCachedThreadPool();
    }
}