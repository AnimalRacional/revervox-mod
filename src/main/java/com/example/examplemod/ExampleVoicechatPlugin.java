package com.example.examplemod;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;

@ForgeVoicechatPlugin
public class ExampleVoicechatPlugin implements VoicechatPlugin {

    /**
     * @return the unique ID for this voice chat plugin
     */
    @Override
    public String getPluginId() {
        return ExampleMod.MOD_ID;
    }

    /**
     * Called when the voice chat initializes the plugin.
     *
     * @param api the voice chat API
     */
    @Override
    public void initialize(VoicechatApi api) {
        ExampleMod.LOGGER.info("Example voice chat plugin initialized!");
    }

    /**
     * Called once by the voice chat to register all events.
     *
     * @param registration the event registration
     */
    @Override
    public void registerEvents(EventRegistration registration) {
        // TODO register your events
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket, 100);
    }

    private void onMicrophonePacket(MicrophonePacketEvent e){
        ExampleMod.LOGGER.info("packet! " + e.getPacket().isWhispering());
    }

}
