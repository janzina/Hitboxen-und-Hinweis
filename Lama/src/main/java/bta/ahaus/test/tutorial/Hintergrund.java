package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Hintergrund {
    private final int originalTileSize = 16;
    private final int scale = 4;
    private final int tileSize = originalTileSize * scale;

    private boolean[][] collisionMap;
    private int mapRows;
    private int mapCols;

    // NEU – Gebäude-Blockierung
    private final List<Rectangle2D> blockedAreas = new ArrayList<>();

    public void addBlockedArea(double x, double y, double w, double h) {
        blockedAreas.add(new Rectangle2D(x, y, w, h));
    }

    public void loadMapWithLayers() {
        List<String> lines = FXGL.getAssetLoader().loadText("levels/map.txt");

        List<List<String>> layerLines = new ArrayList<>();
        layerLines.add(new ArrayList<>());
        layerLines.add(new ArrayList<>());
        layerLines.add(new ArrayList<>());

        int currentLayer = -1;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("#LAYER0")) { currentLayer = 0; continue; }
            if (line.startsWith("#LAYER1")) { currentLayer = 1; continue; }
            if (line.startsWith("#LAYER2")) { currentLayer = 2; continue; }
            if (currentLayer != -1) {
                layerLines.get(currentLayer).add(line);
            }
        }

        buildCollisionMap(layerLines.get(1), layerLines.get(2));

        for (int layer = 0; layer < 3; layer++) {
            List<String> l = layerLines.get(layer);
            for (int row = 0; row < l.size(); row++) {
                String[] tokens = l.get(row).split("\\s+");
                for (int col = 0; col < tokens.length; col++) {
                    int id = Integer.parseInt(tokens[col]);
                    if (id == -1) continue;
                    Texture tex = FXGL.texture("tile_%04d.png".formatted(id));
                    tex.setFitWidth(tileSize);
                    tex.setFitHeight(tileSize);
                    FXGL.entityBuilder()
                            .at(col * tileSize, row * tileSize)
                            .view(tex)
                            .zIndex(layer)
                            .buildAndAttach();
                }
            }
        }
    }

    private void buildCollisionMap(List<String> layer1Lines, List<String> layer2Lines) {
        mapRows = layer1Lines.size();
        mapCols = layer1Lines.stream()
                .mapToInt(line -> line.trim().split("\\s+").length)
                .max().orElse(0);

        collisionMap = new boolean[mapRows][mapCols];

        for (int row = 0; row < mapRows; row++) {
            String[] tokensL1 = layer1Lines.get(row).trim().split("\\s+");

            String[] tokensL2 = (row < layer2Lines.size())
                    ? layer2Lines.get(row).trim().split("\\s+")
                    : new String[0];

            for (int col = 0; col < mapCols; col++) {
                boolean walkableL1 = col < tokensL1.length
                        && isWalkable(Integer.parseInt(tokensL1[col].trim()));
                boolean walkableL2 = col < tokensL2.length
                        && isWalkable(Integer.parseInt(tokensL2[col].trim()));

                collisionMap[row][col] = walkableL1 || walkableL2;
            }
        }
    }

    private boolean isWalkable(int tileId) {
        return switch (tileId) {
            case 0  -> true;
            case 2  -> true;
            case 3  -> true;
            case 4  -> true;
            case 5  -> true;
            case 6  -> true;
            case 7  -> true;
            case 8  -> true;
            case 9  -> true;
            case 10 -> true;
            case 11 -> true;
            case 12 -> true;
            case 13 -> true;
            case 14 -> true;
            case 15 -> true;
            case 16 -> true;
            case 17 -> true;
            case 18 -> true;
            case 19 -> true;
            case 20 -> true;
            default -> false;
        };
    }

    // ERWEITERT – prüft zuerst Gebäude, dann Tilemap
    public boolean isWalkableAt(double worldX, double worldY) {
        // NEU – Gebäude-Blockierung
        for (Rectangle2D area : blockedAreas) {
            if (area.contains(worldX, worldY)) return false;
        }

        // Unverändert
        int col = (int)(worldX / tileSize);
        int row = (int)(worldY / tileSize);

        if (row < 0 || row >= mapRows || col < 0 || col >= mapCols) {
            return false;
        }
        return collisionMap[row][col];
    }

    public boolean isAreaWalkable(double worldX, double worldY,
                                   double width, double height) {
        return isWalkableAt(worldX,         worldY)
            && isWalkableAt(worldX + width, worldY)
            && isWalkableAt(worldX,         worldY + height)
            && isWalkableAt(worldX + width, worldY + height);
    }

    public int getTileSize() { return tileSize; }
}