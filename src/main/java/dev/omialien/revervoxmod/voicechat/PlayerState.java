package dev.omialien.revervoxmod.voicechat;

import com.mojang.datafixers.util.Pair;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import dev.omialien.voicechatrecording.VoiceChatRecording;

import java.util.UUID;

public class PlayerState {
    private boolean isScreaming;
    private long lastScream;
    private final UUID uuid;
    private Pair<OpusDecoder, OpusEncoder> coders;
    public PlayerState(UUID uuid){
        this.uuid = uuid;
        this.isScreaming = false;
        coders = new Pair<>(VoiceChatRecording.vcApi.createDecoder(), VoiceChatRecording.vcApi.createEncoder());
    }
    public UUID getUuid() {return this.uuid; }
    public void setScreaming(boolean screaming){
        if(screaming){
            this.lastScream = System.currentTimeMillis();
        }
        this.isScreaming = screaming;
    }
    public boolean isScreaming() { return this.isScreaming && System.currentTimeMillis() - this.lastScream <= 500; }
    public OpusDecoder getDecoder() { return this.coders.getFirst(); }
    public OpusEncoder getEncoder() { return this.coders.getSecond(); }
}
