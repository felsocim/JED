/**
 * Project: CD Starter
 * Created by Marek Felsoci on 7.12.2016 as a part of university studies.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

class Vectorization extends Kernel {
    private List<Segment> segments;
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

    @Override
    public void processImage() {
        int width = this.getImage().getWidth();
        int height = this.getImage().getHeight();
        int i, j;
        int[][] pixels = new int[width][height];
        int white = Color.WHITE.getRGB();
        int black = Color.BLACK.getRGB();
        int nbWhitePixels = 0;

        BufferedImage temp = this.getImage();

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
                    //Direction: DROITE
                    while ((x2 + 1) < width && pixels[x2 + 1][y2] == white) {
                        pixels[x2 + 1][y2] = black;
                        nbWhitePixels--;
                        x2++;
                    }
                    if (x2 != x1) {
                        this.segments.add(new Segment(x1, x2, y1, y2));
                    }
                    x2 = x1;
                    y2 = y1;
                    //Direction: DIAGONALE DROITE
                    while ((x2 + 1) < width && (y2 + 1) < height && pixels[x2 + 1][y2 + 1] == white) {
                        pixels[x2 + 1][y2 + 1] = black;
                        nbWhitePixels--;
                        x2++;
                        y2++;
                    }
                    if (x2 != x1 && y2 != y1) {
                        this.segments.add(new Segment(x1, x2, y1, y2));
                    }
                    x2 = x1;
                    y2 = y1;
                    //Direction: BAS
                    while ((y2 + 1) < height && pixels[x2][y2 + 1] == white) {
                        pixels[x2][y2 + 1] = black;
                        nbWhitePixels--;
                        y2++;
                    }
                    if (y2 != y1) {
                        this.segments.add(new Segment(x1, x2, y1, y2));
                    }
                    x2 = x1;
                    y2 = y1;
                    //Direction: DIAGONALE GAUCHE
                    while ((x2 - 1) > 0 && (y2 + 1) < height && pixels[x2 - 1][y2 + 1] == white) {
                        pixels[x2 - 1][y2 + 1] = black;
                        nbWhitePixels--;
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

    public void draw() {
        Graphics2D drawing = this.output.createGraphics();
        drawing.setColor(Color.BLACK);
        Iterator<Segment> segmentIterator = this.getSegments().iterator();
        while(segmentIterator.hasNext()) {
            Segment temp = segmentIterator.next();
            drawing.drawLine(temp.getX1(), temp.getY1(), temp.getX2(), temp.getY2());
        }
        /*try {
            ImageIO.write(this.output, "PNG", new File("out.png"));
        } catch (IOException e) {
            System.out.println("Error saving drawing: " + e.toString());
        }*/
    }

    public void export(String filename) {
        FileWriter out = null;

        try {
            out = new FileWriter(filename);
        } catch (IOException e) {
            System.out.println("I/O Error saving output document: " + e.toString());
        }

        BufferedWriter buffer = new BufferedWriter(out);

        try {
            buffer.write("<svg xmlns=\"http://www.w3.org/2000/svg\">");
        } catch (IOException e) {
            System.out.println("I/O Error saving output document (buffer level): " + e.toString());
        }

        Iterator<Segment> segmentIterator = this.getSegments().iterator();

        while(segmentIterator.hasNext()) {
            Segment temp = segmentIterator.next();
            try {
                buffer.write("<line x1=\"" + temp.getX1() + "\" y1=\"" + temp.getY1() + "\" x2=\"" + temp.getX2() + "\" y2=\"" + temp.getY2() + "\" stroke=\"black\" stroke-width=\"1\" />");
            } catch (IOException e) {
                System.out.println("I/O Error saving output document (buffer level): " + e.toString());
            }
        }

        try {
            buffer.close();
        } catch (IOException e) {
            System.out.println("I/O Error saving output document (buffer close): " + e.toString());
        }

        try {
            out.close();
        } catch (IOException e) {
            System.out.println("I/O Error saving output document (filewriter close): " + e.toString());
        }
    }
}
