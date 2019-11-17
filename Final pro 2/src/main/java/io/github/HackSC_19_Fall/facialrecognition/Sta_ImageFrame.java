package io.github.HackSC_19_Fall.facialrecognition;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Frame - GUI container for components (holds ImagePanel).
 *
 * @group: Enji Li, Bairen Chen, Xinran Gao, Yunhan Mao
 */
class Sta_ImageFrame
{
    /**
     * Whether or not the frame is currently open
     */
    private boolean isOpen;
    /**
     * Whether or not the face should be saved to the disk
     */
    private boolean shouldSave;
    /**
     * Color to draw components (boxes, text, etc) in
     */
    private Color color;
    /**
     * Panel to hold/display a BufferedImage
     */
    private Sta_ImagePanel imagePanel;

    private JFrame frame;
    private JTextField txtFileName;

    private static final Color DEFAULT_COLOR = Color.BLUE;

    Sta_ImageFrame()
    {
        color = DEFAULT_COLOR;
        buildGUI();
    }

    /**
     * Construct the display and its children.
     */
    private void buildGUI()
    {
        imagePanel = new Sta_ImagePanel();
        isOpen = true;

        frame = new JFrame("SC-face(Prototype)");
        frame.addWindowListener(createWindowListener());
        frame.setLayout(new BorderLayout());
        frame.add("Center", imagePanel);
   //     frame.add("South", createToolbarPanel());
        frame.setVisible(true);
    }

    /**
     * Create a listener to monitor the frame closing event.
     *
     * @return WindowListener
     */
    private WindowListener createWindowListener()
    {
        return new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent windowClosed)
            {
                isOpen = false;
            }
        };
    }

    /**
     * Externally called to see if display frame is still open.
     *
     * @return Open status
     */
    boolean isOpen()
    {
        return isOpen;
    }

    /**
     * Return whether or not the face in frame should be saved to the disk.
     * Set the state to false.
     *
     * @return state
     */
    boolean shouldSave()
    {
        boolean prevState = shouldSave;
        shouldSave = false;
        return prevState;
    }

    /**
     * Get the name of the person in frame (user input).
     *
     * @return name
     */
    String getFileName()
    {
        return txtFileName.getText();
    }

    /**
     * Return the selected text color as an OpenCV Scalar.
     *
     * @return Scalar
     */
    Scalar getTextColor()
    {
        return new Scalar(color.getRed(),color.getBlue(), color.getGreen());
    }

    /**
     * Display an image in the frame.
     *
     * @param image
     * 		Image to be shown
     */
    void showImage(Mat image)
    {
        imagePanel.setImage(convertMatToImage(image));
        frame.repaint();
        frame.pack();
    }

    /**
     * Convert an OpenCV Mat to a Java BufferedImage.
     *
     * @param matrix
     * 		OpenCV Mat
     *
     * @return BufferedImage
     */
    private static BufferedImage convertMatToImage(Mat matrix)
    {
        int width = matrix.width();
        int height = matrix.height();
        int type = matrix.channels() != 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;

        if (type == BufferedImage.TYPE_3BYTE_BGR)
            Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2RGB);

        byte[] data = new byte[width * height * (int) matrix.elemSize()];
        matrix.get(0, 0, data);

        BufferedImage out = new BufferedImage(width, height, type);
        out.getRaster().setDataElements(0, 0, width, height, data);

        return out;
    }
}