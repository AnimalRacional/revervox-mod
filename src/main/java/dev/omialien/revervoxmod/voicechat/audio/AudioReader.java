package dev.omialien.revervoxmod.voicechat.audio;

import dev.omialien.revervoxmod.RevervoxMod;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AudioReader {
    private final Path path;
    boolean destroy;

    public AudioReader(Path path){
        this.path = path;
        this.destroy = false;
    }

    public AudioReader(Path path, boolean deleteAfter) {
        this.path = path;
        this.destroy = deleteAfter;
    }

    public Future<short[]> read() {
        RevervoxMod.LOGGER.debug("AUDIOREADER STARTING {}", path);
        return Executors.newSingleThreadExecutor().submit(() -> getFile(path));
    }

    private short[] getFile(Path path){
        try {
            File file = path.toFile();

            int numberOfShorts = (int) (file.length() / 2); // each short = 2 bytes
            short[] audio = new short[numberOfShorts];

            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            for (int i = 0; i < numberOfShorts; i++) {
                audio[i] = dis.readShort();
            }
            dis.close();
            RevervoxMod.LOGGER.debug("Read from the file!");
            Files.delete(path);
            return audio;
        } catch (FileNotFoundException e){
            RevervoxMod.LOGGER.error("AUDIOREADER File not found: {}", path);
        } catch (Exception e) {
            RevervoxMod.LOGGER.error("ERROR ON AUDIOREADER: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
