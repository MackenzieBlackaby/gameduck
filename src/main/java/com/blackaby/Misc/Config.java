package com.blackaby.Misc;

import com.blackaby.Backend.Emulation.Graphics.GBColor;
import com.blackaby.Backend.Emulation.Peripherals.DuckJoypad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

/**
 * Loads and saves the persistent application configuration.
 * <p>
 * The config file keeps palette data, host theme values, control bindings,
 * shortcut bindings, sound options, and simple window preferences. The class is
 * stateful by design so the rest of the application can request a load once and
 * then work with in-memory settings.
 */
public final class Config {

    private static final Path configPath = Path.of(System.getProperty("user.home"), ".gameduck.properties");
    private static final String currentPalettePrefix = "palette.current.";
    private static final String savedPaletteListKey = "palette.names";
    private static final String savedPalettePrefix = "palette.saved.";
    private static final String savedGbcPaletteListKey = "palette.gbc.saved.names";
    private static final String savedGbcPalettePrefix = "palette.gbc.saved.";
    private static final String savedThemeListKey = "theme.saved.names";
    private static final String savedThemePrefix = "theme.saved.";
    private static final String inputPrefix = "input.";
    private static final String controllerInputPrefix = "controller.input.";
    private static final String shortcutPrefix = "shortcut.";
    private static final String themePrefix = "theme.";
    private static final String controllerEnabledKey = "controller.enabled";
    private static final String controllerPreferredIdKey = "controller.preferred_id";
    private static final String controllerDeadzoneKey = "controller.deadzone_percent";
    private static final String soundEnabledKey = "sound.enabled";
    private static final String soundVolumeKey = "sound.volume";
    private static final String soundChannelMutedPrefix = "sound.channel.muted.";
    private static final String soundChannelVolumePrefix = "sound.channel.volume.";
    private static final String soundEnhancementEnabledKey = "sound.enhancement.enabled";
    private static final String soundEnhancementChainKey = "sound.enhancement.chain";
    private static final String useBootRomKey = "emulation.use_boot_rom";
    private static final String useCgbBootRomKey = "emulation.use_cgb_boot_rom";
    private static final String fillWindowOutputKey = "ui.fill_window_output";
    private static final String showSerialOutputKey = "ui.show_serial_output";
    private static final String gameArtDisplayModeKey = "ui.game_art_display_mode";
    private static final String gameNameBracketDisplayModeKey = "library.game_name_bracket_display_mode";
    private static final String libraryViewModeKey = "library.view_mode";
    private static final String preferDmgModeForGbcCompatibleGamesKey = "palette.prefer_dmg_mode_for_gbc_compatible_games";
    private static final String gbcPaletteModeEnabledKey = "palette.gbc_mode_enabled";
    private static final String gbcBackgroundPalettePrefix = "palette.gbc.background.";
    private static final String gbcSpritePalette0Prefix = "palette.gbc.sprite0.";
    private static final String gbcSpritePalette1Prefix = "palette.gbc.sprite1.";

    private static final Properties properties = new Properties();
    private static boolean loaded;

    private Config() {
    }

    /**
     * Loads the configuration file into {@link Settings}.
     */
    public static synchronized void Load() {
        properties.clear();
        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        ApplyCurrentPalette();
        ApplyAppTheme();
        ApplyInputBindings();
        ApplyControllerBindings();
        ApplyAppShortcuts();
        ApplySoundSettings();
        ApplyEmulationSettings();
        ApplyWindowSettings();
        ApplyLibrarySettings();
        loaded = true;
    }

    /**
     * Saves the current in-memory settings to disk.
     */
    public static synchronized void Save() {
        EnsureLoaded();
        SyncCurrentPalette();
        SyncAppTheme();
        SyncInputBindings();
        SyncControllerBindings();
        SyncAppShortcuts();
        SyncSoundSettings();
        SyncEmulationSettings();
        SyncWindowSettings();
        SyncLibrarySettings();
        Persist();
    }

