/*
 * AutoFarm module for automatic farming
 */
package fun.kubik.modules.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.block.*;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.List;

public class AutoFarm extends Module {
    
    // Настройки на русском языке
    private final SliderOption радиус = new SliderOption("Радиус", 5.0f, 1.0f, 10.0f).increment(1.0f);
    private final SliderOption задержка = new SliderOption("Задержка", 500.0f, 100.0f, 2000.0f).increment(100.0f);
    
    private final MultiOption растения = new MultiOption("Растения", 
        new MultiOptionValue("Тыква", true),
        new MultiOptionValue("Арбуз", true), 
        new MultiOptionValue("Адский нарост", true),
        new MultiOptionValue("Тростник", true),
        new MultiOptionValue("Морковь", true),
        new MultiOptionValue("Картофель", true),
        new MultiOptionValue("Свёкла", true),
        new MultiOptionValue("Пшеница", true)
    );
    
    private final CheckboxOption подбор_предметов = new CheckboxOption("Подбор предметов", true);
    private final CheckboxOption автопосадка = new CheckboxOption("Автопосадка", true);
    private final CheckboxOption отображение_процентов = new CheckboxOption("Отображение процентов", true);
    private final CheckboxOption только_спелые = new CheckboxOption("Только спелые", true);
    
    // Внутренние переменные
    private long lastActionTime = 0;
    private long lastUpdateTime = 0;
    private final Map<BlockPos, CropInfo> cropStates = new HashMap<>();
    private final List<BlockPos> toHarvest = new ArrayList<>();
    private final Set<BlockPos> harvestedPositions = new HashSet<>(); // Запоминаем собранные позиции
    private final List<ItemEntity> toPickup = new ArrayList<>();
    private int currentTask = 0; // 0-harvest, 1-pickup, 2-replant
    private BlockPos currentTarget = null;
    private boolean isWorking = false;
    
    public AutoFarm() {
        super("AutoFarm", Category.PLAYER);
        this.settings(радиус, задержка, растения, подбор_предметов, автопосадка, отображение_процентов, только_спелые);
    }
    
    @Override
    public void onDisabled() {
        stopMovement(); // Останавливаем движение при отключении
        currentTarget = null;
        isWorking = false;
        super.onDisabled();
    }
    
    @EventHook
    public void update(EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.playerController == null) return;
        
        long currentTime = System.currentTimeMillis();
        
        // Проверка задержки для действий
        if (currentTime - lastActionTime < задержка.getValue()) return;
        
        // Обновляем списки только раз в секунду для уменьшения лагов
        if (currentTime - lastUpdateTime > 1000) {
            updateCropLists();
            updateItemList();
            lastUpdateTime = currentTime;
        }
        
        // Если уже работаем с целью, проверяем достижение
        if (isWorking && currentTarget != null) {
            double distance = mc.player.getDistanceSq(currentTarget.getX(), currentTarget.getY(), currentTarget.getZ());
            double targetRange = (currentTask == 1) ? 2.0 * 2.0 : 4.0 * 4.0; // Меньше для предметов
            if (distance <= targetRange) {
                // Достигли цели, выполняем действие
                stopMovement();
                System.out.println("[AutoFarm] Достигли цели: " + currentTarget + " (задача " + currentTask + ")");
                
                if (currentTask == 0) {
                    harvestCrop(currentTarget);
                    harvestedPositions.add(currentTarget);
                } else if (currentTask == 1) {
                    // Для подбора предметов просто подходим ближе
                    System.out.println("[AutoFarm] Подошли к предмету для автоподбора");
                } else if (currentTask == 2) {
                    replantCrop(currentTarget);
                    harvestedPositions.remove(currentTarget);
                }
                
                currentTarget = null;
                isWorking = false;
                lastActionTime = currentTime;
                
                // Убрана пауза для плавности
            } else {
                // Продолжаем движение к цели
                moveToBlock(currentTarget);
            }
            return;
        }
        
