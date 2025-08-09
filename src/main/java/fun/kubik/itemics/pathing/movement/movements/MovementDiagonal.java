/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.pathing.movement.movements;

import com.google.common.collect.ImmutableSet;
import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.pathing.movement.MovementStatus;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import fun.kubik.itemics.api.utils.input.Input;
import fun.kubik.itemics.pathing.movement.CalculationContext;
import fun.kubik.itemics.pathing.movement.Movement;
import fun.kubik.itemics.pathing.movement.MovementHelper;
import fun.kubik.itemics.pathing.movement.MovementState;
import fun.kubik.itemics.utils.BlockStateInterface;
import fun.kubik.itemics.utils.pathing.MutableMoveResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class MovementDiagonal
        extends Movement {
    private static final double SQRT_2 = Math.sqrt(2.0);

    public MovementDiagonal(IItemics itemics, BetterBlockPos start, Direction dir1, Direction dir2, int dy) {
        this(itemics, start, start.offset(dir1), start.offset(dir2), dir2, dy);
    }

    private MovementDiagonal(IItemics itemics, BetterBlockPos start, BetterBlockPos dir1, BetterBlockPos dir2, Direction drr2, int dy) {
        this(itemics, start, dir1.offset(drr2).up(dy), dir1, dir2);
    }

    private MovementDiagonal(IItemics itemics, BetterBlockPos start, BetterBlockPos end, BetterBlockPos dir1, BetterBlockPos dir2) {
        super(itemics, start, end, new BetterBlockPos[]{dir1, dir1.up(), dir2, dir2.up(), end, end.up()});
    }

    @Override
    protected boolean safeToCancel(MovementState state) {
        ClientPlayerEntity player = this.ctx.player();
        double offset = 0.25;
        double x = player.getPositionVec().x;
        double y = player.getPositionVec().y - 1.0;
        double z = player.getPositionVec().z;
        if (this.ctx.playerFeet().equals(this.src)) {
            return true;
        }
        if (MovementHelper.canWalkOn(this.ctx, new BlockPos(this.src.x, this.src.y - 1, this.dest.z)) && MovementHelper.canWalkOn(this.ctx, new BlockPos(this.dest.x, this.src.y - 1, this.src.z))) {
            return true;
        }
        if (this.ctx.playerFeet().equals(new BetterBlockPos(this.src.x, this.src.y, this.dest.z)) || this.ctx.playerFeet().equals(new BetterBlockPos(this.dest.x, this.src.y, this.src.z))) {
            return MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(x + offset, y, z + offset)) || MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(x + offset, y, z - offset)) || MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(x - offset, y, z + offset)) || MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(x - offset, y, z - offset));
        }
        return true;
    }

    @Override
    public double calculateCost(CalculationContext context) {
        MutableMoveResult result = new MutableMoveResult();
        MovementDiagonal.cost(context, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, result);
        if (result.y != this.dest.y) {
            return 1000000.0;
        }
        return result.cost;
    }

    @Override
    protected Set<BetterBlockPos> calculateValidPositions() {
        BetterBlockPos diagA = new BetterBlockPos(this.src.x, this.src.y, this.dest.z);
        BetterBlockPos diagB = new BetterBlockPos(this.dest.x, this.src.y, this.src.z);
        if (this.dest.y < this.src.y) {
            return ImmutableSet.of(this.src, this.dest.up(), diagA, diagB, this.dest, diagA.down(), diagB.down());
        }
        if (this.dest.y > this.src.y) {
            return ImmutableSet.of(this.src, this.src.up(), diagA, diagB, this.dest, diagA.up(), diagB.up());
        }
        return ImmutableSet.of(this.src, this.dest, diagA, diagB);
    }

    public static void cost(CalculationContext context, int x, int y, int z, int destX, int destZ, MutableMoveResult res) {
        BlockState cuttingOver1;
        BlockState fromDown;
        BlockState destWalkOn;
        if (!MovementHelper.canWalkThrough(context, destX, y + 1, destZ)) {
            return;
        }
        BlockState destInto = context.get(destX, y, destZ);
        boolean ascend = false;
        boolean descend = false;
        boolean frostWalker = false;
        if (!MovementHelper.canWalkThrough(context, destX, y, destZ, destInto)) {
            ascend = true;
            if (!(context.allowDiagonalAscend && MovementHelper.canWalkThrough(context, x, y + 2, z) && MovementHelper.canWalkOn(context, destX, y, destZ, destInto) && MovementHelper.canWalkThrough(context, destX, y + 2, destZ))) {
                return;
            }
            destWalkOn = destInto;
            fromDown = context.get(x, y - 1, z);
        } else {
            destWalkOn = context.get(destX, y - 1, destZ);
            fromDown = context.get(x, y - 1, z);
            boolean standingOnABlock = MovementHelper.mustBeSolidToWalkOn(context, x, y - 1, z, fromDown);
            boolean bl = frostWalker = standingOnABlock && MovementHelper.canUseFrostWalker(context, destWalkOn);
            if (!frostWalker && !MovementHelper.canWalkOn(context, destX, y - 1, destZ, destWalkOn)) {
                descend = true;
                if (!(context.allowDiagonalDescend && MovementHelper.canWalkOn(context, destX, y - 2, destZ) && MovementHelper.canWalkThrough(context, destX, y - 1, destZ, destWalkOn))) {
                    return;
                }
            }
            frostWalker &= !context.assumeWalkOnWater;
        }
        double multiplier = 4.63284688441047;
        if (destWalkOn.getBlock() == Blocks.SOUL_SAND) {
            multiplier += 2.316423442205235;
        } else if (!frostWalker && destWalkOn.getBlock() == Blocks.WATER) {
            multiplier += context.walkOnWaterOnePenalty * SQRT_2;
        }
        Block fromDownBlock = fromDown.getBlock();
        if (fromDownBlock == Blocks.LADDER || fromDownBlock == Blocks.VINE) {
            return;
        }
        if (fromDownBlock == Blocks.SOUL_SAND) {
            multiplier += 2.316423442205235;
        }
        if ((cuttingOver1 = context.get(x, y - 1, destZ)).getBlock() == Blocks.MAGMA_BLOCK || MovementHelper.isLava(cuttingOver1)) {
            return;
        }
        BlockState cuttingOver2 = context.get(destX, y - 1, z);
        if (cuttingOver2.getBlock() == Blocks.MAGMA_BLOCK || MovementHelper.isLava(cuttingOver2)) {
            return;
        }
        boolean water = false;
        BlockState startState = context.get(x, y, z);
        Block startIn = startState.getBlock();
        if (MovementHelper.isWater(startState) || MovementHelper.isWater(destInto)) {
            if (ascend) {
                return;
            }
            multiplier = context.waterWalkSpeed;
            water = true;
        }
        BlockState pb0 = context.get(x, y, destZ);
        BlockState pb2 = context.get(destX, y, z);
        if (ascend) {
            boolean ATop = MovementHelper.canWalkThrough(context, x, y + 2, destZ);
            boolean AMid = MovementHelper.canWalkThrough(context, x, y + 1, destZ);
            boolean ALow = MovementHelper.canWalkThrough(context, x, y, destZ, pb0);
            boolean BTop = MovementHelper.canWalkThrough(context, destX, y + 2, z);
            boolean BMid = MovementHelper.canWalkThrough(context, destX, y + 1, z);
            boolean BLow = MovementHelper.canWalkThrough(context, destX, y, z, pb2);
            if ((!ATop || !AMid || !ALow) && (!BTop || !BMid || !BLow) || MovementHelper.avoidWalkingInto(pb0) || MovementHelper.avoidWalkingInto(pb2) || ATop && AMid && MovementHelper.canWalkOn(context, x, y, destZ, pb0) || BTop && BMid && MovementHelper.canWalkOn(context, destX, y, z, pb2) || !ATop && AMid && ALow || !BTop && BMid && BLow) {
                return;
            }
            res.cost = multiplier * SQRT_2 + JUMP_ONE_BLOCK_COST;
            res.x = destX;
            res.z = destZ;
            res.y = y + 1;
            return;
        }
        double optionA = MovementHelper.getMiningDurationTicks(context, x, y, destZ, pb0, false);
        double optionB = MovementHelper.getMiningDurationTicks(context, destX, y, z, pb2, false);
        if (optionA != 0.0 && optionB != 0.0) {
            return;
        }
        BlockState pb1 = context.get(x, y + 1, destZ);
        if ((optionA += MovementHelper.getMiningDurationTicks(context, x, y + 1, destZ, pb1, true)) != 0.0 && optionB != 0.0) {
            return;
        }
        BlockState pb3 = context.get(destX, y + 1, z);
        if (optionA == 0.0 && (MovementHelper.avoidWalkingInto(pb2) && pb2.getBlock() != Blocks.WATER || MovementHelper.avoidWalkingInto(pb3))) {
            return;
        }
        if (optionA != 0.0 && (optionB += MovementHelper.getMiningDurationTicks(context, destX, y + 1, z, pb3, true)) != 0.0) {
            return;
        }
        if (optionB == 0.0 && (MovementHelper.avoidWalkingInto(pb0) && pb0.getBlock() != Blocks.WATER || MovementHelper.avoidWalkingInto(pb1))) {
            return;
        }
        if (optionA != 0.0 || optionB != 0.0) {
            multiplier *= SQRT_2 - 0.001;
            if (startIn == Blocks.LADDER || startIn == Blocks.VINE) {
                return;
            }
        } else if (context.canSprint && !water) {
            multiplier *= 0.7692444761225944;
        }
        res.cost = multiplier * SQRT_2;
        if (descend) {
            res.cost += Math.max(FALL_N_BLOCKS_COST[1], 0.9265693768820937);
            res.y = y - 1;
        } else {
            res.y = y;
        }
        res.x = destX;
        res.z = destZ;
    }

    @Override
    public MovementState updateState(MovementState state) {
        super.updateState(state);
        if (state.getStatus() != MovementStatus.RUNNING) {
            return state;
        }
        if (this.ctx.playerFeet().equals(this.dest)) {
            return state.setStatus(MovementStatus.SUCCESS);
        }
        if (!(this.playerInValidPosition() || MovementHelper.isLiquid(this.ctx, this.src) && this.getValidPositions().contains(this.ctx.playerFeet().up()))) {
            return state.setStatus(MovementStatus.UNREACHABLE);
        }
        if (this.dest.y > this.src.y && this.ctx.player().getPositionVec().y < (double)this.src.y + 0.1 && this.ctx.player().collidedHorizontally) {
            state.setInput(Input.JUMP, true);
        }
        if (this.sprint()) {
            state.setInput(Input.SPRINT, true);
        }
        MovementHelper.moveTowards(this.ctx, state, this.dest);
        return state;
    }

    private boolean sprint() {
        if (MovementHelper.isLiquid(this.ctx, this.ctx.playerFeet()) && !((Boolean)Itemics.settings().sprintInWater.value).booleanValue()) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            if (MovementHelper.canWalkThrough(this.ctx, this.positionsToBreak[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    protected boolean prepared(MovementState state) {
        return true;
    }

    @Override
    public List<BlockPos> toBreak(BlockStateInterface bsi) {
        if (this.toBreakCached != null) {
            return this.toBreakCached;
        }
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (int i = 4; i < 6; ++i) {
            if (MovementHelper.canWalkThrough(bsi, this.positionsToBreak[i].x, this.positionsToBreak[i].y, this.positionsToBreak[i].z)) continue;
            result.add(this.positionsToBreak[i]);
        }
        this.toBreakCached = result;
        return result;
    }

    @Override
    public List<BlockPos> toWalkInto(BlockStateInterface bsi) {
        if (this.toWalkIntoCached == null) {
            this.toWalkIntoCached = new ArrayList<>();
        }
        ArrayList<BlockPos> result = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            if (MovementHelper.canWalkThrough(bsi, this.positionsToBreak[i].x, this.positionsToBreak[i].y, this.positionsToBreak[i].z)) continue;
            result.add(this.positionsToBreak[i]);
        }
        this.toWalkIntoCached = result;
        return this.toWalkIntoCached;
    }
}
