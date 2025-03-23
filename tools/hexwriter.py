#!/usr/bin/env python3
import sys


def convert_rom_to_text(input_filename, output_filename):
    # Open the ROM file in binary mode and read all bytes
    with open(input_filename, "rb") as infile:
        rom_data = infile.read()

    # Open the output file in text mode and write the formatted data
    with open(output_filename, "w") as outfile:
        # Process the ROM data 16 bytes at a time
        for i in range(0, len(rom_data), 16):
            # Get a slice of 16 bytes (or fewer for the final line)
            line_bytes = rom_data[i : i + 16]
            # Format each byte as "0xXX" (uppercase hex)
            line_str = ", ".join("0x{:02X}".format(b) for b in line_bytes)
            outfile.write(line_str + "\n")


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python convert_rom.py input.rom output.txt")
        sys.exit(1)

    input_filename = sys.argv[1]
    output_filename = sys.argv[2]

    convert_rom_to_text(input_filename, output_filename)
