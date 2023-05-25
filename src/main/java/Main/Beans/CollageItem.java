package Main.Beans;

import java.util.UUID;

import static Main.Config.AppConfig.SERVERURI;

public class CollageItem {
    public String uuid;
    public String nom;
    public String message;
    public String email;
    public String CollectionName;
    public String ImageName;
    public String NextUuid;
    public long insertTimeDate;
    public int ROW_POS;
    public int COLUMN_POS;

    public CollageItem() {
        this.uuid = UUID.randomUUID().toString();
    }

    public static CollageItem createFromNew(NewImageRequest newEntry, String ColName) {
        CollageItem item = new CollageItem();
        item.email = newEntry.email;
        item.nom = newEntry.nom;
        item.message = newEntry.message;
        item.CollectionName = ColName;
        item.ImageName = SERVERURI + "/" + ColName + "/" + item.uuid + "/" + "Image.png";
        return item;
    }

    @Override
    public String toString() {
        return "CollageItem{" +
                "uuid='" + uuid + '\'' +
                ", nom='" + nom + '\'' +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", CollectionName='" + CollectionName + '\'' +
                ", ImageName='" + ImageName + '\'' +
                ", NextUuid='" + NextUuid + '\'' +
                ", insertTimeDate=" + insertTimeDate +
                ", ROW_POS=" + ROW_POS +
                ", COLUMN_POS=" + COLUMN_POS +
                '}';
    }
}
