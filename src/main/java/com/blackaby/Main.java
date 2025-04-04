package com.blackaby;

import com.blackaby.Frontend.MainWindow;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;

/**
 * This class is the main class for the GameDuck application.
 * It is the entry point for the application and creates the main window.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MainWindow();
    }
}