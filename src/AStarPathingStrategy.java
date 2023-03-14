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
        HashMap<Point, Node> closedMap = new HashMap<Point, Node>();
        HashMap<Point, Node> openMap = new HashMap<Point, Node>();
        int s = start.getDistance(end);
        Node curr = new Node(0, s, s, null, start );
        // Step 2: add node into open list, currentNode
        openList.add(curr);
        openMap.put(curr.getPoint(), curr);
        curr = openList.peek(); // removes it from open list

        while (curr != null && !withinReach.test(curr.getPoint(), end)) {
//        while (!openList.isEmpty()) {
            //Step 3: Analyze all valid adjacent nodes that are not on closed List
            if (withinReach.test(curr.getPoint(), end)) { // check if it is at the end
                path = Node.getPrevious(curr);
//                System.out.println(path); //path
                return path;
            }
            List <Point> neighbors = potentialNeighbors.apply(curr.getPoint()).filter(canPassThrough).collect(Collectors.toList());
//            List<Point> neighbors = getNeighbors(curr).stream().filter(canPassThrough).collect(Collectors.toList());
            //3 for each neighbor that is not in the closed map
            for (Point p : neighbors) {
                int gValue = curr.getG() + 1;
                int hValue = p.getDistance(end);
                Node newnode = new Node(gValue, hValue, hValue+gValue, curr, p );
                boolean insidemap = false;
                // if node is already used
                if (closedMap.containsKey(p)) {
                    continue;
                }
                if (openMap.containsKey(p)) {
                    //is on open, replace it if g value is better
                    insidemap = true;
                    int oldnodeG = openMap.get(p).getG();
                    if (newnode.getG() > oldnodeG) { //gvalue is the distance away from starting node
//                        newnode.setG(newGValue);
                        continue;
                    }
                }
                newnode.setF(newnode.getG() + newnode.getH());
                if (insidemap) {
                    openList.remove(openMap.get(p));
                    //recalaculate f value
                    openList.add(newnode);
                    openMap.replace(p, newnode);
                }
                else {
                    openList.add(newnode);
                    openMap.put(newnode.getPoint(), newnode);
                }
            }
            closedMap.put(curr.getPoint(), curr);
            openMap.remove(curr.getPoint(), curr);
            openList.remove(curr);
            curr = openList.peek(); // removes it from open list

        }
        path = Node.getPrevious(curr);
        return path; // return path, if empty, cannot get to goal
    }
}
