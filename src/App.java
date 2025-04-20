import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public final class App {

    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 320;
    private static final boolean TESTING = false;

    public static void main(String[] args) throws Exception {
        //test mode is not to test the game:
        //it is a debugging method to test individual methods and classes.

        new Reader();

        if (TESTING) {
            testFileReader();
        }
        else
        {
            //run as normal
            JFrame window = new JFrame();

            MyPanel.width = SCREEN_WIDTH;
            MyPanel.height = SCREEN_HEIGHT;
            MyPanel.scaledWidth = (int) (SCREEN_WIDTH * MyPanel.PIXEL_SCALE_FACTOR);
            MyPanel.scaledHeight = (int) (SCREEN_HEIGHT * MyPanel.PIXEL_SCALE_FACTOR);
            MyPanel.center = new Vector(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
            MyPanel.scaledCenter = Vector.Multiply(MyPanel.center, MyPanel.PIXEL_SCALE_FACTOR);

            window.add(new MyPanel());
            window.setSize(new Dimension((int) (SCREEN_WIDTH * MyPanel.PIXEL_SCALE_FACTOR), (int) (SCREEN_HEIGHT * MyPanel.PIXEL_SCALE_FACTOR)));
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setBackground(Color.BLACK);
            //window.setUndecorated(true);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            window.setLocation((screenSize.width - MyPanel.scaledWidth) / 2, (screenSize.height - MyPanel.scaledHeight) / 2 - 50);
            window.setTitle("Onward! Save the Animals!");
            window.setName("Onward! Save the Animals!");
            window.setIconImage(ImageIO.read(new File("Sprites/UI/CuteGrassBlock.png")));
            window.setResizable(false);
            window.setVisible(true);
        }
    }

    private static void testFileReader() {
        int gridSize = Reader.getGrid("Maps/Map1");
        System.out.println(gridSize);
        new TileManager(gridSize);
        
        Player[] arr = Reader.getPlayers("Maps/Map1");
        for (Player player : arr) {
            System.out.println(player);
        }
        System.out.println(arr.length);

        Enemy[] enemies = Reader.getEnemies("Maps/Map1");
        for (Enemy enemy : enemies) {
            System.out.println(enemy);
        }
        System.out.println(enemies.length);


    }
}
