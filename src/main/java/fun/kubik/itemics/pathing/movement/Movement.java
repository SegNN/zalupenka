/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.pathing.movement;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.pathing.movement.IMovement;
import fun.kubik.itemics.api.pathing.movement.MovementStatus;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import fun.kubik.itemics.api.utils.IPlayerContext;
import fun.kubik.itemics.api.utils.Rotation;
import fun.kubik.itemics.api.utils.RotationUtils;
import fun.kubik.itemics.api.utils.VecUtils;
import fun.kubik.itemics.api.utils.input.Input;
import fun.kubik.itemics.behavior.PathingBehavior;
import fun.kubik.itemics.utils.BlockStateInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class Movement
implements IMovement,
MovementHelper {
    public static final Direction[] HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN};
    protected final IItemics itemics;
    protected final IPlayerContext ctx;
    private MovementState currentState = new MovementState().setStatus(MovementStatus.PREPPING);
    protected final BetterBlockPos src;
    protected final BetterBlockPos dest;
    protected final BetterBlockPos[] positionsToBreak;
    protected final BetterBlockPos positionToPlace;
    private Double cost;
    public List<BlockPos> toBreakCached = null;
    public List<BlockPos> toPlaceCached = null;
    public List<BlockPos> toWalkIntoCached = null;
    private Set<BetterBlockPos> validPositionsCached = null;
    private Boolean calculatedWhileLoaded;

    protected Movement(IItemics itemics, BetterBlockPos src, BetterBlockPos dest, BetterBlockPos[] toBreak, BetterBlockPos toPlace) {
        this.itemics = itemics;
        this.ctx = itemics.getPlayerContext();
        this.src = src;
        this.dest = dest;
        this.positionsToBreak = toBreak;
        this.positionToPlace = toPlace;
    }

    protected Movement(IItemics itemics, BetterBlockPos src, BetterBlockPos dest, BetterBlockPos[] toBreak) {
        this(itemics, src, dest, toBreak, null);
    }

    @Override
    public double getCost() throws NullPointerException {
        return this.cost;
    }

    public double getCost(CalculationContext context) {
        if (this.cost == null) {
            this.cost = this.calculateCost(context);
        }
        return this.cost;
    }

    public abstract double calculateCost(CalculationContext var1);

    public double recalculateCost(CalculationContext context) {
        this.cost = null;
        return this.getCost(context);
    }

    public void override(double cost) {
        this.cost = cost;
    }

    protected abstract Set<BetterBlockPos> calculateValidPositions();

    public Set<BetterBlockPos> getValidPositions() {
        if (this.validPositionsCached == null) {
            this.validPositionsCached = this.calculateValidPositions();
            Objects.requireNonNull(this.validPositionsCached);
        }
        return this.validPositionsCached;
    }

    protected boolean playerInValidPosition() {
        return this.getValidPositions().contains(this.ctx.playerFeet()) || this.getValidPositions().contains(((PathingBehavior)this.itemics.getPathingBehavior()).pathStart());
    }

    @Override
    public MovementStatus update() {
        this.ctx.player().abilities.isFlying = false;
        this.currentState = this.updateState(this.currentState);
        if (MovementHelper.isLiquid(this.ctx, this.ctx.playerFeet())) {
            this.currentState.setInput(Input.JUMP, true);
        }
        if (this.ctx.player().isEntityInsideOpaqueBlock()) {
            this.ctx.getSelectedBlock().ifPresent(pos -> MovementHelper.switchToBestToolFor(this.ctx, BlockStateInterface.get(this.ctx, pos)));
            this.currentState.setInput(Input.CLICK_LEFT, true);
        }
        this.currentState.getTarget().getRotation().ifPresent(rotation -> this.itemics.getLookBehavior().updateTarget((Rotation)rotation, this.currentState.getTarget().hasToForceRotations()));
        this.itemics.getInputOverrideHandler().clearAllKeys();
        this.currentState.getInputStates().forEach((input, forced) -> this.itemics.getInputOverrideHandler().setInputForceState((Input)((Object)input), (boolean)forced));
        this.currentState.getInputStates().clear();
        if (this.currentState.getStatus().isComplete()) {
            this.itemics.getInputOverrideHandler().clearAllKeys();
        }
        return this.currentState.getStatus();
    }

    protected boolean prepared(MovementState state) {
        if (state.getStatus() == MovementStatus.WAITING) {
            return true;
        }
        boolean somethingInTheWay = false;
        for (BetterBlockPos blockPos : this.positionsToBreak) {
            if (!this.ctx.world().getEntitiesWithinAABB(FallingBlockEntity.class, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.1, 1.0).offset(blockPos)).isEmpty() && ((Boolean)Itemics.settings().pauseMiningForFallingBlocks.value).booleanValue()) {
                return false;
            }
            if (MovementHelper.canWalkThrough(this.ctx, blockPos)) continue;
            somethingInTheWay = true;
            MovementHelper.switchToBestToolFor(this.ctx, BlockStateInterface.get(this.ctx, blockPos));
            Optional<Rotation> reachable = RotationUtils.reachable(this.ctx.player(), (BlockPos)blockPos, this.ctx.playerController().getBlockReachDistance());
            if (reachable.isPresent()) {
                Rotation rotTowardsBlock = reachable.get();
                state.setTarget(new MovementState.MovementTarget(rotTowardsBlock, true));
                if (this.ctx.isLookingAt(blockPos) || this.ctx.playerRotations().isReallyCloseTo(rotTowardsBlock)) {
                    state.setInput(Input.CLICK_LEFT, true);
                }
                return false;
            }
            state.setTarget(new MovementState.MovementTarget(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(blockPos), this.ctx.playerRotations()), true));
            state.setInput(Input.CLICK_LEFT, true);
            return false;
        }
        if (somethingInTheWay) {
            state.setStatus(MovementStatus.UNREACHABLE);
            return true;
        }
        return true;
    }

    @Override
    public boolean safeToCancel() {
        return this.safeToCancel(this.currentState);
    }

    protected boolean safeToCancel(MovementState currentState) {
        return true;
    }

    @Override
    public BetterBlockPos getSrc() {
        return this.src;
    }

    @Override
    public BetterBlockPos getDest() {
        return this.dest;
    }

    @Override
    public void reset() {
        this.currentState = new MovementState().setStatus(MovementStatus.PREPPING);
    }

    public MovementState updateState(MovementState state) {
        if (!this.prepared(state)) {
            return state.setStatus(MovementStatus.PREPPING);
        }
        if (state.getStatus() == MovementStatus.PREPPING) {
            state.setStatus(MovementStatus.WAITING);
        }
        if (state.getStatus() == MovementStatus.WAITING) {
            state.setStatus(MovementStatus.RUNNING);
        }
        return state;
    }

    @Override
    public BlockPos getDirection() {
        return this.getDest().subtract(this.getSrc());
    }

    public void checkLoadedChunk(CalculationContext context) {
        this.calculatedWhileLoaded = context.bsi.worldContainsLoadedChunk(this.dest.x, this.dest.z);
    }

    @Override
    public boolean calculatedWhileLoaded() {
        return this.calculatedWhileLoaded;
    }

    @Override
    public void resetBlockCache() {
        this.toBreakCached = null;
        this.toPlaceCached = null;
        this.toWalkIntoCached = null;
    }

    public List<BlockPos> toBreak(BlockStateInterface bsi) {
        if (this.toBreakCached != null) {
            return this.toBreakCached;
        }
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (BetterBlockPos positionToBreak : this.positionsToBreak) {
            if (MovementHelper.canWalkThrough(bsi, positionToBreak.x, positionToBreak.y, positionToBreak.z)) continue;
            result.add(positionToBreak);
        }
        this.toBreakCached = result;
        return result;
    }

    public List<BlockPos> toPlace(BlockStateInterface bsi) {
        if (this.toPlaceCached != null) {
            return this.toPlaceCached;
        }
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        if (this.positionToPlace != null && !MovementHelper.canWalkOn(bsi, this.positionToPlace.x, this.positionToPlace.y, this.positionToPlace.z)) {
            result.add(this.positionToPlace);
        }
        this.toPlaceCached = result;
        return result;
    }

    public List<BlockPos> toWalkInto(BlockStateInterface bsi) {
        if (this.toWalkIntoCached == null) {
            this.toWalkIntoCached = new ArrayList<BlockPos>();
        }
        return this.toWalkIntoCached;
    }

    public BlockPos[] toBreakAll() {
        return this.positionsToBreak;
    }
}

