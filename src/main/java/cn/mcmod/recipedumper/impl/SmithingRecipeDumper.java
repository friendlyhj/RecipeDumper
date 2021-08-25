package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.api.IRecipeInputs;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.SmithingRecipe;

/**
 * @author youyihj
 */
@IRecipeDumper.For(SmithingRecipe.class)
public class SmithingRecipeDumper implements IRecipeDumper<SmithingRecipe> {

    @Override
    public void setInputs(SmithingRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.base);
        inputs.addInput(2, recipe.addition);
    }
}
