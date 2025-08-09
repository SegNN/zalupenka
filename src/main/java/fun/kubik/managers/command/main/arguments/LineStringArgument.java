/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.command.main.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class LineStringArgument
implements ArgumentType<String> {
    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        StringBuilder line = new StringBuilder();
        while (stringReader.canRead()) {
            line.append(stringReader.read());
        }
        return line.toString();
    }
}

