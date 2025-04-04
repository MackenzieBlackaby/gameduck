package com.blackaby.Frontend;

import java.awt.*;
import javax.swing.*;

/**
 * This class represents the about window of the emulator.
 * It contains information about the emulator and the author.
 */
public class AboutWindow extends DuckWindow {
    /**
     * This constructor creates a new AboutWindow with the specified title and size.
     * It sets the default close operation and layout of the window.
     */
    public AboutWindow() {
        super("About", 400, 300, false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String htmlText = "<html>" +
                "<h1>GameDuck</h1>" +
                "<h2>Version 0.1</h2>" +
                "<p><b>Nintendo Game Boy Emulator</b><br>" +
                "By Mackenzie Blackaby<br>" +
                "Final year project for Lancaster University<br><br>" +
                "<i>This Emulator contains no IP or copyrighted material</i></p>" +
                "</html>";
        JLabel mainLabel = new JLabel(htmlText);
        mainLabel.setVerticalAlignment(JLabel.CENTER);
        mainLabel.setHorizontalAlignment(JLabel.CENTER);
        mainLabel.setFont(Styling.MENU_FONT);
        mainLabel.setForeground(Styling.MENU_FOREGROUND_COLOR);
        add(mainLabel, BorderLayout.CENTER);

        setVisible(true);
    }
}
