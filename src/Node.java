import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node implements Comparable<Node> {
    private int g; // g-val distance from starting node
    private int h; // h-val heuristic distance from end node
    private int f; // total distance, f = h + g
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
    public void setG(int g) {
        this.g = g;
    }
    public void setF(int f) {
        this.f = f;
    }
    public Node(int a, int b, int c, Node d, Point e ) {
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
    public int getG() { return g; }
    public int getH() { return h; }
    public int getF() { return f; }

    @Override
    public int compareTo(Node o) {
        return this.f > o.f ? -1 : 1;
    }
}
