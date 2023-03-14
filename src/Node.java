import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node implements Comparable<Node> {
    private double g; // g-val distance from starting node
    private double h; // h-val heuristic distance from end node
    private double f; // total distance, f = h + g
    private Node prev; // previous
    private Point p;

    static public ArrayList<Point> getPrevious(Node n) {
        ArrayList<Point> points = new ArrayList<>();
        Node temp = n;
        while (temp != null) {
            if (temp.prev == null)
                break;
            points.add(0,temp.p);
            temp = temp.prev;
        }

        return points;
    }
    public void setG(double g) {
        this.g = g;
    }
    public void setF(double f) {
        this.f = f;
    }
    public Node(double a, double b, double c, Node d, Point e ) {
        g = a;
        h = b;
        f = c;
        prev = d;
        p = e;
    }
    public boolean contains(Point t) {
        return this.p.equals(t);
    }
    public String toString()
    {
        return "(" + p.x + "," + p.y + ")";
    }

    public Point getPoint() { return p; }
    public double getG() { return g; }
    public double getH() { return h; }
    public double getF() { return f; }

    @Override
    public int compareTo(Node o) {
        return this.f > o.f ? -1 : (this.f == o.f ? 0 : 1);
    }
}
