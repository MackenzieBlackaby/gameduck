package com.blackaby.Misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AudioEnhancementSettingTest {

    @Test
    void legacyPresetOnlyConfigDefaultsToFullIntensity() {
        AudioEnhancementSetting setting = AudioEnhancementSetting.FromConfigValue("SOFT_CLIP");

        assertEquals(AudioEnhancementPreset.SOFT_CLIP, setting.preset());
        assertEquals(100, setting.intensityPercent());
        assertEquals(AudioEnhancementSetting.Default(AudioEnhancementPreset.SOFT_CLIP).primaryPercent(),
                setting.primaryPercent());
        assertEquals(AudioEnhancementSetting.Default(AudioEnhancementPreset.SOFT_CLIP).secondaryPercent(),
                setting.secondaryPercent());
    }

    @Test
    void legacyIntensityConfigPreservesDefaults() {
        AudioEnhancementSetting setting = AudioEnhancementSetting.FromConfigValue("ROOM_REVERB:42");

        assertEquals(AudioEnhancementPreset.ROOM_REVERB, setting.preset());
        assertEquals(42, setting.intensityPercent());
        assertEquals(AudioEnhancementSetting.Default(AudioEnhancementPreset.ROOM_REVERB).primaryPercent(),
                setting.primaryPercent());
        assertEquals(AudioEnhancementSetting.Default(AudioEnhancementPreset.ROOM_REVERB).secondaryPercent(),
                setting.secondaryPercent());
    }

    @Test
    void configRoundTripPreservesPresetAndParameters() {
        AudioEnhancementSetting setting = new AudioEnhancementSetting(AudioEnhancementPreset.DUB_ECHO, 42, 63, 27);

        AudioEnhancementSetting parsedSetting = AudioEnhancementSetting.FromConfigValue(setting.ToConfigValue());

        assertEquals(setting, parsedSetting);
    }

    @Test
    void invalidConfigReturnsNull() {
        assertNull(AudioEnhancementSetting.FromConfigValue("not-a-preset"));
        assertNull(AudioEnhancementSetting.FromConfigValue("ROOM_REVERB:not-a-number"));
        assertNull(AudioEnhancementSetting.FromConfigValue("ROOM_REVERB:50:60"));
    }
}
