/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config.api;

import fun.kubik.helpers.interfaces.IFastAccess;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public abstract class Config
implements IFastAccess {
    protected File path;
    protected final String suffix = ".sk";
    private String name;

    protected Config(String name, String path) {
        this.name = name;
        this.path = new File(Minecraft.getInstance().gameDir, path);
    }

    protected String read() throws IOException {
        return Files.readString((Path)new File(String.valueOf(this.path) + "/" + this.name + ".sk").toPath());
    }

    protected void write(String cfg) throws IOException {
        Files.writeString((Path)new File(String.valueOf(this.path) + "/" + this.name + ".sk").toPath(), (CharSequence)cfg, (OpenOption[])new OpenOption[0]);
    }

    protected abstract void save() throws IOException;

    protected abstract void load() throws IOException;

    public synchronized void fastLoad() {
        if (!this.path.exists()) {
            this.path.mkdirs();
      //      System.out.println("Created new folder");
        }
        if (new File(String.valueOf(this.path) + "/" + this.name + ".sk").exists()) {
            try {
                this.load();
            //    System.out.println("loaded");
            } catch (Exception exception) {}
        } else {
            System.out.println("gg");
        }
    }

    public void fastSave() {
        if (!this.path.exists()) {
            this.path.mkdirs();
  //          System.out.println("Created new folder");
        }
        try {
            this.save();
        } catch (Exception exception) {
            // empty catch block
        }
    }

    @Generated
    public File getPath() {
        return this.path;
    }

    @Generated
    public String getSuffix() {
        return this.suffix;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setPath(File path) {
        this.path = path;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }
}

