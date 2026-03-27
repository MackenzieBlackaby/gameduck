package com.blackaby.Misc;

/**
 * Built-in audio enhancement presets that can be chained in the host output.
 */
public enum AudioEnhancementPreset {
    SOFT_LOW_PASS(
            UiText.AudioEnhancements.SOFT_LOW_PASS_LABEL,
            UiText.AudioEnhancements.SOFT_LOW_PASS_DESCRIPTION,
            new ParameterSpec("Tone", "Filter brightness", 50),
            null),
    POCKET_SPEAKER(
            UiText.AudioEnhancements.POCKET_SPEAKER_LABEL,
            UiText.AudioEnhancements.POCKET_SPEAKER_DESCRIPTION,
            new ParameterSpec("Focus", "Speaker band shape", 50),
            new ParameterSpec("Crunch", "Speaker edge", 50)),
    SOFT_CLIP(
            UiText.AudioEnhancements.SOFT_CLIP_LABEL,
            UiText.AudioEnhancements.SOFT_CLIP_DESCRIPTION,
            new ParameterSpec("Drive", "Clip strength", 50),
            null),
    STEREO_WIDEN(
            UiText.AudioEnhancements.STEREO_WIDEN_LABEL,
            UiText.AudioEnhancements.STEREO_WIDEN_DESCRIPTION,
            new ParameterSpec("Width", "Stereo image", 50),
            null),
    ROOM_REVERB(
            UiText.AudioEnhancements.ROOM_REVERB_LABEL,
            UiText.AudioEnhancements.ROOM_REVERB_DESCRIPTION,
            new ParameterSpec("Space", "Room size", 50),
            new ParameterSpec("Air", "Reverb brightness", 50)),
    SHIMMER_CHORUS(
            UiText.AudioEnhancements.SHIMMER_CHORUS_LABEL,
            UiText.AudioEnhancements.SHIMMER_CHORUS_DESCRIPTION,
            new ParameterSpec("Depth", "Modulation spread", 50),
            new ParameterSpec("Rate", "Motion speed", 50)),
    DUB_ECHO(
            UiText.AudioEnhancements.DUB_ECHO_LABEL,
            UiText.AudioEnhancements.DUB_ECHO_DESCRIPTION,
            new ParameterSpec("Feedback", "Echo sustain", 50),
            new ParameterSpec("Time", "Echo delay", 50)),
    UNDERWATER(
            UiText.AudioEnhancements.UNDERWATER_LABEL,
            UiText.AudioEnhancements.UNDERWATER_DESCRIPTION,
            new ParameterSpec("Murk", "Low-pass depth", 50),
            new ParameterSpec("Drift", "Wave motion", 50));

    /**
     * Metadata for one effect parameter shown in the UI.
     *
     * @param label display label
     * @param description short helper text
     * @param defaultPercent default value from 0 to 100
     */
    public record ParameterSpec(String label, String description, int defaultPercent) {
    }

    private final String label;
    private final String description;
    private final ParameterSpec primaryParameter;
    private final ParameterSpec secondaryParameter;

    AudioEnhancementPreset(String label, String description, ParameterSpec primaryParameter,
            ParameterSpec secondaryParameter) {
        this.label = label;
        this.description = description;
        this.primaryParameter = primaryParameter;
        this.secondaryParameter = secondaryParameter;
    }

    /**
     * Returns the display label shown in the options UI.
     *
     * @return preset label
     */
    public String Label() {
        return label;
    }

    /**
     * Returns the summary shown in the options UI.
     *
     * @return preset description
     */
    public String Description() {
        return description;
    }

    /**
     * Returns the primary editable parameter for this preset.
     *
     * @return primary parameter metadata
     */
    public ParameterSpec PrimaryParameter() {
        return primaryParameter;
    }

    /**
     * Returns the optional secondary editable parameter for this preset.
     *
     * @return secondary parameter metadata, or {@code null} when unused
     */
    public ParameterSpec SecondaryParameter() {
        return secondaryParameter;
    }

    /**
     * Parses one preset from its persisted enum name.
     *
     * @param configValue stored enum name
     * @return matching preset or {@code null} when unknown
     */
    public static AudioEnhancementPreset FromConfigValue(String configValue) {
        if (configValue == null || configValue.isBlank()) {
            return null;
        }

        try {
            return AudioEnhancementPreset.valueOf(configValue.trim());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
