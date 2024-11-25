# This tool is designed to write hex values to a file directly

filename = input("Enter the filename\n> ")

inputData = 0

while inputData != -1:
    inputData = int(input("Enter the hex value\n> "), 16)
    if inputData != -1:
        with open(filename, "ab") as file:
            file.write(inputData.to_bytes(1, byteorder="big"))
