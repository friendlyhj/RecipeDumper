package cn.mcmod.recipedumper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author youyihj
 */
public class DumpCommand extends CommandBase {
    @Override
    public String getName() {
        return "dumprecipe";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dumprecipe <mod>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }
        new RecipeDumperLogic(ForgeRegistries.RECIPES.getValuesCollection(), args[0]).dump(sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, Loader.instance().getIndexedModList().keySet().toArray(new String[]{}));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
