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

/**
 * Ultimate base class for both convolution filtering and vectorization containing source image and grayscale
 * conversion method useful in both cases.
 */
abstract class Kernel {
  /**
   * Image user will apply filter(s) (and vectorize).
   */
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

  /**
   * Converts source image to grayscale.
   */
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

  /**
   * Common method name for applying a filter or vectorization.
   */
  public abstract void processImage();
}
