package fun.kubik.modules.combat;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.utils.client.ChatUtils;

import java.util.Random;

public class RussianRoulette  extends Module {
    private final Random random = new Random();

    public RussianRoulette() {
        super("RussianRoulette", Category.COMBAT);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        this.playRussianRoulette();
    }

    private void playRussianRoulette() {
        int outcome = this.random.nextInt(3);

        switch (outcome) {
            case 0:
                ChatUtils.addClientMessage("");
                break;



            case 2:
                ChatUtils.addClientMessage("");
                break;
        }
    }




    @Override
    public void onDisabled() {
        super.onDisabled();
    }
}