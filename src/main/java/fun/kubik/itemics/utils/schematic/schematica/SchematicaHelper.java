package fun.kubik.itemics.utils.schematic.schematica;

import com.github.lunatrius.schematica.Schematica;
import fun.kubik.itemics.api.schematic.IStaticSchematic;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public final class SchematicaHelper {

    public static boolean isSchematicaPresent() {
        try {
            Class.forName(Schematica.class.getName());
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            return false;
        }
    }

    public static Optional<Tuple<IStaticSchematic, BlockPos>> getOpenSchematic() {
        return Optional.empty();
    }
}
