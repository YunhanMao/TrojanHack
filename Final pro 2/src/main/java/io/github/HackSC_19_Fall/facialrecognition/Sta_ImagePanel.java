package io.github.HackSC_19_Fall.facialrecognition;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Panel - holds image to display in GUI.
 *
 * @group: Enji Li, Bairen Chen, Xinran Gao, Yunhan Mao
 */
class Sta_ImagePanel extends JPanel
{
    /**
     * Image to be displayed to the user
     */
    private BufferedImage image;

    void setImage(BufferedImage image)
    {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (image == null)
            return;

        g.drawImage(image, 0, 0, null);
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }
}