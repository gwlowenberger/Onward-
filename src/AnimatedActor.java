import java.awt.*;

public class AnimatedActor extends Actor {
    //properties
    private Vector renderRect;
    private int currentAnimation = ANIMATION_IDLE;
    private final Animation[] animations;

    //constants
    public static final int ANIMATION_IDLE = 0; //idle is the only repeating animation
    public static final int ANIMATION_ACT_CLICK = 1;
    public static final int ANIMATION_IDLE_HOVER = 2;
    public static final int ANIMATION_FALLING_OFF_GRID = 3;
    public static final int ANIMATION_MOVING_SOUTHWEST = 4;
    public static final int ANIMATION_MOVING_SOUTHEAST = 5;
    public static final int ANIMATION_MOVING_NORTHWEST = 6;
    public static final int ANIMATION_MOVING_NORTHEAST = 7;
    public static final int ANIMATION_MOVING_NONE = 8;
    public static final int ANIMATION_DEATH = 9;

    private static final boolean SHOW_COLLIDERS = false;

    //constructors
    public AnimatedActor() {
        super();
        this.animations = null;
    }
    
    public AnimatedActor(Vector position) {
        super(position);
        this.animations = null;
    }

    public AnimatedActor(Vector position, Vector collisionRect) {
        super(position, collisionRect, Vector.zero);
        renderRect = new Vector(collisionRect);
        this.animations = null;
    }

    public AnimatedActor(Vector position, Vector collisionRect, Animation animation) {
        super(position, collisionRect, Vector.zero);
        renderRect = new Vector(collisionRect);
        this.animations = new Animation[] {animation};
    }

    public AnimatedActor(Vector position, Vector collisionRect, Animation[] animations) {
        super(position, collisionRect, Vector.zero);
        renderRect = new Vector(collisionRect);
        this.animations = animations;
    }

    public AnimatedActor(Vector position, Vector collisionRect, Vector colliderOffset, Animation animation) {
        super(position, collisionRect, colliderOffset);
        renderRect = new Vector(collisionRect);
        this.animations = new Animation[] {animation};
    }

    public AnimatedActor(Vector position, Vector collisionRect, Vector colliderOffset, Animation[] animations) {
        super(position, collisionRect, colliderOffset);
        this.animations = animations;
    }

    public AnimatedActor(Vector position, Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation animation) {
        super(position, collisionRect, colliderOffset);
        this.renderRect = renderRect;
        this.animations = new Animation[] {animation, new Animation()};
    }

    public AnimatedActor(Vector position, Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animation) {
        super(position, collisionRect, colliderOffset);
        this.renderRect = renderRect;
        this.animations = animation;
    }

    public AnimatedActor(AnimatedActor aa) {
        super(aa.position, aa.collisionRect, aa.colliderOffset);
        this.renderRect = new Vector(aa.renderRect);

        this.animations = new Animation[aa.animations.length];
        for (int i = 0; i < animations.length; i++) {
            if (aa.animations[i] instanceof MobileAnimation mobileAnimation) {
                this.animations[i] = new MobileAnimation(mobileAnimation);
            }
            else
            {
                this.animations[i] = new Animation(aa.animations[i]);
            }
        }
    }

    public AnimatedActor(AnimatedActor aa, Animation idle) {
        super(aa.position, aa.collisionRect, aa.colliderOffset);
        this.renderRect = new Vector(aa.renderRect);

        this.animations = new Animation[aa.animations.length];
        for (int i = 0; i < animations.length; i++) {
            if (aa.animations[i] instanceof MobileAnimation mobileAnimation) {
                this.animations[i] = new MobileAnimation(mobileAnimation);
            }
            else
            {
                this.animations[i] = new Animation(aa.animations[i]);
            }
        }
    }

