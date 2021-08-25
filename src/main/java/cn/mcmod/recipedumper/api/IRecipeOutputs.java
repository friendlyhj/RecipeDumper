package cn.mcmod.recipedumper.api;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

/**
 * @author youyihj
 */
public interface IRecipeOutputs {
    void addOutput(int slot, ItemStack stack);

    JsonObject serialize() throws RecipeDumpException;
}
