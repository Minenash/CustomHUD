package com.minenash.customhud.core.editor;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import net.minecraft.util.Util;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuBar {

    public static JMenuBar build() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");

        JMenu theme = new JMenu("Theme");
        theme.add(themeItem("Light",  new FlatLightLaf()));
        theme.add(themeItem("InteliJ", new FlatIntelliJLaf()));
        theme.add(themeItem("Dark", new FlatDarkLaf()));
        theme.add(themeItem("Darkula", new FlatDarculaLaf()));

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(theme);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(linkMenu("Docs/Wiki", "https://customhud.dev/v3/getting_started"));
        menuBar.add(linkMenu("Discord", "https://discord.gg/eYf7DDHhvN"));
        return menuBar;
    }

    private static JMenuItem themeItem(String name, LookAndFeel theme) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> Utils.changeTheme(theme));
        return item;
    }
    private static JMenu linkMenu(String name, String url) {
        JMenu menu = new JMenu(name);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Util.getOperatingSystem().open(url);
            }
        });
        return menu;
    }

}
