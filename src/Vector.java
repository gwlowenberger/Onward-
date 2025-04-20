public class Vector {
    //properties
    public float x;
    public float y;
    
    //static Vectors
    public static final Vector zero = new Vector();
    public static final Vector north = new Vector(0, -1);
    public static final Vector northEast = new Vector(1, -1);
    public static final Vector east = new Vector(1, 0);
    public static final Vector southEast = new Vector(1, 1);
    public static final Vector south = new Vector(0, 1);
    public static final Vector southWest = new Vector(-1, 1);
    public static final Vector west = new Vector(-1, 0);
    public static final Vector northWest = new Vector(-1, -1);
    public static final Vector defaultSpriteDimensions = new Vector(32, 32);
    public static final Vector quarterSpriteDimensions = new Vector(16, 16);
    public static final Vector defaultSpriteCollider = new Vector(16,8);
    public static final Vector defaultSpriteColliderOffset = new Vector(0, 8);
    public static final Vector defaultNumberRect = new Vector(5, 7);

    //constructors
    public Vector() {
        x = 0;
        y = 0;
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector a) {
        x = a.x;
        y = a.y;
    }

    //instance methods
    public float SqrDistanceTo(Vector a) {
        return ((a.x - x) * (a.x - x)) + ((a.y - y) * (a.y - y));
    }

    public float DistanceTo(Vector a) {
        return (float) Math.sqrt(SqrDistanceTo(a));
    }

    public Vector Multiply(Vector b) {
        x *= b.x;
        y *= b.y;
        return this;
    }

    public Vector Multiply(float b) {
        x *= b;
        y *= b;
        return this;
    }

    public Vector Divide(Vector b) {
        if (b.x != 0) {
            x /= b.x;
        }
        else
        {
            x = 0;
        }

        if (b.y != 0) {
            y /= b.y;
        }
        else
        {
            y = 0;
        }
        
        return this;
    }

    public Vector Divide(float b) {
        if (b == 0) {
            return Vector.zero;
        }
        else
        {
            x /= b;
            y /= b;
            return this;
        }
    }

    public Vector Add(Vector b) {
        x += b.x;
        y += b.y;
        return this;
    }

    public Vector Add(float b) {
        x += b;
        y += b;
        return this;
    }

    public Vector Subtract(Vector b) {
        x -= b.x;
        y -= b.y;
        return this;
    }

    public Vector Subtract(float b) {
        x -= b;
        y -= b;
        return this;
    }

    public float Interpolate(float p) {

        return x + p * (x - y);
    }

    public boolean IsAdjacent(Vector a, int distance) {
        if (x == a.x) {
            if (Math.abs(a.y - y) <= distance) {
                return true;
            }
        }
        
        if (y == a.y) {
            if (Math.abs(a.x - x) <= distance) {
                return true;
            }
        }

        return false;
    }

    public boolean Equals(Vector a) {
        return x == a.x && y == a.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Vector Normalize() {
        Vector a = Normalize(this);
        x = a.x;
        y = a.y;
        return this;
    }

    public float Magnitude() {
        return Magnitude(this);
    }

    public Vector Round() {
        Vector a = Round(this);
        x = a.x;
        y = a.y;
        return this;
    }

    //will always return false if bounds.x > bounds.y (the min is greater than the max)
    public boolean WithinBounds(float a) {
        return a >= x && a <= y;
    }

    //static methods
    public static float SqrDistance(Vector a, Vector b) {
        return ((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y));
    }

    public static float Distance(Vector a, Vector b) {
        return (float) Math.sqrt(SqrDistance(a, b));
    }

    public static Vector Multiply(Vector a, Vector b) {
        return new Vector(a.x * b.x, a.y * b.y);
    }

    public static Vector Multiply(Vector a, float b) {
        return new Vector(a.x * b, a.y * b);
    }

    public static Vector Divide(Vector a, Vector b) {
        if (b.x == 0 && b.y == 0) {
            return Vector.zero;
        }
        else if (b.x != 0 && b.y == 0) {
            return new Vector(a.x / b.x, 0);
        }
        else if (b.x == 0 && b.y != 0) {
            return new Vector(0, a.y / b.y);
        }
        else {
            return new Vector(a.x / b.x, a.y / b.y);
        }
    }

    public static Vector Divide(Vector a, float b) {
        if (b == 0) {
            return Vector.zero;
        }
        else {
            return new Vector(a.x / b, a.y / b);
        }
    }

    public static Vector Add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y);
    }

    public static Vector Add(Vector a, float b) {
        return new Vector(a.x + b, a.y + b);
    }

    public static Vector Subtract(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }

    public static Vector Subtract(Vector a, float b) {
        return new Vector(a.x - b, a.y - b);
    }

    public static float Interpolate(Vector a, float b) {

    
        return a.x + b * (a.x - a.y);
    }

    public static boolean IsAdjacent(Vector a, Vector b, int distance) {
        return a.IsAdjacent(b, distance);
    }

    public static boolean Equals(Vector a, Vector b) {
        return b.Equals(a);
    }

    public static Vector Normalize(Vector a) {
        if (a.Equals(Vector.zero)) {
            return Vector.zero;
        }

        return Divide(a, Magnitude(a));
    }

    public static float Magnitude(Vector a) {
        return (float) Math.sqrt((a.x * a.x) + (a.y * a.y));
    }

    public static Vector Round(Vector a) {
        return new Vector(Math.round(a.x), Math.round(a.y));
    }
}