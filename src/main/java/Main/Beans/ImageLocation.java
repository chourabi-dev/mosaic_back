package Main.Beans;

public class ImageLocation {
    public int LINE_POS;
    public int COLUMN_POS;

    public ImageLocation(int LINE_POS, int COLUMN_POS) {
        this.LINE_POS = LINE_POS;
        this.COLUMN_POS = COLUMN_POS;
    }

    @Override
    public String toString() {
        return "ImageLocation{" +
                "ROW_POS=" + LINE_POS +
                ", COLUMN_POS=" + COLUMN_POS +
                '}';
    }
}
