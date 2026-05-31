package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

public class BuildingFactory {

    public Entity createBuildingEntity(String pngName, double worldX, double worldY,
                                        String interactLabel) {
        Texture tex = FXGL.texture("buildings/" + pngName);

        double scale = 2.0;
        if (pngName.equalsIgnoreCase("Futtier.png")) scale = 1.5;
        if (pngName.equalsIgnoreCase("Gebaude.png")) scale = 1.0;

        tex.setScaleX(scale);
        tex.setScaleY(scale);

        double w = tex.getImage().getWidth()  * scale;
        double h = tex.getImage().getHeight() * scale;

        return FXGL.entityBuilder()
                .at(worldX, worldY)
                .type(EntityType.BUILDING)
                .view(tex)
                .bbox(new HitBox(
                        new Point2D(10, 10),
                        BoundingShape.box(w - 20, h - 10)))
                .with(new BuildingInteractComponent(interactLabel))
                .zIndex(50)
                .buildAndAttach();
    }
}