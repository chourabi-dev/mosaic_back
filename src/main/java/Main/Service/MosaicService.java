package Main.Service;

import Main.Beans.CollageItem;
import Main.Beans.ConfigItem;
import Main.Beans.Levels;
import Main.Beans.NewImageRequest;
import Main.Config.AppConfig;
import Main.DataRepository.ConfigDAO;
import Main.DataRepository.ImagesCollectionsDAO;
import Main.Utils.FileUtils;
import Main.Utils.ImageUtils;
import Main.Utils.ProcessingUtils;
import com.mongodb.BasicDBObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import static Main.Config.AppConfig.THUMBNAILS_DIR;
import static Main.Utils.CollageUtils.awaitTerminationAfterShutdown;
import static Main.Utils.CollageUtils.getRandomNumber;
import static Main.Utils.ImageUtils.*;

@Service
public class MosaicService {

    static ConfigDAO configDAO = new ConfigDAO();
    static ImagesCollectionsDAO imagesCollectionsDAO = new ImagesCollectionsDAO();

    public ResponseEntity<byte[]> L17Service(String collectionname, String imagename) throws IOException {
        /**
         * L17 le niveau du zoom ou l’image est composer de 64 image = 8*8 (8 ligne 8 colonne)
         * taille totale 160px x 160px
         */
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            return new ResponseEntity<>(thumbnailServiceHandler(configItem, imagename, Levels.L17), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<byte[]> LiveService(String collectionname, String imagename) throws IOException {
        /**
         * Live le niveau du zoom ou l’image est composer de 64 image = 8*8 (8 ligne 8 colonne)
         * taille totale 160px x 160px
         */
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            return new ResponseEntity<>(thumbnailServiceHandler(configItem, imagename, Levels.Live), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<byte[]> L18Service(String collectionname, String imagename) throws IOException {
        /**
         * L18 le niveau du zoom ou l’image est composée de 16 images = 4*4 (4 ligne 4 colonne)
         * taille totale 160px x 160px
         */
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            return new ResponseEntity<>(thumbnailServiceHandler(configItem, imagename, Levels.L18), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<byte[]> L19Service(String collectionname, String imagename) throws IOException {
        /**
         * L19 le niveau du zoom ou l’image est composée de 4 image = 2*2 (2 ligne 2 colone )
         * taille totale 160px x 160px
         */
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            return new ResponseEntity<>(thumbnailServiceHandler(configItem, imagename, Levels.L19), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<byte[]> L20Service(String collectionname, String imagename) throws IOException {
        /**
         * L20 le niveau du zoom ou l’image est composée de 1 image = 1*1
         * taille totale 160px x 160px
         */
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            return new ResponseEntity<>(thumbnailServiceHandler(configItem, imagename, Levels.L20), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CollageItem> postNewImage(String collectionname, NewImageRequest body) {
        Boolean isFirst = true;
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            if (imagesCollectionsDAO.findItemBy1Properties(collectionname, "email", body.email) == null) {
                CollageItem toBeInserted = CollageItem.createFromNew(body, collectionname);
                CollageItem lastInserted = null;
                try {
                    lastInserted = imagesCollectionsDAO.getLastElement(collectionname);
                    isFirst = false;
                } catch (Exception e) {
                    System.err.println("First element, passing ...");
                }
                CollageItem res = imagesCollectionsDAO.save(toBeInserted);
                String base64Image = body.image.split(",")[1];
                try {
                    
                    
                    byte[] img = Base64.getDecoder().decode(base64Image); 

                    
                    
                    String filename = THUMBNAILS_DIR + "/" + configItem.COLLECTION_NAME + "/ORIGINAL/" + res.uuid + ".png";
                    if (FileUtils.exists(filename)) {
                        FileUtils.delete(filename);
                    }
                    FileUtils.writeFile(img, filename);
                } catch (Exception e) {
                    System.err.println("Error writing file." + e.getMessage());
                }
                ExecutorService executor = AppConfig.executorType();
                ProcessingUtils.ThumbnailsGenerationService(executor, imagesCollectionsDAO, configItem, res);
                CollageItem finalLastInserted = lastInserted;
                Boolean finalIsFirst = isFirst;
                executor.submit(() -> {
                    if (!finalIsFirst) {
                        finalLastInserted.NextUuid = res.uuid;
                        imagesCollectionsDAO.update(finalLastInserted);
                    }
                });
                executor.submit(() -> {
                    String filename = THUMBNAILS_DIR + "/" + collectionname + "/LiveBigImage.png";
                    FileUtils.delete(filename);
                });
                awaitTerminationAfterShutdown(executor);
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<CollageItem>> getAllItems(String collectionname) {
        if (configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname) != null) {
            return new ResponseEntity<>(imagesCollectionsDAO.findAll(collectionname), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<CollageItem>> filterByMail(String collectionname, String Mail) {
        if (configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname) != null) {
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("email", Pattern.compile(".*" + Mail + ".*", Pattern.CASE_INSENSITIVE));
            return new ResponseEntity<>(imagesCollectionsDAO.findAllBy1Properties(collectionname, whereQuery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<CollageItem>> filterByName(String collectionname, String Name) {
        if (configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname) != null) {
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("nom", Pattern.compile(".*" + Name + ".*", Pattern.CASE_INSENSITIVE));
            return new ResponseEntity<>(imagesCollectionsDAO.findAllBy1Properties(collectionname, whereQuery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CollageItem> getItem(String collectionname, int col, int row) {
        return new ResponseEntity<>(imagesCollectionsDAO.findItemBy2Properties(collectionname, "ROW_POS", row, "COLUMN_POS", col), HttpStatus.OK);
    }

    public ResponseEntity<ConfigItem> getConfig(String collectionname) {
        ConfigItem configItem = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
        if (configItem != null) {
            return new ResponseEntity<>(configItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private byte[] thumbnailServiceHandler(ConfigItem configItem, String imagename, Levels level) throws IOException {
        String ImagePath = THUMBNAILS_DIR + "/" + configItem.COLLECTION_NAME + "/" + level.name() + "/" + imagename;
        if (FileUtils.exists(ImagePath)) {
            return FileUtils.read(ImagePath);
        } else {
            byte[] generatedImage = ImageUtils.toByteArray(createThumbnail(ImageUtils.joinBufferedImages(ImageUtils.fetchThumbnailImages(configItem, level, imagename, imagesCollectionsDAO.findAllForThumbnail(configItem.COLLECTION_NAME, imagename, level)), configItem.THUMBNAIL_SIZE, configItem.THUMBNAIL_SIZE, configItem.IMAGE_OFFSET), configItem.THUMBNAIL_SIZE));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.writeFile(generatedImage, ImagePath);
                }
            }).start();
            return generatedImage;
        }
    }

    public ResponseEntity<byte[]> getImage(String collectionname, int col, int row) {
        CollageItem item = imagesCollectionsDAO.findItemBy2Properties(collectionname, "ROW_POS", row, "COLUMN_POS", col);
        if (item != null) {
            String filename = THUMBNAILS_DIR + "/" + collectionname + "/ORIGINAL/" + item.ImageName;
            if (FileUtils.exists(filename)) {
                return new ResponseEntity<>(FileUtils.read(filename), HttpStatus.OK);
            } else {
                String base64Image = B64_BlackImage.split(",")[1];
                try {
                    //byte[] img = ImageUtils.toByteArray(createImageFromBytes(javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image)));
                    
                    byte[] img = Base64.getDecoder().decode(base64Image);
                    
                    return new ResponseEntity<>(img, HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CollageItem> getItemByUuid(String collectionname, String uuid) {
        if (uuid.equalsIgnoreCase("first")) {
            List<CollageItem> collageItemList = imagesCollectionsDAO.findAll(collectionname);
            return new ResponseEntity<>(collageItemList.get(getRandomNumber(0, collageItemList.size() - 1)), HttpStatus.OK);
        } else if (uuid.equalsIgnoreCase("last")) {
            return getFirstElement(collectionname);
        } else {
            return new ResponseEntity<>(imagesCollectionsDAO.findItemBy1Properties(collectionname, "uuid", uuid), HttpStatus.OK);
        }
    }

    public ResponseEntity<Long> countItems(String collectionname) {
        if (configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname) != null) {
            return new ResponseEntity<>(imagesCollectionsDAO.countCollectionItems(collectionname), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CollageItem> getLastElement(String collectionName) {
        if (configDAO.findItemBy1Properties("COLLECTION_NAME", collectionName) != null) {
            return new ResponseEntity<>(imagesCollectionsDAO.getLastElement(collectionName), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CollageItem> getFirstElement(String collectionName) {
        if (configDAO.findItemBy1Properties("COLLECTION_NAME", collectionName) != null) {
            return new ResponseEntity<>(imagesCollectionsDAO.getFirstElement(collectionName), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CollageItem> getItemNext(String collectionname, int col, int row) {
        int pos = -1;
        List<CollageItem> items = imagesCollectionsDAO.findAll(collectionname);
        Collections.sort(items, new Comparator<CollageItem>() {
            @Override
            public int compare(CollageItem p1, CollageItem p2) {
                if ((p1.ROW_POS - p2.ROW_POS) == 0) {
                    return p1.COLUMN_POS - p2.COLUMN_POS;
                } else {
                    return p1.ROW_POS - p2.ROW_POS;
                }
            }
        });
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).ROW_POS == row && items.get(i).COLUMN_POS == col) {
                pos = i;
                try {
                    return new ResponseEntity<>(items.get(i + 1), HttpStatus.OK);
                } catch (Exception e) {
                    System.err.println("Error returning next item. " + e.getMessage());
                }
                break;
            }
        }
        return new ResponseEntity<CollageItem>(items.get(pos), HttpStatus.OK);
    }

    public ResponseEntity<CollageItem> getItemPrev(String collectionname, int col, int row) {
        int pos = -1;
        List<CollageItem> items = imagesCollectionsDAO.findAll(collectionname);
        Collections.sort(items, new Comparator<CollageItem>() {
            @Override
            public int compare(CollageItem p1, CollageItem p2) {
                if ((p1.ROW_POS - p2.ROW_POS) == 0) {
                    return p1.COLUMN_POS - p2.COLUMN_POS;
                } else {
                    return p1.ROW_POS - p2.ROW_POS;
                }
            }
        });
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).ROW_POS == row && items.get(i).COLUMN_POS == col) {
                pos = i;
                try {
                    return new ResponseEntity<>(items.get(i - 1), HttpStatus.OK);
                } catch (Exception e) {
                    System.err.println("Error returning previous item. " + e.getMessage());
                }
                break;
            }
        }
        return new ResponseEntity<CollageItem>(items.get(pos), HttpStatus.OK);
    }

    public ResponseEntity<String> getUuidFromClientName(String clientname) {
        ConfigItem configItem = configDAO.findItemBy1Properties("CLIENT_NAME", clientname);
        if (configItem != null) {
            return new ResponseEntity<>(configItem.COLLECTION_NAME, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<byte[]> getBanner(String collectionname) {
        return new ResponseEntity<>(FileUtils.read(THUMBNAILS_DIR + "/" + collectionname + "/Banner.png"), HttpStatus.OK);
    }

    public ResponseEntity<byte[]> getLogo(String collectionname) {
        return new ResponseEntity<>(FileUtils.read(THUMBNAILS_DIR + "/" + collectionname + "/Logo.png"), HttpStatus.OK);
    }

    public ResponseEntity<byte[]> getHeaderImage(String collectionname) {
        return new ResponseEntity<>(FileUtils.read(THUMBNAILS_DIR + "/" + collectionname + "/HeaderImage.png"), HttpStatus.OK);
    }

    public ResponseEntity<byte[]> getLiveBigImage(String collectionname) {
        String filename = THUMBNAILS_DIR + "/" + collectionname + "/LiveBigImage.png";
        if (FileUtils.exists(filename)) {
            return new ResponseEntity<>(FileUtils.read(filename), HttpStatus.OK);
        } else {
            ConfigItem config = configDAO.findItemBy1Properties("COLLECTION_NAME", collectionname);
            List<List<BufferedImage>> LiveImagesCollection = new ArrayList<>();
            int LIVE_OFFSET = 8;
            int COL_COUNT = config.COLUMNS / LIVE_OFFSET;
            if ((config.COLUMNS) % LIVE_OFFSET != 0) {
                COL_COUNT = COL_COUNT + 1;
            }
            int ROW_COUNT = config.ROWS / LIVE_OFFSET;
            if ((config.ROWS) % LIVE_OFFSET != 0) {
                ROW_COUNT = ROW_COUNT + 1;
            }
            int imageHeight = 0;
            int imageWidth = 0;
            //Rows
            for (int R = 1; R <= ROW_COUNT; R++) {
                imageWidth = 0;
                List<BufferedImage> row = new ArrayList<>();
                //Columns
                for (int C = 1; C <= COL_COUNT; C++) {
                    String imagename = "C" + C + "L" + R + ".png";
                    try {
                        BufferedImage image = createImageFromBytes(thumbnailServiceHandler(config, imagename, Levels.Live));
                        imageWidth = imageWidth + image.getWidth();
                        row.add(image);
                    } catch (IOException e) {
                        String base64Image = ImageUtils.B64_BlackImage.split(",")[1];
                        byte[] img = Base64.getDecoder().decode(base64Image);
                        
                        row.add(createImageFromBytes(img));
                    }
                }
                imageHeight = imageHeight + row.get(0).getHeight();
                LiveImagesCollection.add(row);
            }
            BufferedImage resImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resImage.createGraphics();
            ExecutorService executor = AppConfig.executorType();
            for (int i = 0; i < ROW_COUNT; i++) {
                List<BufferedImage> currRow = LiveImagesCollection.get(i);
                for (int j = 0; j < COL_COUNT; j++) {
                    try {
                        int finalJ = j;
                        int finalI = i;
                        executor.submit(() -> {
                            g2.drawImage(currRow.get(finalJ), null, (config.THUMBNAIL_SIZE * finalJ) + (finalJ * config.IMAGE_OFFSET), (config.THUMBNAIL_SIZE * finalI) + (finalI * config.IMAGE_OFFSET));
                        });
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            awaitTerminationAfterShutdown(executor);
            g2.dispose();
            /**
             * Putting on top of other one
             */
            BufferedImage banner = ImageUtils.createImageFromBytes(FileUtils.read(THUMBNAILS_DIR + "/" + collectionname + "/Banner.png"));
            banner = ImageUtils.resizeImage(banner, resImage.getWidth(), resImage.getHeight());
            Graphics2D gFinal = banner.createGraphics();
            gFinal.drawImage(resImage, null, 0, 0);
            gFinal.dispose();
            //-----------------------------
            byte[] img = new byte[0];
            try {
                img = ImageUtils.toByteArray(banner);
                FileUtils.writeFile(img, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(img, HttpStatus.OK);
        }
    }

    public ResponseEntity<byte[]> getImage(String collectionname, String uuid) {
        String filename = THUMBNAILS_DIR + "/" + collectionname + "/ORIGINAL/" + uuid + ".png";
        return new ResponseEntity<>(FileUtils.read(filename), HttpStatus.OK);
    }
}
