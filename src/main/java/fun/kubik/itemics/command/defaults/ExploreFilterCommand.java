/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import com.google.gson.JsonSyntaxException;
import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.datatypes.RelativeFile;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.command.exception.CommandInvalidTypeException;
import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ExploreFilterCommand
extends Command {
    public ExploreFilterCommand(IItemics itemics) {
        super(itemics, "explorefilter");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(2);
        File file = (File)args.getDatatypePost(RelativeFile.INSTANCE, ExploreFilterCommand.mc.gameDir.getAbsoluteFile().getParentFile());
        boolean invert = false;
        if (args.hasAny()) {
            if (args.getString().equalsIgnoreCase("invert")) {
                invert = true;
            } else {
                throw new CommandInvalidTypeException(args.consumed(), "either \"invert\" or nothing");
            }
        }
        try {
            this.itemics.getExploreProcess().applyJsonFilter(file.toPath().toAbsolutePath(), invert);
        } catch (NoSuchFileException e) {
            throw new CommandInvalidStateException("File not found");
        } catch (JsonSyntaxException e) {
            throw new CommandInvalidStateException("Invalid JSON syntax");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        this.logDirect(String.format("Explore filter applied. Inverted: %s", Boolean.toString(invert)));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return RelativeFile.tabComplete(args, RelativeFile.gameDir());
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Explore chunks from a json";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Apply an explore filter before using explore, which tells the explore process which chunks have been explored/not explored.", "", "The JSON file will follow this format: [{\"x\":0,\"z\":0},...]", "", "If 'invert' is specified, the chunks listed will be considered NOT explored, rather than explored.", "", "Usage:", "> explorefilter <path> [invert] - Load the JSON file referenced by the specified path. If invert is specified, it must be the literal word 'invert'.");
    }
}

