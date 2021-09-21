package cn.mcmod.recipedumper.command;

import cn.mcmod.recipedumper.RecipeDumper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import mezz.jei.Internal;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author youyihj
 */
public class RecipeDumperLogic {
    private static final Map<Class<?>, String> supportClasses = Maps.asMap(
            Sets.newHashSet(ShapedRecipes.class, ShapedOreRecipe.class, ShapelessRecipes.class, ShapelessOreRecipe.class),
            clazz -> IShapedRecipe.class.isAssignableFrom(clazz) ? "crafting_shaped" : "crafting_shapeless"
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Collection<IRecipe> recipes;
    private final String modFilter;
    private final JsonArray recipesJson = new JsonArray();
    private final List<IRecipe> errorRecipes = new ArrayList<>();

    public RecipeDumperLogic(Collection<IRecipe> recipes, String modFilter) {
        this.recipes = recipes;
        this.modFilter = modFilter;
    }

    public void dump(ICommandSender sender) {
        JsonObject root = new JsonObject();
        for (IRecipe recipe : recipes) {
            if (isInvalidRecipe(recipe))
                continue;
            try {
                recipesJson.add(convertRecipe(recipe));
            } catch (RecipeDumpException e) {
                errorRecipes.add(recipe);
            }
        }
        root.add("recipes", recipesJson);
        dumpFurnaceRecipes(recipesJson);
        JsonArray errorRecipesJson = new JsonArray();
        for (IRecipe errorRecipe : errorRecipes) {
            errorRecipesJson.add(errorRecipe.getRegistryName().toString());
        }
        root.add("error", errorRecipesJson);
        outputJson(new File(String.format("export/dump_recipes_%s.json", modFilter)), root);
        sender.sendMessage(new TextComponentString("Dump recipes successfully! See export Directory."));
        sender.sendMessage(new TextComponentString(String.format("%s recipes dumped, %s recipes skipped", recipesJson.size(), errorRecipes.size())));
    }

    private JsonObject convertRecipe(IRecipe recipe) throws RecipeDumpException {
        JsonObject recipeJson = new JsonObject();
        recipeJson.addProperty("type", supportClasses.get(recipe.getClass()));
        recipeJson.addProperty("name", recipe.getRegistryName().toString());
        JsonObject input = new JsonObject();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (recipe instanceof IShapedRecipe) {
            int width = ((IShapedRecipe) recipe).getRecipeWidth();
            for (int i = 0; i < ingredients.size(); i++) {
                int x = i % width;
                int y = i / width;
                JsonObject ingredientJson = convertIngredient(ingredients.get(i));
                if (ingredientJson.size() != 0) {
                    input.add(Integer.toString(y * 3 + x + 1), ingredientJson);
                }
            }
        } else {
            for (int i = 0; i < ingredients.size(); i++) {
                JsonObject ingredientJson = convertIngredient(ingredients.get(i));
                if (ingredientJson.size() != 0) {
                    input.add(Integer.toString(i + 1), ingredientJson);
                }
            }
        }
        JsonObject outputs = new JsonObject();
        JsonObject output = new JsonObject();
        convertItemStack(output, recipe.getRecipeOutput());
        outputs.add("1", output);
        recipeJson.addProperty("name", recipe.getRegistryName().toString());
        recipeJson.add("input", input);
        recipeJson.add("output", outputs);
        return recipeJson;
    }

    private JsonObject convertIngredient(Ingredient ingredient) throws RecipeDumpException {
        JsonObject json = new JsonObject();
        if ((ingredient.getClass() == Ingredient.class && ingredient.getMatchingStacks().length == 1) || ingredient.getClass() == IngredientNBT.class) {
            convertItemStack(json, ingredient.getMatchingStacks()[0]);
        } else if (ingredient.getClass() == OreIngredient.class) {
            json.addProperty("oredict", getOreDict(ingredient.getMatchingStacks(), ingredient));
        } else if (ingredient.getMatchingStacks().length != 0) {
            throw new RecipeDumpException("Unsupported Ingredient: " + ingredient);
        }
        return json;
    }

    private void convertItemStack(JsonObject json, ItemStack item) {
        json.addProperty("item", item.getItem().getRegistryName().toString());
        json.addProperty("meta", item.getItemDamage());
        json.addProperty("count", item.getCount());
        if (item.hasTagCompound()) {
            json.addProperty("nbt", item.getTagCompound().toString());
        }
    }

    private JsonObject convertItemStack(ItemStack item) {
        JsonObject jsonObject = new JsonObject();
        convertItemStack(jsonObject, item);
        return jsonObject;
    }

    private boolean isInvalidRecipe(IRecipe recipe) {
        return !recipe.getRegistryName().getResourceDomain().equals(modFilter) || !supportClasses.containsKey(recipe.getClass());
    }

    private void outputJson(File file, JsonElement element) {
        try {
            FileUtils.write(file, GSON.toJson(element), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dumpFurnaceRecipes(JsonArray recipesArray) {
        Collection<Map.Entry<ItemStack, ItemStack>> modRecipes = RecipeDumper.INSTANCE.smeltingList.getModEntry(modFilter);
        Collection<Map.Entry<ItemStack, Float>> modExpList = RecipeDumper.INSTANCE.experienceList.getModEntry(modFilter);
        for (Map.Entry<ItemStack, ItemStack> recipe : modRecipes) {
            JsonObject root = new JsonObject();
            float exp = -1.0f;
            for (Map.Entry<ItemStack, Float> itemStackFloatEntry : modExpList) {
                if (compareItemStacks(recipe.getValue(), itemStackFloatEntry.getKey())) {
                    exp = itemStackFloatEntry.getValue();
                    break;
                }
            }
            root.addProperty("type", "smelting");
            JsonObject input = new JsonObject();
            input.add("1", convertItemStack(recipe.getKey()));
            JsonObject output = new JsonObject();
            output.add("1", convertItemStack(recipe.getValue()));
            root.add("input", input);
            root.add("output", output);
            if (exp != -1.0f) {
                root.addProperty("experience", exp);
            }
            recipesArray.add(root);
        }
    }

    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }

    private String getOreDict(ItemStack[] items, Ingredient ingredient) throws RecipeDumpException {
        if (items.length == 1) {
            int[] oreIDs = OreDictionary.getOreIDs(items[0]);
            if (oreIDs.length == 1) {
                return OreDictionary.getOreName(oreIDs[0]);
            } else {
                for (int oreID : oreIDs) {
                    String oreName = OreDictionary.getOreName(oreID);
                    if (OreDictionary.getOres(oreName).stream().allMatch(ingredient)) {
                        return oreName;
                    }
                }
                throw new RecipeDumpException("Invalid ore ingredient: " + ingredient);
            }
        } else {
            String result = Internal.getStackHelper().getOreDictEquivalent(Arrays.asList(items));
            if (result == null) {
                throw new RecipeDumpException(Arrays.toString(items) + " don't have the same ore dict entry");
            }
            return result;
        }
    }

    public static class RecipeDumpException extends Exception {
        public RecipeDumpException(String message) {
            super(message);
        }

        public RecipeDumpException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}