    /**
     * Saves the current DMG palette under a user-supplied name.
     *
     * @param name palette name
     */
    public static void SavePalette(String name) {
        synchronized (Config.class) {
            EnsureLoaded();

            String encodedName = EncodeName(name);
            List<String> paletteNames = GetSavedPaletteNamesInternal();
            if (!paletteNames.contains(name)) {
                paletteNames.add(name);
            }

            GBColor[] palette = Settings.CurrentPalette();
            for (int index = 0; index < palette.length; index++) {
                properties.setProperty(savedPalettePrefix + encodedName + "." + index, palette[index].ToHex());
            }
            properties.setProperty(savedPaletteListKey, String.join(",", EncodeNames(paletteNames)));
            SyncCurrentPalette();
            SyncAppTheme();
            SyncInputBindings();
            SyncControllerBindings();
            SyncAppShortcuts();
            SyncSoundSettings();
            SyncEmulationSettings();
            SyncWindowSettings();
            SyncLibrarySettings();
            Persist();
        }
    }

    /**
     * Saves the current GBC colourisation palettes under a user-supplied name.
     *
     * @param name palette name
     */
    public static void SaveGbcPalette(String name) {
        synchronized (Config.class) {
            EnsureLoaded();

            String encodedName = EncodeName(name);
            List<String> paletteNames = GetSavedGbcPaletteNamesInternal();
            if (!paletteNames.contains(name)) {
                paletteNames.add(name);
            }

            SyncSavedGbcPalette(savedGbcPalettePrefix + encodedName + ".background.", Settings.CurrentGbcBackgroundPalette());
            SyncSavedGbcPalette(savedGbcPalettePrefix + encodedName + ".sprite0.", Settings.CurrentGbcSpritePalette0());
            SyncSavedGbcPalette(savedGbcPalettePrefix + encodedName + ".sprite1.", Settings.CurrentGbcSpritePalette1());
            properties.setProperty(savedGbcPaletteListKey, String.join(",", EncodeNames(paletteNames)));
            SyncCurrentPalette();
            SyncAppTheme();
            SyncInputBindings();
            SyncControllerBindings();
            SyncAppShortcuts();
            SyncSoundSettings();
            SyncEmulationSettings();
            SyncWindowSettings();
            SyncLibrarySettings();
            Persist();
        }
    }

    /**
     * Saves the current host theme under a user-supplied name.
     *
     * @param name theme name
     */
    public static void SaveTheme(String name) {
        synchronized (Config.class) {
            EnsureLoaded();

            String encodedName = EncodeName(name);
            List<String> themeNames = GetSavedThemeNamesInternal();
            if (!themeNames.contains(name)) {
                themeNames.add(name);
            }

            AppTheme theme = Settings.CurrentAppTheme().Renamed(name);
            for (AppThemeColorRole role : AppThemeColorRole.values()) {
                properties.setProperty(savedThemePrefix + encodedName + "." + role.name(), theme.CoreHex(role));
            }
            properties.setProperty(savedThemeListKey, String.join(",", EncodeNames(themeNames)));
            SyncCurrentPalette();
            SyncAppTheme();
            SyncInputBindings();
            SyncControllerBindings();
            SyncAppShortcuts();
            SyncSoundSettings();
            SyncEmulationSettings();
            SyncWindowSettings();
            SyncLibrarySettings();
            Persist();
        }
    }

    /**
     * Returns the saved palette names in display order.
     *
     * @return saved palette names
     */
    public static synchronized List<String> GetSavedPaletteNames() {
        EnsureLoaded();
        return new ArrayList<>(GetSavedPaletteNamesInternal());
    }

    /**
     * Returns the saved GBC palette names in display order.
     *
     * @return saved GBC palette names
     */
    public static synchronized List<String> GetSavedGbcPaletteNames() {
        EnsureLoaded();
        return new ArrayList<>(GetSavedGbcPaletteNamesInternal());
    }

    /**
     * Returns the saved theme names in display order.
     *
     * @return saved theme names
     */
    public static synchronized List<String> GetSavedThemeNames() {
        EnsureLoaded();
        return new ArrayList<>(GetSavedThemeNamesInternal());
    }

