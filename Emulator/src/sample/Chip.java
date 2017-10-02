package sample;

public class Chip {

    char opcode;

    /**
     * Memory map
     * 0x000-0x1FF - Chip 8 Interpreter (contains font set in emu)
     * 0x050-0x0A0 - Used for the built in 4x5 pixel font set (0-F)
     * 0x200-0xFFF - Program ROM and work RAM
     */
    byte[] memory = new byte[4096];
    byte[] V = new byte[16];
    char I; //Index register
    char pc; //Program counter

    /**
     * The graphics are black and white
     * Total pixels is 2048 (64 x 32)
     */
    byte[] gfx = new byte[64 * 32];

    /**
     * Timers that count at 60Hz
     * When set above 0 will count down to zero
     */
    byte delay_timer;
    byte sound_timer;

    /**
     * The stack remembers the current location before a jump is performed.
     * The stack pointer is used to remember which level of the 16 levels of stack.
     */
    char[] stack = new char[16];
    char sp;

    /**
     * HEX based keypad (0x0 - 0xF)
     */
    byte[] key = new byte[16];

    //fontset?
    byte[] chip8_fontset = new byte[256];

    public Chip(){

    }

    public void initialize(){
        pc = 0x200;
        opcode = 0;
        I = 0;
        sp = 0;

        gfx = new byte[64 * 32]; //Clear display
        stack = new char[16]; //Clear stack
        V = new byte[16]; //Clear registers
        memory = new byte[4096]; //Clear memory

        /**
         * Load fontset
         */

        for (int i = 0; i < 80; i++){
            memory[i] = chip8_fontset[i];
        }

        delay_timer = 0;
        sound_timer = 0;
    }

    public void loadGame(String path){
        byte[] buffer; //Will be the game
        int bufferSize = 0;

        buffer = new byte[bufferSize];

        for (int i = 0; i < bufferSize; i++){
            memory[i + 512] = buffer[i];
        }
    }

    public void emulateCycle(){
        /**
         * Fetch Opcode
         */
        int a = memory[pc] << 8 | memory[pc + 1];
        opcode = (char)a;

        /**
         * Decode Opcode
         *
         * NNN = address
         * NN = 8-bit constant
         * N = 4-bit constant
         * X and Y = 4-bit register identifier
         * PC = Program counter
         * I = 16-bit register
         */
        byte vx;

        switch (opcode & 0xF000){
            case 0x0000:
                switch (opcode & 0x000F){
                    case 0x0000: // 0x00E0 Clears the screen
                        gfx = new byte[64*32];
                        break;
                    case 0x000E: // 0x00EE Returns the subroutine
                        break;
                }
                break;
            case 0x1000: // 0x1NNN Jump to address NNN
                break;
            case 0x2000: // 0x2NNN Calls subroutine at NNN
                stack[sp] = pc;
                ++sp;
                pc = (char)(opcode & 0x0FFF);
                break;
            case 0x3000: // 0x3XNN Skips the next instruction if VX equals NN (usually the next instruction is a jump to skip a code block)
                break;
            case 0x4000: // 0x4XNN Skips the next instruction if VX doesn't equal NN
                break;
            case 0x5000: // 0x5XY0 Skips the next instruction if VX equals VY
                break;
            case 0x6000: // 0x6XNN Sets VX to NN
                V[(opcode & 0x0F00)] = (byte)(opcode & 0x00FF);
                pc += 2;
                break;
            case 0x7000: // 0x7XNN Adds NN to VX (carry flag is not changed)
                break;
            case 0x8000:
                switch (opcode & 0x000F){
                    case 0x0000: // 0x8XY0 Sets VX to the value of VY
                        V[(opcode & 0x0F00)] = V[(opcode & 0x00F0)];
                        pc += 2;
                        break;
                    case 0x0001: // 0x8XY1 Sets VX to VX or VY (Bitwise OR operation)
                        break;
                    case 0x0002: // 0x8XY2 Sets VX to VX and VY (Bitwise AND operation)
                        break;
                    case 0x0003: // 0x8XY3 Sets VX to VX xor VY
                        break;
                    case 0x0004: // 0x8XY4 Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
                        break;
                    case 0x0005: // 0x8XY5 VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't
                        break;
                    case 0x0006: // 0x8XY6 Shifts VY right by one and copies the result to VX. VF is set to the value of the least significant bit of VY before the shift.
                        break;
                    case 0x0007: // 0x8XY7 Sets VX to VY minus VX. VF is set to 0 when there's a borrow and 1 when there isn't
                        break;
                    case 0x000E: // 0x8XYE Shifts VY left by one nad copies the result to VX. VF is set to the value of the most significant bit of VY before the shift.
                        break;
                }
                break;
            case 0x9000: // 0x9XY0 Skips the next instruction if VX doesn't equal VY.
                break;
            case 0xA000: // 0xANNN Sets I to the address NNN
                I = (char)(opcode & 0x0FFF);
                pc += 2;
                break;
            case 0xB000: // 0xBNNN Jumps to the address NNN plus V0
                break;
            case 0xC000: // 0xCXNN Set VX to the result of a bitwise and operation on a random number (typically: 0 to 255) and NN.
                break;
            case 0xD000: // 0xDXYN Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.
                break;
            case 0xE000:
                switch (opcode & 0x00F){
                    case 0x000E: // 0xEX9E Skips the next instruction if the key stored in VX is pressed
                        break;
                    case 0x0001: // 0xEXA1 Skips the next instruction if the key stored in VX isn't pressed
                        break;
                }
                break;
            case 0xF000:
                switch (opcode & 0x000F){
                    case 0x0007: // 0xFX07 Sets VX to the value of the delay timer
                        V[(opcode & 0x0F00)] = delay_timer;
                        pc += 2;
                        break;
                    case 0x000A: // 0xFX0A A key press is awaited, and then stored in VX (Blocking operation. All instruction halted until next key event)
                        break;
                    case 0x0008: // 0xFX18 Sets the sound timer to VX
                        vx = V[(opcode & 0x0F00)];
                        sound_timer = vx;
                        pc += 2;
                        break;
                    case 0x000E: // 0xFX1E Adds VX to I
                        vx = V[(opcode & 0x0F00)];
                        I += vx;
                        pc += 2;
                        break;
                    case 0x0009: // 0xFX29 Sets I to the location of the sprite for the character in VX. Character 0-F are represented by a 4x5 font
                        break;
                    case 0x0003: // 0xFX33 !
                        memory[I] = (byte)(V[(opcode & 0x0F00) >> 8] / 100);
                        memory[I + 1] = (byte)((V[(opcode & 0x0F00) >> 8] / 10) % 10);
                        memory[I + 2] = (byte)((V[(opcode & 0x0F00) >> 8] % 100) % 10);
                        pc += 2;
                        break;
                    case 0x0005:
                        switch (opcode & 0x00F0){
                            case 0x0010: // 0xFX15 Sets the delay timer to VX
                                vx = V[(opcode & 0x0F00)];
                                delay_timer = vx;
                                pc += 2;
                                break;
                            case 0x0050: // 0xFX55 Stores V0 to VX (including VX) in memory starting at address I. I is increased by 1 for each value written
                                break;
                            case 0x0060: // 0xFX65 Stores V0 to VX (including VX) with values from memory starting at address I. I is increased by 1 for each value wirtten
                                break;
                        }
                        break;
                }
                break;
            default:
                System.out.println("Unkown opcode: " + opcode);
        }

        /**
         * Execute Opcode
         */

        /**
         * Update timers
         */
    }
}
