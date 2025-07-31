package com.example.examplemod.voicechat.audio;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.voicechat.ExampleVoicechatPlugin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AudioReader {
    private final Path path;
    private static ConcurrentHashMap<Path, Future<short[]>> audioCache;

    public AudioReader(Path path){
        this.path = path;
        audioCache = ExampleVoicechatPlugin.getAudioCache();
    }

    public Future<short[]> read() {
        if (!audioCache.containsKey(path)) {
            audioCache.put(path, Executors.newSingleThreadExecutor().submit(() -> {
                return getFile(path);
            }));
        }
        return audioCache.get(path);
    }

    private short[] getFile(Path path){
        try {
            File file = path.toFile();

            int numberOfShorts = (int) (file.length() / 2); // each short = 2 bytes
            short[] audio = new short[numberOfShorts];
            ExampleMod.LOGGER.info("Short Array Size: " + audio.length);

            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            for (int i = 0; i < numberOfShorts; i++) {
                audio[i] = dis.readShort();
            }
            dis.close();
            ExampleMod.LOGGER.info("Read from the file!");

            return audio;
        } catch (FileNotFoundException e){
            ExampleMod.LOGGER.error("File not found: " + path);
        } catch (Exception e) {
            ExampleMod.LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
