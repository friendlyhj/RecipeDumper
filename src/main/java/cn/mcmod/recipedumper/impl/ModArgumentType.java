package cn.mcmod.recipedumper.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * @author youyihj
 */
public enum ModArgumentType implements ArgumentType<ModInfo> {
    INSTANCE;

    private final Collection<String> EXAMPLES = Lists.newArrayList("minecraft", "thermal");
    private final SimpleCommandExceptionType commandExceptionType = new SimpleCommandExceptionType(new LiteralMessage("No such a mod"));

    @Override
    public ModInfo parse(StringReader reader) throws CommandSyntaxException {
        String modId = reader.readUnquotedString();
        return ModList.get().getMods()
                .stream()
                .filter(modInfo -> modInfo.getModId().equals(modId))
                .findFirst()
                .orElseThrow(() -> commandExceptionType.createWithContext(reader));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(ModList.get().getMods().stream().map(ModInfo::getModId), builder);
    }
}
