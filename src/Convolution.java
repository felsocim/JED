/*
 * JED - Java Edge Detector
 *
 * Convolution filtering and simple edge detection program.
 *
 * Copyright (C) 2016  Marek Felsoci
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Abstract mother class for all supported convolution filters users can apply to a source image.
 */
abstract class Convolution extends Kernel{
  /**
   * Convolution matrices
   */
  protected int[][] filterX;
  protected int[][] filterY;

  /**
   * Black and white threshold
   * @see Convolution#applyThreshold()
   */
  private int threshold;

  /**
   * Filter matrix size (e. g. 3x3 for the Sobel filter).
   */
  private int matrixSize;

  /**
   * Create new convolution filter instace based on provided source image and filter matrix size.
   * @param source Source image to apply filter(s) to.
   * @param matrixSize Size of the filter matrix (e. g. 3x3 for the Sobel filter).
   */
  public Convolution(BufferedImage source, int matrixSize) {
    super(source);
    this.matrixSize = matrixSize;
    this.filterX = new int[matrixSize][matrixSize];
    this.filterY = new int[matrixSize][matrixSize];
    this.threshold = -1;
  }

  /**
   * Same as the default constructor but allows to define the value for black and white threshold as well.
   *
   * @see Convolution
   *
   * @param source Source image to apply filter(s) to.
   * @param threshold Black and white threshold value.
   * @param matrixSize Size of the filter matrix (e. g. 3x3 for the Sobel filter).
   */
  public Convolution(BufferedImage source, int threshold, int matrixSize) {
    super(source);
    this.matrixSize = matrixSize;
    this.filterX = new int[matrixSize][matrixSize];
    this.filterY = new int[matrixSize][matrixSize];
    this.threshold = threshold;
  }

  /**
   * Setter for filter matrices (X and Y axis).
   * @param filterX X-axis filter matrix.
   * @param filterY Y-axis filter matrix.
   */
  public void setConvolutionMatrix(int[][] filterX, int[][] filterY) {
    int i, j;

    for(i = 0;i < this.matrixSize;i++) {
      for(j = 0;j < this.matrixSize;j++) {
        this.filterX[i][j] = filterX[i][j];
        this.filterY[i][j] = filterY[i][j];
      }
    }
  }

  /**
   * Apply selected filter (depending on the child class calling the method) to source image
   * using provided filter matrices.
   */
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

        int pixel = (int) Math.sqrt(pixelX * pixelX + pixelY * pixelY);

        if(pixel < 0) {
          pixels[i][j] = 0;
        }
        else if(pixel > 255) {
          pixels[i][j] = 255;
        }
        else {
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

  /**
   * Apply a threshold to a grayscaled image such that all the pixels with color value
   * less than the threshold become black and all the pixels with color value greater than
   * the threshold become white.
   */
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
