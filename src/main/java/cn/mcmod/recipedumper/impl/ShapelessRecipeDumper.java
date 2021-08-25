package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.api.IRecipeInputs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;

/**
 * @author youyihj
 */
@IRecipeDumper.For(ShapelessRecipe.class)
public class ShapelessRecipeDumper implements IRecipeDumper<ShapelessRecipe> {

    @Override
    public void setInputs(ShapelessRecipe recipe, IRecipeInputs inputs) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            inputs.addInput(i + 1, ingredients.get(i));
        }
    }

    @Override
    public String getRecipeTypeName(ShapelessRecipe recipe) {
        return "crafting_shapeless";
    }
}
