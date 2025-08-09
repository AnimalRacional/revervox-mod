package com.example.revervoxmod.voicechat.audio;

import com.example.revervoxmod.RevervoxMod;
import com.example.revervoxmod.voicechat.RevervoxVoicechatPlugin;

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
        audioCache = RevervoxVoicechatPlugin.getAudioCache();
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

            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            for (int i = 0; i < numberOfShorts; i++) {
                audio[i] = dis.readShort();
            }
            dis.close();
            RevervoxMod.LOGGER.debug("Read from the file!");

            return audio;
        } catch (FileNotFoundException e){
            RevervoxMod.LOGGER.error("File not found: " + path);
        } catch (Exception e) {
            RevervoxMod.LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
