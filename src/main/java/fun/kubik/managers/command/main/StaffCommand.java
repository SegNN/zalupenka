/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fun.kubik.Load;
import fun.kubik.managers.command.api.Command;
import fun.kubik.managers.staff.StaffManagers;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;

public class StaffCommand
        extends Command {
    public StaffCommand() {
        super("стафф брух", "staff", "personal");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        StaffManagers staff = Load.getInstance().getHooks().getStaffManagers();

        if (args.length < 2) {
            error();
            return;
        }

        switch (args[1]) {
            case "add":
                if (args.length < 3) {
                    error();
                    return;
                }
                if (!staff.is(args[2])) {
                    staff.add(args[2]);
                    ChatUtils.addClientMessage("������� ������� ���������� � �����: " + args[2] + "!");
                } else {
                    ChatUtils.addClientMessage("������ ��������� ��� ����������!");
                }
                break;

            case "remove":
                if (args.length < 3) {
                    error();
                    return;
                }
                if (staff.is(args[2])) {
                    staff.remove(args[2]);
                    ChatUtils.addClientMessage("������� ������ ���������� � �����: " + args[2] + "!");
                } else {
                    ChatUtils.addClientMessage("������� ���������� ��� � ������!");
                }
                break;

            case "list":
                ChatUtils.addClientMessage(staff.get().toString()
                        .replace("[", "")
                        .replace("]", ""));
                break;

            case "clear":
                if (!staff.isEmpty()) {
                    staff.clears();
                    ChatUtils.addClientMessage("������� ������� ������ ���������!");
                } else {
                    ChatUtils.addClientMessage("������ ��������� ����!");
                }
                break;

            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("������������ ������������� �������!");
        ChatUtils.addClientMessage("�������������:");
        ChatUtils.addClientMessage(".staff add <name>");
        ChatUtils.addClientMessage(".staff remove <name>");
        ChatUtils.addClientMessage(".staff clear");
        ChatUtils.addClientMessage(".staff list");
    }
}