/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command;

import fun.kubik.itemics.api.command.ICommandSystem;
import fun.kubik.itemics.api.command.argparser.IArgParserManager;
import fun.kubik.itemics.command.argparser.ArgParserManager;

public enum CommandSystem implements ICommandSystem
{
    INSTANCE;


    @Override
    public IArgParserManager getParserManager() {
        return ArgParserManager.INSTANCE;
    }
}

