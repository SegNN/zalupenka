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

public interface AbstractGameEventListener
extends IGameEventListener {
    @Override
    default public void onTick(TickEvent event) {
    }

    @Override
    default public void onPlayerUpdate(PlayerUpdateEvent event) {
    }

    @Override
    default public void onSendChatMessage(ChatEvent event) {
    }

    @Override
    default public void onPreTabComplete(TabCompleteEvent event) {
    }

    @Override
    default public void onChunkEvent(ChunkEvent event) {
    }

    @Override
    default public void onRenderPass(RenderEvent event) {
    }

    @Override
    default public void onWorldEvent(WorldEvent event) {
    }

    @Override
    default public void onSendPacket(PacketEvent event) {
    }

    @Override
    default public void onReceivePacket(PacketEvent event) {
    }

    @Override
    default public void onPlayerRotationMove(RotationMoveEvent event) {
    }

    @Override
    default public void onPlayerSprintState(SprintStateEvent event) {
    }

    @Override
    default public void onBlockInteract(BlockInteractEvent event) {
    }

    @Override
    default public void onPlayerDeath() {
    }

    @Override
    default public void onPathEvent(PathEvent event) {
    }
}

