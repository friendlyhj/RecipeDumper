package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeOutputs;
import cn.mcmod.recipedumper.api.RecipeDumpException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 * @author youyihj
 */
public class RecipeOutputs implements IRecipeOutputs {
    Int2ObjectMap<ItemStack> outputs = new Int2ObjectArrayMap<>();

    @Override
    public void addOutput(int slot, ItemStack stack) {
        outputs.put(slot, stack);
    }

    @Override
    public JsonObject serialize() throws RecipeDumpException {
        JsonObject json = new JsonObject();
        try {
            for (Int2ObjectMap.Entry<ItemStack> entry : outputs.int2ObjectEntrySet()) {
                JsonObject stackJson = new JsonObject();
                ItemStack stack = entry.getValue();
                stackJson.addProperty("item", stack.getItem().getRegistryName().toString());
                stackJson.addProperty("count", stack.getCount());
                if (stack.hasTag()) {
                    stackJson.addProperty("nbt", stack.getTag().toString());
                }
                json.add(String.valueOf(entry.getIntKey()), stackJson);
            }
        } catch (Throwable throwable) {
            throw new RecipeDumpException();
        }
        return json;
    }
}
