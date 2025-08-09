// Временный тест для проверки блоков
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;

public class TestBlocks {
    public static void main(String[] args) {
        // Тестируем iron_ore
        testBlock("iron_ore");
        testBlock("minecraft:iron_ore");
        testBlock("stone");
        testBlock("minecraft:stone");
    }
    
    public static void testBlock(String name) {
        try {
            String resourceLocationString = name.contains(":") ? name : "minecraft:" + name;
            ResourceLocation resourceLocation = ResourceLocation.tryCreate(resourceLocationString);
            
            if (resourceLocation != null) {
                Block block = Registry.BLOCK.getOptional(resourceLocation).orElse(null);
                System.out.println(name + " -> " + resourceLocationString + " -> " + (block != null ? "FOUND" : "NOT FOUND"));
            } else {
                System.out.println(name + " -> " + resourceLocationString + " -> INVALID RESOURCE LOCATION");
            }
        } catch (Exception e) {
            System.out.println(name + " -> ERROR: " + e.getMessage());
        }
    }
}