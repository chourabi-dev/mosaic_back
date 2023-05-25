package Main.Beans;

import Main.Utils.CollageUtils;

public class ConfigItem {
    public String id;
    public String COLLECTION_NAME;
    public String CLIENT_NAME;
    public String BASE64_HEADERIMAGE;
    public String BASE64_BANNER;
    public String BASE64_LOGO;
    public String COLOR;
    public int THUMBNAIL_SIZE;
    public int COLUMNS;
    public int ROWS;
    public int IMAGE_OFFSET;

    public ConfigItem(String CLIENT_NAME, String BASE64_HEADERIMAGE, String BASE64_BANNER, String BASE64_LOGO, int COLUMNS, int ROWS, int IMAGE_OFFSET, int THUMBNAIL_SIZE, String COLOR) {
        this.id = CollageUtils.RandomString(8);
        this.COLLECTION_NAME = this.id;
        this.CLIENT_NAME = CLIENT_NAME;
        this.BASE64_HEADERIMAGE = BASE64_HEADERIMAGE;
        this.BASE64_BANNER = BASE64_BANNER;
        this.BASE64_LOGO = BASE64_LOGO;
        this.COLUMNS = COLUMNS;
        this.ROWS = ROWS;
        this.COLOR = COLOR;
        this.IMAGE_OFFSET = IMAGE_OFFSET;
        this.THUMBNAIL_SIZE = THUMBNAIL_SIZE;
    }

    public ConfigItem() {
        this.id = CollageUtils.RandomString(8);
        this.COLLECTION_NAME = this.id;
    }

    public static ConfigItem createFromNew(NewConfigRequest newEntry) {
        ConfigItem item = new ConfigItem();
        item.CLIENT_NAME = newEntry.CLIENT_NAME;
        item.BASE64_HEADERIMAGE = newEntry.BASE64_HEADERIMAGE;
        item.BASE64_BANNER = newEntry.BASE64_BANNER;
        item.BASE64_LOGO = newEntry.BASE64_LOGO;
        item.COLUMNS = newEntry.COLUMNS;
        item.ROWS = newEntry.ROWS;
        item.COLOR = newEntry.COLOR;
        item.IMAGE_OFFSET = newEntry.IMAGE_OFFSET;
        item.THUMBNAIL_SIZE = newEntry.THUMBNAIL_SIZE;
        return item;
    }

    @Override
    public String toString() {
        return "ConfigItem{" +
                "id='" + id + '\'' +
                ", COLLECTION_NAME='" + COLLECTION_NAME + '\'' +
                ", CLIENT_NAME='" + CLIENT_NAME + '\'' +
                ", BASE64_HEADERIMAGE='" + BASE64_HEADERIMAGE + '\'' +
                ", BASE64_BANNER='" + BASE64_BANNER + '\'' +
                ", BASE64_LOGO='" + BASE64_LOGO + '\'' +
                ", COLOR='" + COLOR + '\'' +
                ", THUMBNAIL_SIZE=" + THUMBNAIL_SIZE +
                ", COLUMNS=" + COLUMNS +
                ", ROWS=" + ROWS +
                ", IMAGE_OFFSET=" + IMAGE_OFFSET +
                '}';
    }
}
