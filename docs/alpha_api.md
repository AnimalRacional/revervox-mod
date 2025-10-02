## For developers
This is still an alpha, but the API shouldn't change too much in the final release; official documentation will be written later, but for now here is a simple overview of how to use it.
### Starting out
First, add it as a dependency for your mod, through something like https://support.modrinth.com/en/articles/8801191-modrinth-maven or by having the mod's jar as a library in your workspace.
This will add a new event, ``dev.omialien.voicechatrecording_api.events.RecordingSetupEvent``, which you can listen to just like any other NeoForge event. In here, you should use the ``RecordingSetupEvent#addCategory(String id, String name, String description, int[][] icon)`` to register your own volume category in the voice chat. For example:

``event.addCategory(CoolMod.MODID, "Cool Mod Voices", "The volume of audios played through my cool mod", null)`` 

Do note the ID should be less than 16 characters. Afterwards, you'll use this ID to play audios through that category.
You should also store the ``VoiceChatRecordingApi`` object returned by the ``RecordingSetupEvent#getApi()`` method, for example in your main Mod class. More on this object later.
### Receiving audios: Events
After that, there's 4 other events you can listen to to receive audios:
* AudioEvent - this will be fired anytime an audio is recorded or loaded. You can use ``AudioEvent#getAudio()`` to get the audio from this event.
* AudioRecordedEvent - this will be fired anytime a player finishes speaking. You can get the audio the same way as AudioEvent.
* AudioLoadedEvent - like the above event, but it's only fired when an audio is read from disk. There's several reasons an audio may be loaded, and you may not want to use all audios that come through here: you can find the reason a specific audio was loaded through ``AudioLoadedEvent#getLoadReason``, which may be SINGLE, meaning it's a single audio being loaded, ALL_FROM_USER, meaning it's one audio being loaded at the same time as all other audios from the user who recorded it, or NAMESPACE, meaning it's one audio being loaded at the same time as all other audios saved by a specific namespace (more on namespaces later, but if this is the load reason, you can use ``AudioEvent#getNamespace()`` to know which namespace loaded it)
* MicPacketReceivedEvent - this is a wrapper around the Simple Voice Chat API's MicrophonePacketEvent. You can use ``getPacket()`` to get the corresponding Simple Voice Chat packet, ``getPlayer()`` to get the player that sent the packet, and ``getVoiceChatApiEvent()`` to get the Simple Voice Chat event. You're probably better off making your own Simple Voice Chat plugin and listening to this event there, but this is also an option.
### Using audios: IRecordedAudio object
Except for MicPacketReceivedEvent, the Audio events will all have the ``getAudio()`` method, which returns an ``IRecordedAudio``, the API's representation of an audio. This class has various methods to use the audio however you want, and you can store audios you receive from events and want to use repeatedly, for example, in a list in your main class, so you don't have to get them from the API every time.
Some useful methods from this class are:
* ``short[] getAudio()`` - returns a short array, which is the actual sound data. You can use this to play the audio manually, or make your own modifications to the audio: however, be sure to clone the array instead of using it directly, as otherwise you might change the audio for other mods.
* ``UUID getPlayerUUID()`` - gets the UUID of the player who spoke this audio.
* ``UUID getId()`` - gets the UUID that, together with the player's UUID, uniquely identifies this audio. This can be used to refer to audios without keeping the actual audio in memory.
* ``void saveAudio(String namespace)`` - Saves the audio in the specified namespace: usually, the namespace should be your mod's ID. This will make it so that the audio is stored to disk after the server or world closes, and the namespace is used to identify which mods saved which audios. Loading audios that were saved will be explained later.
* ``void unsaveAudio(String namespace)`` - Removes the saved audio from the specified namespace. This would be the same namespace you used in saveAudio; this doesn't mean the audio file will be deleted from the disk, because other mods, with different namespaces, might have saved it, and an audio file will only be deleted once all namespaces unsaved it. Note that these 2 methods only matter for saving audios to disk so they persist after the server is shutdown; you can store audios temporarily in your mod without saving them, and they'll keep existing until the server shuts down.
* ``short[] applyEffects(AudioEffect effects)`` - The AudioEffects object can be used to represent some audio effects to apply to audios: pitch, reverb and robotic. The AudioEffects object is only a way of identifying which effects to apply, and has nothing to do with the actual audios. You can use this method to apply the effects passed in to the audio, returning a copy of the audio with those effects applied.
* ``FilterResult getFilterResult()`` - This will get a FilterResult object, which identifies how the API evaluates the audio: the possible values are:
1. PASSED, which means the audio is good
2. TOO_LONG, which means the audio was too long
3. TOO_SHORT, which means the audio was too short
4. NO_ACTIVE_AUDIO, which means the audio was likely just noise with no actual voices
5. LOW_RMS, which means the audio was likely too low-volume.
* ``double getDuration()`` - returns the duration, in seconds, of the audio.
* ``double getActiveDuration()`` - returns the duration, in seconds, of the active area of the audio: that is, the area of the audio that likely has voices and not just background noise.
* ``double getRms()`` - returns the "Root Mean Square" of the audio, which can be used to measure how loud the audio is.
### Loading Audios
After saving audios to disk, there are a lot of ways of loading them back, all of them through the ``VoiceChatRecordingApi`` object you got from the ``RecordingSetupEvent`` in the beginning.
* ``Set<Pair<UUID, UUID>> getNamespaceAudios(String namespace)`` - This won't actually load audios: it will return a set listing all the audios that were saved by a specific namespace. The first UUID is the UUID of the player who the audio is from, the second is the UUID to uniquely identify the audio.
  Audio loading will return a [``Future``](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html) object, which, in Java, represents a task being run in the background that will have a result. To get that result, you can use the ``Future#get()`` method, but do note that if the task is still being done, meaning, if the audio is still being read from disk, that the call will block the execution of the game until it finishes. The loading methods often have an optional ``Consumer<IRecordedAudio>`` parameter, in which you can pass a function to execute once the audio finishes loading. Loading audios will also fire a ``AudioLoadedEvent`` once they finish loading. The audio loading methods are the following:
