/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.IItemicsChatControl;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.datatypes.BlockById;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.helpers.TabCompleteHelper;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import fun.kubik.itemics.cache.CachedChunk;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class FindCommand
extends Command {
    public FindCommand(IItemics itemics) {
        super(itemics, "find");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMin(1);
        ArrayList<Block> toFind = new ArrayList<Block>();
        while (args.hasAny()) {
            toFind.add((Block)args.getDatatypeFor(BlockById.INSTANCE));
        }
        BetterBlockPos origin = this.ctx.playerFeet();
        ITextComponent[] components = (ITextComponent[])toFind.stream().flatMap(block -> this.ctx.worldData().getCachedWorld().getLocationsOf(Registry.BLOCK.getKey((Block)block).getPath(), Integer.MAX_VALUE, origin.x, origin.y, 4).stream()).map(BetterBlockPos::new).map(this::positionToComponent).toArray(ITextComponent[]::new);
        if (components.length > 0) {
            Arrays.asList(components).forEach(xva$0 -> this.logDirect((ITextComponent)xva$0));
        } else {
            this.logDirect("No positions known, are you sure the blocks are cached?");
        }
    }

    private ITextComponent positionToComponent(BetterBlockPos pos) {
        String positionText = String.format("%s %s %s", pos.x, pos.y, pos.z);
        String command = String.format("%sgoal %s", IItemicsChatControl.FORCE_COMMAND_PREFIX, positionText);
        StringTextComponent baseComponent = new StringTextComponent(pos.toString());
        StringTextComponent hoverComponent = new StringTextComponent("Click to set goal to this position");
        baseComponent.setStyle(baseComponent.getStyle().setFormatting(TextFormatting.GRAY).setInsertion(positionText).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent)));
        return baseComponent;
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return new TabCompleteHelper().append(CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.stream().map(Registry.BLOCK::getKey).map(Object::toString)).filterPrefixNamespaced(args.getString()).sortAlphabetically().stream();
    }

    @Override
    public String getShortDesc() {
        return "Find positions of a certain block";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The find command searches through Itemics's cache and attempts to find the location of the block.", "Tab completion will suggest only cached blocks and uncached blocks can not be found.", "", "Usage:", "> find <block> [...] - Try finding the listed blocks");
    }
}

