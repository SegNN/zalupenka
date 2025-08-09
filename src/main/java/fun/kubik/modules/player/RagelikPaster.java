/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.utils.client.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SJoinGamePacket;

public class RagelikPaster
extends Module {
    private final List<Short> actionNumbers = new ArrayList<Short>();
    private boolean checking = false;

    public RagelikPaster() {
        super("RagelikPaster", Category.PLAYER);
    }

    @EventHook
    public void onPacket(EventReceivePacket eventPacket) {
        IPacket<?> var3;
        if (eventPacket.getPacket() instanceof SJoinGamePacket) {
            this.reset();
            this.checking = true;
        }
        if ((var3 = eventPacket.getPacket()) instanceof SConfirmTransactionPacket) {
            SConfirmTransactionPacket packet = (SConfirmTransactionPacket)var3;
            this.handleTransaction(packet.getActionNumber());
        }
    }

    private void reset() {
        this.actionNumbers.clear();
        this.checking = false;
    }

    private void handleTransaction(short action) {
        this.actionNumbers.add(action);
        if (this.actionNumbers.size() >= 5) {
            this.analyzeActionNumbers();
        }
    }

    private void analyzeActionNumbers() {
        ArrayList<Integer> diffs = new ArrayList<Integer>();
        for (int i = 1; i < this.actionNumbers.size(); ++i) {
            diffs.add(this.actionNumbers.get(i) - this.actionNumbers.get(i - 1));
        }
        short first = this.actionNumbers.get(0);
        ChatUtils.addClientMessage("" + first);
        String antiCheat = null;
        if (this.allEqual(diffs)) {
            int diff = (Integer)diffs.get(0);
            if (diff == 1) {
                antiCheat = this.inRange(first, (short)-23772, (short)-23762) ? "Vulcan" : (!this.inRange(first, (short)95, (short)105) && !this.inRange(first, (short)-20005, (short)-19995) ? (this.inRange(first, (short)32763, (short)-32762) ? "Grizzly" : "Verus") : "Matrix");
            } else if (diff == -1) {
                antiCheat = this.inRange(first, (short)-8287, (short)-8280) ? "Errata" : (first < -3000 ? "Intave" : (this.inRange(first, (short)-5, (short)0) ? "Grim" : (this.inRange(first, (short)-3000, (short)-2995) ? "Karhu" : "not found")));
            }
            if (antiCheat != null) {
                this.notifyAC(antiCheat);
                return;
            }
        }
        if (this.actionNumbers.size() >= 5) {
            int i;
            if (Objects.equals(this.actionNumbers.get(0), this.actionNumbers.get(1))) {
                boolean restSequential = true;
                for (i = 3; i < this.actionNumbers.size(); ++i) {
                    if (this.actionNumbers.get(i) - this.actionNumbers.get(i - 1) == 1) continue;
                    restSequential = false;
                    break;
                }
                if (restSequential) {
                    this.notifyAC("Verus");
                    return;
                }
            }
            if (diffs.size() >= 5 && (Integer)diffs.get(0) >= 100 && (Integer)diffs.get(1) == -1) {
                boolean allMinusOne = true;
                for (i = 2; i < diffs.size(); ++i) {
                    if ((Integer)diffs.get(i) == -1) continue;
                    allMinusOne = false;
                    break;
                }
                if (allMinusOne) {
                    this.notifyAC("Grim || Polar");
                    return;
                }
            }
            if (first < -3000 && this.actionNumbers.contains((short)0)) {
                this.notifyAC("Intave");
                return;
            }
            if (this.actionNumbers.size() >= 5 && this.actionNumbers.get(0) == -30767 && this.actionNumbers.get(1) == -30766 && this.actionNumbers.get(2) == -25767) {
                boolean sequential = true;
                for (i = 4; i < this.actionNumbers.size(); ++i) {
                    if (this.actionNumbers.get(i) - this.actionNumbers.get(i - 1) == 1) continue;
                    sequential = false;
                    break;
                }
                if (sequential) {
                    this.notifyAC("Old Vulcan");
                    return;
                }
            }
        }
        this.reset();
    }

    private void notifyAC(String str) {
        ChatUtils.addClientMessage("AC: " + str);
        this.reset();
        this.toggle();
    }

    private boolean allEqual(List<Integer> list) {
        if (list.isEmpty()) {
            return true;
        }
        int first = list.get(0);
        for (int val2 : list) {
            if (val2 == first) continue;
            return false;
        }
        return true;
    }

    private boolean inRange(short value, short min, short max) {
        return value >= min && value <= max;
    }
}

