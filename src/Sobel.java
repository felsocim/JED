/**
 * Project: CD Starter
 * Created by Marek Felsoci on 27.11.2016 as a part of university studies.
 */
import java.awt.image.BufferedImage;

class Sobel extends Convolution {
    public Sobel(BufferedImage source) {
        super(source, 3);
    }

    public Sobel(BufferedImage source, int threshold) {
        super(source, threshold, 3);
    }

    public void applySobel() {
        int[][] filterX = {
                { -1, 0, 1 },
                { -2, 0, 2 },
                { -1, 0, 1 }
        };
        int[][] filterY = {
                { -1, -2, -1 },
                { 0, 0, 0 },
                { 1, 2, 1 }
        };

        this.setConvolutionMatrix(filterX, filterY);

        this.processImage();
    }
}