    //instance methods
    public void draw(Graphics2D g) {
        if (animations[currentAnimation] == null || animations[currentAnimation].isNull()) {
            if (SHOW_COLLIDERS) {
                g.setColor(Color.orange);
                g.fillRect((int) ((position.x + colliderOffset.x) * MyPanel.PIXEL_SCALE_FACTOR), (int) ((position.y + colliderOffset.y) * MyPanel.PIXEL_SCALE_FACTOR),
                    (int) (collisionRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (collisionRect.y * MyPanel.PIXEL_SCALE_FACTOR));
            }
            return;
        }

        if (MyPanel.SHOW_COLLIDERS_ONLY)
        {
            g.setColor(Color.red);
            g.fillRect((int) ((position.x + colliderOffset.x) * MyPanel.PIXEL_SCALE_FACTOR), (int) ((position.y + colliderOffset.y) * MyPanel.PIXEL_SCALE_FACTOR),
                (int) (collisionRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (collisionRect.y * MyPanel.PIXEL_SCALE_FACTOR));
        }
        else
        {
            g.drawImage(animations[currentAnimation].GetCurrentFrame(), (int) (position.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (position.y * MyPanel.PIXEL_SCALE_FACTOR),
                (int) (renderRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (renderRect.y * MyPanel.PIXEL_SCALE_FACTOR), null);

            if (SHOW_COLLIDERS) {
                g.setColor(Color.orange);
                g.fillRect((int) ((position.x + colliderOffset.x) * MyPanel.PIXEL_SCALE_FACTOR), (int) ((position.y + colliderOffset.y) * MyPanel.PIXEL_SCALE_FACTOR),
                    (int) (collisionRect.x * MyPanel.PIXEL_SCALE_FACTOR), (int) (collisionRect.y * MyPanel.PIXEL_SCALE_FACTOR));
            }
        }
    }

    public void update() {
        if (animations[currentAnimation].Step(this)) {
            switch (currentAnimation) {
                case ANIMATION_IDLE_HOVER -> {
                    //Does not change out of the hovering state
                    if (!IsHovering()) {
                        animations[currentAnimation].Finish(this);
                        currentAnimation = ANIMATION_IDLE;
                    }
                }

                case ANIMATION_MOVING_SOUTHWEST -> {
                    //stops moving once finished, and sets to idle
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneMoving();
                }

                case ANIMATION_MOVING_SOUTHEAST -> {
                    //stops moving once finished, and sets to idle
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneMoving();
                }

                case ANIMATION_MOVING_NORTHWEST -> {
                    //stops moving once finished, and sets to idle
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneMoving();
                }

                case ANIMATION_MOVING_NORTHEAST -> {
                    //stops moving once finished, and sets to idle
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneMoving();
                }

                case ANIMATION_FALLING_OFF_GRID -> {
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneFalling();
                }

                case ANIMATION_ACT_CLICK -> {
                    animations[currentAnimation].Finish(this);
                    if (IsHovering()) {
                        if (animations.length <= ANIMATION_IDLE_HOVER || animations[ANIMATION_IDLE_HOVER].isNull()) {
                            currentAnimation = ANIMATION_IDLE;
                        }
                        else
                        {
                            currentAnimation = ANIMATION_IDLE_HOVER; //WORK ON THIS
                        }
                    }
                    else {
                        currentAnimation = ANIMATION_IDLE;
                    }
                }

                case ANIMATION_MOVING_NONE -> {
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneMoving();
                }

                case ANIMATION_DEATH -> {
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                    DoneDying();
                }

                default -> {
                    //sets to idle by default
                    animations[currentAnimation].Finish(this);
                    currentAnimation = ANIMATION_IDLE;
                }
            }
        }

        FrameCompleted();
    }

    public boolean ChangeAnimation(int animationNum) {

        //restrictions for animationNum
        if (animationNum >= animations.length) {
            return false;
        }
        if (animationNum < 0) {
            return false;
        }
        if (animations[animationNum] == null || animations[animationNum].isNull()) {
            return false;
        }

        //no hovering while clicking
        if (currentAnimation == ANIMATION_ACT_CLICK && animationNum == ANIMATION_IDLE_HOVER) {
            return false;
        }

        //no change if it's moving
        //if (IsMoving()) {
        //    return false;
        //}

        animations[currentAnimation].Finish(this);

        currentAnimation = animationNum;

        return true;
    }

    //This method is not reccommended: it creates many nullReferenceExeptions.
    public boolean ForceAnimation(int animationNum) {
        currentAnimation = animationNum;
        return true;
    }

    public int getCurrentAnimation() {
        return currentAnimation;
    }

    @Override
    public boolean OnClick() {
        ChangeAnimation(ANIMATION_ACT_CLICK);
        return true;
    }

    public boolean IsMoving() {
        return currentAnimation >= ANIMATION_MOVING_SOUTHWEST && currentAnimation <= ANIMATION_MOVING_NORTHEAST;
    }

    protected boolean DoneDying() {
        return true;
    }

    protected boolean DoneMoving() {
        return true;
    }

    protected boolean DoneFalling() {
        return true;
    }

    protected boolean FrameCompleted() {
        return true;
    } 

    private boolean IsHovering() {
        return DidCollide(MyPanel.mouseX, MyPanel.mouseY);
    }
}