package Main.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static void writeFile(byte[] image, String pathname) {
        try {
            Path filePath = Paths.get(pathname);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, image);
        } catch (Exception e) {
            System.err.println("File " + pathname + " not written. " + e.getMessage());
        }
    }

    public static void delete(String pathname) {
        try {
            Path fileToDeletePath = Paths.get(pathname);
            Files.delete(fileToDeletePath);
        } catch (Exception e) {
            System.err.println("File " + pathname + " not exist. " + e.getMessage());
        }
    }

    public static void deleteFolder(String pathname) {
        try {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(new File(pathname));
        } catch (Exception e) {
            System.err.println("File " + pathname + " not exist. " + e.getMessage());
        }
    }

    public static byte[] read(String pathname) {
        try {
            Path fileToRead = Paths.get(pathname);
            return Files.readAllBytes(fileToRead);
        } catch (Exception e) {
            System.err.println("File " + pathname + " can't be read. " + e.getMessage());
            return null;
        }
    }

    public static boolean exists(String pathname) {
        try {
            Path fileToCheck = Paths.get(pathname);
            return Files.exists(fileToCheck);
        } catch (Exception e) {
            System.err.println("File " + pathname + " can't be read. " + e.getMessage());
            return false;
        }
    }
}
