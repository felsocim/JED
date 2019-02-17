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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Our vectorization strategy is very straightforward. Starting with a thresholded image we iterate over, we try
 * to detect segments by inspecting neighbor points in three directions (right, diagonal right, down and diagonal left).
 * Finally we take the detected segments and redraw the source image as a scalable vector graphics (SVG).
 */
class Vectorization extends Kernel {
  /**
   * List of detected segments.
   */
  private List<Segment> segments;

  /**
   * Vectorized image.
   */
  private BufferedImage output;

  public Vectorization(BufferedImage thresholded) {
    super(thresholded);
    this.segments = new ArrayList<>();
    this.output = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
  }

  public List<Segment> getSegments() {
    return segments;
  }

  public BufferedImage getOutput() {
    return output;
  }

  /**
   * Implementation of our vectorization algorithm.
   * @see Vectorization
   */
  @Override
  public void processImage() {
    int width = this.getImage().getWidth();
    int height = this.getImage().getHeight();
    int i, j;
    int[][] pixels = new int[width][height];
    int white = Color.WHITE.getRGB();
    int black = Color.BLACK.getRGB();

    for(i = 0; i < width; i++) {
      for(j = 0; j < height; j++) {
        pixels[i][j] = this.getImage().getRGB(i, j);
      }
    }

    for(i = 1;i < width;i++) {
      for(j = 1;j < height;j++) {
        if (pixels[i][j] == white) {
          int x1 = i, y1 = j;
          int x2 = x1, y2 = y1;

          // Right direction of segment detection
          while ((x2 + 1) < width && pixels[x2 + 1][y2] == white) {
            pixels[x2 + 1][y2] = black;
            x2++;
          }

          if (x2 != x1) {
            this.segments.add(new Segment(x1, x2, y1, y2));
          }

          x2 = x1;
          y2 = y1;

          // Right diagonal direction of segment detection
          while ((x2 + 1) < width && (y2 + 1) < height && pixels[x2 + 1][y2 + 1] == white) {
            pixels[x2 + 1][y2 + 1] = black;
            x2++;
            y2++;
          }

          if (x2 != x1 && y2 != y1) {
            this.segments.add(new Segment(x1, x2, y1, y2));
          }

          x2 = x1;
          y2 = y1;

          // Down direction of segment detection
          while ((y2 + 1) < height && pixels[x2][y2 + 1] == white) {
            pixels[x2][y2 + 1] = black;
            y2++;
          }

          if (y2 != y1) {
            this.segments.add(new Segment(x1, x2, y1, y2));
          }

          x2 = x1;
          y2 = y1;

          // Left diagonal direction of segment detection
          while ((x2 - 1) > 0 && (y2 + 1) < height && pixels[x2 - 1][y2 + 1] == white) {
            pixels[x2 - 1][y2 + 1] = black;
            x2--;
            y2++;
          }

          if (x2 != x1 && y2 != y1) {
            this.segments.add(new Segment(x1, x2, y1, y2));
          }
        }
      }
    }
  }

  /**
   * Redraw the image based on detected segments.
   */
  public void draw() {
    Graphics2D drawing = this.output.createGraphics();
    drawing.setColor(Color.BLACK);

    for (Segment s : this.getSegments()) {
      drawing.drawLine(s.getX1(), s.getY1(), s.getX2(), s.getY2());
    }
  }

  /**
   * Save vectorization results to a SVG file.
   * @param filename Output file name.
   */
  public void export(String filename) {
    FileWriter out;
    BufferedWriter buffer;

    try {
      out = new FileWriter(filename);
      buffer = new BufferedWriter(out);

      buffer.write("<svg xmlns=\"http://www.w3.org/2000/svg\">");

      for(Segment s : this.getSegments()) {
        buffer.write("<line x1=\"" + s.getX1() + "\" y1=\"" + s.getY1() + "\" x2=\"" + s.getX2() + "\" y2=\"" + s.getY2() + "\" stroke=\"black\" stroke-width=\"1\" />");
      }

      buffer.close();
      out.close();
    } catch (IOException e) {
      System.out.println("I/O Error while saving output document!\n\tError message: " + e.toString());
    }
  }
}
