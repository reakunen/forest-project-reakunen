import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class DudeFull extends Entity implements ActivityEntity, Schedules {

    public DudeFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);

    }
    public void scheduleActions( EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    private void transformFull( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = Functions.createDudeNotFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit(), this.getImages());
        world.removeEntity( scheduler, this);
        world.addEntity(dude);
        dude.scheduleActions( scheduler, world, imageStore);
    }

    private boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {

            return true;
        } else {
            Point nextPos = this.nextPositionDude( world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }


    private Point nextPositionDude( WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
//        PathingStrategy strat = new SingleStepPathingStrategy();
        Predicate<Point> canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getClass() == Stump.class);
        BiPredicate<Point, Point> withinReach = (p1, p2) -> (p1.adjacentTo(p2));
        List<Point> path = strat.computePath( this.getPosition(), destPos,
                canPassThrough,
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS );
        System.out.println("DudeFull Position: " + path);
        if (path.size() != 0 ) {
            return path.get(0);
        }
        return this.getPosition();
//        int horiz = Integer.signum(destPos.x - this.getPosition().x);
//        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);
//    //idk here with the getkind()
//        if (horiz == 0 || world.isOccupied( newPos) && world.getOccupancyCell( newPos).getClass() != Stump.class) {
//            int vert = Integer.signum(destPos.y - this.getPosition().y);
//            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
//
//            if (vert == 0 || world.isOccupied( newPos) && world.getOccupancyCell( newPos).getClass() != Stump.class) {
//                newPos = this.getPosition();
//            }
//        }
//
//        return newPos;
    }

    public void executeActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = Functions.findNearest(world, this.getPosition(), new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && this.moveToFull( world, fullTarget.get(), scheduler)) {
            this.transformFull( world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }
}
