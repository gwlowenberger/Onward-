import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy extends Player {
    
    public Vector[] instructions;
    public int currentStep = 0;

    private StillActor arrow;
    private static final boolean HIDE_ARROW_WHILE_SHIFTING = false;
    private boolean arrowFollows = true;

    private BufferedImage number = null;

    //constructors
    public Enemy() { //number one!
        super();
        instructions = null;
        arrow = new StillActor();
        invincible = true;
    }

    public Enemy(Vector tilePosition, Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, Vector[] instructions) {
        super(tilePosition, collisionRect, colliderOffset, renderRect, animations);
        
        if (instructions.length == 0) {
            instructions = new Vector[] {Vector.zero};
        }
        this.instructions = instructions;
        
        arrow = new StillActor(TileManager.GetTilePosition(tilePosition), Vector.zero, Vector.zero, Vector.defaultSpriteDimensions, InstructionToArrowImage(instructions[0]));

        invincible = true;
    }

    public Enemy(Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, Vector[] instructions) {
        super(collisionRect, colliderOffset, renderRect, animations);
        
        if (instructions.length == 0) {
            instructions = new Vector[] {Vector.zero};
        }
        this.instructions = instructions;
        
        arrow = new StillActor(Vector.zero, Vector.zero, Vector.zero, Vector.defaultSpriteDimensions, InstructionToArrowImage(instructions[0]));

        invincible = true;
    }

    /*
    public Enemy(Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, int health, int damage, Vector[] instructions) {
        super(collisionRect, colliderOffset, renderRect, animations);
        
        if (instructions.length == 0) {
            instructions = new Vector[] {Vector.zero};
        }
        this.instructions = instructions;
        
        arrow = new StillActor(Vector.zero, Vector.zero, Vector.zero, Vector.defaultSpriteDimensions, InstructionToArrowImage(instructions[0]));
    }

    public Enemy(Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, int health, int damage, int maxHealth, Vector[] instructions) {
        super(collisionRect, colliderOffset, renderRect, animations);
        
        if (instructions.length == 0) {
            instructions = new Vector[] {Vector.zero};
        }
        this.instructions = instructions;
        
        arrow = new StillActor(Vector.zero, Vector.zero, Vector.zero, Vector.defaultSpriteDimensions, InstructionToArrowImage(instructions[0]));
    }
    */

    public Enemy(Enemy e) {
        super(e);

        this.instructions = e.instructions; // doesn't copy

        arrow = new StillActor(Vector.zero, Vector.zero, Vector.zero, Vector.defaultSpriteDimensions, InstructionToArrowImage(instructions[0]));

        invincible = true;
    }
    
    //methods
    public void Shift() {
        arrowFollows = false; //set to false here so that the enemy slides across the arrow instead of taking it with

        triggerEnemies = true; //once finished moving, prepare to shift all other enemies

        Push(instructions[currentStep]);
        currentStep++;
        if (currentStep >= instructions.length) {
            currentStep = 0;
        }

        //OPTIONAL: hides the arrow while shifting
        if (HIDE_ARROW_WHILE_SHIFTING) {
            arrow.sprite = null;
        }
    }

    @Override
    public boolean OnClick() {
        return ChangeAnimation(ANIMATION_ACT_CLICK);
    }

    @Override
    public boolean DoneMoving() {
        arrow = new StillActor(TileManager.GetTilePosition(tilePosition), Vector.zero, Vector.zero, Vector.defaultSpriteDimensions,
            InstructionToArrowImage(instructions[currentStep]));
        arrowFollows = true;
        
        return super.DoneMoving();
    }

    @Override
    protected boolean Die() {
        TileManager.KillPlayer(this);
        if (triggerEnemies) {
            TileManager.ShiftEnemy();
            triggerEnemies = false;
        }
        return true;
    }

    private String InstructionToArrowImage(Vector move) {
        if (move.Equals(Vector.east)) {
            return "Sprites/Arrows/SouthwestArrow.png";
        }
        else if (move.Equals(Vector.south)) {
            return "Sprites/Arrows/SoutheastArrow.png";
        }
        else if (move.Equals(Vector.north)) {
            return "Sprites/Arrows/NorthwestArrow.png";
        }
        else if (move.Equals(Vector.west)) {
            return "Sprites/Arrows/NortheastArrow.png";
        }
        else {
            return "";
        }
    }

    private BufferedImage NumberToImage(int num) {
        if (num < 1 || num > 9) {
            return null;
        }
        
        num--;

        try {
            return ImageIO.read(new File("Sprites/UI/Numbers.png")).getSubimage(num * (int) Vector.defaultNumberRect.x, 0, (int) Vector.defaultNumberRect.x, (int) Vector.defaultNumberRect.y);
        }
        catch (IOException e) {
            return null;
        }
    }

    public boolean SetNumber(int num) {
        number = NumberToImage(num);

        return number != null;
    }

    public void drawArrow(Graphics2D g) {
        if (arrow != null && arrow.sprite != null && getCurrentAnimation() != ANIMATION_FALLING_OFF_GRID) {
            arrow.Draw(g, false);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g); //draw this enemy

        //only draw the number if this enemy is not moving
        if (number != null && !IsMoving()) {
            g.drawImage(number, (int) (position.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (position.y * MyPanel.PIXEL_SCALE_FACTOR),
                (int) (Vector.defaultNumberRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (Vector.defaultNumberRect.y * MyPanel.PIXEL_SCALE_FACTOR), null);
        }
    }

    @Override
    protected boolean FrameCompleted() {
        if (arrow != null && arrowFollows) {
            arrow.position = Vector.Subtract(position, TileManager.offset);
        }
        return super.FrameCompleted();
    }
}