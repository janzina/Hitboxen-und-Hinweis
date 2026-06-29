package bta.ahaus.lamaDrama.view.ui;

import com.almasb.fxgl.app.scene.FXGLDefaultMenu;
import com.almasb.fxgl.app.scene.SceneFactory;

// SceneFactory = FXGL fragt diese Klasse:
// "Welches Menü soll ich anzeigen?"
public class MenuSceneFactory extends SceneFactory {

    @Override
    public FXGLDefaultMenu newMainMenu() {
        // Wir geben unser eigenes Menü zurück
        return new MainMenuScene();
    }
}