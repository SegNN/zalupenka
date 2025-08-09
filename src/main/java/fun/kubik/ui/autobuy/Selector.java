/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.ui.autobuy;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.render.GLHelpers;
import fun.kubik.helpers.render.ScreenHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Selector
implements IFastAccess {
    public Item item;
    public ItemStack stack;
    public String price = "0";
    public String count = "0";
    public String stacks = "0";
    public boolean priceClicked;
    public boolean countClicked;
    public boolean stackClicked;
    public boolean ench;
    public boolean items;
    public boolean fake;
    public boolean don;
    float x;
    float y;
    float width;
    float height;
    public float setH = 200.0f;

    public Selector(Item item) {
        this.item = item;
    }

    public void setCoord(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        float catHeight = 140.0f;
        float catY = this.y + 50.0f;
        float priceW = 200.0f;
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 30.0f, this.y + 60.0f + 85.0f + catHeight, 355.0f, this.setH, 10.0f, ColorHelpers.rgba(200, 200, 200, 50));
        sf_medium.drawCenteredText(matrixStack, "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", this.x + this.width / 1.5f + (this.width - this.width / 1.5f) / 2.0f, catY + 15.0f + catHeight + 85.0f, ColorHelpers.rgba(255, 255, 255, 255), 25.0f);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 40.0f, this.y + 55.0f + 130.0f + catHeight, 335.0f, 30.0f, 10.0f, ColorHelpers.rgba(1, 1, 1, 50));
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(this.item, 1), this.x + this.width / 1.5f + 40.0f, this.y + 60.0f + 120.0f + catHeight, true, true, 2.0f);
        sf_medium.drawText(matrixStack, this.item.getName().getString(), this.x + this.width / 1.5f + 80.0f, catY + 17.0f + catHeight + 120.0f, ColorHelpers.rgba(200, 200, 200, 255), 20.0f);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 40.0f, catY + catHeight + 170.0f, 335.0f, 110.0f, 10.0f, ColorHelpers.rgba(1, 1, 1, 50));
        sf_medium.drawText(matrixStack, "\u0426\u0435\u043d\u0430", this.x + this.width / 1.5f + 50.0f, catY + 30.0f + catHeight + 150.0f, ColorHelpers.rgba(200, 200, 200, 255), 20.0f);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 10.0f + 355.0f - priceW, catY + 32.0f + catHeight + 150.0f, 200.0f, 20.0f, 5.0f, ColorHelpers.rgba(1, 1, 1, 70));
        sf_medium.drawText(matrixStack, (String)(this.priceClicked ? this.price + (System.currentTimeMillis() % 1000L > 500L ? "|" : "") : (this.price.isEmpty() ? "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0446\u0435\u043d\u0443" : this.price)), this.x + this.width / 1.5f + 10.0f + 355.0f - priceW + 100.0f, catY + 33.0f + catHeight + 150.0f, ColorHelpers.rgba(200, 200, 200, 255), 15.0f);
        sf_medium.drawText(matrixStack, "\u041a\u043e\u043b-\u0432\u043e", this.x + this.width / 1.5f + 50.0f, catY + 55.0f + catHeight + 150.0f, ColorHelpers.rgba(200, 200, 200, 255), 20.0f);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 10.0f + 355.0f - priceW, catY + 57.0f + catHeight + 150.0f, 200.0f, 20.0f, 5.0f, ColorHelpers.rgba(1, 1, 1, 70));
        sf_medium.drawText(matrixStack, (String)(this.countClicked ? this.count + (System.currentTimeMillis() % 1000L > 500L ? "|" : "") : (this.count.isEmpty() ? "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0446\u0435\u043d\u0443" : this.count)), this.x + this.width / 1.5f + 10.0f + 355.0f - priceW + 100.0f, catY + 58.0f + catHeight + 150.0f, ColorHelpers.rgba(200, 200, 200, 255), 15.0f);
        sf_medium.drawText(matrixStack, "\u0421\u0442\u0430\u043a\u043e\u0432", this.x + this.width / 1.5f + 50.0f, catY + 80.0f + catHeight + 150.0f, ColorHelpers.rgba(200, 200, 200, 255), 20.0f);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 10.0f + 355.0f - priceW, catY + 82.0f + catHeight + 150.0f, 200.0f, 20.0f, 5.0f, ColorHelpers.rgba(1, 1, 1, 70));
        sf_medium.drawText(matrixStack, (String)(this.stackClicked ? this.stacks + (System.currentTimeMillis() % 1000L > 500L ? "|" : "") : (this.stacks.isEmpty() ? "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0446\u0435\u043d\u0443" : this.stacks)), this.x + this.width / 1.5f + 10.0f + 355.0f - priceW + 100.0f, catY + 83.0f + catHeight + 150.0f, ColorHelpers.rgba(200, 200, 200, 255), 15.0f);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        float priceW = 200.0f;
        float catY = this.y + 50.0f;
        float catHeight = 140.0f;
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width / 1.5f + 10.0f + 355.0f - priceW, catY + 32.0f + catHeight + 150.0f, 200.0f, 20.0f)) {
            this.priceClicked = !this.priceClicked;
            this.countClicked = false;
            this.stackClicked = false;
            ChatUtils.addMessage("1");
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width / 1.5f + 10.0f + 355.0f - priceW, catY + 57.0f + catHeight + 150.0f, 200.0f, 20.0f)) {
            this.countClicked = !this.countClicked;
            this.stackClicked = false;
            this.priceClicked = false;
            ChatUtils.addMessage("12");
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width / 1.5f + 10.0f + 355.0f - priceW, catY + 82.0f + catHeight + 150.0f, 200.0f, 20.0f)) {
            this.stackClicked = !this.stackClicked;
            this.priceClicked = false;
            this.countClicked = false;
            ChatUtils.addMessage("3");
        }
    }

    public void release(double mX, double mY) {
    }

    public void charTyped(char c) {
    }

    public void scroll(double delta, double mouseX, double mouseY) {
    }

    public void key(int k) {
    }
}

