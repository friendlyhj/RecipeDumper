package cn.mcmod.recipedumper.api;

import com.google.common.collect.Iterators;
import com.google.gson.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author youyihj
 */
public final class DumpRecipeCommand {
    private static final Map<Class<? extends IRecipe<?>>, IRecipeDumper<IRecipe<?>>> DUMPERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void addRecipeDumper(Class<? extends IRecipe<?>> recipeClass, IRecipeDumper<?> recipeDumper) {
        DUMPERS.put(recipeClass, ((IRecipeDumper<IRecipe<?>>) recipeDumper));
    }

    public static int executeCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ModInfo modInfo = context.getArgument("mod", ModInfo.class);
        String modId = modInfo.getModId();
        RecipeManager recipeManager = context.getSource().asPlayer().world.getRecipeManager();
        JsonArray jsonArray = DumpRecipeCommand.dumpAllRecipes(recipeManager, modId);
        outputJson(new File(String.format("export/dump_recipes_%s.json", modId)), jsonArray);
        context.getSource().sendFeedback(new StringTextComponent("Dump recipes successfully! See export Directory."), false);
        return Iterators.size(jsonArray.iterator());
    }

    public static JsonArray dumpAllRecipes(RecipeManager recipeManager, String modFilter) {
        JsonArray array = new JsonArray();
        recipeManager.getRecipes().stream()
                .filter(recipe -> recipe.getId().getNamespace().equals(modFilter))
                .filter(recipe -> DUMPERS.containsKey(recipe.getClass()))
                .map(recipe -> {
                    JsonObject jsonObject = new JsonObject();
                    IRecipeDumper<IRecipe<?>> recipeDumper = DUMPERS.get(recipe.getClass());
                    jsonObject.addProperty("type", recipeDumper.getRecipeTypeName(recipe));
                    jsonObject.addProperty("name", recipe.getId().toString());
                    recipeDumper.writeJsonObject(recipe, jsonObject);
                    return jsonObject;
                })
                .forEach(array::add);
        return array;
    }

    private static void outputJson(File file, JsonElement element) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileUtils.write(file, gson.toJson(element), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
