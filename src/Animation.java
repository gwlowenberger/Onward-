import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Animation {
    //instance variables
    private BufferedImage spriteSheet;
    private Vector dimensions = Vector.defaultSpriteDimensions;
    protected int currentFrame = 0;

    //animations are read from the spritesheet from left to right, then up and down
    protected int rows = 0;
    protected int columns = 0;

    protected int distance = 0;
    protected int maxDistance = DEFAULT_MAX_DISTANCE;
    private boolean sendCompletedMessages = true;

    //static variables
    public static final int DEFAULT_MAX_DISTANCE = 5;

    //constructors
    public Animation() {}

    public Animation(String path) {
        try {
            spriteSheet = ImageIO.read(new File(path));
        }
        catch (IOException e) {}

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }

    public Animation(String path, Vector dimensions) {
        this.dimensions = dimensions;
        
        try {
            spriteSheet = ImageIO.read(new File(path));
        }
        catch (IOException e) {}

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }

    public Animation(String path, int maxDistance) {
        this.maxDistance = maxDistance;
        
        try {
            spriteSheet = ImageIO.read(new File(path));
        }
        catch (IOException e) {}

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }

    public Animation(String path, Vector dimensions, int maxDistance) {
        this.maxDistance = maxDistance;
        this.dimensions = dimensions;

        try {
            spriteSheet = ImageIO.read(new File(path));
        }
        catch (IOException e) {}

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }

    public Animation(String path, Vector dimensions, int maxDistance, boolean sendCompletedMessages) {
        this.maxDistance = maxDistance;
        this.dimensions = dimensions;
        this.sendCompletedMessages = sendCompletedMessages;

        try {
            spriteSheet = ImageIO.read(new File(path));
        }
        catch (IOException e) {}

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }

    public Animation(BufferedImage spriteSheet, Vector dimensions, int maxDistance) {
        this.maxDistance = maxDistance;
        this.dimensions = dimensions;
        this.spriteSheet = spriteSheet;

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }

    public Animation(BufferedImage spriteSheet, Vector dimensions, int maxDistance, boolean sendCompletedMessages) {
        this.maxDistance = maxDistance;
        this.dimensions = dimensions;
        this.spriteSheet = spriteSheet;
        this.sendCompletedMessages = sendCompletedMessages;

        columns = spriteSheet.getWidth() / (int) dimensions.x;
        rows = spriteSheet.getHeight() / (int) dimensions.y;
    }
    
    public Animation(Animation a) {
        this.spriteSheet = a.spriteSheet; //doesn't clone

        if (a.dimensions == null) {
            this.dimensions = Vector.defaultSpriteDimensions;
        }
        else
        {
            this.dimensions = new Vector(a.dimensions); //does clone
        }
        this.currentFrame = a.currentFrame; //primitives pass by value

        this.rows = a.rows;
        this.columns = a.columns;
        this.maxDistance = a.maxDistance;
        this.sendCompletedMessages = a.sendCompletedMessages;
    }

    //Instance Methods
    public BufferedImage GetFrame(int f) {
        int x = (f % columns) * (int) dimensions.x;
        int y = (f / columns) % rows * (int) dimensions.y;

        return spriteSheet.getSubimage(x, y, (int) dimensions.x, (int) dimensions.y);
    }

    public BufferedImage GetCurrentFrame() {
        return GetFrame(currentFrame);
    }

    public boolean isNull() {
        return spriteSheet == null;
    }

    public boolean Step(AnimatedActor a) {
        if (columns * rows == 0) {
            return false;
        }

        distance++;

        if (distance >= maxDistance) {
            distance = 0;
            currentFrame++;
            if (sendCompletedMessages && currentFrame % (columns * rows) == 0) {
                return true;
            }
        }

        return false;
    }

    public boolean Finish(AnimatedActor a) {
        currentFrame = 0;
        return true;
    }

    public Vector getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        return spriteSheet + " " + dimensions.toString();
    }
}