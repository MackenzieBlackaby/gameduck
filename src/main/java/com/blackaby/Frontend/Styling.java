package com.blackaby.Frontend;

import com.blackaby.Misc.AppTheme;
import com.blackaby.Misc.AppThemePreset;

import java.awt.Color;
import java.awt.Font;

/**
 * Shared visual tokens for the host UI.
 * <p>
 * The mutable colours are derived from the active {@link AppTheme}. Fonts and
 * a few fixed overlay colours remain constant across themes.
 */
public final class Styling {

    public static Color backgroundColour;
    public static Color appBackgroundColour;
    public static Color surfaceColour;
    public static Color surfaceBorderColour;
    public static Color accentColour;
    public static Color mutedTextColour;
    public static Color displayFrameColour;
    public static Color statusBackgroundColour;
    public static Color buttonSecondaryBackground;
    public static Color primaryButtonBorderColour;
    public static Color displayFrameBorderColour;
    public static Color sectionHighlightColour;
    public static Color sectionHighlightBorderColour;
    public static Color cardTintColour;
    public static Color cardTintBorderColour;
    public static Color listSelectionColour;

    public static final Font menuFont = new Font("Roboto", Font.PLAIN, 12);
    public static final Font titleFont = new Font("Roboto", Font.BOLD, 36);
    public static final Color menuBackgroundColour = new Color(0, 0, 0, 0);
    public static final Color menuForegroundColour = Color.BLACK;
    public static final Color menuSelectionBackgroundColour = new Color(0, 0, 0, 0);
    public static final Color menuSelectionForegroundColour = Color.WHITE;
    public static final Color displayBackgroundColour = Color.BLACK;
    public static final Color fpsBackgroundColour = new Color(0, 0, 0, 192);
    public static final Color fpsForegroundColour = Color.WHITE;

    static {
        ApplyTheme(AppThemePreset.HARBOR.Theme());
    }

    private Styling() {
    }

    /**
     * Rebuilds the shared colour tokens from the active host theme.
     *
     * @param theme theme to apply
     */
    public static void ApplyTheme(AppTheme theme) {
        backgroundColour = theme.BackgroundColour();
        appBackgroundColour = theme.AppBackgroundColour();
        surfaceColour = theme.SurfaceColour();
        surfaceBorderColour = theme.SurfaceBorderColour();
        accentColour = theme.AccentColour();
        mutedTextColour = theme.MutedTextColour();
        displayFrameColour = theme.DisplayFrameColour();
        statusBackgroundColour = theme.StatusBackgroundColour();
        buttonSecondaryBackground = theme.ButtonSecondaryBackground();
        primaryButtonBorderColour = theme.PrimaryButtonBorderColour();
        displayFrameBorderColour = theme.DisplayFrameBorderColour();
        sectionHighlightColour = theme.SectionHighlightColour();
        sectionHighlightBorderColour = theme.SectionHighlightBorderColour();
        cardTintColour = theme.CardTintColour();
        cardTintBorderColour = theme.CardTintBorderColour();
        listSelectionColour = theme.ListSelectionColour();
    }
}
