package jp.worldjourney.gachafx;

import java.util.*;

/** Pure data library. No dependency on World Journey. Stable preset IDs for integrations. */
public final class GachaFxLibrary {
    private GachaFxLibrary() {}
    public record Theme(String id, String title, int base, int accent, int highlight) {}
    public record Preset(String id, String title, String motion, Theme theme, int durationTicks) {}

    public static final List<Theme> THEMES = List.of(
        new Theme("celestial", "星空", 0x13153E, 0x8578FF, 0xFFE69A),
        new Theme("sakura", "桜花", 0x361A35, 0xFF8AC0, 0xFFF2E2),
        new Theme("abyss", "深淵", 0x0A1021, 0x9456FF, 0xF34A9D),
        new Theme("solar", "太陽", 0x321704, 0xFF9E34, 0xFFF7B1),
        new Theme("ocean", "深海", 0x032438, 0x24CDE2, 0xBBFFF5)
    );
    public static final List<String> MOTIONS = List.of(
        "burst", "orbit", "rain", "portal", "shatter", "spiral",
        "pulse", "comet", "constellation", "roulette", "wave", "ascend"
    );
    public static final List<Preset> ALL;
    private static final Map<String, Preset> INDEX;
    static {
        List<Preset> all = new ArrayList<>();
        Map<String, Preset> index = new LinkedHashMap<>();
        for (Theme theme : THEMES) for (String motion : MOTIONS) {
            String id = theme.id() + "_" + motion;
            Preset p = new Preset(id, theme.title() + " / " + motion.toUpperCase(Locale.ROOT), motion, theme, 110);
            all.add(p); index.put(id, p);
        }
        ALL = Collections.unmodifiableList(all);
        INDEX = Collections.unmodifiableMap(index);
    }
    public static Preset get(String id) { return INDEX.get(id); }
    public static Preset random(Random r) { return ALL.get(r.nextInt(ALL.size())); }
    public static Preset byIndex(int i) { return ALL.get(Math.floorMod(i, ALL.size())); }
}
