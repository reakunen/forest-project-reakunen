import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new ArrayList<>();
        PriorityQueue<Node> openList = new PriorityQueue<>(new PointComparator());
//        HashMap<Point, Node> closedMap = new HashMap<>();
//        HashMap<Point, Node> openMap = new HashMap<>();
        HashSet<Point> closedMap = new HashSet<>();
        HashSet<Point> openMap = new HashSet<>();
        double s = start.getDistance(end);
        Node curr = new Node(0, s, s, null, start );
        // Step 2: add node into open list, currentNode
        openList.add(curr);

//        while (!withinReach.test(curr.getPoint(), end)) {
        while (!openList.isEmpty()) {
            System.out.println(openList);
            //Step 3: Analyze all valid adjacent nodes that are not on closed List
            curr = openList.poll(); // removes it from open list
            if (withinReach.test(curr.getPoint(), end)) { // check if it is at the end
                path = Node.getPrevious(curr);
                System.out.println("return list: " + path);
                return path;
            }
            List <Point> neighbors = potentialNeighbors.apply(curr.getPoint()).filter(canPassThrough)
                    .collect(Collectors.toList());
//            List<Point> neighbors = getNeighbors(curr).stream().filter(canPassThrough).collect(Collectors.toList());
            //3 for each neighbor that is not in the closed map
            for (Point p : neighbors) {
                double gValue = curr.getG() + 1;
                double hValue = p.getDistance(end);
                Node newnode = new Node(gValue, hValue, hValue+gValue, curr, p );

                // if node is already used
                if (closedMap.contains(p)) {
                    continue;
                }
                if (openList.contains(p)) {
                    double newGValue = p.getDistance(start);
                    if (gValue < newGValue) { //gvalue is the distance away from starting node
                        newnode.setG(newGValue);
                    }
                }
                newnode.setF(newnode.getG() + newnode.getH());
                openList.add(newnode);
            }
            closedMap.add(curr.getPoint());
            openMap.remove(curr.getPoint());
        }
        return path; // return path, if empty, cannot get to goal
    }
}
