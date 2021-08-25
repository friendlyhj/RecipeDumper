package cn.mcmod.recipedumper.api;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;

/**
 * @author youyihj
 */
public interface IRecipeInputs {
    void addInput(int slot, Ingredient ingredient, int count);

    default void addInput(int slot, Ingredient ingredient) {
        addInput(slot, ingredient, 1);
    }

    JsonObject serialize() throws RecipeDumpException;
}
