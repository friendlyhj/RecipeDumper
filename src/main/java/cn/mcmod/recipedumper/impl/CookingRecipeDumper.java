package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.api.IRecipeInputs;
import cn.mcmod.recipedumper.api.IRecipeOutputs;
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
    public void setInputs(AbstractCookingRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.getIngredients().get(0));
    }

    @Override
    public void setOutputs(AbstractCookingRecipe recipe, IRecipeOutputs outputs) {
        outputs.addOutput(1, recipe.getRecipeOutput());
    }

    @Override
    public void writeExtraInformation(AbstractCookingRecipe recipe, JsonObject jsonObject) {
        jsonObject.addProperty("experience", recipe.getExperience());
        jsonObject.addProperty("cookTime", recipe.getCookTime());
    }
}
