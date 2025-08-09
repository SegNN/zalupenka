/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.process;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.process.PathingCommand;
import fun.kubik.itemics.api.process.PathingCommandType;
import fun.kubik.itemics.api.utils.input.Input;
import fun.kubik.itemics.pathing.movement.Movement;
import fun.kubik.itemics.pathing.movement.MovementHelper;
import fun.kubik.itemics.pathing.movement.MovementState;
import fun.kubik.itemics.pathing.path.PathExecutor;
import fun.kubik.itemics.utils.ItemicsProcessHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.chunk.EmptyChunk;

public final class BackfillProcess
        extends ItemicsProcessHelper {
    public HashMap<BlockPos, BlockState> blocksToReplace = new HashMap();

    public BackfillProcess(Itemics itemics) {
        super(itemics);
    }

    @Override
    public boolean isActive() {
        if (this.ctx.player() == null || this.ctx.world() == null) {
            return false;
        }
        if (!((Boolean)Itemics.settings().backfill.value).booleanValue()) {
            return false;
        }
        if (((Boolean)Itemics.settings().allowParkour.value).booleanValue()) {
            this.logDirect("Backfill cannot be used with allowParkour true");
            Itemics.settings().backfill.value = false;
            return false;
        }
        for (BlockPos pos : new ArrayList<BlockPos>(this.blocksToReplace.keySet())) {
            if (!(this.ctx.world().getChunk(pos) instanceof EmptyChunk) && this.ctx.world().getBlockState(pos).getBlock() == Blocks.AIR) continue;
            this.blocksToReplace.remove(pos);
        }
        this.amIBreakingABlockHMMMMMMM();
        this.itemics.getInputOverrideHandler().clearAllKeys();
        return !this.toFillIn().isEmpty();
    }

    @Override
    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
        if (!isSafeToCancel) {
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        this.itemics.getInputOverrideHandler().clearAllKeys();
        block5: for (BlockPos toPlace : this.toFillIn()) {
            MovementState fake = new MovementState();
            switch (MovementHelper.attemptToPlaceABlock(fake, this.itemics, toPlace, false, false)) {
                case NO_OPTION: {
                    continue block5;
                }
                case READY_TO_PLACE: {
                    this.itemics.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
                    return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
                }
                case ATTEMPTING: {
                    this.itemics.getLookBehavior().updateTarget(fake.getTarget().getRotation().get(), true);
                    return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
                }
            }
            throw new IllegalStateException();
        }
        return new PathingCommand(null, PathingCommandType.DEFER);
    }

    private void amIBreakingABlockHMMMMMMM() {
        if (!this.ctx.getSelectedBlock().isPresent() || !this.itemics.getPathingBehavior().isPathing()) {
            return;
        }
        this.blocksToReplace.put(this.ctx.getSelectedBlock().get(), this.ctx.world().getBlockState(this.ctx.getSelectedBlock().get()));
    }

    public List<BlockPos> toFillIn() {
        return this.blocksToReplace.keySet().stream().filter(pos -> this.ctx.world().getBlockState((BlockPos)pos).getBlock() == Blocks.AIR).filter(pos -> this.itemics.getBuilderProcess().placementPlausible((BlockPos)pos, Blocks.DIRT.getDefaultState())).filter(pos -> !this.partOfCurrentMovement((BlockPos)pos)).sorted(Comparator.comparingDouble(pos -> this.ctx.playerFeet().distanceSq((Vector3i) pos)).reversed()).collect(Collectors.toList());
    }

    private boolean partOfCurrentMovement(BlockPos pos) {
        PathExecutor exec = this.itemics.getPathingBehavior().getCurrent();
        if (exec == null || exec.finished() || exec.failed()) {
            return false;
        }
        Movement movement = (Movement)exec.getPath().movements().get(exec.getPosition());
        return Arrays.asList(movement.toBreakAll()).contains(pos);
    }

    @Override
    public void onLostControl() {
        if (this.blocksToReplace != null && !this.blocksToReplace.isEmpty()) {
            this.blocksToReplace.clear();
        }
    }

    @Override
    public String displayName0() {
        return "Backfill";
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public double priority() {
        return 5.0;
    }
}
