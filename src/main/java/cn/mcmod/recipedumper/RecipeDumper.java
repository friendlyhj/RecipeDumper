package cn.mcmod.recipedumper;

import cn.mcmod.recipedumper.command.DumpCommand;
import cn.mcmod.recipedumper.util.DelegateFurnaceRecipeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "recipedumper", name = "Recipe Dumper", version = "0.1", dependencies = "required-after:jei")
public class RecipeDumper {
    public DelegateFurnaceRecipeMap<ItemStack> smeltingList;
    public DelegateFurnaceRecipeMap<Float> experienceList;

    @Mod.Instance("recipedumper")
    public static RecipeDumper INSTANCE;

    @Mod.EventHandler
    public void onConstruct(FMLConstructionEvent event) {
        FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();
        this.smeltingList = new DelegateFurnaceRecipeMap<>(furnaceRecipes.smeltingList);
        this.experienceList = new DelegateFurnaceRecipeMap<>(furnaceRecipes.experienceList);
        furnaceRecipes.smeltingList = this.smeltingList;
        furnaceRecipes.experienceList = this.experienceList;
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DumpCommand());
    }
}
