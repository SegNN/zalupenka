package fun.kubik.itemics.api.command.helpers;

import fun.kubik.itemics.api.ItemicsAPI;
import fun.kubik.itemics.api.Settings;
import fun.kubik.itemics.api.command.manager.ICommandManager;
import fun.kubik.itemics.api.utils.SettingsUtil;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TabCompleteHelper {
    private Stream<String> stream;

    public TabCompleteHelper(String[] base) {
        this.stream = Stream.of(base);
    }

    public TabCompleteHelper(List<String> base) {
        this.stream = base.stream();
    }

    public TabCompleteHelper() {
        this.stream = Stream.empty();
    }

    public TabCompleteHelper append(Stream<String> source) {
        this.stream = Stream.concat(this.stream, source);
        return this;
    }

    public TabCompleteHelper append(String... source) {
        return this.append(Stream.of(source));
    }

    public TabCompleteHelper append(Class<? extends Enum<?>> enumClass) {
        return this.append(Stream.of(enumClass.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase));
    }

    public TabCompleteHelper prepend(Stream<String> source) {
        this.stream = Stream.concat(source, this.stream);
        return this;
    }

    public TabCompleteHelper prepend(String... source) {
        return this.prepend(Stream.of(source));
    }

    public TabCompleteHelper prepend(Class<? extends Enum<?>> enumClass) {
        return this.prepend(Stream.of(enumClass.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase));
    }

    public TabCompleteHelper map(Function<String, String> transform) {
        this.stream = this.stream.map(transform);
        return this;
    }

    public TabCompleteHelper filter(Predicate<String> filter) {
        this.stream = this.stream.filter(filter);
        return this;
    }

    public TabCompleteHelper sort(Comparator<String> comparator) {
        this.stream = this.stream.sorted(comparator);
        return this;
    }

    public TabCompleteHelper sortAlphabetically() {
        return this.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public TabCompleteHelper filterPrefix(String prefix) {
        return this.filter(x -> x.toLowerCase(Locale.US).startsWith(prefix.toLowerCase(Locale.US)));
    }

    public TabCompleteHelper filterPrefixNamespaced(String prefix) {
        return this.filterPrefix(new ResourceLocation(prefix).toString());
    }

    public String[] build() {
        return this.stream.toArray(String[]::new);
    }

    public Stream<String> stream() {
        return this.stream;
    }

    public TabCompleteHelper addCommands(ICommandManager manager) {
        return this.append(manager.getRegistry().descendingStream()
                .flatMap(command -> command.getNames().stream())
                .distinct());
    }

    public TabCompleteHelper addSettings() {
        return this.append(ItemicsAPI.getSettings().allSettings.stream()
                .filter(s -> !SettingsUtil.javaOnlySetting(s))
                .map(Settings.Setting::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER));
    }

    public TabCompleteHelper addModifiedSettings() {
        return this.append(SettingsUtil.modifiedSettings(ItemicsAPI.getSettings()).stream()
                .map(Settings.Setting::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER));
    }

    public TabCompleteHelper addToggleableSettings() {
        return this.append(ItemicsAPI.getSettings().getAllValuesByType(Boolean.class).stream()
                .map(Settings.Setting::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER));
    }
}