        // Выполняем действия по очереди - только ОДНО действие за раз
        switch (currentTask) {
            case 0: // Сбор урожая
                if (!toHarvest.isEmpty()) {
                    BlockPos target = toHarvest.get(0);
                    toHarvest.remove(0); // Удаляем из списка сразу
                    currentTarget = target;
                    isWorking = true;
                    System.out.println("[AutoFarm] Начинаем сбор: " + target);
                } else {
                    currentTask = 1;
                    currentTarget = null;
                    isWorking = false;
                    System.out.println("[AutoFarm] Переходим к подбору предметов");
                }
                break;
                
            case 1: // Подбор предметов
                if (подбор_предметов.getValue() && !toPickup.isEmpty()) {
                    ItemEntity item = toPickup.get(0);
                    if (item != null && item.isAlive()) {
                        Vector3d itemPos = item.getPositionVec();
                        Vector3d playerPos = mc.player.getPositionVec();
                        double distance = playerPos.distanceTo(itemPos);
                        
                        if (distance <= 1.5) {
                            // Предмет близко, ждём автоподбора или удаляем из списка
                            toPickup.remove(0);
                            System.out.println("[AutoFarm] Предмет подобран автоматически");
                        } else {
                            // Идём к предмету
                            currentTarget = new BlockPos(itemPos.x, itemPos.y, itemPos.z);
                            isWorking = true;
                            System.out.println("[AutoFarm] Идём к предмету: " + item.getItem().getDisplayName().getString() + 
                                " (расстояние: " + String.format("%.1f", distance) + ")");
                        }
                    } else {
                        // Предмет исчез
                        toPickup.remove(0);
                        System.out.println("[AutoFarm] Предмет исчез");
                    }
                } else {
                    currentTask = 2;
                    currentTarget = null;
                    isWorking = false;
                    System.out.println("[AutoFarm] Переходим к посадке");
                }
                break;
                
            case 2: // Посадка
                if (автопосадка.getValue() && !harvestedPositions.isEmpty()) {
                    BlockPos target = harvestedPositions.iterator().next();
                    currentTarget = target;
                    isWorking = true;
                    System.out.println("[AutoFarm] Начинаем посадку: " + target);
                } else {
                    currentTask = 0;
                    currentTarget = null;
                    isWorking = false;
                    System.out.println("[AutoFarm] Возвращаемся к сбору");
                }
                break;
        }
    }
    
    @EventHook
    public void render(EventRender2D.Pre event) {
        if (!отображение_процентов.getValue() || mc.player == null) return;
        
        MatrixStack matrixStack = event.getMatrixStack();
        int y = 100;
        
        // Отображаем информацию о растениях
        for (Map.Entry<BlockPos, CropInfo> entry : cropStates.entrySet()) {
            BlockPos pos = entry.getKey();
            CropInfo info = entry.getValue();
            
            // Проверяем расстояние
            double distance = mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
            if (distance > радиус.getValue() * радиус.getValue()) continue;
            
            String status;
            int color;
            
            if (info.isReady) {
                status = "Готово";
                color = ColorHelpers.rgb(0, 255, 0); // Зелёный
            } else {
                status = "Не готово";
                color = ColorHelpers.rgb(255, 0, 0); // Красный
            }
            
            String text = String.format("%s (%d, %d, %d): %s", 
                info.name, pos.getX(), pos.getY(), pos.getZ(), status);
            
            suisse_intl.drawText(matrixStack, text, 10, y, color, 12.0f);
            y += 15;
        }
        
        // Статистика
        String stats = String.format("Сбор: %d | Подбор: %d | Посадка: %d", 
            toHarvest.size(), toPickup.size(), harvestedPositions.size());
        suisse_intl.drawText(matrixStack, stats, 10, y + 10, ColorHelpers.rgb(255, 255, 255), 12.0f);
        
        // Показываем текущее действие
        String currentAction = "";
        switch (currentTask) {
            case 0: currentAction = "Собираю урожай"; break;
            case 1: currentAction = "Подбираю предметы"; break;
            case 2: currentAction = "Сажаю растения"; break;
        }
        if (isWorking && currentTarget != null) {
            currentAction += String.format(" (%d, %d, %d)", currentTarget.getX(), currentTarget.getY(), currentTarget.getZ());
        }
        suisse_intl.drawText(matrixStack, currentAction, 10, y + 25, ColorHelpers.rgb(0, 255, 255), 11.0f);
    }
    
    private void updateCropLists() {
        if (mc.player == null || mc.world == null) return;
        
        toHarvest.clear();
        cropStates.clear();
        
        BlockPos playerPos = mc.player.getPosition();
        int radius = радиус.getValue().intValue();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    
                    if (isFarmableBlock(block)) {
                        CropInfo info = analyzeCrop(pos, block);
                        cropStates.put(pos, info);
                        
                        if (info.isReady) {
                            toHarvest.add(pos);
                            System.out.println("[AutoFarm] Найден готовый урожай: " + block.getClass().getSimpleName() + 
                                " на " + pos + " (возраст: " + info.age + "/" + info.maxAge + ")");
                        }
                        
                        // Не добавляем в список посадки здесь - будем сажать на собранные позиции
                    }
                }
            }
        }
    }
    
    private void updateItemList() {
        if (mc.player == null || mc.world == null) return;
        
        toPickup.clear();
        
        Vector3d playerPos = mc.player.getPositionVec();
        double searchRadius = радиус.getValue() + 3.0; // Увеличенный радиус поиска
        
        List<ItemEntity> items = mc.world.getEntitiesWithinAABB(ItemEntity.class, 
            mc.player.getBoundingBox().grow(searchRadius));
        
        for (ItemEntity item : items) {
            if (item.isAlive() && item.getItem() != null) {
                // Проверяем расстояние до предмета
                double distance = playerPos.distanceTo(item.getPositionVec());
                if (distance <= searchRadius && isFarmItem(item.getItem().getItem())) {
                    toPickup.add(item);
                    System.out.println("[AutoFarm] Найден предмет для подбора: " + 
                        item.getItem().getDisplayName().getString() + 
                        " на расстоянии " + String.format("%.1f", distance));
                }
            }
        }
        
        System.out.println("[AutoFarm] Найдено предметов для подбора: " + toPickup.size());
    }
    
    private boolean isFarmableBlock(Block block) {
        if (block instanceof CropsBlock) {
            String name = block.getTranslationKey();
            return (name.contains("wheat") && растения.getSelected("Пшеница")) ||
                   (name.contains("carrot") && растения.getSelected("Морковь")) ||
                   (name.contains("potato") && растения.getSelected("Картофель")) ||
                   (name.contains("beetroot") && растения.getSelected("Свёкла"));
        }
        
        if (block instanceof StemBlock || block instanceof AttachedStemBlock) {
            String name = block.getTranslationKey();
            return (name.contains("pumpkin") && растения.getSelected("Тыква")) ||
                   (name.contains("melon") && растения.getSelected("Арбуз"));
        }
        
        if (block instanceof SugarCaneBlock && растения.getSelected("Тростник")) {
            return true;
        }
        
        if (block instanceof NetherWartBlock && растения.getSelected("Адский нарост")) {
            return true;
        }
        
        // Проверяем блоки плодов
        return (block instanceof PumpkinBlock && растения.getSelected("Тыква")) ||
               (block instanceof MelonBlock && растения.getSelected("Арбуз"));
    }
    
    private CropInfo analyzeCrop(BlockPos pos, Block block) {
        CropInfo info = new CropInfo();
        info.pos = pos;
        info.block = block;
        
        if (block instanceof CropsBlock) {
            CropsBlock crop = (CropsBlock) block;
            int age = mc.world.getBlockState(pos).get(crop.getAgeProperty());
            int maxAge = crop.getMaxAge();
            
            info.name = getBlockDisplayName(block);
            info.age = age;
            info.maxAge = maxAge;
            info.isReady = age >= maxAge;
            info.needsReplanting = false;
            
        } else if (block instanceof SugarCaneBlock) {
            info.name = "Тростник";
            info.isReady = isSugarCaneReady(pos);
            info.needsReplanting = false;
            
        } else if (block instanceof NetherWartBlock) {
            int age = mc.world.getBlockState(pos).get(NetherWartBlock.AGE);
            info.name = "Адский нарост";
            info.age = age;
            info.maxAge = 3;
            info.isReady = age >= 3;
            info.needsReplanting = false;
            
        } else if (block instanceof PumpkinBlock || block instanceof MelonBlock) {
            info.name = block instanceof PumpkinBlock ? "Тыква" : "Арбуз";
            info.isReady = true; // Плоды всегда готовы к сбору
            info.needsReplanting = false;
        }
        
        return info;
    }
    
    private boolean isSugarCaneReady(BlockPos pos) {
        if (mc.world == null) return false;
        
        // Проверяем высоту тростника
        int height = 0;
        BlockPos checkPos = pos;
        
        // Находим основание
        while (mc.world.getBlockState(checkPos.down()).getBlock() instanceof SugarCaneBlock) {
            checkPos = checkPos.down();
        }
        
        // Считаем высоту
        while (mc.world.getBlockState(checkPos.up(height)).getBlock() instanceof SugarCaneBlock) {
            height++;
        }
        
        return height >= 3; // Готов если высота 3+ блока
    }
    
    private void harvestCrop(BlockPos pos) {
        if (mc.world == null || mc.playerController == null) return;
        
        Block block = mc.world.getBlockState(pos).getBlock();
        
        // Быстрый поворот к блоку
        lookAt(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        
        if (block instanceof SugarCaneBlock) {
            // Для тростника собираем только верхушку
            harvestSugarCane(pos);
        } else {
            // Обычный сбор
            harvestBlock(pos);
        }
        
        System.out.println("[AutoFarm] Собрано: " + block.getClass().getSimpleName() + " на " + pos);
    }
    
    private void harvestSugarCane(BlockPos pos) {
        if (mc.world == null) return;
        
        // Находим верхушку тростника
        BlockPos topPos = pos;
        while (mc.world.getBlockState(topPos.up()).getBlock() instanceof SugarCaneBlock) {
            topPos = topPos.up();
        }
        
        // Собираем только верхние блоки, оставляя основание
        BlockPos basePos = pos;
        while (mc.world.getBlockState(basePos.down()).getBlock() instanceof SugarCaneBlock) {
            basePos = basePos.down();
        }
        
        // Оставляем один блок у основания
        BlockPos harvestPos = basePos.up();
        while (harvestPos.getY() <= topPos.getY()) {
            if (harvestPos.getY() > basePos.getY()) { // Не ломаем базовый блок
                harvestBlock(harvestPos);
            }
            harvestPos = harvestPos.up();
        }
    }
    
    private void harvestBlock(BlockPos pos) {
        if (mc.player == null || mc.playerController == null) return;
        
        // Поворачиваемся к блоку
        Vector3d blockCenter = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        
        // Проверяем можем ли достать
        if (eyePos.distanceTo(blockCenter) > mc.playerController.getBlockReachDistance()) {
            return;
        }
        
        // Ломаем блок
        mc.playerController.clickBlock(pos, Direction.UP);
    }
    
    // Методы для движения
    private boolean isInRange(BlockPos pos, double range) {
        if (mc.player == null) return false;
        return mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) <= range * range;
    }
    
    private void moveToBlock(BlockPos pos) {
        if (mc.player == null) return;
        Vector3d target = new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        moveToPosition(target);
    }
    
    private void moveToPosition(Vector3d target) {
        if (mc.player == null) return;
        
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d direction = target.subtract(playerPos);
        double distance = direction.length();
        
        // Если уже близко к цели, не двигаемся
        if (distance < 1.5) {
            stopMovement();
            return;
        }
        
        direction = direction.normalize();
        
        // Более мягкая скорость для плавного движения
        double speed = Math.min(0.1, distance * 0.05); // Уменьшенная скорость
        
        // Устанавливаем движение напрямую, но плавнее
        Vector3d currentMotion = mc.player.getMotion();
        Vector3d targetMotion = new Vector3d(direction.x * speed, currentMotion.y, direction.z * speed);
        
        // Плавная интерполяция движения
        Vector3d smoothMotion = currentMotion.scale(0.7).add(targetMotion.scale(0.3));
        mc.player.setMotion(smoothMotion.x, currentMotion.y, smoothMotion.z);
        
        // Поворачиваемся к цели
        lookAt(target);
    }
    
    private void lookAt(Vector3d target) {
        if (mc.player == null) return;
        
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d direction = target.subtract(eyePos);
        
        double distance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) (Math.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(direction.y, distance) * 180.0 / Math.PI));
        
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }
    
    private void stopMovement() {
        if (mc.player == null) return;
        
        // Останавливаем движение через motion
        mc.player.setMotion(0, mc.player.getMotion().y, 0);
    }
    
    private void replantCrop(BlockPos pos) {
        if (mc.player == null || mc.playerController == null || mc.world == null) return;
        
        // Проверяем что блок пустой (воздух)
        Block currentBlock = mc.world.getBlockState(pos).getBlock();
        if (!(currentBlock instanceof AirBlock)) {
            return;
        }
        
        // Проверяем что под блоком подходящая почва
        Block belowBlock = mc.world.getBlockState(pos.down()).getBlock();
        Item seedItem = null;
        
        if (belowBlock instanceof FarmlandBlock) {
            // Пробуем найти подходящие семена для грядки
            seedItem = findBestSeedForFarmland();
        } else if (belowBlock == Blocks.SOUL_SAND) {
            seedItem = Items.NETHER_WART;
        } else {
            return; // Неподходящая почва
        }
        
        if (seedItem == null) return;
        
        // Ищем семена в хотбаре
        int seedSlot = findItemInHotbar(seedItem);
        
        if (seedSlot == -1) {
            // Ищем в инвентаре и перемещаем
            seedSlot = moveItemToHotbar(seedItem);
        }
        
        if (seedSlot == -1) {
            System.out.println("[AutoFarm] Нет семян для посадки: " + seedItem.toString());
            return; // Нет подходящих семян
        }
        
        // Переключаемся на семена
        int oldSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = seedSlot;
        
        // Поворачиваемся к блоку
        lookAt(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        
        // Убрана задержка для плавности
        
        // Сажаем - кликаем ПКМ по грядке
        mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, 
            new BlockRayTraceResult(
                new Vector3d(pos.getX() + 0.5, pos.getY() - 0.1, pos.getZ() + 0.5),
                Direction.UP, pos.down(), false
            )
        );
        
        System.out.println("[AutoFarm] Посажено: " + seedItem.toString() + " на " + pos);
        
        // Возвращаем старый слот
        mc.player.inventory.currentItem = oldSlot;
    }
    
    private Item findBestSeedForFarmland() {
        if (mc.player == null) return null;
        
        // Приоритет семян для грядки
        Item[] seeds = {
            Items.WHEAT_SEEDS,
            Items.CARROT,
            Items.POTATO,
            Items.BEETROOT_SEEDS,
            Items.PUMPKIN_SEEDS,
            Items.MELON_SEEDS
        };
        
        // Ищем первые доступные семена
        for (Item seed : seeds) {
            if (findItemInHotbar(seed) != -1 || hasItemInInventory(seed)) {
                return seed;
            }
        }
        
        return null;
    }
    
    private boolean hasItemInInventory(Item item) {
        if (mc.player == null) return false;
        
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private int findItemInHotbar(Item item) {
        if (mc.player == null) return -1;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item && !stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }
    
    private int moveItemToHotbar(Item item) {
        if (mc.player == null || mc.playerController == null) return -1;
        
        // Ищем предмет в инвентаре
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item && !stack.isEmpty()) {
                // Ищем свободный слот в хотбаре
                for (int j = 0; j < 9; j++) {
                    if (mc.player.inventory.getStackInSlot(j).isEmpty()) {
                        // Простое перемещение через Shift+Click
                        mc.playerController.windowClick(0, i, 0, ClickType.QUICK_MOVE, mc.player);
                        return j;
                    }
                }
            }
        }
        return -1;
    }
    
    private Item getSeedForSoil(Block soilBlock) {
        if (soilBlock instanceof FarmlandBlock) {
            // Для грядок возвращаем семена пшеницы по умолчанию
            // Можно расширить логику для других семян
            return Items.WHEAT_SEEDS;
        } else if (soilBlock == Blocks.SOUL_SAND) {
            return Items.NETHER_WART;
        }
        return null;
    }
    
    private ItemStack findSuitableSeeds(BlockPos pos) {
        if (mc.world == null || mc.player == null) return ItemStack.EMPTY;
        
        Block block = mc.world.getBlockState(pos.down()).getBlock();
        
        // Определяем какие семена нужны
        Item seedItem = null;
        if (block == Blocks.FARMLAND) {
            // Для грядок нужны семена растений
            seedItem = Items.WHEAT_SEEDS; // По умолчанию пшеница
        } else if (block == Blocks.SOUL_SAND) {
            seedItem = Items.NETHER_WART;
        }
        
        if (seedItem == null) return ItemStack.EMPTY;
        
        // Ищем в инвентаре
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == seedItem) {
                return stack;
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    private boolean isFarmItem(Item item) {
        return item == Items.WHEAT || item == Items.WHEAT_SEEDS ||
               item == Items.CARROT || item == Items.POTATO ||
               item == Items.BEETROOT || item == Items.BEETROOT_SEEDS ||
               item == Items.PUMPKIN || item == Items.PUMPKIN_SEEDS ||
               item == Items.MELON_SLICE || item == Items.MELON_SEEDS ||
               item == Items.SUGAR_CANE || 
               item == Items.NETHER_WART;
    }
    
    private String getBlockDisplayName(Block block) {
        String name = block.getTranslationKey();
        if (name.contains("wheat")) return "Пшеница";
        if (name.contains("carrot")) return "Морковь";
        if (name.contains("potato")) return "Картофель";
        if (name.contains("beetroot")) return "Свёкла";
        if (name.contains("pumpkin")) return "Тыква";
        if (name.contains("melon")) return "Арбуз";
        return name;
    }
    
    // Класс для хранения информации о растении
    private static class CropInfo {
        BlockPos pos;
        Block block;
        String name;
        int age;
        int maxAge;
        boolean isReady;
        boolean needsReplanting;
    }
}
