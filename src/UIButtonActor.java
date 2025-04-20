import java.awt.image.BufferedImage;

public class UIButtonActor extends StillActor {
    //properties
    public int state;

    //constructors
    public UIButtonActor() {
        super();
        state = MyPanel.STATE_NONE;
    }

    @Deprecated
    public UIButtonActor(Vector position, Vector collisionRect, BufferedImage sprite, int state) {
        super(position, collisionRect, sprite);
        this.state = state;
    }

    public UIButtonActor(Vector position, Vector collisionRect, String path, int state) {
        super(position, collisionRect, path);
        this.state = state;
    }

    public UIButtonActor(Vector position, Vector collisionRect, String path, int state, Vector subImagePosition, Vector subImageDimensions) {
        super(position, collisionRect, path, subImagePosition, subImageDimensions);
        this.state = state;
    }

    //instance methods
    @Override
    public boolean OnClick() {
        if (state != MyPanel.STATE_NONE) {
            MyPanel.gameState = state;
        }
        return true;
    }
}
