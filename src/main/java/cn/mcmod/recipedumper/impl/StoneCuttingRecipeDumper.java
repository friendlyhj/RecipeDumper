package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.api.IRecipeInputs;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.StonecuttingRecipe;

/**
 * @author youyihj
 */
@IRecipeDumper.For(StonecuttingRecipe.class)
public class StoneCuttingRecipeDumper implements IRecipeDumper<StonecuttingRecipe> {
    @Override
    public void setInputs(StonecuttingRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.getIngredients().get(0));
    }
}
