package cn.mcmod.recipedumper.api;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.lang.annotation.*;

/**
 * @author youyihj
 */
public interface IRecipeDumper<T extends IRecipe<?>> {
    void setInputs(T recipe, IRecipeInputs inputs);

    default void setOutputs(T recipe, IRecipeOutputs outputs) {
        outputs.addOutput(1, recipe.getRecipeOutput());
    }

    default void writeExtraInformation(T recipe, JsonObject jsonObject) {

    }

    default String getRecipeTypeName(T recipe) {
        return recipe.getType().toString();
    }

    default void writeRecipeOutput(T recipe, JsonObject jsonObject) {
        JsonObject output = new JsonObject();
        ItemStack stack = recipe.getRecipeOutput();
        output.addProperty("item", stack.getItem().getRegistryName().toString());
        output.addProperty("count", stack.getCount());
        if (stack.hasTag()) {
            output.addProperty("nbt", stack.getTag().toString());
        }
        JsonObject outputs = new JsonObject();
        outputs.add("1", output);
        jsonObject.add("output", outputs);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Container.class)
    @Target(ElementType.TYPE)
    @interface For {
        Class<? extends IRecipe<?>> value();

        @SuppressWarnings("unused")
        String modDeps() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Container {
        For[] value();
    }
}
