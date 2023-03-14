import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node implements Comparable<Node> {
    private int g; // g-val distance from starting node
    private int h; // h-val heuristic distance from end node
    private int f; // total distance, f = h + g
    private Node prev; // previous
    private Point p;

    public int hashCode()
    {
        int result = 17;
        result = result * 31 + g;
        result = result * 31 + h;
        result = result * 31 + f;
        result = result * 31 + p.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return g == node.g &&
                h == node.h &&
                f == node.f &&
                Objects.equals(prev, node.prev) &&
                Objects.equals(p, node.p);
    }
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
        if (this.f == o.f)
            return 0;
        return this.f > o.f ? -1 : 1;
    }
}
