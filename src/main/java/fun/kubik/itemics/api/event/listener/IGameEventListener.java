/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.event.listener;

import fun.kubik.itemics.api.event.events.BlockInteractEvent;
import fun.kubik.itemics.api.event.events.ChatEvent;
import fun.kubik.itemics.api.event.events.ChunkEvent;
import fun.kubik.itemics.api.event.events.PacketEvent;
import fun.kubik.itemics.api.event.events.PathEvent;
import fun.kubik.itemics.api.event.events.PlayerUpdateEvent;
import fun.kubik.itemics.api.event.events.RenderEvent;
import fun.kubik.itemics.api.event.events.RotationMoveEvent;
import fun.kubik.itemics.api.event.events.SprintStateEvent;
import fun.kubik.itemics.api.event.events.TabCompleteEvent;
import fun.kubik.itemics.api.event.events.TickEvent;
import fun.kubik.itemics.api.event.events.WorldEvent;

public interface IGameEventListener {
    public void onTick(TickEvent var1);

    public void onPlayerUpdate(PlayerUpdateEvent var1);

    public void onSendChatMessage(ChatEvent var1);

    public void onPreTabComplete(TabCompleteEvent var1);

    public void onChunkEvent(ChunkEvent var1);

    public void onRenderPass(RenderEvent var1);

    public void onWorldEvent(WorldEvent var1);

    public void onSendPacket(PacketEvent var1);

    public void onReceivePacket(PacketEvent var1);

    public void onPlayerRotationMove(RotationMoveEvent var1);

    public void onPlayerSprintState(SprintStateEvent var1);

    public void onBlockInteract(BlockInteractEvent var1);

    public void onPlayerDeath();

    public void onPathEvent(PathEvent var1);
}

