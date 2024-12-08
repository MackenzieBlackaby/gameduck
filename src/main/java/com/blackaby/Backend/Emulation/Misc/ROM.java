package com.blackaby.Backend.Emulation.Misc;

import com.blackaby.Backend.Emulation.CPU.Instructions.InstructionType;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents a ROM file.
 * It has a filename and the data of the ROM.
 * The data is stored as a byte array and can be read with the given function
 */
public class ROM {
    private String filename;
    private ArrayList<BinaryInstruction> data;

    public ROM(String filename) {
        this.filename = filename;
        data = new ArrayList<BinaryInstruction>();
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
        // Read in raw data
        byte buffer[] = new byte[size];
        try (FileInputStream reader = new FileInputStream(filename)) {
            reader.read(buffer);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < size;) {
            // Slice 2 bytes from the buffer
            byte slice[] = new byte[2];
            slice[0] = buffer[i];
            // Get the type of the instruction and the number of operands
            InstructionType currentType = InstructionType.fromOpcode(slice);
            System.out.println("Current type: " + currentType.getDescription());
            int operandCount = currentType.getOperandCount();
            int opcodeLength = currentType.getOpcode().length;
            // Get the opcode
            byte[] opcode = new byte[opcodeLength];
            for (int j = 0; j < opcode.length; j++) {
                opcode[j] = buffer[i];
            }
            // Get the operands
            byte[] operands = new byte[operandCount];
            for (int j = 0; j < operands.length; j++) {
                operands[j] = buffer[i + j + opcodeLength];
            }
            // Create the instruction
            data.add(new BinaryInstruction(opcode, operands));
            // Increment the index by the number of operands and the opcode length
            i += operandCount + opcodeLength;
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
     * @param PC The program counter
     * @return The instruction at the given program counter
     */
    public byte[] getOpcode(int PC) {
        if (PC >= data.size()) {
            return new byte[0];
        }
        return data.get(PC).getOpcode();
    }

    /**
     * This method reads the operands of an instruction from the ROM
     * 
     * @param PC The program counter
     * @return The operands of the instruction at the given program counter
     */
    public byte[] getOperands(int PC) {
        if (PC >= data.size()) {
            return new byte[0];
        }
        return data.get(PC).getOperands();
    }
}
