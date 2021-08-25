package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.api.IRecipeInputs;
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
    public void setInputs(ShapedRecipe recipe, IRecipeInputs inputs) {
        int width = recipe.getWidth();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int x = i % width;
            int y = i / width;
            inputs.addInput(y * 3 + x + 1, ingredient);
        }
    }

    @Override
    public String getRecipeTypeName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }
}
