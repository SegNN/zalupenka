/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.pathing.movement.movements;

import com.google.common.collect.ImmutableSet;
import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.pathing.movement.MovementStatus;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import fun.kubik.itemics.api.utils.Rotation;
import fun.kubik.itemics.api.utils.RotationUtils;
import fun.kubik.itemics.api.utils.input.Input;
import fun.kubik.itemics.pathing.movement.CalculationContext;
import fun.kubik.itemics.pathing.movement.Movement;
import fun.kubik.itemics.pathing.movement.MovementHelper;
import fun.kubik.itemics.pathing.movement.MovementState;
import fun.kubik.itemics.utils.BlockStateInterface;
import fun.kubik.itemics.utils.pathing.MutableMoveResult;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class MovementDescend
extends Movement {
    private int numTicks = 0;
    public boolean forceSafeMode = false;

    public MovementDescend(IItemics itemics, BetterBlockPos start, BetterBlockPos end) {
        super(itemics, start, end, new BetterBlockPos[]{end.up(2), end.up(), end}, end.down());
    }

    @Override
    public void reset() {
        super.reset();
        this.numTicks = 0;
        this.forceSafeMode = false;
    }

    public void forceSafeMode() {
        this.forceSafeMode = true;
    }

    @Override
    public double calculateCost(CalculationContext context) {
        MutableMoveResult result = new MutableMoveResult();
        MovementDescend.cost(context, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, result);
        if (result.y != this.dest.y) {
            return 1000000.0;
        }
        return result.cost;
    }

    @Override
    protected Set<BetterBlockPos> calculateValidPositions() {
        return ImmutableSet.of(this.src, this.dest.up(), this.dest);
    }

    public static void cost(CalculationContext context, int x, int y, int z, int destX, int destZ, MutableMoveResult res) {
        double totalCost = 0.0;
        BlockState destDown = context.get(destX, y - 1, destZ);
        if ((totalCost += MovementHelper.getMiningDurationTicks(context, destX, y - 1, destZ, destDown, false)) >= 1000000.0) {
            return;
        }
        if ((totalCost += MovementHelper.getMiningDurationTicks(context, destX, y, destZ, false)) >= 1000000.0) {
            return;
        }
        if ((totalCost += MovementHelper.getMiningDurationTicks(context, destX, y + 1, destZ, true)) >= 1000000.0) {
            return;
        }
        Block fromDown = context.get(x, y - 1, z).getBlock();
        if (fromDown == Blocks.LADDER || fromDown == Blocks.VINE) {
            return;
        }
        BlockState below = context.get(destX, y - 2, destZ);
        if (!MovementHelper.canWalkOn(context, destX, y - 2, destZ, below)) {
            MovementDescend.dynamicFallCost(context, x, y, z, destX, destZ, totalCost, below, res);
            return;
        }
        if (destDown.getBlock() == Blocks.LADDER || destDown.getBlock() == Blocks.VINE) {
            return;
        }
        if (MovementHelper.canUseFrostWalker(context, destDown)) {
            return;
        }
        double walk = 3.7062775075283763;
        if (fromDown == Blocks.SOUL_SAND) {
            walk *= 2.0;
        }
        res.x = destX;
        res.y = y - 1;
        res.z = destZ;
        res.cost = totalCost += walk + Math.max(FALL_N_BLOCKS_COST[1], 0.9265693768820937);
    }

    public static boolean dynamicFallCost(CalculationContext context, int x, int y, int z, int destX, int destZ, double frontBreak, BlockState below, MutableMoveResult res) {
        if (frontBreak != 0.0 && context.get(destX, y + 2, destZ).getBlock() instanceof FallingBlock) {
            return false;
        }
        if (!MovementHelper.canWalkThrough(context, destX, y - 2, destZ, below)) {
            return false;
        }
        double costSoFar = 0.0;
        int effectiveStartHeight = y;
        int fallHeight = 3;
        int newY;
        while ((newY = y - fallHeight) >= 0) {
            BlockState ontoBlock = context.get(destX, newY, destZ);
            int unprotectedFallHeight = fallHeight - (y - effectiveStartHeight);
            double tentativeCost = 3.7062775075283763 + FALL_N_BLOCKS_COST[unprotectedFallHeight] + frontBreak + costSoFar;
            if (MovementHelper.isWater(ontoBlock)) {
                if (!MovementHelper.canWalkThrough(context, destX, newY, destZ, ontoBlock)) {
                    return false;
                }
                if (context.assumeWalkOnWater) {
                    return false;
                }
                if (MovementHelper.isFlowing(destX, newY, destZ, ontoBlock, context.bsi)) {
                    return false;
                }
                if (!MovementHelper.canWalkOn(context, destX, newY - 1, destZ)) {
                    return false;
                }
                res.x = destX;
                res.y = newY;
                res.z = destZ;
                res.cost = tentativeCost;
                return false;
            }
            if (unprotectedFallHeight <= 11 && (ontoBlock.getBlock() == Blocks.VINE || ontoBlock.getBlock() == Blocks.LADDER)) {
                costSoFar += FALL_N_BLOCKS_COST[unprotectedFallHeight - 1];
                costSoFar += 6.666666666666667;
                effectiveStartHeight = newY;
            } else if (!MovementHelper.canWalkThrough(context, destX, newY, destZ, ontoBlock)) {
                if (!MovementHelper.canWalkOn(context, destX, newY, destZ, ontoBlock)) {
                    return false;
                }
                if (MovementHelper.isBottomSlab(ontoBlock)) {
                    return false;
                }
                if (unprotectedFallHeight <= context.maxFallHeightNoWater + 1) {
                    res.x = destX;
                    res.y = newY + 1;
                    res.z = destZ;
                    res.cost = tentativeCost;
                    return false;
                }
                if (context.hasWaterBucket && unprotectedFallHeight <= context.maxFallHeightBucket + 1) {
                    res.x = destX;
                    res.y = newY + 1;
                    res.z = destZ;
                    res.cost = tentativeCost + context.placeBucketCost();
                    return true;
                }
                return false;
            }
            ++fallHeight;
        }
        return false;
    }

    @Override
    public MovementState updateState(MovementState state) {
        super.updateState(state);
        if (state.getStatus() != MovementStatus.RUNNING) {
            return state;
        }
        BetterBlockPos playerFeet = this.ctx.playerFeet();
        BlockPos fakeDest = new BlockPos(this.dest.getX() * 2 - this.src.getX(), this.dest.getY(), this.dest.getZ() * 2 - this.src.getZ());
        if ((((Vector3i)playerFeet).equals(this.dest) || ((Vector3i)playerFeet).equals(fakeDest)) && (MovementHelper.isLiquid(this.ctx, this.dest) || this.ctx.player().getPositionVec().y - (double)this.dest.getY() < 0.5)) {
            return state.setStatus(MovementStatus.SUCCESS);
        }
        if (this.safeMode()) {
            double destX = ((double)this.src.getX() + 0.5) * 0.17 + ((double)this.dest.getX() + 0.5) * 0.83;
            double destZ = ((double)this.src.getZ() + 0.5) * 0.17 + ((double)this.dest.getZ() + 0.5) * 0.83;
            ClientPlayerEntity player = this.ctx.player();
            state.setTarget(new MovementState.MovementTarget(new Rotation(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), new Vector3d(destX, this.dest.getY(), destZ), new Rotation(player.packetYaw, player.packetPitch)).getYaw(), player.packetPitch), false)).setInput(Input.MOVE_FORWARD, true);
            return state;
        }
        double diffX = this.ctx.player().getPositionVec().x - ((double)this.dest.getX() + 0.5);
        double diffZ = this.ctx.player().getPositionVec().z - ((double)this.dest.getZ() + 0.5);
        double ab = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double x = this.ctx.player().getPositionVec().x - ((double)this.src.getX() + 0.5);
        double z = this.ctx.player().getPositionVec().z - ((double)this.src.getZ() + 0.5);
        double fromStart = Math.sqrt(x * x + z * z);
        if (!((Vector3i)playerFeet).equals(this.dest) || ab > 0.25) {
            if (this.numTicks++ < 20 && fromStart < 1.25) {
                MovementHelper.moveTowards(this.ctx, state, fakeDest);
            } else {
                MovementHelper.moveTowards(this.ctx, state, this.dest);
            }
        }
        return state;
    }

    public boolean safeMode() {
        if (this.forceSafeMode) {
            return true;
        }
        BlockPos into = this.dest.subtract(this.src.down()).add(this.dest);
        if (this.skipToAscend()) {
            return true;
        }
        for (int y = 0; y <= 2; ++y) {
            if (!MovementHelper.avoidWalkingInto(BlockStateInterface.get(this.ctx, into.up(y)))) continue;
            return true;
        }
        return false;
    }

    public boolean skipToAscend() {
        BlockPos into = this.dest.subtract(this.src.down()).add(this.dest);
        return !MovementHelper.canWalkThrough(this.ctx, new BetterBlockPos(into)) && MovementHelper.canWalkThrough(this.ctx, new BetterBlockPos(into).up()) && MovementHelper.canWalkThrough(this.ctx, new BetterBlockPos(into).up(2));
    }
}

