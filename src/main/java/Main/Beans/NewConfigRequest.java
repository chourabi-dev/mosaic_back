package Main.Beans;

public class NewConfigRequest {
    public String CLIENT_NAME;
    public String BASE64_HEADERIMAGE;
    public String BASE64_BANNER;
    public String BASE64_LOGO;
    public String COLOR;
    public int COLUMNS;
    public int ROWS;
    public int IMAGE_OFFSET;
    public int THUMBNAIL_SIZE;

    public NewConfigRequest(String CLIENT_NAME, String BASE64_HEADERIMAGE, String BASE64_BANNER, String BASE64_LOGO, String COLOR, int COLUMNS, int ROWS, int IMAGE_OFFSET, int THUMBNAIL_SIZE) {
        this.CLIENT_NAME = CLIENT_NAME;
        this.BASE64_HEADERIMAGE = BASE64_HEADERIMAGE;
        this.BASE64_BANNER = BASE64_BANNER;
        this.BASE64_LOGO = BASE64_LOGO;
        this.COLOR = COLOR;
        this.COLUMNS = COLUMNS;
        this.ROWS = ROWS;
        this.IMAGE_OFFSET = IMAGE_OFFSET;
        this.THUMBNAIL_SIZE = THUMBNAIL_SIZE;
    }

    public NewConfigRequest() {
    }
}
