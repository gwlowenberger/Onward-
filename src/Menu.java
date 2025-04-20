import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class Menu extends UIMovingButtonActor {
    private AnimatedActor text = new AnimatedActor();
    private final String[] paths;
    private int index = 0;

    /*
     * Generally, the first two animations of a menu (idle and click)
     * are empty so that the menu does not appear by default, and it
     * dissappears when clicked. When it changes
     * animation to currentAnimation > 1, they should stay toggled on.
     * This can be ensured by turning the sendCompletedMessages property
     * to false.
     */

    public Menu(AnimatedActor menu, String startTextPath) {
        super(menu);
        paths = new String[] {startTextPath};
        setText(startTextPath, position);
    }

    public Menu(AnimatedActor menu, int state, String startTextPath) {
        super(menu, state);
        paths = new String[] {startTextPath};
        setText(startTextPath, position);
    }

    public Menu(AnimatedActor menu, String[] paths) {
        super(menu);
        this.paths = paths;
        setText(paths[index], position);
    }

    public Menu(AnimatedActor menu, int state, String[] paths) {
        super(menu, state);
        this.paths = paths;
        setText(paths[index], position);
    }

    @Override
    public void update() {
        super.update();
        text.update();
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        text.draw(g);
    }

    @Override
    public boolean OnClick() {
        nextText();
        return true;
    }

    private void nextText() {
        index++;
        if (index >= paths.length) {
            setText(null, null);
            index = 0;
            if (state != MyPanel.STATE_NONE) {
                MyPanel.gameState = state;
            }
        }
        else
        {
            setText(paths[index], position);
        }
    }

    public void setText(String path, Vector pos) {
        BufferedImage t = Reader.getText(path);
        if (t == null) {
            this.ForceAnimation(0); //makes the panel dissappear
            text.ForceAnimation(0); //makes the text dissappear
            return;
        }
        if (pos == null) {
            pos = position;
        }
        Vector rect = new Vector(t.getWidth(), t.getHeight());
        text = new AnimatedActor(Vector.Add(pos, 8), rect, new Animation[] {new Animation(),
            new Animation(t, rect, 30, false)});
        text.ChangeAnimation(1);
        this.ChangeAnimation(2); //makes the panel dissappear
    }

    public void hide() {
        ForceAnimation(0);
        text.ForceAnimation(0);
    }
}