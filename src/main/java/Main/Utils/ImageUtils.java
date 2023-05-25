package Main.Utils;

import Main.Beans.CollageItem;
import Main.Beans.ConfigItem;
import Main.Beans.Levels;
import Main.Config.AppConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static Main.Config.AppConfig.THUMBNAILS_DIR;
import static Main.Utils.CollageUtils.awaitTerminationAfterShutdown;
import static org.imgscalr.Scalr.Method;
import static org.imgscalr.Scalr.resize;

public class ImageUtils {
    public static String B64_BlackImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKAAAACgCAIAAAAErfB6AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAABiSURBVHhe7cExAQAAAMKg9U9tCU8gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC4qQEsrwABoWuV8gAAAABJRU5ErkJggg==";

    public static BufferedImage joinBufferedImages(List<List<BufferedImage>> imagesMatrix, int SingleImageWidth, int SingleImageHeight, int IMAGE_OFFSET) {
        /**
         * We must calculate the new Width and height of images
         * DISCLAIMER : All Images must have same height & width
         */
        int rows = imagesMatrix.size();
        int rowsSize = (SingleImageWidth * rows) + ((rows - 1) * IMAGE_OFFSET);
        int columns = imagesMatrix.get(0).size();
        int columnsSize = (SingleImageHeight * columns) + ((columns - 1) * IMAGE_OFFSET);
        BufferedImage newImage = new BufferedImage(columnsSize, rowsSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        ExecutorService executor = AppConfig.executorType();
        for (int i = 0; i < rows; i++) {
            List<BufferedImage> currRow = imagesMatrix.get(i);
            for (int j = 0; j < columns; j++) {
                try {
                    int finalJ = j;
                    int finalI = i;
                    executor.submit(() -> {
                        g2.drawImage(createThumbnail(currRow.get(finalJ), SingleImageWidth, SingleImageHeight), null, (SingleImageHeight * finalJ) + (finalJ * IMAGE_OFFSET), (SingleImageWidth * finalI) + (finalI * IMAGE_OFFSET));
                    });
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        awaitTerminationAfterShutdown(executor);
        g2.dispose();
        return newImage;
    }

    public static BufferedImage createThumbnail(BufferedImage img, int size) {
        return resize(img, Method.AUTOMATIC, size);
    }

    public static BufferedImage resizeImage(BufferedImage img, int targetWidth, int targetHeight) {
        return resize(img, targetWidth, targetHeight);
    }

    private static BufferedImage createThumbnail(BufferedImage img, int targetWidth, int targetHeight) {
        return resize(img, Method.AUTOMATIC, targetWidth, targetHeight);
    }

    public static byte[] toByteArray(BufferedImage bi) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos.toByteArray();
    }

    public static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int imageNameToColumns(String s) {
        return Integer.parseInt(s.substring(s.indexOf("C") + 1, s.indexOf("L")));
    }

    public static int imageNameToRow(String s) {
        return Integer.parseInt(s.substring(s.indexOf("L") + 1, s.indexOf(".")));
    }

    public static List<List<BufferedImage>> fetchThumbnailImages(ConfigItem config, Levels level, String imagename, List<CollageItem> items) {
        int CONFIG_COL = config.COLUMNS;
        int CONFIG_ROW = config.ROWS;
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
        List<List<BufferedImage>> BufferedImageList = new ArrayList<List<BufferedImage>>();
        int RECEIVED_COL = ImageUtils.imageNameToColumns(imagename);
        int RECEIVED_ROW = ImageUtils.imageNameToRow(imagename);
        int BEGIN_COL = (OFFSET * RECEIVED_COL) - (OFFSET - 1);
        int BEGIN_ROW = (OFFSET * RECEIVED_ROW) - (OFFSET - 1);
        int END_COL = BEGIN_COL + (OFFSET - 1);
        int END_ROW = BEGIN_ROW + (OFFSET - 1);

        for (int ROW_IND = BEGIN_ROW, MAT_ROW_IND = 0; ROW_IND <= END_ROW && ROW_IND <= CONFIG_ROW; ROW_IND++, MAT_ROW_IND++) {
            List<BufferedImage> newItem = new ArrayList<BufferedImage>();
            for (int COL_IND = BEGIN_COL, MAT_COL_IND = 0; COL_IND <= END_COL && COL_IND <= CONFIG_COL; COL_IND++, MAT_COL_IND++) {
                BufferedImage item = getImageAt(items, ROW_IND, COL_IND, level);
                newItem.add(item);
            }
            BufferedImageList.add(newItem);
        }
        return BufferedImageList;
    }

    private static BufferedImage getImageAt(List<CollageItem> items, int row, int col, Levels level) {
        String base64Image = ImageUtils.B64_BlackImage.split(",")[1];
        
        
        
        byte[] bytes = Base64.getDecoder().decode(base64Image); 
        
        
        final BufferedImage[] res = {ImageUtils.createImageFromBytes(bytes)};
        
        
        

        
        
        
        ExecutorService executor = AppConfig.executorType();
        for (CollageItem item : items) {
            executor.submit(() -> {
                if ((item.COLUMN_POS == col) && (item.ROW_POS == row)) {
                    try {
                        String filename = THUMBNAILS_DIR + "/" + item.CollectionName + "/ORIGINAL/" + item.uuid + ".png";
                        switch (level) {
                            case L17:
                                res[0] = setTransparent(ImageUtils.createImageFromBytes(FileUtils.read(filename)), 0.05f);
                                break;
                            case L18:
                                res[0] = setTransparent(ImageUtils.createImageFromBytes(FileUtils.read(filename)), 0.3f);
                                break;
                            case L19:
                                res[0] = setTransparent(ImageUtils.createImageFromBytes(FileUtils.read(filename)), 0.5f);
                                break;
                            //Live & L20
                            case L20:
                                res[0] = ImageUtils.createImageFromBytes(FileUtils.read(filename));
                                break;
                            case Live:
                                res[0] = setTransparent(ImageUtils.createImageFromBytes(FileUtils.read(filename)), 0.05f);
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("Error, returning black image.");
                    }
                }
            });
        }
        awaitTerminationAfterShutdown(executor);
        return res[0];
    }

    public static String locateImageThumbnail(Levels level, String imagename) {
        int OFFSET = 0;
        int RECEIVED_COL = ImageUtils.imageNameToColumns(imagename);
        int RECEIVED_ROW = ImageUtils.imageNameToRow(imagename);
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
                return imagename;
        }
        int COL = ((RECEIVED_COL - 1) / OFFSET) + 1;
        int ROW = ((RECEIVED_ROW - 1) / OFFSET) + 1;
        return "C" + COL + "L" + ROW + ".png";
    }

    private static BufferedImage setTransparent(BufferedImage source, Float a) {
        float[] alpha = {1f, 1f, 1f, a};  // R, G, B, A
        float[] def = {0, 0, 0, 0};   // R, G, B, A
        return new RescaleOp(alpha, def, null).filter(source, null);
    }
}
