package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;

/**
 * @author youyihj
 */
@IRecipeDumper.For(ShapedRecipe.class)
public class ShapedRecipeDumper implements IRecipeDumper<ShapedRecipe> {

    @Override
    public void writeJsonObject(ShapedRecipe recipe, JsonObject jsonObject) {
        this.writeRecipeOutput(recipe, jsonObject);
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        JsonArray input = new JsonArray();
        int width = recipe.getWidth();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            JsonObject ingredientObject = new JsonObject();
            int x = i % width;
            int y = i / width;
            ingredientObject.addProperty("key", y * 3 + x + 1);
            ingredientObject.add("ingredient", ingredient.serialize());
            input.add(ingredientObject);
        }
        jsonObject.add("input", input);
    }

    @Override
    public String getRecipeTypeName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }
}
