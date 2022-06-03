package me.tropicalfan344.musicbot;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        File musicFile = new File("music.wav");
        Process process = new ProcessBuilder("ffmpeg", "-i", "music.mp3", "-y", "-ar", "48000", "-ac", "2", "-f", "s16be", "-acodec", "pcm_s16be", "pipe:1").start();
        InputStream stream = process.getInputStream();
        while (true) {
            int data = stream.read();
            if (data == -1) break;
            System.out.write(data);
        }
        process.waitFor();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
        AudioFormat format = audioInputStream.getFormat();
        System.out.println(format + " should be " + AudioSendHandler.INPUT_FORMAT);
        byte[] readBuffer = new byte[16];
        SourceDataLine device = null;
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(info);
//            System.out.println(info);
            try {
                device = (SourceDataLine) mixer.getLine(new DataLine.Info(SourceDataLine.class, audioInputStream.getFormat()));
                break;
            } catch (Exception ignored) {}
        }
        device.open(audioInputStream.getFormat());
        device.start();
        while (true) {
            int read = audioInputStream.read(readBuffer);
            if (read == -1) {
                break;
            }
            for (int i = 0; i < readBuffer.length; i+=2) {
                byte old = readBuffer[i+1];
                int i1 = (int) ((readBuffer[i + 1] << 8) | (readBuffer[i] & 0xff));
//                float fuck = i1/Float.MAX_VALUE;
//                fuck *= 0.3;
//                fuck = Math.max(Math.min(fuck, 1), -1);
                i1*=0.5;
                readBuffer[i] = ((byte) i1);
                readBuffer[i+1] = (byte) (i1 >> 8);
//                if (old != readBuffer[i+1]) {
//                    System.out.println(readBuffer[i+1] + "/" + old + "/" + i1 + "/" + (old << 8));
//                }

            }
            device.write(readBuffer, 0, read);
        }
        device.flush();
        device.close();
        device.stop();
        device.drain();
    }
}
