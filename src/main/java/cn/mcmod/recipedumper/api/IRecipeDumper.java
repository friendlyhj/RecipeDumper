package cn.mcmod.recipedumper.api;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;

import java.lang.annotation.*;

/**
 * @author youyihj
 */
public interface IRecipeDumper<T extends IRecipe<?>> {
    void writeJsonObject(T recipe, JsonObject jsonObject);

    default String getRecipeTypeName(T recipe) {
        return recipe.getType().toString();
    }

    default void writeRecipeOutput(T recipe, JsonObject jsonObject) {
        jsonObject.addProperty("output", recipe.getRecipeOutput().getItem().getRegistryName().toString());
        jsonObject.addProperty("output_count", recipe.getRecipeOutput().getCount());
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
