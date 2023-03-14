import java.util.Comparator;

public class PointComparator implements Comparator<Node> {
    public int compare(Node n1, Node n2) {
        return Integer.compare(n1.getF(), n2.getF());
    }
}
