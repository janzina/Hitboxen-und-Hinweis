package bta.ahaus.lamaDrama.view.ui;

import bta.ahaus.lamaDrama.model.entity.EntityType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public void loadMap(String path, int tileSize) {

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));

            List<int[][]> layers = new ArrayList<>();

            int currentLayer = -1;
            List<int[]> temp = new ArrayList<>();

            for (String line : lines) {

                if (line.startsWith("#LAYER")) {
                    if (!temp.isEmpty()) {
                        layers.add(temp.stream().toArray(int[][]::new));
                        temp.clear();
                    }
                    currentLayer++;
                    continue;
                }

                if (line.isBlank()) continue;

                String[] parts = line.trim().split(" ");
                int[] row = new int[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    row[i] = Integer.parseInt(parts[i]);
                }

                temp.add(row);
            }

            if (!temp.isEmpty()) {
                layers.add(temp.stream().toArray(int[][]::new));
            }

            int[][] layer0 = layers.get(0); // Wasser
            int[][] layer1 = layers.get(1); // Inseln

            int height = layer0.length;
            int width = layer0[0].length;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    boolean isWater = layer0[y][x] == 1;
                    boolean isIsland = layer1[y][x] != -1;

                    if (isWater && !isIsland) {

                        FXGL.entityBuilder()
                                .type(EntityType.WATER)
                                .at(x * tileSize, y * tileSize)
                                .bbox(new HitBox(BoundingShape.box(tileSize, tileSize)))
                                .with(new CollidableComponent(true))
                                .zIndex(1)
                                .buildAndAttach();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}