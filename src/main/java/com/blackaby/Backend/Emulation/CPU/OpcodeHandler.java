package com.blackaby.Backend.Emulation.CPU;

@FunctionalInterface
public interface OpcodeHandler {
    int Execute();
}
