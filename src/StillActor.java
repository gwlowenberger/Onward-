import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class StillActor extends Actor {
    //properties
    public Vector renderRect;
    public BufferedImage sprite;

    //constructors
    public StillActor() {
        super();
        this.sprite = null;
    }
    
    public StillActor(Vector position) {
        super(position);
        renderRect = Vector.zero;
        this.sprite = null;
    }

    public StillActor(Vector position, Vector collisionRect) {
        super(position, collisionRect);
        renderRect = Vector.zero;
        this.sprite = null;
    }

    public StillActor(Vector position, Vector collisionRect, BufferedImage sprite) {
        super(position, collisionRect);
        renderRect = collisionRect;
        this.sprite = sprite;
    }

    public StillActor(Vector position, Vector collisionRect, String path) {
        super(position, collisionRect);
        colliderOffset = Vector.zero;
        renderRect = collisionRect;
        
        if (path != null && !path.isEmpty()) {
            try {
                sprite = ImageIO.read(new File(path));
            }
            catch (IOException e) {}
        }
    }

    public StillActor(Vector position, Vector collisionRect, String path, Vector subImagePosition, Vector subImageDimensions) {
        super(position, collisionRect);
        colliderOffset = Vector.zero;
        renderRect = collisionRect;
        
        if (path != null && !path.isEmpty()) {
            try {
                sprite = ImageIO.read(new File(path)).getSubimage((int) subImagePosition.x,
                    (int) subImagePosition.y, (int) subImageDimensions.x, (int) subImageDimensions.y);
            }
            catch (IOException e) {}
        }
    }

    public StillActor(Vector position, Vector collisionRect, Vector colliderOffset, BufferedImage sprite) {
        super(position, collisionRect, colliderOffset);
        renderRect = collisionRect;
        this.sprite = sprite;
    }

    public StillActor(Vector position, Vector collisionRect, Vector colliderOffset, String path) {
        super(position, collisionRect, colliderOffset);
        renderRect = collisionRect;

        if (path != null && !path.isEmpty()) {
            try {
                sprite = ImageIO.read(new File(path));
            }
            catch (IOException e) {}
        }
    }

    public StillActor(Vector position, Vector collisionRect, Vector colliderOffset, Vector renderRect, BufferedImage sprite) {
        super(position, collisionRect, colliderOffset);
        this.renderRect = renderRect;
        this.sprite = sprite;
    }

    public StillActor(Vector position, Vector collisionRect, Vector colliderOffset, Vector renderRect, String path) {
        super(position, collisionRect, colliderOffset);
        this.renderRect = renderRect;
        
        if (path != null && !path.isEmpty()) {
            try {
                sprite = ImageIO.read(new File(path));
            }
            catch (IOException e) {}
        }
    }

    public StillActor(StillActor sa) {
        super(sa.position, sa.collisionRect, sa.colliderOffset);
        this.renderRect = sa.renderRect;
        this.sprite = sa.sprite;
    }

    //instance methods
    public void Draw(Graphics2D g, boolean showColliders) {
        if (sprite == null || MyPanel.SHOW_COLLIDERS_ONLY)
        {
            g.setColor(Color.red);
            g.fillRect((int) ((position.x + colliderOffset.x) * MyPanel.PIXEL_SCALE_FACTOR), (int) ((position.y + colliderOffset.y) * MyPanel.PIXEL_SCALE_FACTOR),
                (int) (collisionRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (collisionRect.y * MyPanel.PIXEL_SCALE_FACTOR));
        }
        else
        {
            g.drawImage(sprite, (int) (position.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (position.y * MyPanel.PIXEL_SCALE_FACTOR),
                (int) (renderRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (renderRect.y * MyPanel.PIXEL_SCALE_FACTOR), null);

            if (showColliders) {
                g.setColor(Color.red);
                g.fillRect((int) ((position.x + colliderOffset.x) * MyPanel.PIXEL_SCALE_FACTOR), (int) ((position.y + colliderOffset.y) * MyPanel.PIXEL_SCALE_FACTOR),
                (int) (collisionRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (collisionRect.y * MyPanel.PIXEL_SCALE_FACTOR));
            }
        }
    }
}