    /**
     * Loads a saved palette into the active settings.
     *
     * @param name palette name
     * @return {@code true} if the palette existed
     */
    public static synchronized boolean LoadPalette(String name) {
        EnsureLoaded();

        String encodedName = EncodeName(name);
        boolean found = false;
        for (int index = 0; index < 4; index++) {
            String value = properties.getProperty(savedPalettePrefix + encodedName + "." + index);
            if (value == null) {
                continue;
            }
            Settings.SetPaletteColour(index, value);
            found = true;
        }

        if (found) {
            SyncCurrentPalette();
            SyncAppTheme();
            SyncInputBindings();
            SyncControllerBindings();
            SyncAppShortcuts();
            SyncSoundSettings();
            SyncWindowSettings();
            SyncLibrarySettings();
            Persist();
        }

        return found;
    }

    /**
     * Loads a saved GBC colourisation palette set into the active settings.
     *
     * @param name palette name
     * @return {@code true} if the palette existed
     */
    public static synchronized boolean LoadGbcPalette(String name) {
        EnsureLoaded();

        String encodedName = EncodeName(name);
        boolean found = false;
        found |= ApplySavedGbcPalette(savedGbcPalettePrefix + encodedName + ".background.", 0);
        found |= ApplySavedGbcPalette(savedGbcPalettePrefix + encodedName + ".sprite0.", 1);
        found |= ApplySavedGbcPalette(savedGbcPalettePrefix + encodedName + ".sprite1.", 2);

        if (found) {
            SyncCurrentPalette();
            SyncAppTheme();
            SyncInputBindings();
            SyncControllerBindings();
            SyncAppShortcuts();
            SyncSoundSettings();
            SyncWindowSettings();
            SyncLibrarySettings();
            Persist();
        }

        return found;
    }

    /**
     * Loads a saved theme into the active settings.
     *
     * @param name theme name
     * @return {@code true} if the theme existed
     */
    public static synchronized boolean LoadTheme(String name) {
        EnsureLoaded();

        String encodedName = EncodeName(name);
        AppTheme currentTheme = Settings.CurrentAppTheme();
        AppTheme loadedTheme = currentTheme.Renamed(name);
        boolean found = false;

        for (AppThemeColorRole role : AppThemeColorRole.values()) {
            String value = properties.getProperty(savedThemePrefix + encodedName + "." + role.name());
            if (value == null || value.isBlank()) {
                continue;
            }
            loadedTheme.SetCoreColour(role, value);
            found = true;
        }

        if (found) {
            Settings.ApplyAppTheme(loadedTheme);
            SyncCurrentPalette();
            SyncAppTheme();
            SyncInputBindings();
            SyncAppShortcuts();
            SyncSoundSettings();
            SyncEmulationSettings();
            SyncWindowSettings();
            SyncLibrarySettings();
            Persist();
        }

        return found;
    }

    /**
     * Deletes a stored palette.
     *
     * @param name palette name
     */
    public static synchronized void DeletePalette(String name) {
        EnsureLoaded();

        String encodedName = EncodeName(name);
        for (int index = 0; index < 4; index++) {
            properties.remove(savedPalettePrefix + encodedName + "." + index);
        }

        List<String> paletteNames = GetSavedPaletteNamesInternal();
        paletteNames.remove(name);
        properties.setProperty(savedPaletteListKey, String.join(",", EncodeNames(paletteNames)));
        SyncCurrentPalette();
        SyncAppTheme();
        SyncInputBindings();
        SyncControllerBindings();
        SyncAppShortcuts();
        SyncSoundSettings();
        SyncEmulationSettings();
        SyncWindowSettings();
        SyncLibrarySettings();
        Persist();
    }

