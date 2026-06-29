package bta.ahaus.lamaDrama.model.data;

import bta.ahaus.lamaDrama.model.entity.PlantType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

public class SaveSlot {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private int           slotIndex;
    private String        characterName;
    private LocalDateTime saveDate;
    private int           xp;
    private int           coins;
    private int           hunger;
    private int           dirt;
    private final Map<PlantType, Integer> inventoryItems = new EnumMap<>(PlantType.class);

    // ── Konstruktor (neues Spiel) ─────────────────────────────────────────────
    public SaveSlot(int slotIndex, String characterName) {
        this.slotIndex     = slotIndex;
        this.characterName = characterName;
        this.saveDate      = LocalDateTime.now();
        this.xp            = 0;
        this.coins         = 0;
        this.hunger        = 100;
        this.dirt          = 0;
    }

    // ── Getter & Setter ───────────────────────────────────────────────────────
    public int    getSlotIndex()     { return slotIndex; }
    public String getCharacterName() { return characterName; }
    public int    getXp()            { return xp; }
    public int    getCoins()         { return coins; }
    public int    getHunger()        { return hunger; }
    public int    getDreckProzent()          { return dirt; }

    public void setXp(int xp)         { this.xp     = xp; }
    public void setCoins(int coins)   { this.coins  = coins; }
    public void setHunger(int hunger) { this.hunger = hunger; }
    public void setDreckLevel(int dirt)     { this.dirt   = dirt; }

    public Map<PlantType, Integer> getInventoryItems() { return inventoryItems; }

    public int getInventorySize() {
        return inventoryItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    public java.util.List<String> getInventoryPreview(int max) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (Map.Entry<PlantType, Integer> e : inventoryItems.entrySet()) {
            if (e.getValue() <= 0) continue;
            result.add(e.getKey().displayName + " x" + e.getValue());
            if (result.size() >= max) break;
        }
        return result;
    }

    public String getFormattedDate() {
        if (saveDate == null) return "";
        return saveDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm"));
    }

    public void updateSaveDate() { this.saveDate = LocalDateTime.now(); }

    // ── Serialisierung ────────────────────────────────────────────────────────
    // Format: slotIndex|name|datum|xp|coins|hunger|dirt|WHEAT:2,CORN:5,...

    public String serialize() {
        StringBuilder inv = new StringBuilder();
        for (Map.Entry<PlantType, Integer> e : inventoryItems.entrySet()) {
            if (e.getValue() > 0) {
                if (inv.length() > 0) inv.append(",");
                inv.append(e.getKey().name()).append(":").append(e.getValue());
            }
        }
        String date = (saveDate != null ? saveDate : LocalDateTime.now()).format(FMT);
        return slotIndex + "|" + characterName + "|" + date + "|"
             + xp + "|" + coins + "|" + hunger + "|" + dirt + "|" + inv;
    }

    public static SaveSlot deserialize(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        try {
            int           idx    = Integer.parseInt(parts[0].trim());
            String        name   = parts[1].trim();
            LocalDateTime date   = LocalDateTime.parse(parts[2].trim(), FMT);
            int           xp     = Integer.parseInt(parts[3].trim());
            int           coins  = Integer.parseInt(parts[4].trim());

            int    hunger      = 100;
            int    dirt        = 0;
            String inventarStr = "";

            if (parts.length >= 8) {
                // Neues Format mit hunger & dirt
                hunger      = Integer.parseInt(parts[5].trim());
                dirt        = Integer.parseInt(parts[6].trim());
                inventarStr = parts[7];
            } else {
                // Altes Format (6 Felder) – hunger/dirt = 0
                inventarStr = parts[5];
            }

            SaveSlot slot = new SaveSlot(idx, name);
            slot.saveDate = date;
            slot.xp       = xp;
            slot.coins    = coins;
            slot.hunger   = hunger;
            slot.dirt     = dirt;

            if (!inventarStr.isBlank()) {
                for (String entry : inventarStr.split(",")) {
                    String[] kv = entry.split(":");
                    if (kv.length != 2) continue;
                    try {
                        PlantType type  = PlantType.valueOf(kv[0].trim());
                        int       count = Integer.parseInt(kv[1].trim());
                        if (count > 0) slot.inventoryItems.put(type, count);
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            return slot;
        } catch (Exception e) {
            System.err.println("[SaveSlot] Fehler beim Deserialisieren: " + line);
            return null;
        }
    }
}