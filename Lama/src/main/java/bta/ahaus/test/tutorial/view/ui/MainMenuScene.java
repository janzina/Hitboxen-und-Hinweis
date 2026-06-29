package bta.ahaus.lamaDrama.view.ui;

import com.almasb.fxgl.app.scene.FXGLDefaultMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MainMenuScene extends FXGLDefaultMenu {

    private static final String FARBE_HIMMEL_OBEN  = "#87CEEB";
    private static final String FARBE_HIMMEL_UNTEN = "#FFF176";
    private static final String FARBE_GRAS         = "#66BB6A";
    private static final String FARBE_TITEL        = "#FF6F00";

    public MainMenuScene() {
        super(MenuType.MAIN_MENU);
    }

    // onCreate() wird aufgerufen wenn die Scene zum ersten Mal angezeigt wird.
    // Hier bauen wir alles – statt createMenuBody()
    @Override
    public void onCreate() {

        // ── Hintergrund ───────────────────────────────────────────────
        StackPane root = new StackPane();
        root.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        LinearGradient himmel = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web(FARBE_HIMMEL_OBEN)),
            new Stop(1.0, Color.web(FARBE_HIMMEL_UNTEN))
        );
        Rectangle hintergrund = new Rectangle(
            FXGL.getAppWidth(), FXGL.getAppHeight(), himmel
        );

        Rectangle gras = new Rectangle(FXGL.getAppWidth(), 120);
        gras.setFill(Color.web(FARBE_GRAS));
        StackPane.setAlignment(gras, Pos.BOTTOM_CENTER);

        javafx.scene.shape.Circle sonne = new javafx.scene.shape.Circle(50);
        sonne.setFill(Color.web("#FFD600"));
        sonne.setEffect(new DropShadow(30, Color.web("#FF6F00")));
        StackPane.setAlignment(sonne, Pos.TOP_RIGHT);
        sonne.setTranslateX(-60);
        sonne.setTranslateY(60);

        

        // ── Titel ─────────────────────────────────────────────────────
        Text titel = new Text("LamaDrama");
        titel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 72));
        titel.setFill(Color.web(FARBE_TITEL));

        DropShadow titelSchatten = new DropShadow();
        titelSchatten.setColor(Color.web("#BF360C"));
        titelSchatten.setRadius(10);
        titelSchatten.setOffsetY(4);
        titel.setEffect(titelSchatten);

        Text untertitel = new Text("Kümmere dich um dein Lama! 🌿");
        untertitel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        untertitel.setFill(Color.web("#33691E"));

        // ── Buttons ───────────────────────────────────────────────────
        Button btnSpielen = erstelleButton("🎮  Spielen", "#FF6F00", "#E65100");
        Button btnLaden   = erstelleButton("📂  Laden",   "#29B6F6", "#0277BD");
        Button btnBeenden = erstelleButton("❌  Beenden", "#EF5350", "#B71C1C");

        btnSpielen.setOnAction(e -> fireNewGame());
        btnLaden.setOnAction(e -> new SaveSlotMenu(SaveSlotMenu.Modus.LADEN).show());
        btnBeenden.setOnAction(e -> fireExit());

        // ── Layout ────────────────────────────────────────────────────
        VBox mitte = new VBox(20, titel, untertitel, btnSpielen, btnLaden, btnBeenden);
        mitte.setAlignment(Pos.CENTER);

        root.getChildren().addAll(hintergrund, gras, sonne, mitte);

        // getContentRoot() = der "Zeichenbereich" der Scene in FXGL
        // Wir hängen unseren root dort rein
        getContentRoot().getChildren().add(root);
    }

    // ── Hilfsmethode: Button erstellen ────────────────────────────────
    private Button erstelleButton(String text, String farbe, String hoverFarbe) {
        Button btn = new Button(text);
        btn.setPrefWidth(260);
        btn.setPrefHeight(55);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        String styleNormal = String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 30;" +
            "-fx-cursor: hand;", farbe
        );
        String styleHover = String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 30;" +
            "-fx-cursor: hand;", hoverFarbe
        );

        btn.setStyle(styleNormal);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(styleHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(styleNormal);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return btn;
    }
}