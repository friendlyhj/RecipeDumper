package cn.mcmod.recipedumper.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.*;

/**
 * @author youyihj
 */
public class DelegateFurnaceRecipeMap<V> extends AbstractMap<ItemStack, V> {
    private final Map<ItemStack, V> internal;
    private final Multimap<String, Map.Entry<ItemStack, V>> modEntries = HashMultimap.create();

    public DelegateFurnaceRecipeMap(Map<ItemStack, V> internal) {
        this.internal = internal;
        modEntries.putAll("minecraft", internal.entrySet());
    }

    @Override
    public Set<Entry<ItemStack, V>> entrySet() {
        return internal.entrySet();
    }

    @Override
    public V put(ItemStack key, V value) {
        ModContainer activeMod = Loader.instance().activeModContainer();
        if (activeMod != null) {
            modEntries.put(activeMod.getModId(), new AbstractMap.SimpleEntry<>(key, value));
        }
        return super.put(key, value);
    }

    public Collection<Map.Entry<ItemStack, V>> getModEntry(String modid) {
        return modEntries.get(modid);
    }
}
