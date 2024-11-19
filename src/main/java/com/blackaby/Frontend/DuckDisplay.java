package com.blackaby.Frontend;

import javax.swing.*;
import java.awt.*;

/**
 * This class is the display for the Duck game.
 * It is a subclass of JPanel.
 * It is responsible for displaying the game to the user.
 */
public class DuckDisplay extends JPanel {

    /**
     * Constructor for DuckDisplay.
     * 
     * @return A new DuckDisplay object.
     */
    public DuckDisplay() {
        super();
        setBackground(Color.red);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 100); // Or whatever minimum size you want
    }

    @Override
    public Dimension getPreferredSize() {
        // Get the size of the parent container
        Container parent = getParent();
        if (parent != null) {
            int size = Math.min(parent.getWidth(), parent.getHeight());
            size = Math.max(size - 20, 100); // Subtract some padding, ensure minimum size
            return new Dimension(size, size);
        }
        return new Dimension(400, 400); // Default size if no parent
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight());
        setSize(size, size);
    }
}
