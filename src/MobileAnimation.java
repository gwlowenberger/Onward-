public class MobileAnimation extends Animation {
    private final Vector[] motions;

    public MobileAnimation() {
        super();
        motions = new Vector[rows * columns];
    }

    public MobileAnimation(Vector[] motions) {
        super();
        this.motions = motions;
    }

    public MobileAnimation(String path, Vector dimensions, Vector[] motions) {
        super(path, dimensions);
        this.motions = motions;
    }

    public MobileAnimation(String path, Vector dimensions, int maxDistance, Vector[] motions) {
        super(path, dimensions, maxDistance);
        this.motions = motions;
    }

    public MobileAnimation(MobileAnimation a) {
        super(a);

        motions = a.motions; //doesn't clone
    }

    //always use 'this' as a parameter
    @Override
    public boolean Step(AnimatedActor a) {
        distance++;

        if (distance >= maxDistance) {
            distance = 0;

            if (currentFrame < motions.length) {
                a.Move(motions[currentFrame]);
            }
            
            currentFrame++;

            if (currentFrame >= columns * rows && currentFrame >= motions.length) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean Finish(AnimatedActor a) {
        for (int i = currentFrame; i < motions.length; i++) {
            a.Move(motions[currentFrame]);
        }
        
        return super.Finish(a);
    }

    @Override
    @SuppressWarnings("ImplicitArrayToString")
    public String toString() {
        return super.toString() + motions;
    }
}