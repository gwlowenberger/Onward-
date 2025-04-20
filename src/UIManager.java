import java.awt.*;
import java.awt.image.BufferedImage;

public final class UIManager {
    //decor
    private static AnimatedActor background;
    private static final AnimatedActor[] clouds = new AnimatedActor[40];
    private static final Vector CLOUD_X_THRESHHOLD = new Vector(MyPanel.width * -0.2f, MyPanel.width * 1.2f);
    /*the animations for transition act differently than others:
     *Idle is a null animation, with no data, so the actor isnt shown when idling.
     *All other animations are temporary transitions, which take advantage of the
     *  fact that each animation returns to idle after it is finished.
    */
    private static AnimatedActor transition;
    private static StillActor egoText;
    private static AnimatedActor soundButton;

    //Game
    private static Menu ecoTips;
    public static Vector defaultMenuPosition;
    public static Vector startMenuPosition;
    
    //menu buttons
    private static UIMovingButtonActor startButton;
    private static UIButtonActor exitButton;
    private static UIButtonActor[] levelButtons;
    private static StillActor levelButtonBackdrop;

    //testing
    private static final boolean SHOW_COLLIDERS = false;
    private static StillActor axisMarkers;
    
    //constructors
    public UIManager() {
        //Backgrounds & Transitions
        {
            Vector screen = new Vector(MyPanel.width, MyPanel.height);

            background = new AnimatedActor(Vector.zero, Vector.zero, Vector.zero, screen,
                new Animation("Sprites/Backgrounds/TiledBack.png", screen, 70));
            
            for (int i = 0; i < clouds.length; i++) {
                clouds[i] = NewCloud();
                clouds[i].position = new Vector((float) (Math.random() * MyPanel.width), clouds[i].position.y);
            }

            transition = new AnimatedActor(Vector.zero, Vector.zero, Vector.zero, screen, 
                new Animation[] {new Animation(), new Animation("Sprites/Backgrounds/GrassLoadingAnim.png", Vector.Multiply(screen, 0.5f), 8),
                new Animation()});
        }

        //Basic GUI (Endless, Exit, etc.)
        {
            Vector rect = new Vector(112, 32);
            startButton = new UIMovingButtonActor(new Vector(MyPanel.center.x - rect.x / 2, (MyPanel.center.y - rect.y) / 2), rect, Vector.zero, rect, new Animation[] {
                new MobileAnimation("Sprites/UI/Endless.png", rect, 40,
                new Vector[] {Vector.north, Vector.north, Vector.south, Vector.south, Vector.south, Vector.south, Vector.north, Vector.north,} ) },
                MyPanel.STATE_LOAD_ENDLESS);

            exitButton = new UIButtonActor(Vector.Multiply(Vector.southEast, 4), new Vector(64, 32), "Sprites/UI/ExitImage.png", MyPanel.STATE_EXIT);

            soundButton = new AnimatedActor(new Vector(4, 40), Vector.defaultSpriteDimensions, new Animation[] {
                new Animation("Sprites/UI/SoundOn.png", Vector.defaultSpriteDimensions, 10, false),
                new Animation("Sprites/UI/SoundOff.png", Vector.defaultSpriteDimensions, 10, false),
            });
        }

        //Game GUI
        {
            Vector panelSize = new Vector(142, 84);
            defaultMenuPosition = new Vector((MyPanel.width - panelSize.x) * 0.5f, (MyPanel.height) * 0.6f);
            setEcoText("Blurbs/Sample", defaultMenuPosition);

            startMenuPosition = new Vector(MyPanel.center.x - panelSize.x / 2, 40);
        }

        //level buttons
        {
            Vector center = new Vector(MyPanel.center.x - 16, MyPanel.center.y - 16);
            //Vector center = new Vector(64, MyPanel.center.y); //for side buttons
            levelButtons = new UIButtonActor[9];
            levelButtons[0] = new UIButtonActor(Vector.Subtract(center, new Vector(40, 0)), Vector.defaultSpriteDimensions, 
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_ONE, new Vector(0, 0), new Vector(32, 32));
            levelButtons[1] = new UIButtonActor(Vector.Subtract(center, new Vector(0, 0)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_TWO, new Vector(32, 0), new Vector(32, 32));
            levelButtons[2] = new UIButtonActor(Vector.Subtract(center, new Vector(-40, 0)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_THREE, new Vector(64, 0), new Vector(32, 32));
            levelButtons[3] = new UIButtonActor(Vector.Subtract(center, new Vector(40, -40)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_FOUR, new Vector(0, 32), new Vector(32, 32));
            levelButtons[4] = new UIButtonActor(Vector.Subtract(center, new Vector(0, -40)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_FIVE, new Vector(32, 32), new Vector(32, 32));
            levelButtons[5] = new UIButtonActor(Vector.Subtract(center, new Vector(-40, -40)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_SIX, new Vector(64, 32), new Vector(32, 32));
            levelButtons[6] = new UIButtonActor(Vector.Subtract(center, new Vector(40, -80)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_SEVEN, new Vector(0, 64), new Vector(32, 32));
            levelButtons[7] = new UIButtonActor(Vector.Subtract(center, new Vector(0, -80)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_EIGHT, new Vector(32, 64), new Vector(32, 32));
            levelButtons[8] = new UIButtonActor(Vector.Subtract(center, new Vector(-40, -80)), Vector.defaultSpriteDimensions,
                "Sprites/UI/LevelNumbers.png", MyPanel.STATE_LEVEL_NINE, new Vector(64, 64), new Vector(32, 32));
            
            levelButtonBackdrop = new StillActor(Vector.Subtract(levelButtons[0].position, 16), Vector.zero, Vector.zero,
                new Vector(144, 144), "Sprites/UI/LevelNumbersBackdrop.png");
        }
        
        //Testing
        {
            axisMarkers = new StillActor(new Vector(MyPanel.center.x - (Vector.defaultSpriteDimensions.x / 2), 0),
                Vector.defaultSpriteDimensions, "Sprites/Testing/AxisMarkers.png");
        }

        //Accredidation
        {
            BufferedImage im = Reader.getTextUnlimited("Blurbs/Credits");
            egoText = new StillActor(new Vector(230, 5), Vector.Multiply(new Vector(im.getWidth(), im.getHeight()), 0.5f), im);
            //egoText = new StillActor(new Vector(10, MyPanel.height - im.getHeight() - 20), new Vector(im.getWidth(), im.getHeight()), im);
        }
    }

    //draw UI
    public static void DrawBackground(Graphics2D g) {
        background.draw(g);
    }

    public static void drawStartButton(Graphics2D g) {
        startButton.draw(g);
    }

    public static void updateUI() {
        background.update();
        if (MyPanel.gameState == MyPanel.STATE_START) {
            startButton.update();
        }
        for (AnimatedActor c : clouds) {
            c.update();
        }
        ecoTips.update();
        transition.update();
    }

    public static void DrawExitButton(Graphics2D g) {
        exitButton.Draw(g, false);
    }

    public static void DrawAxisMarkers(Graphics2D g) {
        axisMarkers.Draw(g, SHOW_COLLIDERS);
    }

    public static void drawLevelButtons(Graphics2D g) {
        levelButtonBackdrop.Draw(g, SHOW_COLLIDERS);
        for (UIButtonActor uiButtonActor : levelButtons) {
            uiButtonActor.Draw(g, SHOW_COLLIDERS);
        }
    }

    public static void DrawBackgroundEffects(Graphics2D g) {
        //Clouds, sun, etc.. Whatever moves in the background.
        
        for (int i = 0; i < clouds.length; i++) {
            clouds[i].draw(g);

            if (!CLOUD_X_THRESHHOLD.WithinBounds(clouds[i].position.x)) {
                clouds[i] = NewCloud();
            }
        }
    }

    public static void drawTransition(Graphics2D g) {
        transition.draw(g);
    }

    public static void TriggerTransition(int num) {
        if (num == 0 || num == 2) {
            return;
        }
        transition.ChangeAnimation(num);
    }

    public static void drawEcoTips(Graphics2D g) {
        ecoTips.draw(g);
    }

    public static void drawAccredidation(Graphics2D g) {
        egoText.Draw(g, SHOW_COLLIDERS);
    }

    public static void drawSoundButton(Graphics2D g) {
        soundButton.draw(g);
    }

    //Clouds
    private static AnimatedActor NewCloud() {
        float y = (float) Math.random() * 150 + 10;
        float speed = (float) (Math.random() * 0.3) + 0.5f;
        String path = "Sprites/Backgrounds/Cloud";
        path += (int) (Math.random() * 9); //clouds 0-8
        path += ".png";

        return new AnimatedActor(new Vector(-Vector.defaultSpriteDimensions.x, y), Vector.defaultSpriteDimensions,
                new Animation[] {new MobileAnimation(path, Vector.defaultSpriteDimensions, new Vector[] {new Vector(speed, 0)}),
                                 new MobileAnimation(path, Vector.defaultSpriteDimensions, new Vector[] {new Vector(-speed * 30, 0)})});

        /*
        if (Math.random() >= 0) {
            //left side
            return new AnimatedActor(new Vector(-Vector.defaultSpriteDimensions.x, y), Vector.defaultSpriteDimensions,
                new Animation[] {new MobileAnimation(path, Vector.defaultSpriteDimensions, new Vector[] {new Vector(speed, 0)}),
                                 new MobileAnimation(path, Vector.defaultSpriteDimensions, new Vector[] {new Vector(speed, 0)})});
        }
        else
        {
            //right side
            return new AnimatedActor(new Vector(MyPanel.width, y), Vector.defaultSpriteDimensions,
                new MobileAnimation(path, Vector.defaultSpriteDimensions, new Vector[] {new Vector(-speed, 0)}));
        }
        */
    }

    //Text
    //if path is an empty string, it does not make a new menu, just sets
    //it to dissappear by forcing the animation to 0.
    public static void setEcoText(String path, Vector pos) {
        if (path.isEmpty()) {
            ecoTips.hide();
            return;
        }
        setEcoText(new String[] {path}, pos);
    }

    public static void setEcoText(String[] paths, Vector pos) {
        setEcoText(paths, pos, MyPanel.STATE_NONE);
    }

    public static void setEcoText(String[] paths, Vector pos, int state) {
        Vector panelSize = new Vector(142, 84);
        if (pos == null) {
            pos = defaultMenuPosition; // pos = ecoTips.position;
        }
        ecoTips = new Menu(new AnimatedActor(pos, new Vector(16, 16), Vector.zero, panelSize,
                new Animation[] {new Animation(), new Animation(),
                new Animation("Sprites/UI/Panel.png", panelSize, 10, false)}), 
                state, paths);
    }

    //clicking/hovering detection
    public static boolean UIElementClicked() {
        if (exitButton.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
            exitButton.OnClick();
            return true;
        }
        if (soundButton.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
            soundButton.ChangeAnimation(SoundManager.toggleSound());
            return true;
        }
        if (ecoTips.getCurrentAnimation() > 1 && ecoTips.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
            ecoTips.OnClick();
            SoundManager.uiClicked();
            return true;
        }
        if (MyPanel.gameState == MyPanel.STATE_START) {
            for (UIButtonActor uiButtonActor : levelButtons) {
                if (uiButtonActor.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                    uiButtonActor.OnClick();
                    SoundManager.uiClicked();
                    return true;
                }
            }
        }
        if (MyPanel.gameState == MyPanel.STATE_START && startButton.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
            startButton.OnClick();
            SoundManager.uiClicked();
            return true;
        }

        return false;
    }

    public static boolean BackgroundElementClicked() {
        for(AnimatedActor a : clouds) {
            if (a.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                a.OnClick();
                SoundManager.cloudClicked();
                return true;
            }
        }
        return false;
    }

    public static boolean UIElementHovered() {
        return false;
    }
}