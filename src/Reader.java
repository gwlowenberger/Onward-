import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Reader {
    private static final boolean TESTING = false;

    public static int getGrid(String path) {
        if (TESTING) {
            System.out.println("GETTING GRID SIZE");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine(); //reads the first line

            int output = 0;
            //find all of the numbers in the first line of the text file
            for (int i = 0; i < line.length(); i++) {
                int ch = line.charAt(i);

                if (TESTING) {
                    System.out.println(ch);
                    System.out.println(line.charAt(i));
                }

                //if its a number, it appends it to the end of the output
                if (ch >= 48 && ch <= 57) {
                    output *= 10;
                    output += ch - 48;
                }
            }

            if (TESTING) {
                System.out.println("Size is " + output);
            }

            return output;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return -1;
        }
    }

    @SuppressWarnings("Convert2Diamond")
    public static Player[] getPlayers(String path) {
        if (TESTING) {
            System.out.println("GETTING PLAYERS");
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            ArrayList<Player> output = new ArrayList<Player>();

            while (line != null) {
                if (line.length() > 0 && line.charAt(0) == 'p') {
                    int x = -1;
                    int y = -1;
                    int index = -1;
                    //find all of the numbers in the first line of the text file
                    for (int i = 1; i < line.length(); i++) {
                        int ch = line.charAt(i);

                        //finds the next number
                        if (ch >= 48 && ch <= 57) {

                            if (index == -1) {
                                index = ch - 48;
                                for (int j = i + 1; j < line.length(); j++) {
                                    ch = line.charAt(j);
                                    if (ch >= 48 && ch <= 57) {
                                        index *= 10;
                                        index += ch - 48;
                                    }
                                    else
                                    {
                                        if (TESTING) {
                                            System.out.println(index);
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                            else if (x == -1) {
                                x = ch - 48;
                                for (int j = i + 1; j < line.length(); j++) {
                                    ch = line.charAt(j);
                                    if (ch >= 48 && ch <= 57) {
                                        x *= 10;
                                        x += ch - 48;
                                    }
                                    else
                                    {
                                        if (TESTING) {
                                            System.out.println(x);
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                            else if (y == -1) {
                                y = ch - 48;
                                for (int j = i + 1; j < line.length(); j++) {
                                    ch = line.charAt(j);
                                    if (ch >= 48 && ch <= 57) {
                                        y *= 10;
                                        y += ch - 48;
                                    }
                                    else
                                    {
                                        if (TESTING) {
                                            System.out.println(y);
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Player p = TileManager.getPlayer(index);
                    if (p != null) {
                        p.tilePosition = new Vector(x, y);
                        if (!p.IsOffGrid()) {
                            p.position = TileManager.GetTilePositionWithOffset(p.tilePosition);
                            output.add(p);
                        }
                    }
                }

                line = reader.readLine();
                if (TESTING) {
                    System.out.println(line);
                }
            }

            if (TESTING) {
                for (Player player : output) {
                    System.out.println(player.toString());
                }
            }

            Player[] arr = new Player[output.size()];

            for (int i = 0; i < arr.length; i++) {
                arr[i] = output.get(i);
            }

            return arr;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("Convert2Diamond")
    public static Enemy[] getEnemies(String path) {
        if (TESTING) {
            System.out.println("GETTING ENEMIES");
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            ArrayList<Enemy> output = new ArrayList<Enemy>();

            while (line != null) {
                if (line.length() > 0 && line.charAt(0) == 'e') {
                    int x = -1;
                    int y = -1;
                    int index = -1;
                    //find all of the numbers in the first line of the text file
                    for (int i = 1; i < line.length(); i++) {
                        int ch = line.charAt(i);

                        //finds the next number
                        if (ch >= 48 && ch <= 57) {

                            if (index == -1) {
                                index = ch - 48;
                                for (int j = i + 1; j < line.length(); j++) {
                                    ch = line.charAt(j);
                                    if (ch >= 48 && ch <= 57) {
                                        index *= 10;
                                        index += ch - 48;
                                    }
                                    else
                                    {
                                        if (TESTING) {
                                            System.out.println(index);
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                            else if (x == -1) {
                                x = ch - 48;
                                for (int j = i + 1; j < line.length(); j++) {
                                    ch = line.charAt(j);
                                    if (ch >= 48 && ch <= 57) {
                                        x *= 10;
                                        x += ch - 48;
                                    }
                                    else
                                    {
                                        if (TESTING) {
                                            System.out.println(x);
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                            else if (y == -1) {
                                y = ch - 48;
                                for (int j = i + 1; j < line.length(); j++) {
                                    ch = line.charAt(j);
                                    if (ch >= 48 && ch <= 57) {
                                        y *= 10;
                                        y += ch - 48;
                                    }
                                    else
                                    {
                                        if (TESTING) {
                                            System.out.println(y);
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Enemy e = TileManager.getEnemy(index);
                    if (e != null) {
                        e.tilePosition = new Vector(x, y);
                        if (!e.IsOffGrid()) {
                            e.position = TileManager.GetTilePositionWithOffset(e.tilePosition);
                            output.add(e);
                        }
                    }
                }

                line = reader.readLine();
                if (TESTING) {
                    System.out.println(line);
                }
            }

            if (TESTING) {
                for (Enemy enemy : output) {
                    System.out.println(enemy.toString());
                }
            }

            Enemy[] arr = new Enemy[output.size()];

            for (int i = 0; i < arr.length; i++) {
                arr[i] = output.get(i);
            }

            return arr;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    private static final BufferedImage[] LETTERS = new BufferedImage[27];
    public Reader() throws IOException {
        BufferedImage b = ImageIO.read(new File("Sprites/UI/Font.png"));
        for (int i = 0; i < LETTERS.length; i++) {
            LETTERS[i] = b.getSubimage((i % 9) * 5, (i / 9) % 3 * 7, 5, 7);
        }
    }

    private static final int MAX_TEXT_COLUMNS = 16;
    private static final int MAX_TEXT_ROWS = 6;
    private static final int HORIZONTAL_PIXEL_SPACING = 8;
    private static final int VERTICAL_PIXEL_SPACING = 12;
    public static BufferedImage getText(String path) {
        if (TESTING) {
            System.out.println("GETTING TEXT");
        }

        if (path == null) {
            if (TESTING) {
                System.out.println("PATH IS NULL");
            }
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            //A is 65, but should be 0
            //Z is 90, but should be 25
            //_ is 95, but should be 26
            BufferedImage output = new BufferedImage(MAX_TEXT_COLUMNS * 16, MAX_TEXT_ROWS * 12, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = output.createGraphics();

            for (int y = 0; y < MAX_TEXT_ROWS; y++) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                int x = 0;
                for (int i = 0; i < line.length(); i++) {
                    int ch = line.charAt(i);

                    if (TESTING) {
                        System.out.println(ch);
                        System.out.println(line.charAt(i));
                    }

                    //if its a number, it appends it to the end of the output
                    if (ch >= 65 && ch <= 90) {
                        g.drawImage(LETTERS[ch - 65], (x * HORIZONTAL_PIXEL_SPACING), (y * VERTICAL_PIXEL_SPACING) + 2, null);
                        x++;
                    }
                    else if (ch == 95) {
                        g.drawImage(LETTERS[26], (x * HORIZONTAL_PIXEL_SPACING), (y * VERTICAL_PIXEL_SPACING) + 2, null);
                        x++;
                    }

                    if (x >= MAX_TEXT_COLUMNS) {
                        break;
                    }
                }
            }

            g.dispose();

            return output;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    public static BufferedImage getTextUnlimited(String path) {
        if (TESTING) {
            System.out.println("GETTING TEXTUnlimited");
        }

        if (path == null) {
            if (TESTING) {
                System.out.println("PATH IS NULL");
            }
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            //A is 65, but should be 0
            //Z is 90, but should be 25
            //_ is 95, but should be 26
            Vector size = fileDimensions(path);
            if (size == null) {
                return null;
            }
            BufferedImage output = new BufferedImage((int) size.x * 16, (int) size.y * 12, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = output.createGraphics();

            String line = reader.readLine();
            int y = 0;

            while (line != null && y < 9999) {
                int x = 0;
                for (int i = 0; i < line.length(); i++) {
                    int ch = line.charAt(i);

                    if (TESTING) {
                        System.out.println(ch);
                        System.out.println(line.charAt(i));
                    }

                    //if its a number, it appends it to the end of the output
                    if (ch >= 65 && ch <= 90) {
                        g.drawImage(LETTERS[ch - 65], (x * HORIZONTAL_PIXEL_SPACING), (y * VERTICAL_PIXEL_SPACING) + 2, null);
                        x++;
                    }
                    else if (ch == 95) {
                        g.drawImage(LETTERS[26], (x * HORIZONTAL_PIXEL_SPACING), (y * VERTICAL_PIXEL_SPACING) + 2, null);
                        x++;
                    }
                }

                y++;
                line = reader.readLine();
            }

            g.dispose();

            return output;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    private static Vector fileDimensions(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            Vector size = new Vector();

            while (line != null && size.y < 9999) {
                if (line.length() > size.x) {
                    size.x = line.length();
                }
                
                size.y++;
                line = reader.readLine();
            }

            return size;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }
}