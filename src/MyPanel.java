import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class MyPanel extends JPanel implements KeyListener, MouseMotionListener, MouseListener, Runnable
{
    //Random Game Variables
    public static Vector tilePercentOffsets = new Vector(0.5f, 0.33333f);
    public static float playerSpawnChance = 0.2f; //percentage as a decimal
    public static int maxPlayers = 2;
    public static float enemySpawnChance = 0.2f;
    public static int maxEnemies = 3;
    private static int gridSize = 2;

    private static void newRandomGame() {
        render = false;
        float magnitude = (float) Math.random() * 0.1f + 0.05f;
        tilePercentOffsets = new Vector(0.5f, (int) (Math.random() * 2f) * 0.3333333f + 0.25f);
        playerSpawnChance = (float) Math.random() * 0.2f + magnitude + 0.1f;
        maxPlayers = (int) (Math.random() * 3) + 1;
        enemySpawnChance = (float) Math.random() * 0.3f + magnitude + 0.2f;
        maxEnemies = (int) (Math.random() * 5) + 2;
        gridSize = (int) (Math.random() * 2) + 3;

        new TileManager(gridSize);
        TileManager.randomlyCreateNewPlayers();
        render = true;
    }

    private static void newGame(String path) {
        render = false;
        tilePercentOffsets = new Vector(0.5f, 0.25f);

        new TileManager(Reader.getGrid(path));
        TileManager.createNewPlayers(Reader.getPlayers(path), Reader.getEnemies(path));
        render = true;
    }
    
    /*
    @Deprecated
    public static void NextRound() {
        currentRound++;

        if (currentRound > totalRounds) {
            gameState = STATE_WIN_GAME;
        }
        else
        {
            tilePercentOffsets = new Vector(0.5f, (float) Math.random() * 0.33333333f + 0.3333333f);
            enemySpawnChance = (float) Math.random() * 0.3f + 0.2f;
            maxEnemies = (int) (Math.random() * 5) + 2;
            gridSize = (int) (Math.random() * 4) + 3;

            TileManager.NextRound(gridSize);
        }
    } */

    //#region
    //input variables
    public static int mouseX = 0, mouseY = 0, mouseScreenX = 0, mouseScreenY = 0;

    //functioning variables
    private Thread gameThread;
    private long previousTime = 0;
    private static final long NANO_SECONDS_PER_FRAME = 16_666_667 * 2 / 5; //16_666_667 is 60FPS
    
    //graphics variables
    public static int width, height, scaledWidth, scaledHeight;
    public static Vector center, scaledCenter;
    public final static float PIXEL_SCALE_FACTOR = 3f;
    private static boolean render = true;

    //testing variables
    public final static boolean SHOW_COLLIDERS_ONLY = false;
    private static final boolean SHOW_BOOT_SCENE = true;

    //Start
    @SuppressWarnings("unused")
    public MyPanel()
    {
        if (PIXEL_SCALE_FACTOR == 0) {
            System.out.println("Cannot scale all pixels by 0!!!!!");
            System.exit(0);
        }

        //JPanel Set up options
        setBackground(Color.BLACK);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        //setFocusable(true);
        setOpaque(false);
        setSize(new Dimension(scaledWidth, scaledHeight));

        new UIManager();
        new SoundManager();

        gameThread = new Thread(this);
        gameThread.start();

        if (SHOW_BOOT_SCENE) {
            gameState = STATE_BOOT;
            UIManager.setEcoText(new String[] {"Blurbs/Backstory1", "Blurbs/Backstory2","Blurbs/Backstory3", "Blurbs/Backstory4"}, 
                new Vector(width / 2 - 71, height - 110), STATE_LOAD_START);
        }
        else
        {
            gameState = STATE_LOAD_START;
        }
        

        UIManager.TriggerTransition(1);
    }
    
    //Update
    @Override
    public void run() {

        while (gameThread != null) {

            long currentTime = System.nanoTime();

            if (previousTime + NANO_SECONDS_PER_FRAME <= currentTime) {
                update();
                repaint(0, 0, scaledWidth, scaledHeight);
                previousTime = currentTime;
            }
        }
    }
    //#endregion

    public static int gameState;
    public static final int STATE_NONE = -3; //STATE_NONE lets UI Button Actors not change the game's state upon being clicked
    public static final int STATE_BOOT = -2; //The default: for when the game begins
    public static final int STATE_EXIT = -1; //A state that, when the next update is called, ends the program
    public static final int STATE_START = 0;
    public static final int STATE_IN_ROUND = 1;
    public static final int STATE_LOSE = 2;
    //public static final int STATE_WIN_ROUND = 3;   Deprecated
    public static final int STATE_WIN_GAME = 4;

    /*
     * These game states act as triggers, telling the game
     * to load a new level, and starting a UI transition
    */
    public static final int STATE_LOAD_ENDLESS = 5;
    public static final int STATE_LEVEL_ONE = 6;
    public static final int STATE_LEVEL_TWO = 7;
    public static final int STATE_LEVEL_THREE = 8;
    public static final int STATE_LEVEL_FOUR = 9;
    public static final int STATE_LEVEL_FIVE = 10;
    public static final int STATE_LEVEL_SIX = 11;
    public static final int STATE_LEVEL_SEVEN = 12;
    public static final int STATE_LEVEL_EIGHT = 13;
    public static final int STATE_LEVEL_NINE = 14;
    public static final int STATE_LOAD_START = 15;

    //Update
    private void update() {
        switch (gameState) {
            //case STATE_BOOT -> {}
            case STATE_EXIT -> {
                System.exit(0);
            }
            //case STATE_START -> {}
            case STATE_LOAD_START -> {
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Welcome1", "Blurbs/Welcome2", "Blurbs/Welcome3"}, UIManager.startMenuPosition); //null string means it dissappears
                gameState = STATE_START;
            }
            case STATE_LOAD_ENDLESS -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText("", null); //null string means it dissappears
                newRandomGame();
            }
            case STATE_LEVEL_ONE -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Darwin's Fox 1", "Blurbs/Darwin's Fox 2", "Blurbs/Darwin's Fox 3"}, null);
                newGame("Maps/Map1");
            }
            case STATE_LEVEL_TWO -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Riverine Rabbit 1", "Blurbs/Riverine Rabbit 2", "Blurbs/Riverine Rabbit 3"}, null);
                newGame("Maps/Map2");
            }
            case STATE_LEVEL_THREE -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Pet Pig 1", "Blurbs/Pet Pig 2"}, null);
                newGame("Maps/Map3");
            }
            case STATE_LEVEL_FOUR -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Land Squid 1", "Blurbs/Land Squid 2"}, null);
                newGame("Maps/Map4");
            }
            case STATE_LEVEL_FIVE -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Pet Ownership 1", "Blurbs/Pet Ownership 2", "Blurbs/Pet Ownership 3"}, null);
                newGame("Maps/Map5");
            }
            case STATE_LEVEL_SIX -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Gardening 1", "Blurbs/Gardening 2", "Blurbs/Gardening 3"}, null);
                newGame("Maps/Map6");
            }
            case STATE_LEVEL_SEVEN -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Local Environmental Groups 1", "Blurbs/Local Environmental Groups 2", "Blurbs/Local Environmental Groups 3",}, null);
                newGame("Maps/Map7");
            }
            case STATE_LEVEL_EIGHT -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText(new String[] {"Blurbs/Helping Clear Invaders 1", "Blurbs/Helping Clear Invaders 2"}, null);
                newGame("Maps/Map8");
            }
            case STATE_LEVEL_NINE -> {
                gameState = STATE_IN_ROUND;
                UIManager.TriggerTransition(1);
                UIManager.setEcoText("Blurbs/Go Outside 1", null);
                newGame("Maps/Map9");
            }
            case STATE_IN_ROUND -> {
                TileManager.updateGame();
            }
            case STATE_LOSE -> {
                UIManager.TriggerTransition(1);
                UIManager.setEcoText("", null);
                gameState = STATE_START;
            }
            case STATE_WIN_GAME -> {
                UIManager.TriggerTransition(1);
                UIManager.setEcoText("", null);
                gameState = STATE_START;
            }
            default -> {}
        }
        
        UIManager.updateUI();
    }

    //Render
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g); //ALWAYS COMES FIRST
        Graphics2D g2 = (Graphics2D) g;

        //draw background
        UIManager.DrawBackground(g2);
        UIManager.DrawBackgroundEffects(g2);

        if (render) {
            switch (gameState) {
                case STATE_BOOT -> {
                    TileManager.drawGame(g2);
                    UIManager.drawEcoTips(g2);
                }
                //case STATE_EXIT -> System.exit(0);
                case STATE_START -> {
                    UIManager.drawStartButton(g2);
                    UIManager.drawLevelButtons(g2);
                    UIManager.drawAccredidation(g2);
                    UIManager.drawEcoTips(g2);
                }
                //case STATE_LOAD_ENDLESS -> {}
                //case STATE_LEVEL_ONE -> {}
                //case STATE_LEVEL_TWO -> {}
                //case STATE_LEVEL_THREE -> {}
                case STATE_IN_ROUND -> {
                    TileManager.drawGame(g2);
                    UIManager.drawEcoTips(g2);
                }
                //case STATE_LOSE -> {}
                //case STATE_WIN_GAME -> {}
                default -> {}
            }
        }
        
        //Start and exit
        //Load various game states
        //in a round
        //win/lose logic
        /* case STATE_WIN_ROUND:
        if (currentRound >= totalRounds) {
        gameState = STATE_WIN_GAME;
        }
        else
        {
        NextRound();
        gameState = STATE_IN_ROUND;
        }
        break; */
        //render nothing
        
        UIManager.drawSoundButton(g2);
        UIManager.DrawExitButton(g2);
        UIManager.drawTransition(g2);

        //draw debugging tools
        //DebugDraw(g2);

        //g2.dispose();
        //g.dispose();
    }

    //Input events
    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseScreenX = e.getX();
        mouseScreenY = e.getY();
        mouseX = (int) (mouseScreenX / PIXEL_SCALE_FACTOR);
        mouseY = (int) (mouseScreenY / PIXEL_SCALE_FACTOR);

        if (UIManager.UIElementHovered()) {}
        else if (TileManager.PlayerHovered()) {TileManager.NoTilesHovered();}
        else if (TileManager.TileHovered()) {}
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() > 1) {
            return;
        }

        if (UIManager.UIElementClicked()) {}
        else if (TileManager.PlayerClicked()) {}
        //else if (TileManager.EnemyClicked()) {}
        else if (TileManager.TileClicked()) {}
        else if (UIManager.BackgroundElementClicked()) {}
        else {TileManager.NothingClicked();}
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0); //instant quit functionality
        }
    }
    
    //Debugging
    @SuppressWarnings("unused")
    private void DebugDraw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, (int) (scaledHeight / 2 - PIXEL_SCALE_FACTOR), scaledWidth, (int) (PIXEL_SCALE_FACTOR * 2));
        g.fillRect((int) (scaledWidth / 2 - PIXEL_SCALE_FACTOR), 0, (int) (PIXEL_SCALE_FACTOR * 2), scaledHeight);

        g.setColor(Color.RED);
        for (int i = 0; i <= PIXEL_SCALE_FACTOR; i++) {
            g.fillRect(0, (int) (height * i - PIXEL_SCALE_FACTOR), scaledWidth, (int) (PIXEL_SCALE_FACTOR * 2));
        }

        UIManager.DrawAxisMarkers(g);
    }
}