/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config.main;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.kubik.managers.client.ClientManagers;
import fun.kubik.managers.config.api.Config;

import java.io.IOException;

public class ClientConfig
extends Config {
    public ClientConfig() {
        super("client", "fun/kubik/client");
    }

    @Override
    protected void save() throws IOException {
        JsonObject global = new JsonObject();
        JsonObject information = new JsonObject();
        information.addProperty("isUnhooked", ClientManagers.isUnHook());
        information.addProperty("Value", ClientManagers.getRandom());
        information.addProperty("Language", ClientManagers.getLanguage());
        global.add("Global", information);
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(global);
        this.write(contentPrettyPrint);
    }

    @Override
    protected void load() throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject global = (JsonObject)parser.parse(this.read());
        if (global.has("Global")) {
            JsonObject information = global.getAsJsonObject("Global");
            String lang = information.get("Language").getAsString();
            ClientManagers.changeLanguage(lang);
            boolean bool = information.get("isUnhooked").getAsBoolean();
            String value = information.get("Value").getAsString();
            ClientManagers.setUnHook(bool);
            ClientManagers.setRandom(value);
        }
    }
}

