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
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class of the user interface.
 */
public class Communicator extends JFrame {
  private JLabel lblTitle;
  private JLabel lblImagePath;
  private JTextField txfImagePath;
  private JPanel pnlMain;
  private JPanel pnlTitle;
  private JPanel pnlImagePath;
  private JButton btnBrowse;
  private JPanel pnlFilterSelection;
  private JLabel lblFilterSelect;
  private JRadioButton rdbFilterSobel;
  private JRadioButton rdbFilterPrewitt;
  private JRadioButton rdbFilterRoberts;
  private JButton btnFilterApply;
  private JPanel pnlThresholdChange;
  private JLabel lblThresholdChange;
  private JLabel lblThresholdMin;
  private JSlider sldThresholdChanger;
  private JLabel lblThresholdMax;
  private JLabel lblThresholdCurrentValue;
  private JPanel pnlImages;
  private JPanel pnlImagesTitles;
  private JLabel lblImagesTitle;
  private JLabel lblBeforeImage;
  private JLabel lblAfterImage;
  private JPanel pnlVect;
  private JPanel pnlImageProcessing;
  private JLabel lblVectTitle;
  private JPanel pnlVectorizedImage;
  private JPanel pnlVectSaveSVG;
  private JLabel lblVectResult;
  private JLabel lblSavePath;
  private JTextField txfVectSavePath;
  private JButton btnSelectSVGPath;
  private JPanel pnlVectSave;
  private JButton btnVectExport;
  private JPanel pnlProgramInfo;
  private JLabel lblProgramInfoContent;
  private JScrollPane pnlNew;
  private JPanel pnlBody;
  private File image;
  private final JFileChooser selectImage = new JFileChooser();
  private final JFileChooser saveImage = new JFileChooser();

