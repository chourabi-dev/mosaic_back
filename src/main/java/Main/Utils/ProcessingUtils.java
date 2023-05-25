package Main.Utils;

import Main.Beans.CollageItem;
import Main.Beans.ConfigItem;
import Main.Beans.Levels;
import Main.DataRepository.ImagesCollectionsDAO;

import java.util.concurrent.ExecutorService;

import static Main.Config.AppConfig.THUMBNAILS_DIR;

public class ProcessingUtils {

    public static void ThumbnailsGenerationService(ExecutorService executor, ImagesCollectionsDAO imagesCollectionsDAO, ConfigItem configItem, CollageItem res) {
        //Thumbnails
        for (Levels l : Levels.values()) {
            executor.submit(() -> {
                Levels level = l;
                String currentImageName = "C" + res.COLUMN_POS + "L" + res.ROW_POS + ".png";
                String targetImageName = ImageUtils.locateImageThumbnail(level, currentImageName);
                try {
                    byte[] generatedImage = ImageUtils.toByteArray(ImageUtils.createThumbnail(ImageUtils.joinBufferedImages(ImageUtils.fetchThumbnailImages(configItem, level, targetImageName, imagesCollectionsDAO.findAllForThumbnail(configItem.COLLECTION_NAME, targetImageName, level)), configItem.THUMBNAIL_SIZE, configItem.THUMBNAIL_SIZE, configItem.IMAGE_OFFSET), configItem.THUMBNAIL_SIZE));
                    String ImagePath = THUMBNAILS_DIR + "/" + configItem.COLLECTION_NAME + "/" + level.name() + "/" + targetImageName;
                    FileUtils.delete(ImagePath);
                    FileUtils.writeFile(generatedImage, ImagePath);
                } catch (Exception e) {
                    System.err.println("Error while creating the thumbnails. " + e.getLocalizedMessage());
                }
            });
        }
    }
}
