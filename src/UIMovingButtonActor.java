public class UIMovingButtonActor extends AnimatedActor {
    //properties
    public int state;

    //constructors
    public UIMovingButtonActor() {
        super();
        state = MyPanel.STATE_NONE;
    }

    public UIMovingButtonActor(Vector position, Vector collisionRect, Vector colliderOffset, Vector renderRect, Animation[] animations, int state) {
        super(position, collisionRect, colliderOffset, renderRect, animations);
        this.state = state;
    }

    public UIMovingButtonActor(AnimatedActor a, int state) {
        super(a);
        this.state = state;
    }

    public UIMovingButtonActor(AnimatedActor a) {
        super(a);
        this.state = MyPanel.STATE_NONE;
    }

    //instance methods
    @Override
    public boolean OnClick() {
        if (state != MyPanel.STATE_NONE) {
            MyPanel.gameState = state;
        }
        ChangeAnimation(ANIMATION_ACT_CLICK);
        return true;
    }
}
