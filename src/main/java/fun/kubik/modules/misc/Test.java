/* Decompiler 17ms, total 403ms, lines 37 */
package fun.kubik.modules.misc;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.input.EventInput;
import fun.kubik.helpers.interfaces.ITranslate;
import fun.kubik.managers.client.ClientManagers;
import fun.kubik.managers.config.main.DraggableConfig;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;

public class Test extends Module implements ITranslate {
    public Test() {
        super("Test", Category.MISC);
    }

    @EventHook
    public void input(EventInput event) {
        if (event.getKey() == 90) {
            if (ClientManagers.getLanguage().equals("eng")) {
                ClientManagers.changeLanguage("ru");
            } else {
                ClientManagers.changeLanguage("eng");
            }
        }

        if (event.getKey() == 71) {
            ((DraggableConfig)Load.getInstance().getHooks().getConfigManagers().findClass(DraggableConfig.class)).fastLoad();
        }

        if (event.getKey() == 72) {
            ((DraggableConfig)Load.getInstance().getHooks().getConfigManagers().findClass(DraggableConfig.class)).fastSave();
        }

    }
}