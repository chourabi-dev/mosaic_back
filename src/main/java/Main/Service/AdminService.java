package Main.Service;

import Main.Beans.*;
import Main.Config.AppConfig;
import Main.DataRepository.ConfigDAO;
import Main.DataRepository.ImagesCollectionsDAO;
import Main.DataRepository.MongoDBConnectionClass;
import Main.Utils.CollageUtils;
import Main.Utils.FileUtils;
import Main.Utils.ImageUtils;
import Main.Utils.ProcessingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static Main.Config.AppConfig.SERVERURI;
import static Main.Config.AppConfig.THUMBNAILS_DIR;
import static Main.Utils.CollageUtils.awaitTerminationAfterShutdown;


import java.util.Base64;



@Service
public class AdminService {
    static ConfigDAO configDAO = new ConfigDAO();
    static ImagesCollectionsDAO imagesCollectionsDAO = new ImagesCollectionsDAO();

    public ResponseEntity<?> AddNewConfiguration(NewConfigRequest item) {
        if (configDAO.findItemBy1Properties("CLIENT_NAME", item.CLIENT_NAME) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ConfigItem newConfigItem = ConfigItem.createFromNew(item);
        try {
            
        	
        	
        			
        	byte[] one = Base64.getDecoder().decode(newConfigItem.BASE64_BANNER); 
        	byte[] two = Base64.getDecoder().decode(newConfigItem.BASE64_LOGO); 
        	byte[] three = Base64.getDecoder().decode(newConfigItem.BASE64_HEADERIMAGE); 
        	
        	
        	final BufferedImage Banner = ImageUtils.createImageFromBytes(one);
            FileUtils.writeFile(ImageUtils.toByteArray(Banner), THUMBNAILS_DIR + "/" + newConfigItem.id + "/Banner.png");
            
            
            
            
            newConfigItem.BASE64_BANNER = SERVERURI + "/" + newConfigItem.id + "/Banner.png";
            final BufferedImage Logo = ImageUtils.createImageFromBytes(two);
            FileUtils.writeFile(ImageUtils.toByteArray(Logo), THUMBNAILS_DIR + "/" + newConfigItem.id + "/Logo.png");
            
            
            
            
            newConfigItem.BASE64_LOGO = SERVERURI + "/" + newConfigItem.id + "/Logo.png";
            final BufferedImage HeaderImage = ImageUtils.createImageFromBytes(three);
            FileUtils.writeFile(ImageUtils.toByteArray(HeaderImage), THUMBNAILS_DIR + "/" + newConfigItem.id + "/HeaderImage.png");
            newConfigItem.BASE64_HEADERIMAGE = SERVERURI + "/" + newConfigItem.id + "/HeaderImage.png";
            if (configDAO.save(newConfigItem)) {
                return new ResponseEntity<>(newConfigItem, HttpStatus.OK);
            } else {
            	return  ResponseEntity.status(400).body("bad request");
            }
        } catch (Exception e) {

            return  ResponseEntity.status(400).body(e.toString());
        }
    }

    public ResponseEntity<List<ConfigItem>> fetchConfigurations() {
        return new ResponseEntity<>(configDAO.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Boolean> deleteConfiguration(String collectionname) {
        FileUtils.deleteFolder(THUMBNAILS_DIR + "/" + collectionname);
        return new ResponseEntity<>(configDAO.delete(collectionname), HttpStatus.OK);
    }

    public ResponseEntity<Boolean> updateConfiguration(ConfigItem item) {
        ConfigItem OriginalConfig = configDAO.findItemBy1Properties("COLLECTION_NAME", item.COLLECTION_NAME);
        Boolean reindexingItems = false;
        if (OriginalConfig != null) {
            if (imagesCollectionsDAO.countCollectionItems(item.COLLECTION_NAME) > (item.ROWS * item.COLUMNS)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            ConfigItem fetchedItem = OriginalConfig;
            if ((fetchedItem.ROWS != item.ROWS) || (fetchedItem.COLUMNS != item.COLUMNS)) {
                reindexingItems = true;
            }
            fetchedItem.CLIENT_NAME = item.CLIENT_NAME;
            //fetchedItem.BASE64_HEADERIMAGE = item.BASE64_HEADERIMAGE;
            //fetchedItem.BASE64_BANNER = item.BASE64_BANNER;
            fetchedItem.THUMBNAIL_SIZE = item.THUMBNAIL_SIZE;
            fetchedItem.IMAGE_OFFSET = item.IMAGE_OFFSET;
            fetchedItem.COLUMNS = item.COLUMNS;
            fetchedItem.ROWS = item.ROWS;
            configDAO.update(fetchedItem);
            if (reindexingItems) {
                new Thread(new Runnable() {
                    public void run() {
                        String tempCollectionName = CollageUtils.RandomString(16);
                        try {
                            List<CollageItem> backupCollageItemList = imagesCollectionsDAO.findAll(item.COLLECTION_NAME);
                            imagesCollectionsDAO.renameCollection(item.COLLECTION_NAME, tempCollectionName);
                            imagesCollectionsDAO.createNewCollection(item.COLLECTION_NAME);
                            for (CollageItem collageItem : backupCollageItemList) {
                                imagesCollectionsDAO.save(collageItem);
                            }
                            imagesCollectionsDAO.deleteAllItems(tempCollectionName);
                        } catch (Exception e) {
                            System.err.println("-= updateConfiguration : Reverting Back cols and rows config=-");
                            System.err.println("-= Exception : " + e.getMessage());
                            //Restore Things to normal
                            imagesCollectionsDAO.deleteAllItems(item.COLLECTION_NAME);
                            imagesCollectionsDAO.renameCollection(tempCollectionName, item.COLLECTION_NAME);
                            fetchedItem.COLUMNS = OriginalConfig.COLUMNS;
                            fetchedItem.ROWS = OriginalConfig.ROWS;
                            configDAO.update(fetchedItem);
                            System.err.println("-= Restore done=-");
                        }
                        FileUtils.deleteFolder(THUMBNAILS_DIR + "/" + fetchedItem.COLLECTION_NAME);
                    }
                }).start();
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Boolean> checkDataServiceConnectivity() {
        return new ResponseEntity<>(MongoDBConnectionClass.isConnected(), HttpStatus.OK);
    }

    public ResponseEntity<Boolean> deleteCaches(String collectionname) {
        ConfigItem item = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (item != null) {
            for (Levels l : Levels.values()) {
                FileUtils.deleteFolder(AppConfig.THUMBNAILS_DIR + "/" + collectionname + "/" + l.name());
            }
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Boolean> deleteAllImages(String collectionname) {
        ConfigItem item = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (item != null) {
            imagesCollectionsDAO.refreshCollection(collectionname);
            for (Levels l : Levels.values()) {
                FileUtils.deleteFolder(AppConfig.THUMBNAILS_DIR + "/" + collectionname + "/" + l.name());
            }
            FileUtils.deleteFolder(AppConfig.THUMBNAILS_DIR + "/" + collectionname + "/ORIGINAL");
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Boolean> updateLogoImage(String collectionname, NewImageUpdateRequest imageUpdateRequest) {
        /*try {
            final BufferedImage Logo = ImageUtils.createImageFromBytes(javax.xml.bind.DatatypeConverter.parseBase64Binary(imageUpdateRequest.image.split(",")[1]));
            FileUtils.delete(THUMBNAILS_DIR + "/" + collectionname + "/Logo.png");
            FileUtils.writeFile(ImageUtils.toByteArray(Logo), THUMBNAILS_DIR + "/" + collectionname + "/Logo.png");
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(true, HttpStatus.INTERNAL_SERVER_ERROR);
        }*/
        
        return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Boolean> updateBannerImage(String collectionname, NewImageUpdateRequest imageUpdateRequest) {
        /*try {
            final BufferedImage Banner = ImageUtils.createImageFromBytes(javax.xml.bind.DatatypeConverter.parseBase64Binary(imageUpdateRequest.image.split(",")[1]));
            FileUtils.delete(THUMBNAILS_DIR + "/" + collectionname + "/Banner.png");
            FileUtils.writeFile(ImageUtils.toByteArray(Banner), THUMBNAILS_DIR + "/" + collectionname + "/Banner.png");
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(true, HttpStatus.INTERNAL_SERVER_ERROR);
        }*/
    	
    	return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Boolean> updateHeaderImage(String collectionname, NewImageUpdateRequest imageUpdateRequest) {
        /*try {
            final BufferedImage Banner = ImageUtils.createImageFromBytes(javax.xml.bind.DatatypeConverter.parseBase64Binary(imageUpdateRequest.image.split(",")[1]));
            FileUtils.delete(THUMBNAILS_DIR + "/" + collectionname + "/HeaderImage.png");
            FileUtils.writeFile(ImageUtils.toByteArray(Banner), THUMBNAILS_DIR + "/" + collectionname + "/HeaderImage.png");
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(true, HttpStatus.INTERNAL_SERVER_ERROR);
        }*/
    	return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deleteItem(String collectionname, String uuid) {
        try {
            CollageItem itemToDelete = imagesCollectionsDAO.findItemBy1Properties(collectionname, "uuid", uuid);
            CollageItem previousItem = imagesCollectionsDAO.findItemBy1Properties(collectionname, "NextUuid", uuid);
            imagesCollectionsDAO.delete(itemToDelete);
            if (previousItem != null) {
                previousItem.NextUuid = itemToDelete.NextUuid;
                imagesCollectionsDAO.update(previousItem);
            }
            String filename = THUMBNAILS_DIR + "/" + collectionname + "/ORIGINAL/" + itemToDelete.uuid + ".png";
            FileUtils.delete(filename);
            ExecutorService executor = AppConfig.executorType();
            ProcessingUtils.ThumbnailsGenerationService(executor, imagesCollectionsDAO, configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname), itemToDelete);
            executor.submit(() -> {
                String LiveBigImage = THUMBNAILS_DIR + "/" + collectionname + "/LiveBigImage.png";
                FileUtils.delete(LiveBigImage);
            });
            awaitTerminationAfterShutdown(executor);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
