import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Project: CD Starter
 * Created by Marek Felsoci on 7.12.2016 as a part of university studies.
 */
abstract class Convolution extends Kernel{
    protected int[][] filterX;
    protected int[][] filterY;
    private int threshold;
    private int matrixSize;

    public Convolution(BufferedImage source, int matrixSize) {
        super(source);
        this.matrixSize = matrixSize;
        this.filterX = new int[matrixSize][matrixSize];
        this.filterY = new int[matrixSize][matrixSize];
        this.threshold = -1;
    }

    public Convolution(BufferedImage source, int threshold, int matrixSize) {
        super(source);
        this.matrixSize = matrixSize;
        this.filterX = new int[matrixSize][matrixSize];
        this.filterY = new int[matrixSize][matrixSize];
        this.threshold = threshold;
    }

    public void setConvolutionMatrix(int[][] filterX, int[][] filterY) {
        int i, j;

        for(i = 0;i < this.matrixSize;i++) {
            for(j = 0;j < this.matrixSize;j++) {
                this.filterX[i][j] = filterX[i][j];
                this.filterY[i][j] = filterY[i][j];
            }
        }
    }

    @Override
    public void processImage() {
        int width = this.getImage().getWidth();
        int height = this.getImage().getHeight();
        int i, j;
        int[][] pixels = new int[width][height];

        this.convertToGrayscale();

        BufferedImage temp = this.getImage();

        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                Color color = new Color(this.getImage().getRGB(i, j));
                pixels[i][j] = color.getRed();
            }
        }

        for(i = 0; i < (width - 2); i++) {
            for (j = 0; j < (height - 2); j++) {
                int pixelX = (pixels[i][j] * this.filterX[0][0]) + (pixels[i + 1][j] * this.filterX[0][1]) + (pixels[i + 2][j] * this.filterX[0][2]) +
                             (pixels[i][j + 1] * this.filterX[1][0]) + (pixels[i + 1][j + 1] * this.filterX[1][1]) + (pixels[i + 2][j + 1] * this.filterX[1][2]) +
                             (pixels[i][j + 2] * this.filterX[2][0]) + (pixels[i + 1][j + 2] * this.filterX[2][1]) + (pixels[i + 2][j + 2] * this.filterX[2][2]);

                int pixelY = (pixels[i][j] * this.filterY[0][0]) + (pixels[i + 1][j] * this.filterY[0][1]) + (pixels[i + 2][j] * this.filterY[0][2]) +
                             (pixels[i][j + 1] * this.filterY[1][0]) + (pixels[i + 1][j + 1] * this.filterY[1][1]) + (pixels[i + 2][j + 1] * this.filterY[1][2]) +
                             (pixels[i][j + 2] * this.filterY[2][0]) + (pixels[i + 1][j + 2] * this.filterY[2][1]) + (pixels[i + 2][j + 2] * this.filterY[2][2]);

                int pixel = (int) Math.sqrt(pixelX*pixelX + pixelY*pixelY);

                if(pixel < 0)
                {
                    pixels[i][j] = 0;
                }
                else if(pixel > 255)
                {
                    pixels[i][j] = 255;
                }
                else
                {
                    pixels[i][j] = pixel;
                }
            }
        }

        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                Color color = new Color(pixels[i][j], pixels[i][j], pixels[i][j]);
                int rgb = color.getRGB();
                temp.setRGB(i, j, rgb);
            }
        }

        this.setImage(temp);
    }

    public void applyThreshold() {
        if(this.threshold < 0 || this.threshold > 255)
            return;

        int width = this.getImage().getWidth();
        int height = this.getImage().getHeight();
        int i, j;
        BufferedImage temp = this.getImage();

        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                Color color = new Color(temp.getRGB(i, j));
                if(color.getRed() > this.threshold )
                    temp.setRGB(i, j, Color.WHITE.getRGB());
                else
                    temp.setRGB(i, j, Color.BLACK.getRGB());
            }
        }

        this.setImage(temp);
    }
}
