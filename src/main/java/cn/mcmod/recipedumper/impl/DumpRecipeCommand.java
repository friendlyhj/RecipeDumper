package cn.mcmod.recipedumper.impl;

import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.api.IRecipeInputs;
import cn.mcmod.recipedumper.api.IRecipeOutputs;
import cn.mcmod.recipedumper.api.RecipeDumpException;
import com.google.common.base.Functions;
import com.google.common.collect.Iterators;
import com.google.gson.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author youyihj
 */
public final class DumpRecipeCommand {
    private static final Map<Class<? extends IRecipe<?>>, IRecipeDumper<IRecipe<?>>> DUMPERS = new HashMap<>();
    private static final Set<ResourceLocation> ERROR_RECIPES = new HashSet<>();

    @SuppressWarnings("unchecked")
    public static void addRecipeDumper(Class<? extends IRecipe<?>> recipeClass, IRecipeDumper<?> recipeDumper) {
        DUMPERS.put(recipeClass, ((IRecipeDumper<IRecipe<?>>) recipeDumper));
    }

    public static int executeCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ModInfo modInfo = context.getArgument("mod", ModInfo.class);
        String modId = modInfo.getModId();
        CommandSource source = context.getSource();
        RecipeManager recipeManager = source.asPlayer().world.getRecipeManager();
        JsonArray jsonArray = DumpRecipeCommand.dumpAllRecipes(recipeManager, modId);
        outputJson(new File(String.format("export/dump_recipes_%s.json", modId)), jsonArray);
        source.sendFeedback(new StringTextComponent("Dump recipes successfully! See export Directory."), false);
        if (!ERROR_RECIPES.isEmpty()) {
            source.sendFeedback(new StringTextComponent("Some recipes are not dumped due to exception"), true);
            ERROR_RECIPES.stream()
                    .filter(Objects::nonNull)
                    .map(Functions.toStringFunction())
                    .map(StringTextComponent::new)
                    .forEach(text -> source.sendFeedback(text, true));
        }
        ERROR_RECIPES.clear();
        return Iterators.size(jsonArray.iterator());
    }

    public static JsonArray dumpAllRecipes(RecipeManager recipeManager, String modFilter) {
        JsonArray array = new JsonArray();
        for (IRecipe<?> recipe : recipeManager.getRecipes()) {
            if (recipe.getId().getNamespace().equals(modFilter) && DUMPERS.containsKey(recipe.getClass())) {
                try {
                    array.add(dumpRecipe(recipe));
                } catch (RecipeDumpException e) {
                    ERROR_RECIPES.add(recipe.getId());
                }
            }
        }
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

    private static JsonObject dumpRecipe(IRecipe<?> recipe) throws RecipeDumpException {
        JsonObject jsonObject = new JsonObject();
        IRecipeDumper<IRecipe<?>> recipeDumper = DUMPERS.get(recipe.getClass());
        jsonObject.addProperty("type", recipeDumper.getRecipeTypeName(recipe));
        jsonObject.addProperty("name", recipe.getId().toString());
        IRecipeInputs inputs = new RecipeInputs();
        IRecipeOutputs outputs = new RecipeOutputs();
        recipeDumper.setInputs(recipe, inputs);
        recipeDumper.setOutputs(recipe, outputs);
        jsonObject.add("input", inputs.serialize());
        jsonObject.add("output", outputs.serialize());
        recipeDumper.writeExtraInformation(recipe, jsonObject);
        return jsonObject;
    }
}
