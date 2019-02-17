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
 * Roberts filter class.
 */
class Roberts extends Convolution {
  public Roberts(BufferedImage source) {
    super(source, 2);
  }

  public Roberts(BufferedImage source, int threshold) {
    super(source, threshold, 2);
  }

  /**
   * Redefinition of the filter applying method because Roberts filter uses matrices of size
   * 2x2 and not 3x3 as the other filters.
   *
   * @see Convolution#processImage()
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

    for(i = 0; i < (width - 1); i++) {
      for (j = 0; j < (height - 1); j++) {
        int pixelX = (pixels[i][j] * this.filterX[0][0]) + (pixels[i + 1][j] * this.filterX[1][0]) +
          (pixels[i][j + 1] * this.filterX[0][1]) + (pixels[i + 1][j + 1] * this.filterX[1][1]);

        int pixelY = (pixels[i][j] * this.filterY[0][0]) + (pixels[i + 1][j] * this.filterY[1][0]) +
          (pixels[i][j + 1] * this.filterY[0][1]) + (pixels[i + 1][j + 1] * this.filterY[1][1]);

        int pixel = (int) Math.sqrt(pixelX*pixelX + pixelY*pixelY);

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
   * Set filter matrices and apply the filter.
   */
  public void applyRoberts() {
    int[][] filterX = {
      { 1, 0 },
      { 0, -1 }
    };
    int[][] filterY = {
      { 0, 1 },
      { -1, 0 }
    };

    this.setConvolutionMatrix(filterX, filterY);
    this.processImage();
  }
}
