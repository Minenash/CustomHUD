package com.minenash.customhud.core.editor;

import net.minecraft.client.MinecraftClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Utils {

    public static void open(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeTheme(LookAndFeel theme) {
        try {
            UIManager.setLookAndFeel(theme);

            for(Window window : JFrame.getWindows())
                SwingUtilities.updateComponentTreeUI(window);

        } catch (UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ImageIcon imageIcon(String path, String description) {
        URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static ImageIcon imageIconS(String path, String description) {
        Image image;
        try {
            image = ImageIO.read(Utils.class.getResource(path));
            image = image.getScaledInstance(22,22, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null) {
            return new ImageIcon(image, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

}