    /**
     * Deletes a stored GBC palette set.
     *
     * @param name palette name
     */
    public static synchronized void DeleteGbcPalette(String name) {
        EnsureLoaded();

        String encodedName = EncodeName(name);
        RemoveSavedGbcPalette(savedGbcPalettePrefix + encodedName + ".background.");
        RemoveSavedGbcPalette(savedGbcPalettePrefix + encodedName + ".sprite0.");
        RemoveSavedGbcPalette(savedGbcPalettePrefix + encodedName + ".sprite1.");

        List<String> paletteNames = GetSavedGbcPaletteNamesInternal();
        paletteNames.remove(name);
        properties.setProperty(savedGbcPaletteListKey, String.join(",", EncodeNames(paletteNames)));
        SyncCurrentPalette();
        SyncAppTheme();
        SyncInputBindings();
        SyncControllerBindings();
        SyncAppShortcuts();
        SyncSoundSettings();
        SyncEmulationSettings();
        SyncWindowSettings();
        SyncLibrarySettings();
        Persist();
    }

    /**
     * Deletes a stored host theme.
     *
     * @param name theme name
     */
    public static synchronized void DeleteTheme(String name) {
        EnsureLoaded();

        String encodedName = EncodeName(name);
        for (AppThemeColorRole role : AppThemeColorRole.values()) {
            properties.remove(savedThemePrefix + encodedName + "." + role.name());
        }

        List<String> themeNames = GetSavedThemeNamesInternal();
        themeNames.remove(name);
        properties.setProperty(savedThemeListKey, String.join(",", EncodeNames(themeNames)));
        SyncCurrentPalette();
        SyncAppTheme();
        SyncInputBindings();
        SyncControllerBindings();
        SyncAppShortcuts();
        SyncSoundSettings();
        SyncEmulationSettings();
        SyncWindowSettings();
        SyncLibrarySettings();
        Persist();
    }

    private static void ApplyCurrentPalette() {
        String[] defaults = {
                Settings.gbColour0,
                Settings.gbColour1,
                Settings.gbColour2,
                Settings.gbColour3
        };

        for (int index = 0; index < defaults.length; index++) {
            Settings.SetPaletteColour(index, properties.getProperty(currentPalettePrefix + index, defaults[index]));
        }

        Settings.ResetGbcPaletteMode();
        Settings.preferDmgModeForGbcCompatibleGames = Boolean.parseBoolean(
                properties.getProperty(preferDmgModeForGbcCompatibleGamesKey, "false"));
        Settings.gbcPaletteModeEnabled = Boolean.parseBoolean(properties.getProperty(gbcPaletteModeEnabledKey, "false"));
        ApplyGbcPalette(gbcBackgroundPalettePrefix, Settings.gbcBackgroundPaletteDefaults, 0);
        ApplyGbcPalette(gbcSpritePalette0Prefix, Settings.gbcSpritePalette0Defaults, 1);
        ApplyGbcPalette(gbcSpritePalette1Prefix, Settings.gbcSpritePalette1Defaults, 2);
    }

    private static void ApplyAppTheme() {
        Settings.ResetAppTheme();
        AppTheme theme = Settings.CurrentAppTheme();
        for (AppThemeColorRole role : AppThemeColorRole.values()) {
            String value = properties.getProperty(themePrefix + role.name());
            if (value != null && !value.isBlank()) {
                theme.SetCoreColour(role, value);
            }
        }
        Settings.ApplyAppTheme(theme);
    }

    private static void ApplyInputBindings() {
        Settings.ResetControls();
        for (DuckJoypad.Button button : DuckJoypad.Button.values()) {
            String storedCode = properties.getProperty(inputPrefix + button.name());
            if (storedCode == null) {
                continue;
            }

            try {
                Settings.inputBindings.SetKeyCode(button, Integer.parseInt(storedCode));
            } catch (NumberFormatException exception) {
                Settings.ResetControls();
                return;
            }
        }
    }

    private static void ApplyAppShortcuts() {
        Settings.ResetAppShortcuts();
        for (AppShortcut shortcut : AppShortcut.values()) {
            Settings.appShortcutBindings.LoadFromConfigValue(shortcut,
                    properties.getProperty(shortcutPrefix + shortcut.name()));
        }
    }

