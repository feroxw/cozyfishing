package net.mazee.cozyfishing.data;

import net.mazee.cozyfishing.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public class Fish {

    public static List<Map<String, Object>> listOfFish = new ArrayList<>();

    static int ID = 0;
    public static void main() {

        addFish(ModItems.RED_SNAPPER, "red_snapper", 150, "dart", 0, 24000, false,
                new ArrayList<>(Arrays.asList(Tags.Biomes.IS_COLD)));

        addFish(ModItems.HADDOCK, "haddock", 90, "smooth", 0, 13000, false,
                new ArrayList<>(Arrays.asList(Tags.Biomes.IS_HOT)));

        addFish(ModItems.CATFISH, "catfish", 50, "mixed", 13000, 24000, true,
                new ArrayList<>(Arrays.asList(Tags.Biomes.IS_HOT,Tags.Biomes.IS_WATER)));

        addFish(ModItems.GREEN_SUNFISH, "green_sunfish", 80, "floater", 0, 24000, false,
                new ArrayList<>(Arrays.asList(Tags.Biomes.IS_HOT,Tags.Biomes.IS_WATER)));

        addFish(ModItems.FANTAIL, "fantail", 90, "dart", 0, 24000, false,
                new ArrayList<>(Arrays.asList(Tags.Biomes.IS_HOT,Tags.Biomes.IS_WATER)));

        addFish(ModItems.GOLDFISH, "goldfish", 100, "dart", 0, 24000, false,
                new ArrayList<>(Arrays.asList(Tags.Biomes.IS_HOT,Tags.Biomes.IS_WATER)));

    }

    private static void addFish(RegistryObject fish, String name, int difficulty, String motionType, long minTime, long maxTime, boolean raining, ArrayList<TagKey<Biome>> newFishMap_biome_tags){
        Map<String, Object> newFishMap = new HashMap<>();
        newFishMap.put("id", ID++);
        newFishMap.put("name", name);
        Map<String, ItemLike> newFishMap_registry = new HashMap<>();
        newFishMap_registry.put("fishRegistry", (ItemLike) fish.get());
        newFishMap.put("registry", newFishMap_registry);
        newFishMap.put("difficulty", difficulty);
        newFishMap.put("motionType", motionType);
        newFishMap.put("minTime", minTime);
        newFishMap.put("maxTime", maxTime);
        newFishMap.put("raining", raining);
        //ArrayList<TagKey<Biome>> red_snapper_biome_tags = new ArrayList<>();
//        red_snapper_biome_tags.add(Tags.Biomes.IS_COLD);
//        red_snapper.put("biome_tags",red_snapper_biome_tags);
        newFishMap.put("biome_tags",newFishMap_biome_tags);
        listOfFish.add(newFishMap);
    }
}
