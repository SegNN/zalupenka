/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.pathing.movement;

import fun.kubik.itemics.api.utils.BetterBlockPos;
import fun.kubik.itemics.pathing.movement.movements.MovementAscend;
import fun.kubik.itemics.pathing.movement.movements.MovementDescend;
import fun.kubik.itemics.pathing.movement.movements.MovementDiagonal;
import fun.kubik.itemics.pathing.movement.movements.MovementDownward;
import fun.kubik.itemics.pathing.movement.movements.MovementFall;
import fun.kubik.itemics.pathing.movement.movements.MovementParkour;
import fun.kubik.itemics.pathing.movement.movements.MovementPillar;
import fun.kubik.itemics.pathing.movement.movements.MovementTraverse;
import fun.kubik.itemics.utils.pathing.MutableMoveResult;
import net.minecraft.util.Direction;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum Moves {
    DOWNWARD(0, -1, 0){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementDownward(context.getItemics(), src, src.down());
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementDownward.cost(context, x, y, z);
        }
    }
    ,
    PILLAR(0, 1, 0){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementPillar(context.getItemics(), src, src.up());
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementPillar.cost(context, x, y, z);
        }
    }
    ,
    TRAVERSE_NORTH(0, 0, -1){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementTraverse(context.getItemics(), src, src.north());
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementTraverse.cost(context, x, y, z, x, z - 1);
        }
    }
    ,
    TRAVERSE_SOUTH(0, 0, 1){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementTraverse(context.getItemics(), src, src.south());
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementTraverse.cost(context, x, y, z, x, z + 1);
        }
    }
    ,
    TRAVERSE_EAST(1, 0, 0){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementTraverse(context.getItemics(), src, src.east());
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementTraverse.cost(context, x, y, z, x + 1, z);
        }
    }
    ,
    TRAVERSE_WEST(-1, 0, 0){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementTraverse(context.getItemics(), src, src.west());
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementTraverse.cost(context, x, y, z, x - 1, z);
        }
    }
    ,
    ASCEND_NORTH(0, 1, -1){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementAscend(context.getItemics(), src, new BetterBlockPos(src.x, src.y + 1, src.z - 1));
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementAscend.cost(context, x, y, z, x, z - 1);
        }
    }
    ,
    ASCEND_SOUTH(0, 1, 1){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementAscend(context.getItemics(), src, new BetterBlockPos(src.x, src.y + 1, src.z + 1));
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementAscend.cost(context, x, y, z, x, z + 1);
        }
    }
    ,
    ASCEND_EAST(1, 1, 0){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementAscend(context.getItemics(), src, new BetterBlockPos(src.x + 1, src.y + 1, src.z));
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementAscend.cost(context, x, y, z, x + 1, z);
        }
    }
    ,
    ASCEND_WEST(-1, 1, 0){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return new MovementAscend(context.getItemics(), src, new BetterBlockPos(src.x - 1, src.y + 1, src.z));
        }

        @Override
        public double cost(CalculationContext context, int x, int y, int z) {
            return MovementAscend.cost(context, x, y, z, x - 1, z);
        }
    }
    ,
    DESCEND_EAST(1, -1, 0, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            if (res.y == src.y - 1) {
                return new MovementDescend(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
            }
            return new MovementFall(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDescend.cost(context, x, y, z, x + 1, z, result);
        }
    }
    ,
    DESCEND_WEST(-1, -1, 0, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            if (res.y == src.y - 1) {
                return new MovementDescend(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
            }
            return new MovementFall(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDescend.cost(context, x, y, z, x - 1, z, result);
        }
    }
    ,
    DESCEND_NORTH(0, -1, -1, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            if (res.y == src.y - 1) {
                return new MovementDescend(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
            }
            return new MovementFall(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDescend.cost(context, x, y, z, x, z - 1, result);
        }
    }
    ,
    DESCEND_SOUTH(0, -1, 1, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            if (res.y == src.y - 1) {
                return new MovementDescend(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
            }
            return new MovementFall(context.getItemics(), src, new BetterBlockPos(res.x, res.y, res.z));
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDescend.cost(context, x, y, z, x, z + 1, result);
        }
    }
    ,
    DIAGONAL_NORTHEAST(1, 0, -1, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            return new MovementDiagonal(context.getItemics(), src, Direction.NORTH, Direction.EAST, res.y - src.y);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDiagonal.cost(context, x, y, z, x + 1, z - 1, result);
        }
    }
    ,
    DIAGONAL_NORTHWEST(-1, 0, -1, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            return new MovementDiagonal(context.getItemics(), src, Direction.NORTH, Direction.WEST, res.y - src.y);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDiagonal.cost(context, x, y, z, x - 1, z - 1, result);
        }
    }
    ,
    DIAGONAL_SOUTHEAST(1, 0, 1, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            return new MovementDiagonal(context.getItemics(), src, Direction.SOUTH, Direction.EAST, res.y - src.y);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDiagonal.cost(context, x, y, z, x + 1, z + 1, result);
        }
    }
    ,
    DIAGONAL_SOUTHWEST(-1, 0, 1, false, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            MutableMoveResult res = new MutableMoveResult();
            this.apply(context, src.x, src.y, src.z, res);
            return new MovementDiagonal(context.getItemics(), src, Direction.SOUTH, Direction.WEST, res.y - src.y);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementDiagonal.cost(context, x, y, z, x - 1, z + 1, result);
        }
    }
    ,
    PARKOUR_NORTH(0, 0, -4, true, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return MovementParkour.cost(context, src, Direction.NORTH);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementParkour.cost(context, x, y, z, Direction.NORTH, result);
        }
    }
    ,
    PARKOUR_SOUTH(0, 0, 4, true, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return MovementParkour.cost(context, src, Direction.SOUTH);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementParkour.cost(context, x, y, z, Direction.SOUTH, result);
        }
    }
    ,
    PARKOUR_EAST(4, 0, 0, true, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return MovementParkour.cost(context, src, Direction.EAST);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementParkour.cost(context, x, y, z, Direction.EAST, result);
        }
    }
    ,
    PARKOUR_WEST(-4, 0, 0, true, true){

        @Override
        public Movement apply0(CalculationContext context, BetterBlockPos src) {
            return MovementParkour.cost(context, src, Direction.WEST);
        }

        @Override
        public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
            MovementParkour.cost(context, x, y, z, Direction.WEST, result);
        }
    };

    public final boolean dynamicXZ;
    public final boolean dynamicY;
    public final int xOffset;
    public final int yOffset;
    public final int zOffset;

    private Moves(int x, int y, int z, boolean dynamicXZ, boolean dynamicY) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
        this.dynamicXZ = dynamicXZ;
        this.dynamicY = dynamicY;
    }

    private Moves(int x, int y, int z) {
        this(x, y, z, false, false);
    }

    public abstract Movement apply0(CalculationContext var1, BetterBlockPos var2);

    public void apply(CalculationContext context, int x, int y, int z, MutableMoveResult result) {
        if (this.dynamicXZ || this.dynamicY) {
            throw new UnsupportedOperationException();
        }
        result.x = x + this.xOffset;
        result.y = y + this.yOffset;
        result.z = z + this.zOffset;
        result.cost = this.cost(context, x, y, z);
    }

    public double cost(CalculationContext context, int x, int y, int z) {
        throw new UnsupportedOperationException();
    }
}

