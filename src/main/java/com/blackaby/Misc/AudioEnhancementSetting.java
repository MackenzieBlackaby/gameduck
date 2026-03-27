package com.blackaby.Misc;

/**
 * Stores one host-side audio enhancement plus its editable parameters.
 */
public record AudioEnhancementSetting(AudioEnhancementPreset preset, int intensityPercent, int primaryPercent,
        int secondaryPercent) {

    private static final int defaultIntensityPercent = 100;

    public AudioEnhancementSetting {
        if (preset == null) {
            throw new IllegalArgumentException("Audio enhancement preset is required.");
        }
        intensityPercent = Math.max(0, Math.min(100, intensityPercent));
        primaryPercent = Math.max(0, Math.min(100, primaryPercent));
        secondaryPercent = Math.max(0, Math.min(100, secondaryPercent));
    }

    /**
     * Creates a setting at the default full intensity.
     *
     * @param preset enhancement preset
     * @return enhancement setting
     */
    public static AudioEnhancementSetting Default(AudioEnhancementPreset preset) {
        AudioEnhancementPreset.ParameterSpec primaryParameter = preset.PrimaryParameter();
        AudioEnhancementPreset.ParameterSpec secondaryParameter = preset.SecondaryParameter();
        return new AudioEnhancementSetting(
                preset,
                defaultIntensityPercent,
                primaryParameter == null ? 50 : primaryParameter.defaultPercent(),
                secondaryParameter == null ? 50 : secondaryParameter.defaultPercent());
    }

    /**
     * Returns the setting serialised for the config file.
     *
     * @return config-safe value
     */
    public String ToConfigValue() {
        return preset.name() + ":" + intensityPercent + ":" + primaryPercent + ":" + secondaryPercent;
    }

    /**
     * Returns a copy with a new intensity value.
     *
     * @param newIntensityPercent replacement intensity
     * @return updated setting
     */
    public AudioEnhancementSetting WithIntensity(int newIntensityPercent) {
        return new AudioEnhancementSetting(preset, newIntensityPercent, primaryPercent, secondaryPercent);
    }

    /**
     * Returns a copy with a new primary parameter value.
     *
     * @param newPrimaryPercent replacement parameter value
     * @return updated setting
     */
    public AudioEnhancementSetting WithPrimary(int newPrimaryPercent) {
        return new AudioEnhancementSetting(preset, intensityPercent, newPrimaryPercent, secondaryPercent);
    }

    /**
     * Returns a copy with a new secondary parameter value.
     *
     * @param newSecondaryPercent replacement parameter value
     * @return updated setting
     */
    public AudioEnhancementSetting WithSecondary(int newSecondaryPercent) {
        return new AudioEnhancementSetting(preset, intensityPercent, primaryPercent, newSecondaryPercent);
    }

    /**
     * Parses one stored enhancement setting.
     * Legacy values that only store a preset name default to 100% intensity.
     *
     * @param configValue stored config token
     * @return parsed setting, or {@code null} if invalid
     */
    public static AudioEnhancementSetting FromConfigValue(String configValue) {
        if (configValue == null || configValue.isBlank()) {
            return null;
        }

        String trimmedValue = configValue.trim();
        String[] parts = trimmedValue.split(":");
        if (parts.length == 0) {
            return null;
        }

        AudioEnhancementPreset preset = AudioEnhancementPreset.FromConfigValue(parts[0]);
        if (preset == null) {
            return null;
        }

        if (parts.length == 1) {
            return Default(preset);
        }

        AudioEnhancementSetting defaults = Default(preset);
        if (parts.length == 2) {
            try {
                int intensityPercent = Integer.parseInt(parts[1]);
                return new AudioEnhancementSetting(
                        preset,
                        intensityPercent,
                        defaults.primaryPercent(),
                        defaults.secondaryPercent());
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        if (parts.length != 4) {
            return null;
        }

        try {
            int intensityPercent = Integer.parseInt(parts[1]);
            int primaryPercent = Integer.parseInt(parts[2]);
            int secondaryPercent = Integer.parseInt(parts[3]);
            return new AudioEnhancementSetting(preset, intensityPercent, primaryPercent, secondaryPercent);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        return preset.Label() + " (" + intensityPercent + "%)";
    }
}
