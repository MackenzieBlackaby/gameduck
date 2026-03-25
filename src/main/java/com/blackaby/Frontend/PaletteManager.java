package com.blackaby.Frontend;

import com.blackaby.Misc.Config;
import com.blackaby.Misc.UiText;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Browser for loading and deleting saved DMG and GBC palettes.
 */
public final class PaletteManager extends DuckWindow {

    public enum PaletteKind {
        DMG,
        GBC
    }

    private final Color panelBackground;
    private final Color cardBackground;
    private final Color cardBorder;
    private final Color accentColour;
    private final Color mutedTextColour;
    private final DefaultListModel<String> paletteListModel = new DefaultListModel<>();
    private final PaletteKind paletteKind;
    private final Runnable onPaletteChanged;

    /**
     * Creates the palette browser.
     *
     * @param onPaletteChanged callback fired after a palette is loaded
     */
    public PaletteManager(Runnable onPaletteChanged) {
        this(PaletteKind.DMG, onPaletteChanged);
    }

    /**
     * Creates the palette browser for the requested palette type.
     *
     * @param paletteKind      saved palette library to browse
     * @param onPaletteChanged callback fired after a palette is loaded
     */
    public PaletteManager(PaletteKind paletteKind, Runnable onPaletteChanged) {
        super(UiText.PaletteManager.WindowTitle(paletteKind == PaletteKind.GBC), 460, 360, false);
        this.paletteKind = paletteKind;
        this.onPaletteChanged = onPaletteChanged;
        panelBackground = Styling.appBackgroundColour;
        cardBackground = Styling.surfaceColour;
        cardBorder = Styling.surfaceBorderColour;
        accentColour = Styling.accentColour;
        mutedTextColour = Styling.mutedTextColour;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(panelBackground);

        add(BuildHeader(), BorderLayout.NORTH);
        add(BuildBody(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JComponent BuildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setBackground(panelBackground);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        boolean gbcPalette = paletteKind == PaletteKind.GBC;

        JLabel titleLabel = new JLabel(UiText.PaletteManager.Title(gbcPalette));
        titleLabel.setFont(Styling.menuFont.deriveFont(Font.BOLD, 24f));
        titleLabel.setForeground(accentColour);

        JLabel subtitleLabel = new JLabel(UiText.PaletteManager.Subtitle(gbcPalette));
        subtitleLabel.setFont(Styling.menuFont.deriveFont(Font.PLAIN, 13f));
        subtitleLabel.setForeground(mutedTextColour);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.CENTER);
        return header;
    }

    private JComponent BuildBody() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(panelBackground);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(cardBackground);
        card.setBorder(CreateCardBorder());

        JList<String> paletteList = new JList<>(paletteListModel);
        paletteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paletteList.setFont(Styling.menuFont.deriveFont(13f));
        paletteList.setFixedCellHeight(30);
        paletteList.setBackground(Styling.cardTintColour);
        paletteList.setSelectionBackground(Styling.listSelectionColour);
        paletteList.setSelectionForeground(accentColour);

        RefreshPaletteList();

        if (!paletteListModel.isEmpty()) {
            paletteList.setSelectedIndex(0);
        }

        JScrollPane scrollPane = new JScrollPane(paletteList);
        scrollPane.setBorder(BorderFactory.createLineBorder(cardBorder, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton importButton = CreateSecondaryButton(UiText.PaletteManager.IMPORT_BUTTON);
        importButton.addActionListener(event -> importPalettes(paletteList));

        JButton deleteButton = CreateSecondaryButton(UiText.PaletteManager.DELETE_BUTTON);
        deleteButton.addActionListener(event -> {
            String selectedPalette = paletteList.getSelectedValue();
            if (selectedPalette == null) {
                return;
            }

            boolean gbcPalette = paletteKind == PaletteKind.GBC;
            int result = JOptionPane.showConfirmDialog(this,
                    UiText.PaletteManager.DeleteConfirmMessage(gbcPalette, selectedPalette),
                    UiText.PaletteManager.DeleteConfirmTitle(gbcPalette),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                DeletePalette(selectedPalette);
                RefreshPaletteList();
                if (!paletteListModel.isEmpty()) {
                    paletteList.setSelectedIndex(0);
                }
            }
        });

        JButton loadButton = CreatePrimaryButton(UiText.PaletteManager.LOAD_BUTTON);
        loadButton.addActionListener(event -> {
            String selectedPalette = paletteList.getSelectedValue();
            if (selectedPalette == null) {
                return;
            }

            if (LoadPalette(selectedPalette) && onPaletteChanged != null) {
                onPaletteChanged.run();
            }
            dispose();
        });

        buttonPanel.add(importButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private void RefreshPaletteList() {
        paletteListModel.clear();
        List<String> paletteNames = paletteKind == PaletteKind.GBC
                ? Config.GetSavedGbcPaletteNames()
                : Config.GetSavedPaletteNames();
        for (String paletteName : paletteNames) {
            paletteListModel.addElement(paletteName);
        }
    }

    private boolean LoadPalette(String paletteName) {
        return paletteKind == PaletteKind.GBC
                ? Config.LoadGbcPalette(paletteName)
                : Config.LoadPalette(paletteName);
    }

    private void DeletePalette(String paletteName) {
        if (paletteKind == PaletteKind.GBC) {
            Config.DeleteGbcPalette(paletteName);
            return;
        }
        Config.DeletePalette(paletteName);
    }

    private void importPalettes(JList<String> paletteList) {
        File importFile = promptForPaletteImportFile();
        if (importFile == null) {
            return;
        }

        try {
            var mergeResult = paletteKind == PaletteKind.GBC
                    ? Config.ImportGbcPalettes(importFile.toPath())
                    : Config.ImportPalettes(importFile.toPath());
            RefreshPaletteList();
            if (!paletteListModel.isEmpty()) {
                paletteList.setSelectedIndex(0);
            }
            JOptionPane.showMessageDialog(this,
                    UiText.PaletteManager.ImportSuccessMessage(
                            paletteKind == PaletteKind.GBC,
                            mergeResult.importedCount(),
                            mergeResult.duplicateCount()));
        } catch (IOException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this,
                    exception.getMessage(),
                    UiText.PaletteManager.IMPORT_FAILED_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private File promptForPaletteImportFile() {
        FileDialog fileDialog = new FileDialog(this,
                UiText.PaletteManager.ImportDialogTitle(paletteKind == PaletteKind.GBC),
                FileDialog.LOAD);
        fileDialog.setAlwaysOnTop(true);
        fileDialog.setFilenameFilter((directory, name) -> name.toLowerCase().endsWith(".json"));
        fileDialog.setVisible(true);
        return fileDialog.getFiles().length == 0 ? null : fileDialog.getFiles()[0];
    }

    private Border CreateCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cardBorder, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18));
    }

    private JButton CreatePrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBackground(accentColour);
        button.setForeground(Color.WHITE);
        button.setFont(Styling.menuFont.deriveFont(Font.BOLD, 13f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Styling.primaryButtonBorderColour, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        return button;
    }

    private JButton CreateSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBackground(Styling.buttonSecondaryBackground);
        button.setForeground(accentColour);
        button.setFont(Styling.menuFont.deriveFont(Font.BOLD, 13f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cardBorder, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        return button;
    }
}
