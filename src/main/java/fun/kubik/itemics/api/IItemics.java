/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api;

import fun.kubik.itemics.api.behavior.ILookBehavior;
import fun.kubik.itemics.api.behavior.IPathingBehavior;
import fun.kubik.itemics.api.cache.IWorldProvider;
import fun.kubik.itemics.api.command.manager.ICommandManager;
import fun.kubik.itemics.api.event.listener.IEventBus;
import fun.kubik.itemics.api.pathing.calc.IPathingControlManager;
import fun.kubik.itemics.api.process.IBuilderProcess;
import fun.kubik.itemics.api.process.ICustomGoalProcess;
import fun.kubik.itemics.api.process.IExploreProcess;
import fun.kubik.itemics.api.process.IFarmProcess;
import fun.kubik.itemics.api.process.IFollowProcess;
import fun.kubik.itemics.api.process.IGetToBlockProcess;
import fun.kubik.itemics.api.process.IMineProcess;
import fun.kubik.itemics.api.selection.ISelectionManager;
import fun.kubik.itemics.api.utils.IInputOverrideHandler;
import fun.kubik.itemics.api.utils.IPlayerContext;

public interface IItemics {
    public IPathingBehavior getPathingBehavior();

    public ILookBehavior getLookBehavior();

    public IFollowProcess getFollowProcess();

    public IMineProcess getMineProcess();

    public IBuilderProcess getBuilderProcess();

    public IExploreProcess getExploreProcess();

    public IFarmProcess getFarmProcess();

    public ICustomGoalProcess getCustomGoalProcess();

    public IGetToBlockProcess getGetToBlockProcess();

    public IWorldProvider getWorldProvider();

    public IPathingControlManager getPathingControlManager();

    public IInputOverrideHandler getInputOverrideHandler();

    public IPlayerContext getPlayerContext();

    public IEventBus getGameEventHandler();

    public ISelectionManager getSelectionManager();

    public ICommandManager getCommandManager();

    public void openClick();
}

