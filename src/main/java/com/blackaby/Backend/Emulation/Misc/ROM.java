package com.blackaby.Backend.Emulation.Misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

/**
 * Represents a Game Boy ROM file.
 * <p>
 * Loads the contents of the ROM from disk and provides access to the ROM's
 * binary data and metadata (filename, display name).
 * </p>
 */
public class ROM {
    private String filename;
    private int data[];

    /**
     * Constructs a ROM object with the given file path.
     * Automatically loads the ROM data into memory.
     *
     * @param filename The path to the ROM file.
     */
    public ROM(String filename) {
        this.filename = filename;
        if (!filename.equals(""))
            LoadRom();
    }

    /**
     * Returns the raw ROM data as an array of unsigned 8-bit values.
     *
     * @return An array containing the ROM contents.
     */
    public int[] getData() {
        return data;
    }

    private void LoadRom() {
        int size = 0;
        try (FileInputStream reader = new FileInputStream(filename)) {
            while (reader.read() != -1) {
                size++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        byte tempData[] = new byte[size];
        try (FileInputStream reader = new FileInputStream(filename)) {
            reader.read(tempData);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = 0xFF & tempData[i];
        }
    }

    /**
     * This method returns the filename of the ROM
     * 
     * @return The filename of the ROM
     */
    public String getPath() {
        return filename;
    }

    /**
     * Returns the display name of the ROM file (without path or extension).
     *
     * @return The ROM's base name.
     */
    public String getName() {
        File file = new File(filename);
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) {
            name = name.substring(0, dotIndex);
        }
        return name;
    }
}
