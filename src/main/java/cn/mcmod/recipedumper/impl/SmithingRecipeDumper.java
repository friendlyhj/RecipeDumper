package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.SmithingRecipe;

/**
 * @author youyihj
 */
@IRecipeDumper.For(SmithingRecipe.class)
public class SmithingRecipeDumper implements IRecipeDumper<SmithingRecipe> {
    @Override
    public void writeJsonObject(SmithingRecipe recipe, JsonObject jsonObject) {
        writeRecipeOutput(recipe, jsonObject);
        jsonObject.add("base", recipe.base.serialize());
        jsonObject.add("addition", recipe.addition.serialize());
    }
}
