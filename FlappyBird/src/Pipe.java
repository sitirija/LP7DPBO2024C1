import java.awt.*;

public class Pipe {
    private int posX;
    private int posY;
    private int width;
    private int height;
    private Image image;
    private int veloncityX;
    boolean passed = false;
    private int gap; // Jarak antara pipa atas dan pipa bawah

    public Pipe(int posX, int posY, int width, int height, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;

        this.veloncityX = -3;
        this.passed = false;
        this.gap = gap;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getVeloncityX() {
        return veloncityX;
    }

    public void setVeloncityX(int veloncityX) {
        this.veloncityX = veloncityX;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    // Getter dan setter untuk properti gap
    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    // Metode untuk menghitung posisi pipa bawah berdasarkan posisi pipa atas dan jarak gap
    public int getBottomPipePosY() {
        return posY + height + gap;
    }
}