  public Communicator() {
    super("JED (Java Edge Detector)");
    setContentPane(pnlMain);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
    String[] imageExtensions = new String[] {"png", "jpg", "jpeg", "bmp"};
    FileNameExtensionFilter imagesOnly = new FileNameExtensionFilter("Image files (*.png, *.jpg, *.jpeg, *.bmp)", imageExtensions);
    selectImage.setFileFilter(imagesOnly);
    selectImage.addChoosableFileFilter(imagesOnly);
    String[] svgExtension = new String[] {"svg"};
    FileNameExtensionFilter svgOnly = new FileNameExtensionFilter("SVG - Scalable Vector Graphics (*.svg)", svgExtension);
    saveImage.setFileFilter(svgOnly);
    saveImage.addChoosableFileFilter(svgOnly);

    btnBrowse.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int status = selectImage.showOpenDialog(pnlMain);

        if(status == JFileChooser.APPROVE_OPTION) {
          image = selectImage.getSelectedFile();

          txfImagePath.setText(selectImage.getSelectedFile().getAbsolutePath());
          ImageIcon beforeImage = new ImageIcon(image.toString());
          lblBeforeImage.setText("");
          lblBeforeImage.setIcon(beforeImage);
        }
      }
    });
    rdbFilterSobel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(rdbFilterSobel.isSelected()) {
          rdbFilterPrewitt.setSelected(false);
          rdbFilterRoberts.setSelected(false);
        }
      }
    });
    rdbFilterPrewitt.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(rdbFilterPrewitt.isSelected()) {
          rdbFilterSobel.setSelected(false);
          rdbFilterRoberts.setSelected(false);
        }
      }
    });
    rdbFilterRoberts.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(rdbFilterRoberts.isSelected()) {
          rdbFilterSobel.setSelected(false);
          rdbFilterPrewitt.setSelected(false);
        }
      }
    });
    btnFilterApply.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(image == null) {
          JOptionPane.showMessageDialog(pnlMain, "No source was selected!\nPlease, choose source image before performing any action.", "No Image Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if(!rdbFilterSobel.isSelected() && !rdbFilterPrewitt.isSelected() && !rdbFilterRoberts.isSelected()) {
          JOptionPane.showMessageDialog(pnlMain, "You have not chosen any of filters!\nPlease, choose a filter before applying it.", "No Filter Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        BufferedImage source = null;

        try {
          source = ImageIO.read(image);
        } catch (IOException exception) {
          System.out.println("I/O Error while reading selected image: " + exception.toString());
        }

        if(rdbFilterSobel.isSelected()) {
          Sobel sobel = new Sobel(source);
          sobel.applySobel();

          ImageIcon afterImage = new ImageIcon(sobel.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if(rdbFilterPrewitt.isSelected()) {
          Prewitt prewitt = new Prewitt(source);
          prewitt.applyPrewitt();

          ImageIcon afterImage = new ImageIcon(prewitt.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if(rdbFilterRoberts.isSelected()) {
          Roberts roberts = new Roberts(source);
          roberts.applyRoberts();

          ImageIcon afterImage = new ImageIcon(roberts.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }
      }
    });
    sldThresholdChanger.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if(image == null) {
          JOptionPane.showMessageDialog(pnlMain, "No source was selected!\nPlease, choose source image before performing any action.", "No Image Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if(!rdbFilterSobel.isSelected() && !rdbFilterPrewitt.isSelected() && !rdbFilterRoberts.isSelected()) {
          JOptionPane.showMessageDialog(pnlMain, "You have not chosen any of filters!\nPlease, choose a filter before applying it.", "No Filter Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        Integer currentThreshold = sldThresholdChanger.getValue();
        lblThresholdCurrentValue.setText(currentThreshold.toString());

        BufferedImage source = null;

        try {
          source = ImageIO.read(image);
        } catch (IOException exception) {
          System.out.println("I/O Error while reading selected image: " + exception.toString());
        }

        if(rdbFilterSobel.isSelected()) {
          Sobel sobel = new Sobel(source, currentThreshold);
          sobel.applySobel();
          sobel.applyThreshold();

          ImageIcon afterImage = new ImageIcon(sobel.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if(rdbFilterPrewitt.isSelected()) {
          Prewitt prewitt = new Prewitt(source, currentThreshold);
          prewitt.applyPrewitt();
          prewitt.applyThreshold();

          ImageIcon afterImage = new ImageIcon(prewitt.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if(rdbFilterRoberts.isSelected()) {
          Roberts roberts = new Roberts(source, currentThreshold);
          roberts.applyRoberts();
          roberts.applyThreshold();

          ImageIcon afterImage = new ImageIcon(roberts.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }
      }
    });
    btnVectExport.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(image == null) {
          JOptionPane.showMessageDialog(pnlMain, "No source was selected!\nPlease, choose source image before performing any action.", "No Image Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if(!rdbFilterSobel.isSelected() && !rdbFilterPrewitt.isSelected() && !rdbFilterRoberts.isSelected()) {
          JOptionPane.showMessageDialog(pnlMain, "You have not chosen any of filters!\nPlease, choose a filter before applying it.", "No Filter Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if(txfImagePath.getText().isEmpty()) {
          JOptionPane.showMessageDialog(pnlMain, "No destination file path specified!\nPlease, browse for the destination file before launching vectorization.", "No Destination File Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        Integer currentThreshold = sldThresholdChanger.getValue();
        String output = txfVectSavePath.getText();

        BufferedImage source = null;

        try {
          source = ImageIO.read(image);
        } catch (IOException exception) {
          JOptionPane.showMessageDialog(pnlMain, exception.getMessage(), "Failed to read source image!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if(rdbFilterSobel.isSelected()) {
          Sobel sobel = new Sobel(source, currentThreshold);
          sobel.applySobel();
          sobel.applyThreshold();

          ImageIcon afterImage = new ImageIcon(sobel.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);

          Vectorization vectorization = new Vectorization(sobel.getImage());
          vectorization.processImage();
          vectorization.draw();

          ImageIcon vectImage = new ImageIcon(vectorization.getOutput());
          lblVectResult.setText("");
          lblVectResult.setIcon(vectImage);

          vectorization.export(output);
        }

        if(rdbFilterPrewitt.isSelected()) {
          Prewitt prewitt = new Prewitt(source, currentThreshold);
          prewitt.applyPrewitt();
          prewitt.applyThreshold();

          ImageIcon afterImage = new ImageIcon(prewitt.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);

          Vectorization vectorization = new Vectorization(prewitt.getImage());
          vectorization.processImage();
          vectorization.draw();

          ImageIcon vectImage = new ImageIcon(vectorization.getOutput());
          lblVectResult.setText("");
          lblVectResult.setIcon(vectImage);

          vectorization.export(output);
        }

        if(rdbFilterRoberts.isSelected()) {
          Roberts roberts = new Roberts(source, currentThreshold);
          roberts.applyRoberts();
          roberts.applyThreshold();

          ImageIcon afterImage = new ImageIcon(roberts.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);

          Vectorization vectorization = new Vectorization(roberts.getImage());
          vectorization.processImage();
          vectorization.draw();

          ImageIcon vectImage = new ImageIcon(vectorization.getOutput());
          lblVectResult.setText("");
          lblVectResult.setIcon(vectImage);

          vectorization.export(output);
        }
      }
    });
    btnSelectSVGPath.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int status = saveImage.showSaveDialog(pnlMain);

        if(status == JFileChooser.APPROVE_OPTION) {
          String outputPath = saveImage.getSelectedFile().getAbsolutePath();

          if(!outputPath.endsWith(".svg") && !outputPath.endsWith(".SVG")) {
            outputPath += ".svg";
          }
          txfVectSavePath.setText(outputPath);
        }
      }
    });
  }
}
