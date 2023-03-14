/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
    /**
     * Returns true if this point is adjacent to the given point, which means they share an edge or a corner.
     */
    public boolean adjacentTo(Point b) {
        int dx = Math.abs(this.x - b.x);
        int dy = Math.abs(this.y - b.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1) || (dx == 1 && dy == 1);
    }

//    public double getDistance(Point a) {
//        return Math.sqrt(Math.pow((this.x - a.x), 2) + (Math.pow((this.y - a.y), 2)));
//    }

    public int getDistance(Point a) {
        return Math.abs(a.x-this.x) + Math.abs(a.y-this.y);
    }
}
