package dev.omialien.revervoxmod.registries;

import dev.omialien.revervoxmod.RevervoxMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RevervoxTags {
    public static class Items {
        public static final TagKey<Item> ATTRACTS_REVERVOX = createTag("attracts_revervox");
        public static final TagKey<Item> AUDIO_ON_KILL = createTag("audio_on_kill");
        private static TagKey<Item> createTag(String name){
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, name));
        }
    }
    public static class Blocks {
        private static TagKey<Block> createTag(String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, name));
        }
    }
    public static class Entities {
        public static final TagKey<EntityType<?>> REVERVOX_BONUS_DAMAGE = createTag("revervox_bonus_damage");
        public static final TagKey<EntityType<?>> INSECTS = createTag("insects");
        private static TagKey<EntityType<?>> createTag(String name){
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(RevervoxMod.MOD_ID, name));
        }
    }
}
