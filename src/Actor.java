public class Actor {
    //properties
    public Vector position; //top left corner of object
    public Vector collisionRect; //dimensions of collider
    public Vector colliderOffset; //offset of collider

    //constructors
    public Actor() {
        position = Vector.zero;
        collisionRect = Vector.zero;
        colliderOffset = Vector.zero;
    }

    public Actor(Vector position) {
        this.position = new Vector(position);
        collisionRect = Vector.zero;
        colliderOffset = Vector.zero;
    }

    public Actor(Vector position, Vector collisionRect) {
        this.position = new Vector(position);
        this.collisionRect = collisionRect;
        colliderOffset = Vector.zero;
    }

    public Actor(Vector position, Vector collisionRect, Vector colliderOffset) {
        this.position = new Vector(position);
        this.collisionRect = collisionRect;
        this.colliderOffset = colliderOffset;
    }

    public Actor(Actor a) {
        this.position = new Vector(a.position);
        this.collisionRect = new Vector(a.collisionRect);
        this.colliderOffset = new Vector(a.colliderOffset);
    }

    //instance methods
    public boolean DidCollide(Vector point) {
        return DidCollide(point.x, point.y);
    }

    public boolean DidCollide(float x, float y) {
        return x >= position.x + colliderOffset.x && x <= collisionRect.x + position.x + colliderOffset.x && y >= position.y + colliderOffset.y && y <= collisionRect.y + position.y + colliderOffset.y;
    }

    public boolean Move(Vector vector) {
        position.Add(vector);
        return true;
    }
    
    public boolean OnClick() {
        return true;
    } //to be overriden

    @Override
    public String toString() {
        return position.toString();
    }
}