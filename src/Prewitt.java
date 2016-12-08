/**
 * Project: CD Starter
 * Created by Marek Felsoci on 7.12.2016 as a part of university studies.
 */
import java.awt.image.BufferedImage;

class Prewitt extends Convolution {
    public Prewitt(BufferedImage source) {
        super(source, 3);
    }

    public Prewitt(BufferedImage source, int threshold) {
        super(source, threshold, 3);
    }

    public void applyPrewitt() {
        int[][] filterX = {
                { -1, 0, 1 },
                { -1, 0, 1 },
                { -1, 0, 1 }
        };
        int[][] filterY = {
                { -1, -1, -1 },
                { 0, 0, 0 },
                { 1, 1, 1 }
        };

        this.setConvolutionMatrix(filterX, filterY);

        this.processImage();
    }
}
