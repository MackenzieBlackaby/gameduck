package com.blackaby.Backend.Emulation.CPU;

import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class DuckPPU {

    private DuckCPU cpu;
    private DuckMemory memory;

    public DuckPPU(DuckCPU cpu, DuckMemory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }
}
