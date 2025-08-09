/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.command.main.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fun.kubik.Load;
import fun.kubik.managers.command.api.ArgumentConstants;
import fun.kubik.managers.module.Module;
import java.util.concurrent.CompletableFuture;

public class ModuleListArgument
implements ArgumentType<Module> {
    @Override
    public Module parse(StringReader stringReader) throws CommandSyntaxException {
        String moduleName = ArgumentConstants.LINE_STRING_ARGUMENT.parse(stringReader);
        return Load.getInstance().getHooks().getModuleManagers().stream().filter(module -> module.getName().equalsIgnoreCase(moduleName)).findFirst().orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Load.getInstance().getHooks().getModuleManagers().forEach(module -> builder.suggest(module.getName()));
        return builder.buildFuture();
    }
}

