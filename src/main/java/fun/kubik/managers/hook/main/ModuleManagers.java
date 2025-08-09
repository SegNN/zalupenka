/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.hook.main;

import fun.kubik.helpers.interfaces.IFinderModules;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.managers.module.Module;
import fun.kubik.modules.combat.*;
import fun.kubik.modules.misc.*;
import fun.kubik.modules.movement.*;
import fun.kubik.modules.player.*;
import fun.kubik.modules.render.*;
import fun.kubik.modules.combat.*;
import fun.kubik.modules.misc.*;
import fun.kubik.modules.movement.*;
import fun.kubik.modules.player.*;
import fun.kubik.modules.render.*;

import java.util.ArrayList;
import java.util.Comparator;

public class ModuleManagers
extends ArrayList<Module>
implements IManager<Module>,
IFinderModules<Module> {
    public ModuleManagers() {
        this.init();
    }

    @Override
    public void init() {
        this.register(new Aura());
        this.register(new TPSSync());
        this.register(new TotemPop());
        this.register(new FireworkESP());
        this.register(new TimerFunction());
        this.register(new ElytraTweaks());
        this.register(new AutoSprint());
        this.register(new ClickGui());
        this.register(new AntiBot());
        this.register(new CrystallOptimizer());
        this.register(new AutoFarm());
        this.register(new AimBot());
        this.register(new NoDelay());
        this.register(new AutoTotem());
        this.register(new NoGameOverlay());
        this.register(new SRPSpoofer());
        this.register(new ItemSwapFix());
//        this.register(new IRC());
        this.register(new NoWeb());
        this.register(new GuiMove());
        this.register(new AutoPotion());
        this.register(new AutoDuel());
        this.register(new NoPush());
        this.register(new HitBox());
        this.register(new RussianRoulette());
        this.register(new AutoAccept());
        this.register(new TriggerBot());
        this.register(new ElytraSwap());
        this.register(new ElytraFly());
        this.register(new PacketDebug());
        this.register(new SuperFirework());
        this.register(new LeaveTracker());
        this.register(new BetterChat());
        this.register(new Interface());
        this.register(new NoInteract());
        this.register(new ESP());
        this.register(new ItemScroller());
        this.register(new ElytraBooster());
        this.register(new NoFriendDamage());
        this.register(new ItemESP());
        this.register(new FreeCam());
        this.register(new AutoLeave());
        this.register(new PearlTarget());
        this.register(new ClickAction());
        this.register(new UnHook());
        this.register(new AutoSwap());
        this.register(new Tracers());
        this.register(new CustomWorld());
        this.register(new Fly());
        this.register(new SwingAnimations());
        this.register(new FixHP());
        this.register(new CustomModel());
        this.register(new Test());
        this.register(new CrackCrash());
        this.register(new ItemsCooldown());
        this.register(new Spinner());
        this.register(new AirStuck());
        this.register(new HitSound());
        this.register(new Speed());
        this.register(new Particles());
        this.register(new NoSlow());
        this.register(new ClientSound());
        this.register(new TargetESP());
        this.register(new ElytraBounce());
        this.register(new FullBright());
        this.register(new ItemFinder());
        this.register(new RGExploit());
        this.register(new EnderChestExploit());
        this.register(new ElytraTarget());
        this.register(new FastBreak());
        this.register(new Velocity());
        this.register(new CrystalAura());
        this.register(new GriefJoiner());
        this.register(new BetterMinecraft());
        this.register(new NameProtect());
        this.register(new PearlPrediction());
        this.register(new Arrows());
        this.register(new BlockEsp());
        this.register(new AutoEat());
        this.register(new ChinaHat());
        this.register(new GodMode());
        this.register(new AutoTool());
        this.register(new NoClip());
//        this.register(new AutoCrystal());
        this.register(new FlagDetector());
        this.register(new VoiceChat());
        this.register(new ElytraMotion());
        this.sortModulesByName();
    }

    @Override
    public void register(Module module) {
        this.add(module);
    }

    private void sortModulesByName() {
        this.sort(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER));
    }

    @Override
    public <T extends Module> T findName(String name) {
        return (T)((Module)this.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Module> T findClass(Class<T> clazz) {
        return (T)((Module)this.stream().filter(module -> module.getClass() == clazz).findAny().orElse(null));
    }
}

