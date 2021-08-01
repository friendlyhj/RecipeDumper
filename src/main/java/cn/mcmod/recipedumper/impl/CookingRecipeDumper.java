package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.*;

/**
 * @author youyihj
 */
@IRecipeDumper.For(BlastingRecipe.class)
@IRecipeDumper.For(CampfireCookingRecipe.class)
@IRecipeDumper.For(FurnaceRecipe.class)
@IRecipeDumper.For(SmokingRecipe.class)
public class CookingRecipeDumper implements IRecipeDumper<AbstractCookingRecipe> {

    @Override
    public void writeJsonObject(AbstractCookingRecipe recipe, JsonObject jsonObject) {
        jsonObject.addProperty("output", recipe.getRecipeOutput().getItem().getRegistryName().toString());
        jsonObject.addProperty("output_count", recipe.getRecipeOutput().getCount());
        jsonObject.addProperty("cooking_time", recipe.getCookTime());
        jsonObject.addProperty("experience", recipe.getExperience());
        jsonObject.add("input", recipe.getIngredients().get(0).serialize());
    }
}
