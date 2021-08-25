package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeInputs;
import cn.mcmod.recipedumper.api.RecipeDumpException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.crafting.Ingredient;

/**
 * @author youyihj
 */
public class RecipeInputs implements IRecipeInputs {
    Int2ObjectMap<Ingredient> inputs = new Int2ObjectArrayMap<>();
    Int2IntMap counts = new Int2IntArrayMap();

    @Override
    public void addInput(int slot, Ingredient ingredient, int count) {
        inputs.put(slot, ingredient);
        counts.put(slot, count);
    }

    @Override
    public JsonObject serialize() throws RecipeDumpException {
        JsonObject json = new JsonObject();
        if (inputs.size() != counts.size()) {
            throw new RecipeDumpException();
        }
        try {
            for (Int2ObjectMap.Entry<Ingredient> entry : inputs.int2ObjectEntrySet()) {
                JsonElement ingredientJson = entry.getValue().serialize();
                ingredientJson.getAsJsonObject().addProperty("count", counts.get(entry.getIntKey()));
                json.add(String.valueOf(entry.getIntKey()), ingredientJson);
            }
        } catch (Throwable throwable) {
            throw new RecipeDumpException();
        }
        return json;
    }
}
