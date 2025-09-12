package dev.omialien.revervoxmod.voicechat;

import com.mojang.datafixers.util.Pair;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import dev.omialien.revervoxmod.RevervoxMod;
import dev.omialien.voicechat_recording.VoiceChatRecording;

import java.util.UUID;

public class PlayerState {
    private boolean isUsingMegaphone;
    private boolean isScreaming;
    private final UUID uuid;
    private Pair<OpusDecoder, OpusEncoder> coders;
    public PlayerState(UUID uuid){
        this.uuid = uuid;
        this.isUsingMegaphone = false;
        this.isScreaming = false;
        if(VoiceChatRecording.vcApi instanceof VoicechatServerApi api) {
            coders = new Pair<>(api.createDecoder(), api.createEncoder());
        } else {
            RevervoxMod.LOGGER.error("Tried to create player state without server API!");
        }
    }
    public UUID getUuid() {return this.uuid; }
    public void setScreaming(boolean screaming){ this.isScreaming = screaming; }
    public void setMegaphone(boolean megaphone) { this.isUsingMegaphone = megaphone; }
    public boolean isScreaming() { return this.isScreaming; }
    public boolean isUsingMegaphone() { return this.isUsingMegaphone; }
    public OpusDecoder getDecoder() { return this.coders.getFirst(); }
    public OpusEncoder getEncoder() { return this.coders.getSecond(); }
}
