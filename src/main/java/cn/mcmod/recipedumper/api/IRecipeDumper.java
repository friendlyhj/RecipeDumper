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