    private static void ApplyControllerBindings() {
        Settings.ResetControllerControls();
        Settings.controllerInputEnabled = Boolean.parseBoolean(properties.getProperty(controllerEnabledKey, "true"));
        Settings.preferredControllerId = properties.getProperty(controllerPreferredIdKey, "");

        try {
            int configuredDeadzone = Integer.parseInt(properties.getProperty(controllerDeadzoneKey, "45"));
            Settings.controllerDeadzonePercent = Math.max(0, Math.min(95, configuredDeadzone));
        } catch (NumberFormatException exception) {
            Settings.controllerDeadzonePercent = 45;
        }

        for (DuckJoypad.Button button : DuckJoypad.Button.values()) {
            ControllerBinding binding = ControllerBinding.FromConfigValue(
                    properties.getProperty(controllerInputPrefix + button.name()));
            if (binding != null) {
                Settings.controllerBindings.SetBinding(button, binding);
            }
        }
    }

    private static void ApplySoundSettings() {
        Settings.ResetSound();
        Settings.soundEnabled = Boolean.parseBoolean(properties.getProperty(soundEnabledKey, "true"));
        try {
            int configuredVolume = Integer.parseInt(properties.getProperty(soundVolumeKey, "100"));
            Settings.masterVolume = Math.max(0, Math.min(100, configuredVolume));
        } catch (NumberFormatException exception) {
            Settings.ResetSound();
            return;
        }

        for (int channelIndex = 0; channelIndex < 4; channelIndex++) {
            Settings.SetChannelMuted(channelIndex,
                    Boolean.parseBoolean(properties.getProperty(soundChannelMutedPrefix + channelIndex, "false")));
            try {
                int configuredChannelVolume = Integer.parseInt(
                        properties.getProperty(soundChannelVolumePrefix + channelIndex, "100"));
                Settings.SetChannelVolume(channelIndex, configuredChannelVolume);
            } catch (NumberFormatException exception) {
                Settings.SetChannelVolume(channelIndex, 100);
            }
        }

        Settings.SetAudioEnhancementChain(ParseAudioEnhancementChain(properties.getProperty(soundEnhancementChainKey, "")));
        Settings.SetAudioEnhancementChainEnabled(
                Boolean.parseBoolean(properties.getProperty(soundEnhancementEnabledKey, "true")));
    }

    private static void ApplyWindowSettings() {
        Settings.ResetWindow();
        Settings.fillWindowOutput = Boolean.parseBoolean(properties.getProperty(fillWindowOutputKey, "false"));
        Settings.showSerialOutput = Boolean.parseBoolean(properties.getProperty(showSerialOutputKey, "true"));
        String configuredGameArtMode = properties.getProperty(gameArtDisplayModeKey, GameArtDisplayMode.BOX_ART.name());
        try {
            Settings.gameArtDisplayMode = GameArtDisplayMode.valueOf(configuredGameArtMode);
        } catch (IllegalArgumentException exception) {
            Settings.gameArtDisplayMode = GameArtDisplayMode.BOX_ART;
        }
    }

    private static void ApplyLibrarySettings() {
        Settings.ResetLibrary();
        String configuredMode = properties.getProperty(gameNameBracketDisplayModeKey, GameNameBracketDisplayMode.NONE.name());
        try {
            Settings.gameNameBracketDisplayMode = GameNameBracketDisplayMode.valueOf(configuredMode);
        } catch (IllegalArgumentException exception) {
            Settings.ResetLibrary();
            return;
        }

        String configuredViewMode = properties.getProperty(libraryViewModeKey, "LIST");
        Settings.libraryViewMode = configuredViewMode == null || configuredViewMode.isBlank()
                ? "LIST"
                : configuredViewMode;
    }

    private static void ApplyEmulationSettings() {
        Settings.ResetEmulation();
        Settings.useBootRom = Boolean.parseBoolean(properties.getProperty(useBootRomKey, "false"));
        Settings.useCgbBootRom = Boolean.parseBoolean(properties.getProperty(useCgbBootRomKey, "false"));
    }

