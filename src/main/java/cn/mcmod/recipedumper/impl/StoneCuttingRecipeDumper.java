package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.StonecuttingRecipe;

/**
 * @author youyihj
 */
@IRecipeDumper.For(StonecuttingRecipe.class)
public class StoneCuttingRecipeDumper implements IRecipeDumper<StonecuttingRecipe> {
    @Override
    public void writeJsonObject(StonecuttingRecipe recipe, JsonObject jsonObject) {
        this.writeRecipeOutput(recipe, jsonObject);
        jsonObject.add("input", recipe.getIngredients().get(0).serialize());
    }
}