* ``Set<Future<IRecordedAudio>> loadNamespaceAudios(String namespace, Consumer<IRecordedAudio> reaction)`` - Loads all audios that were saved to a specific namespace. If the reaction parameter is given, every time an audio finishes loading, pass the audio to the reaction. Returns a set with all the audios being loaded.
* ``Future<IRecordedAudio> loadAudio(UUID playerUuid, UUID audioId, Consumer<IRecordedAudio> reaction)`` - Loads a specific audio, identified by the player it was recorded from and it's unique identifier. The reaction parameter is optional. Returns the loaded audio.
* ``Set<Future<IRecordedAudio>> loadPlayerAudios(UUID playerUuid, Consumer<IRecordedAudio> reaction)`` - Loads all audios recorded by a specific player. This will most likely be changed in the future, as it loads audios from all namespaces instead of a specific one. The reaction parameter is optional. Returns a set with all the audios being loaded.
* ``IRecordedPlayer getRecordedPlayer(UUID uuid)`` - Returns a ``IRecordedPlayer`` object representing the player with the specified UUID that is being recorded. More on this object soon, but do note that a player may be in the server, but not connected to voice chat, meaning that this may return ``null`` even for an online player.
### Playing audios: the AudioPlayingUtil class
This class allows you to play recorded audios easily, through the following static methods:
* ``playLocationalAudio(IRecordedAudio audio, Vec3 position, ServerLevel level, AudioEffect effects, String category, float distance)`` - Plays the given audio at the specified location, in the specified level, with the specified effects, through the specified category. The category is the same one you registered with ``RecordingSetupEvent#addCategory``. The ``effects`` and ``distance`` parameters are optional.
* ``playFromEntity(IRecordedAudio audio, Entity entity, AudioEffect effects, String category, float distance)`` - Play the audio from an entity. Note that if the entity dies, the audio will stop. The ``effects`` and ``distance`` parameters are optional.
## Players: IRecordedPlayer object
This object represents the current state of the player's recording. It has very few methods, but they may be useful:
* ``boolean isSilent()`` - true if the player hasn't spoken approximately in the last second.
* ``long getLastSpoke()`` - returns the value of ``System.currentTimeMillis()`` at the last time the player spoke. This can be used to know how long since a player last spoke.
* ``boolean isSpeaking()`` - true if the player has spoken approximately in the last second.
