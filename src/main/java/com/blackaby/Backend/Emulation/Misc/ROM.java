package com.blackaby.Backend.Emulation.Misc;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class represents a ROM file.
 * It has a filename and the data of the ROM.
 * The data is stored as a byte array and can be read with the given function
 */
public class ROM {
    private String filename;
    private byte data[];

    public ROM(String filename) {
        this.filename = filename;
        if (!filename.equals(""))
            LoadRom();
    }

    private void LoadRom() {
        // Get size of the file
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

        // Read in data
        data = new byte[size];
        try (FileInputStream reader = new FileInputStream(filename)) {
            reader.read(data);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * This method returns the filename of the ROM
     * 
     * @return The filename of the ROM
     */
    public String getFilename() {
        return filename;
    }

    /**
     * This method reads an instruction from the ROM
     * 
     * @param location The program counter
     * @return The instruction at the given program counter
     */
    public byte getByte(int location) {
        if (location >= data.length) {
            return 0;
        }
        return data[location];
    }

    /**
     * This method reads a number of bytes from the ROM
     * @param location The location to start reading from
     * @param count The number of bytes to read
     * @return The bytes read
     */
    public byte[] getBytes(int location, int count) {
        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = getByte(location + i);
        }
        return bytes;
    }
}
