/**
 * Project: CD Starter
 * Created by Marek Felsoci on 27.11.2016 as a part of university studies.
 */
import java.awt.image.BufferedImage;

abstract class Kernel {
    private BufferedImage image;

    public Kernel(BufferedImage source) {
        this.image = source;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void convertToGrayscale()
    {
        int width = this.image.getWidth();
        int height = this.image.getHeight();

        BufferedImage temp = this.image;

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                int pixel = temp.getRGB(i, j);
                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                int average = (red + green + blue) / 3;

                pixel = (alpha << 24) | (average << 16) | (average << 8) | average;

                temp.setRGB(i, j, pixel);
            }
        }

        this.setImage(temp);
    }

    public abstract void processImage();
}
