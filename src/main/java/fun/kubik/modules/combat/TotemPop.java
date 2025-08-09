package fun.kubik.modules.combat;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.SEntityStatusPacket;

import java.util.HashMap;
import java.util.Map;

public class TotemPop extends Module {

    private final Map<PlayerEntity, Integer> totemPops = new HashMap<>();
    private final Map<PlayerEntity, Float> lastHealth = new HashMap<>();
    private final Map<PlayerEntity, Long> lastTotemTime = new HashMap<>();
    private final Map<PlayerEntity, Boolean> hasSentMessage = new HashMap<>();
    private final Minecraft mc = Minecraft.getInstance();
    private PlayerEntity lastAuraTarget = null;

    public TotemPop() {
        super("TotemPop", Category.COMBAT);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        totemPops.clear();
        lastHealth.clear();
        lastTotemTime.clear();
        hasSentMessage.clear();
        lastAuraTarget = null;
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        totemPops.clear();
        lastHealth.clear();
        lastTotemTime.clear();
        hasSentMessage.clear();
        lastAuraTarget = null;
    }

    @EventHook
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        try {
            Aura auraModule = Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
            if (auraModule != null) {
                Entity target = auraModule.getTarget();
                if (target instanceof PlayerEntity) {
                    PlayerEntity newTarget = (PlayerEntity) target;
                    lastAuraTarget = newTarget;

                    if (newTarget != null) {
                        float currentHealth = newTarget.getHealth();
                        float previousHealth = lastHealth.getOrDefault(newTarget, currentHealth);

                        long currentTime = System.currentTimeMillis();
                        long lastTime = lastTotemTime.getOrDefault(newTarget, 0L);

                        if (previousHealth <= 1.0f && currentHealth > 1.0f && (currentTime - lastTime) > 750) {
                            registerTotemPop(newTarget);
                            lastTotemTime.put(newTarget, currentTime);
                        }

                        lastHealth.put(newTarget, currentHealth);

                        if (currentHealth <= 0.0f && previousHealth > 0.0f) {
                            handlePlayerDeath(newTarget);
                        }
                    }
                } else {
                    lastAuraTarget = null;
                }
            }
        } catch (Exception e) {
            lastAuraTarget = null;
        }
    }

    @EventHook
    public void onPacketReceive(EventReceivePacket event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof SEntityStatusPacket) {
            SEntityStatusPacket packet = (SEntityStatusPacket) event.getPacket();

            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);

                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    if (player != mc.player) {
                        long currentTime = System.currentTimeMillis();
                        long lastTime = lastTotemTime.getOrDefault(player, 0L);
                        if ((currentTime - lastTime) > 750) {
                            registerTotemPop(player);
                            lastTotemTime.put(player, currentTime);
                        }
                    }
                }
            }
        }
    }

    private void registerTotemPop(PlayerEntity player) {
        long currentTime = System.currentTimeMillis();
        long lastPopTime = lastTotemTime.getOrDefault(player, 0L);

        if (currentTime - lastPopTime < 750 || hasSentMessage.getOrDefault(player, false)) {
            return;
        }

        int pops = totemPops.getOrDefault(player, 0) + 1;
        totemPops.put(player, pops);
        lastTotemTime.put(player, currentTime);
        hasSentMessage.put(player, true);

        ChatUtils.addClientMessage("§b" + player.getName().getString() + "§f попнул §a" + pops + "§e тотемов!" + (pops > 1 ? "" : ""));

        // Сбрасываем флаг через 750 мс, чтобы можно было отправить новое сообщение
        new Thread(() -> {
            try {
                Thread.sleep(750);
                hasSentMessage.put(player, false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handlePlayerDeath(PlayerEntity player) {
        if (totemPops.containsKey(player)) {
            int totalPops = totemPops.get(player);
            if (totalPops > 0) {
                ChatUtils.addClientMessage("§b" + player.getName().getString() + "§f умер было попнуто §a" + totalPops + "§e тотемов" + (totalPops > 1 ? "" : ""));
            }
            totemPops.remove(player);
            lastHealth.remove(player);
            lastTotemTime.remove(player);
            hasSentMessage.remove(player);
        }
    }
}