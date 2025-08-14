package dev.omialien.revervoxmod.voicechat.audio;

import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.revervoxmod.config.RevervoxModServerConfigs;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AudioDirectoryReader extends Thread{
    private final Path path;
    final boolean destroy;
    final Consumer<short[]> reaction;
    final Predicate<Path> shouldRead;

    public AudioDirectoryReader(Path path, Consumer<short[]> reaction, Predicate<Path> shouldRead){
        this.path = path;
        this.destroy = false;
        this.reaction = reaction;
        this.shouldRead = shouldRead;
    }

    public AudioDirectoryReader(Path path, boolean deleteAfter, Consumer<short[]> reaction, Predicate<Path> shouldRead) {
        this.path = path;
        this.destroy = deleteAfter;
        this.reaction = reaction;
        this.shouldRead = shouldRead;
    }

    @Override
    public void run() {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
            Iterator<Path> iter = stream.iterator();
            RevervoxMod.LOGGER.debug("getting audios...");
            ExecutorService threadPool = Executors.newFixedThreadPool(RevervoxModServerConfigs.AUDIO_READER_THREAD_COUNT.get());
            while(iter.hasNext()) {
                Path cur = iter.next();
                if(shouldRead.test(cur)){
                    threadPool.submit(() -> {
                        reaction.accept(getFile(cur));
                        if(destroy){
                            try{
                                Files.delete(cur);
                            } catch(IOException e){
                                RevervoxMod.LOGGER.error("Error deleting file {}\r\n{}\r\n{}", cur, e.getMessage(), e.getStackTrace());
                            }
                        }
                        RevervoxMod.LOGGER.debug("Finishing task for {}", cur);
                    });
                } else {
                    RevervoxMod.LOGGER.warn("Unkown file found in audio folder {}: {}", path, cur);
                    if(destroy){
                        try{
                            Files.delete(cur);
                        } catch(IOException e){
                            RevervoxMod.LOGGER.error("Error deleting unknown file {}\r\n{}\r\n{}", cur, e.getMessage(), e.getStackTrace());
                        }
                    }
                }

            }
            try{
                threadPool.shutdown();
                if(!threadPool.awaitTermination(120, TimeUnit.SECONDS)){
                    RevervoxMod.LOGGER.error("Couldn't finish reading audios in {}!", path);
                } else {
                    RevervoxMod.LOGGER.info("Finished reading audios in {}!", path);
                }
            } catch(InterruptedException e){
                RevervoxMod.LOGGER.debug("AudioReader thread pool interrupted!\r\n{}\r\n{}", e.getMessage(), e.getStackTrace());
            }
        } catch(IOException e){
            RevervoxMod.LOGGER.error("Error reading audio\r\n{}\r\n{}", e.getMessage(), e.getStackTrace());
        }
        if(destroy){
            try{
                Files.delete(path);
            } catch(IOException e){
                RevervoxMod.LOGGER.error("Couldn't delete directory {}\r\n{}\r\n{}", path, e.getMessage(), e.getStackTrace());
            }
        }
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
            RevervoxMod.LOGGER.error("AUDIOREADER File not found: {}", path);
        } catch (Exception e) {
            RevervoxMod.LOGGER.error("ERROR ON AUDIOREADER: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