    private static void SyncCurrentPalette() {
        GBColor[] palette = Settings.CurrentPalette();
        for (int index = 0; index < palette.length; index++) {
            properties.setProperty(currentPalettePrefix + index, palette[index].ToHex());
        }
        properties.setProperty(preferDmgModeForGbcCompatibleGamesKey,
                String.valueOf(Settings.preferDmgModeForGbcCompatibleGames));
        properties.setProperty(gbcPaletteModeEnabledKey, String.valueOf(Settings.gbcPaletteModeEnabled));
        SyncGbcPalette(gbcBackgroundPalettePrefix, Settings.CurrentGbcBackgroundPalette());
        SyncGbcPalette(gbcSpritePalette0Prefix, Settings.CurrentGbcSpritePalette0());
        SyncGbcPalette(gbcSpritePalette1Prefix, Settings.CurrentGbcSpritePalette1());
    }

    private static void SyncAppTheme() {
        AppTheme theme = Settings.CurrentAppTheme();
        for (AppThemeColorRole role : AppThemeColorRole.values()) {
            properties.setProperty(themePrefix + role.name(), theme.CoreHex(role));
        }
    }

    private static void SyncInputBindings() {
        for (DuckJoypad.Button button : DuckJoypad.Button.values()) {
            properties.setProperty(inputPrefix + button.name(),
                    String.valueOf(Settings.inputBindings.GetKeyCode(button)));
        }
    }

    private static void SyncControllerBindings() {
        properties.setProperty(controllerEnabledKey, String.valueOf(Settings.controllerInputEnabled));
        properties.setProperty(controllerPreferredIdKey,
                Settings.preferredControllerId == null ? "" : Settings.preferredControllerId);
        properties.setProperty(controllerDeadzoneKey, String.valueOf(Settings.controllerDeadzonePercent));
        for (DuckJoypad.Button button : DuckJoypad.Button.values()) {
            ControllerBinding binding = Settings.controllerBindings.GetBinding(button);
            properties.setProperty(controllerInputPrefix + button.name(),
                    binding == null ? "" : binding.ToConfigValue());
        }
    }

    private static void SyncAppShortcuts() {
        for (AppShortcut shortcut : AppShortcut.values()) {
            properties.setProperty(shortcutPrefix + shortcut.name(),
                    Settings.appShortcutBindings.ToConfigValue(shortcut));
        }
    }

    private static void SyncSoundSettings() {
        properties.setProperty(soundEnabledKey, String.valueOf(Settings.soundEnabled));
        properties.setProperty(soundVolumeKey, String.valueOf(Settings.masterVolume));
        for (int channelIndex = 0; channelIndex < 4; channelIndex++) {
            properties.setProperty(soundChannelMutedPrefix + channelIndex,
                    String.valueOf(Settings.IsChannelMuted(channelIndex)));
            properties.setProperty(soundChannelVolumePrefix + channelIndex,
                    String.valueOf(Settings.GetChannelVolume(channelIndex)));
        }
        properties.setProperty(soundEnhancementEnabledKey, String.valueOf(Settings.IsAudioEnhancementChainEnabled()));
        properties.setProperty(soundEnhancementChainKey, EncodeAudioEnhancementChain(Settings.CurrentAudioEnhancementChain()));
    }

    private static void SyncEmulationSettings() {
        properties.setProperty(useBootRomKey, String.valueOf(Settings.useBootRom));
        properties.setProperty(useCgbBootRomKey, String.valueOf(Settings.useCgbBootRom));
    }

    private static void SyncWindowSettings() {
        properties.setProperty(fillWindowOutputKey, String.valueOf(Settings.fillWindowOutput));
        properties.setProperty(showSerialOutputKey, String.valueOf(Settings.showSerialOutput));
        properties.setProperty(gameArtDisplayModeKey, Settings.gameArtDisplayMode.name());
    }

    private static void SyncLibrarySettings() {
        properties.setProperty(gameNameBracketDisplayModeKey, Settings.gameNameBracketDisplayMode.name());
        properties.setProperty(libraryViewModeKey,
                Settings.libraryViewMode == null || Settings.libraryViewMode.isBlank() ? "LIST" : Settings.libraryViewMode);
    }

