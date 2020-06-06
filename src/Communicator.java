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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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
    String[] imageExtensions = new String[]{"png", "jpg", "jpeg", "bmp"};
    FileNameExtensionFilter imagesOnly = new FileNameExtensionFilter("Image files (*.png, *.jpg, *.jpeg, *.bmp)", imageExtensions);
    selectImage.setFileFilter(imagesOnly);
    selectImage.addChoosableFileFilter(imagesOnly);
    String[] svgExtension = new String[]{"svg"};
    FileNameExtensionFilter svgOnly = new FileNameExtensionFilter("SVG - Scalable Vector Graphics (*.svg)", svgExtension);
    saveImage.setFileFilter(svgOnly);
    saveImage.addChoosableFileFilter(svgOnly);

    btnBrowse.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int status = selectImage.showOpenDialog(pnlMain);

        if (status == JFileChooser.APPROVE_OPTION) {
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
        if (rdbFilterSobel.isSelected()) {
          rdbFilterPrewitt.setSelected(false);
          rdbFilterRoberts.setSelected(false);
        }
      }
    });
    rdbFilterPrewitt.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (rdbFilterPrewitt.isSelected()) {
          rdbFilterSobel.setSelected(false);
          rdbFilterRoberts.setSelected(false);
        }
      }
    });
    rdbFilterRoberts.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (rdbFilterRoberts.isSelected()) {
          rdbFilterSobel.setSelected(false);
          rdbFilterPrewitt.setSelected(false);
        }
      }
    });
    btnFilterApply.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (image == null) {
          JOptionPane.showMessageDialog(pnlMain, "No source was selected!\nPlease, choose source image before performing any action.", "No Image Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (!rdbFilterSobel.isSelected() && !rdbFilterPrewitt.isSelected() && !rdbFilterRoberts.isSelected()) {
          JOptionPane.showMessageDialog(pnlMain, "You have not chosen any of filters!\nPlease, choose a filter before applying it.", "No Filter Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        BufferedImage source = null;

        try {
          source = ImageIO.read(image);
        } catch (IOException exception) {
          System.out.println("I/O Error while reading selected image: " + exception.toString());
        }

        if (rdbFilterSobel.isSelected()) {
          Sobel sobel = new Sobel(source);
          sobel.applySobel();

          ImageIcon afterImage = new ImageIcon(sobel.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if (rdbFilterPrewitt.isSelected()) {
          Prewitt prewitt = new Prewitt(source);
          prewitt.applyPrewitt();

          ImageIcon afterImage = new ImageIcon(prewitt.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if (rdbFilterRoberts.isSelected()) {
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
        if (image == null) {
          JOptionPane.showMessageDialog(pnlMain, "No source was selected!\nPlease, choose source image before performing any action.", "No Image Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (!rdbFilterSobel.isSelected() && !rdbFilterPrewitt.isSelected() && !rdbFilterRoberts.isSelected()) {
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

        if (rdbFilterSobel.isSelected()) {
          Sobel sobel = new Sobel(source, currentThreshold);
          sobel.applySobel();
          sobel.applyThreshold();

          ImageIcon afterImage = new ImageIcon(sobel.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if (rdbFilterPrewitt.isSelected()) {
          Prewitt prewitt = new Prewitt(source, currentThreshold);
          prewitt.applyPrewitt();
          prewitt.applyThreshold();

          ImageIcon afterImage = new ImageIcon(prewitt.getImage());
          lblAfterImage.setText("");
          lblAfterImage.setIcon(afterImage);
        }

        if (rdbFilterRoberts.isSelected()) {
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
        if (image == null) {
          JOptionPane.showMessageDialog(pnlMain, "No source was selected!\nPlease, choose source image before performing any action.", "No Image Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (!rdbFilterSobel.isSelected() && !rdbFilterPrewitt.isSelected() && !rdbFilterRoberts.isSelected()) {
          JOptionPane.showMessageDialog(pnlMain, "You have not chosen any of filters!\nPlease, choose a filter before applying it.", "No Filter Selected!", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (txfImagePath.getText().isEmpty()) {
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

        if (rdbFilterSobel.isSelected()) {
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

        if (rdbFilterPrewitt.isSelected()) {
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

        if (rdbFilterRoberts.isSelected()) {
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

        if (status == JFileChooser.APPROVE_OPTION) {
          String outputPath = saveImage.getSelectedFile().getAbsolutePath();

          if (!outputPath.endsWith(".svg") && !outputPath.endsWith(".SVG")) {
            outputPath += ".svg";
          }
          txfVectSavePath.setText(outputPath);
        }
      }
    });
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    pnlMain = new JPanel();
    pnlMain.setLayout(new GridBagLayout());
    pnlNew = new JScrollPane();
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlMain.add(pnlNew, gbc);
    pnlNew.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    pnlBody = new JPanel();
    pnlBody.setLayout(new GridBagLayout());
    pnlNew.setViewportView(pnlBody);
    pnlTitle = new JPanel();
    pnlTitle.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlTitle, gbc);
    lblTitle = new JLabel();
    Font lblTitleFont = this.$$$getFont$$$(null, Font.BOLD, 16, lblTitle.getFont());
    if (lblTitleFont != null) lblTitle.setFont(lblTitleFont);
    lblTitle.setText("Image selection and filtering parameters");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlTitle.add(lblTitle, gbc);
    pnlImagePath = new JPanel();
    pnlImagePath.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlImagePath, gbc);
    lblImagePath = new JLabel();
    lblImagePath.setText("Image location:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlImagePath.add(lblImagePath, gbc);
    txfImagePath = new JTextField();
    txfImagePath.setEditable(false);
    txfImagePath.setEnabled(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlImagePath.add(txfImagePath, gbc);
    btnBrowse = new JButton();
    btnBrowse.setText("Browse files...");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlImagePath.add(btnBrowse, gbc);
    pnlFilterSelection = new JPanel();
    pnlFilterSelection.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlFilterSelection, gbc);
    lblFilterSelect = new JLabel();
    lblFilterSelect.setText("Select the filter to apply:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlFilterSelection.add(lblFilterSelect, gbc);
    rdbFilterSobel = new JRadioButton();
    rdbFilterSobel.setText("Sobel");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlFilterSelection.add(rdbFilterSobel, gbc);
    rdbFilterPrewitt = new JRadioButton();
    rdbFilterPrewitt.setText("Prewitt");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlFilterSelection.add(rdbFilterPrewitt, gbc);
    rdbFilterRoberts = new JRadioButton();
    rdbFilterRoberts.setText("Roberts");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlFilterSelection.add(rdbFilterRoberts, gbc);
    btnFilterApply = new JButton();
    btnFilterApply.setHideActionText(false);
    btnFilterApply.setText("Apply filter");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlFilterSelection.add(btnFilterApply, gbc);
    pnlThresholdChange = new JPanel();
    pnlThresholdChange.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlThresholdChange, gbc);
    lblThresholdChange = new JLabel();
    lblThresholdChange.setText("Set black and white threshold (color value in range 0 - 255):");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlThresholdChange.add(lblThresholdChange, gbc);
    lblThresholdMin = new JLabel();
    lblThresholdMin.setText("0");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlThresholdChange.add(lblThresholdMin, gbc);
    sldThresholdChanger = new JSlider();
    sldThresholdChanger.setMaximum(255);
    sldThresholdChanger.setPaintTicks(true);
    sldThresholdChanger.setPaintTrack(true);
    sldThresholdChanger.setValue(100);
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlThresholdChange.add(sldThresholdChanger, gbc);
    lblThresholdMax = new JLabel();
    lblThresholdMax.setText("255");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlThresholdChange.add(lblThresholdMax, gbc);
    lblThresholdCurrentValue = new JLabel();
    Font lblThresholdCurrentValueFont = this.$$$getFont$$$(null, -1, 14, lblThresholdCurrentValue.getFont());
    if (lblThresholdCurrentValueFont != null) lblThresholdCurrentValue.setFont(lblThresholdCurrentValueFont);
    lblThresholdCurrentValue.setText("100");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlThresholdChange.add(lblThresholdCurrentValue, gbc);
    pnlImages = new JPanel();
    pnlImages.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlImages, gbc);
    pnlImagesTitles = new JPanel();
    pnlImagesTitles.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlImages.add(pnlImagesTitles, gbc);
    lblImagesTitle = new JLabel();
    Font lblImagesTitleFont = this.$$$getFont$$$(null, Font.BOLD, 16, lblImagesTitle.getFont());
    if (lblImagesTitleFont != null) lblImagesTitle.setFont(lblImagesTitleFont);
    lblImagesTitle.setText("Result images");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlImagesTitles.add(lblImagesTitle, gbc);
    pnlImageProcessing = new JPanel();
    pnlImageProcessing.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlImages.add(pnlImageProcessing, gbc);
    lblBeforeImage = new JLabel();
    lblBeforeImage.setText("Select a source image.");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlImageProcessing.add(lblBeforeImage, gbc);
    lblAfterImage = new JLabel();
    lblAfterImage.setText("Processed image will be shown here.");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlImageProcessing.add(lblAfterImage, gbc);
    pnlVect = new JPanel();
    pnlVect.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlVect, gbc);
    lblVectTitle = new JLabel();
    Font lblVectTitleFont = this.$$$getFont$$$(null, Font.BOLD, 16, lblVectTitle.getFont());
    if (lblVectTitleFont != null) lblVectTitle.setFont(lblVectTitleFont);
    lblVectTitle.setText("Vectorization");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlVect.add(lblVectTitle, gbc);
    pnlVectorizedImage = new JPanel();
    pnlVectorizedImage.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlVectorizedImage, gbc);
    lblVectResult = new JLabel();
    lblVectResult.setText("Vectorized image will be shown here.");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlVectorizedImage.add(lblVectResult, gbc);
    pnlVectSaveSVG = new JPanel();
    pnlVectSaveSVG.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlVectSaveSVG, gbc);
    lblSavePath = new JLabel();
    lblSavePath.setText("Output drawing to:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    pnlVectSaveSVG.add(lblSavePath, gbc);
    txfVectSavePath = new JTextField();
    txfVectSavePath.setEditable(false);
    txfVectSavePath.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlVectSaveSVG.add(txfVectSavePath, gbc);
    btnSelectSVGPath = new JButton();
    btnSelectSVGPath.setText("Browse files...");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlVectSaveSVG.add(btnSelectSVGPath, gbc);
    pnlVectSave = new JPanel();
    pnlVectSave.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlVectSave, gbc);
    btnVectExport = new JButton();
    btnVectExport.setText("Vectorize and save");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlVectSave.add(btnVectExport, gbc);
    pnlProgramInfo = new JPanel();
    pnlProgramInfo.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    pnlBody.add(pnlProgramInfo, gbc);
    lblProgramInfoContent = new JLabel();
    lblProgramInfoContent.setText("Java Edge Detector (JED), Copyright (C) 2016 by Marek Felsoci. Licensed under the GNU GPLv2 (see LICENSE file for more).");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    pnlProgramInfo.add(lblProgramInfoContent, gbc);
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return pnlMain;
  }
}
