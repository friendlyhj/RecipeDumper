package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;

/**
 * @author youyihj
 */
@IRecipeDumper.For(ShapelessRecipe.class)
public class ShapelessRecipeDumper implements IRecipeDumper<ShapelessRecipe> {

    @Override
    public void writeJsonObject(ShapelessRecipe recipe, JsonObject jsonObject) {
        this.writeRecipeOutput(recipe, jsonObject);
        JsonArray input = new JsonArray();
        recipe.getIngredients().stream().map(Ingredient::serialize).forEach(input::add);
        jsonObject.add("input", input);
    }

    @Override
    public String getRecipeTypeName(ShapelessRecipe recipe) {
        return "crafting_shapeless";
    }
}
