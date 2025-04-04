package com.blackaby.Backend.Emulation.Misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

/**
 * This class represents a ROM file.
 * It has a filename and the data of the ROM.
 * The data is stored as a byte array and can be read with the given function
 */
public class ROM {
    private String filename;
    private int data[];

    public ROM(String filename) {
        this.filename = filename;
        if (!filename.equals(""))
            LoadRom();
    }

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
