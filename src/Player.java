
import java.awt.Graphics2D;

public class Player extends AnimatedActor {
    public Vector tilePosition;
    
    protected boolean triggerEnemies = false;
    private boolean drawBehindTiles = false;
    protected boolean invincible = false;
    private int numShoves = 2;

    private final StillActor heart = new StillActor(Vector.zero, Vector.zero, Vector.zero, new Vector(5, 5), "Sprites/UI/Heart.png");

    //constructors
    public Player() {
        super();
        tilePosition = Vector.zero;
    }

    public Player(Vector tilePosition, Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation animation) {
        super(TileManager.GetTilePositionWithOffset(tilePosition), collisionRect, colliderOffset, renderRect, animation);
        this.tilePosition = tilePosition;
    }

    public Player(Vector tilePosition, Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations) {
        super(TileManager.GetTilePositionWithOffset(tilePosition), collisionRect, colliderOffset, renderRect, animations);
        this.tilePosition = tilePosition;
    }

    public Player(Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations) {
        super(Vector.zero, collisionRect, colliderOffset, renderRect, animations);
        this.tilePosition = Vector.zero;
    }

    /*
    public Player(Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, int health, int damage) {
        super(Vector.zero, collisionRect, colliderOffset, renderRect, animations);
        this.tilePosition = Vector.zero;
    }

    public Player(Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, int health, int damage, int maxHealth) {
        super(Vector.zero, collisionRect, colliderOffset, renderRect, animations);
        this.tilePosition = Vector.zero;
    }
    */

    public Player(Player p) {
        super(p);
        this.tilePosition = new Vector(p.tilePosition);
        invincible = p.invincible;
    } 

    //instance methods
    public boolean Push(Vector force) {
        int anim = ANIMATION_IDLE;

        if (force == null) {
            anim = ANIMATION_MOVING_NONE;

            if (!ChangeAnimation(anim)) {
                return false;
            }
        }
        else
        {
            force.Round(); //ensures all components are integer numbers
            if (force.Equals(Vector.zero)) {
                anim = ANIMATION_MOVING_NONE;
            }
            else if (force.Equals(Vector.east)) {
                anim = ANIMATION_MOVING_SOUTHWEST;
            }
            else if (force.Equals(Vector.south)) {
                anim = ANIMATION_MOVING_SOUTHEAST;
            }
            else if (force.Equals(Vector.north)) {
                anim = ANIMATION_MOVING_NORTHWEST;
            }
            else if (force.Equals(Vector.west)) {
                anim = ANIMATION_MOVING_NORTHEAST;
            }

            if (!ChangeAnimation(anim)) {
                System.out.println("Failure " + getCurrentAnimation());
                return false;
            }

            Vector newPos = Vector.Add(force, tilePosition);
        
            Player p = TileManager.PlayerAt(newPos);

            tilePosition = newPos;

            //this does not detect players between the new position and the destination-- fix this
            if (p != null && !force.Equals(Vector.zero)) {
                //'passes off' the enemy trigger to the last one that was moved
                if (!p.invincible) {
                    p.numShoves--;
                }

                p.Push(force, triggerEnemies);
                
                //p.Push(force, damage, triggerEnemies); //ensures the the last person in the chain triggers the enemies
                triggerEnemies = false;
            }
        }

        return true;
    }

    public boolean Push(Vector force, float knockback) {
        return Push(Vector.Multiply(force, knockback));
    }

    public boolean Push(Vector force, boolean triggerEnemies) {
        if (this.triggerEnemies == false) {
            this.triggerEnemies = triggerEnemies;
        }
        
        boolean push = Push(force);
        
        return push;
    }
    
    /*
    public boolean Push(Vector force, int damage) {
        Damage(damage);
        return Push(force);
    }

    public boolean Push(Vector force, int damage, boolean triggerEnemies) {
        Damage(damage);
        return Push(force, triggerEnemies);
    }
    */

    protected void PushedOffGrid() {
        ChangeAnimation(ANIMATION_FALLING_OFF_GRID);
        if (tilePosition.x < 0 || tilePosition.y < 0) {
            drawBehindTiles = true;
        }
    }

    public boolean drawBehindTiles() {
        return drawBehindTiles;
    }

    @Override
    protected boolean DoneFalling() {
        drawBehindTiles = false;
        Die();
        return super.DoneFalling(); //returns true
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (!invincible && numShoves == 1) {
            heart.position = Vector.Add(position, new Vector(11, 0));
            heart.Draw(g, false);
        }
    }

    @Override
    public boolean OnClick() {
        return ChangeAnimation(ANIMATION_ACT_CLICK);
    }

    /*
    //returns if it's still alive. Also calls die if not.
    public boolean Damage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
            Die();
            return false;
        }
        else {
            return true;
        }
    } */

    protected boolean Die() {
        TileManager.KillPlayer(this);

        if (triggerEnemies) {
            triggerEnemies = false;
            TileManager.ShiftEnemy();
        }

        return true;
    }

    @Override
    protected boolean DoneDying() {
        return Die();
    }


    @Override
    protected boolean DoneMoving() {
        if (IsOffGrid()) {
            PushedOffGrid();
            return false;
        }

        if (numShoves <= 0) {
            ChangeAnimation(ANIMATION_DEATH);
            return false;
        }
        
        //if it has NOT fallen off of the grid, set the position at the new tile and (maybe) trigger enemies to move
        position = TileManager.GetTilePositionWithOffset(tilePosition);
        
        if (triggerEnemies) {
            triggerEnemies = false;
            TileManager.ShiftEnemy();
        }

        return true;
    }

    protected boolean IsOffGrid() {
        return TileManager.IsOffGrid(tilePosition);
    }

    @Override
    public String toString() {
        return tilePosition.toString();
    }
}