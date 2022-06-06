package me.tropicalfan344.musicbot;

public class Test2 {
    public static void main(String[] args) {
        byte[] ba = new byte[]{-0x20, 0x21};
        int i = (int) ((ba[1] << 8) | (ba[0] & 0xff));
    }
}