    private static List<String> GetSavedPaletteNamesInternal() {
        String stored = properties.getProperty(savedPaletteListKey, "");
        List<String> names = new ArrayList<>();
        if (stored.isBlank()) {
            return names;
        }

        for (String encodedName : stored.split(",")) {
            if (!encodedName.isBlank()) {
                names.add(DecodeName(encodedName));
            }
        }
        return names;
    }

    private static List<String> GetSavedGbcPaletteNamesInternal() {
        String stored = properties.getProperty(savedGbcPaletteListKey, "");
        List<String> names = new ArrayList<>();
        if (stored.isBlank()) {
            return names;
        }

        for (String encodedName : stored.split(",")) {
            if (!encodedName.isBlank()) {
                names.add(DecodeName(encodedName));
            }
        }
        return names;
    }

    private static List<String> GetSavedThemeNamesInternal() {
        String stored = properties.getProperty(savedThemeListKey, "");
        List<String> names = new ArrayList<>();
        if (stored.isBlank()) {
            return names;
        }

        for (String encodedName : stored.split(",")) {
            if (!encodedName.isBlank()) {
                names.add(DecodeName(encodedName));
            }
        }
        return names;
    }

    private static List<String> EncodeNames(List<String> names) {
        List<String> encodedNames = new ArrayList<>();
        for (String name : names) {
            encodedNames.add(EncodeName(name));
        }
        return encodedNames;
    }

    private static String EncodeName(String name) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    private static String DecodeName(String encodedName) {
        return new String(Base64.getUrlDecoder().decode(encodedName), StandardCharsets.UTF_8);
    }

    private static void EnsureLoaded() {
        if (!loaded) {
            Load();
        }
    }

    private static void ApplyGbcPalette(String keyPrefix, String[] defaults, int paletteIndex) {
        for (int colourIndex = 0; colourIndex < defaults.length; colourIndex++) {
            Settings.SetGbcPaletteColour(paletteIndex, colourIndex,
                    properties.getProperty(keyPrefix + colourIndex, defaults[colourIndex]));
        }
    }

    private static void SyncGbcPalette(String keyPrefix, GBColor[] palette) {
        for (int colourIndex = 0; colourIndex < palette.length; colourIndex++) {
            properties.setProperty(keyPrefix + colourIndex, palette[colourIndex].ToHex());
        }
    }

    private static void SyncSavedGbcPalette(String keyPrefix, GBColor[] palette) {
        SyncGbcPalette(keyPrefix, palette);
    }

    private static boolean ApplySavedGbcPalette(String keyPrefix, int paletteIndex) {
        boolean found = false;
        for (int colourIndex = 0; colourIndex < 4; colourIndex++) {
            String value = properties.getProperty(keyPrefix + colourIndex);
            if (value == null) {
                continue;
            }
            Settings.SetGbcPaletteColour(paletteIndex, colourIndex, value);
            found = true;
        }
        return found;
    }

    private static void RemoveSavedGbcPalette(String keyPrefix) {
        for (int colourIndex = 0; colourIndex < 4; colourIndex++) {
            properties.remove(keyPrefix + colourIndex);
        }
    }

    private static List<AudioEnhancementPreset> ParseAudioEnhancementChain(String storedValue) {
        List<AudioEnhancementPreset> chain = new ArrayList<>();
        if (storedValue == null || storedValue.isBlank()) {
            return chain;
        }

        for (String token : storedValue.split(",")) {
            AudioEnhancementPreset preset = AudioEnhancementPreset.FromConfigValue(token);
            if (preset != null) {
                chain.add(preset);
            }
        }
        return chain;
    }

    private static String EncodeAudioEnhancementChain(List<AudioEnhancementPreset> chain) {
        if (chain == null || chain.isEmpty()) {
            return "";
        }

        List<String> encoded = new ArrayList<>();
        for (AudioEnhancementPreset preset : chain) {
            if (preset != null) {
                encoded.add(preset.name());
            }
        }
        return String.join(",", encoded);
    }

    private static void Persist() {
        try {
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }
            try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                properties.store(outputStream, "GameDuck configuration");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
