import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity {
    private EntityKind kind;
    private String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int resourceLimit;
    private int resourceCount;
    private final double actionPeriod;

    private final double animationPeriod;
    private int health;
    private final int healthLimit;
    public int getHealth() {
        return health;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public EntityKind getKind() {
        return kind;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public Entity(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public void scheduleActions( EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (this.kind) {
            case DUDE_FULL, DUDE_NOT_FULL, FAIRY, SAPLING, TREE:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
                break;

            case OBSTACLE:
                scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
                break;
            default:
        }
    }

    private boolean transformNotFull( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Entity dude = Functions.createDudeFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

            world.removeEntity( scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity( dude);
            dude.scheduleActions( scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private void transformFull( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Functions.createDudeNotFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

        world.removeEntity( scheduler, this);

        world.addEntity( dude);
        dude.scheduleActions( scheduler, world, imageStore);
    }

    private boolean transformPlant( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.kind == EntityKind.TREE) {
            return this.transformTree( world, scheduler, imageStore);
        } else if (this.kind == EntityKind.SAPLING) {
            return this.transformSapling( world, scheduler, imageStore);
        } else {
            throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", this));
        }
    }

    private  boolean transformTree( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.id, this.position, imageStore.getImageList( Functions.STUMP_KEY));

            world.removeEntity( scheduler, this);

            world.addEntity( stump);

            return true;
        }

        return false;
    }

    private boolean transformSapling( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.id, this.position, imageStore.getImageList( Functions.STUMP_KEY));

            world.removeEntity( scheduler, this);

            world.addEntity( stump);

            return true;
        } else if (this.health >= this.healthLimit) {
            Entity tree = Functions.createTree(Functions.TREE_KEY + "_" + this.id, this.position, Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN), Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN), Functions.getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), imageStore.getImageList( Functions.TREE_KEY));

            world.removeEntity( scheduler, this);

            world.addEntity( tree);
            this.scheduleActions( scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean moveToFairy( WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.position, target.position)) {
            world.removeEntity( scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy( world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }

    private  boolean moveToNotFull( WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.position, target.position)) {
            this.resourceCount += 1;
            target.health--;
            return true;
        } else {
            Point nextPos = this.nextPositionDude( world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }

    private boolean moveToFull( WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.position, target.position)) {

            return true;
        } else {
            Point nextPos = this.nextPositionDude( world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }

    private  Point nextPositionFairy( WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied( newPos)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied( newPos)) {
                newPos = this.position;
            }
        }
        return newPos;
    }

    private  Point nextPositionDude( WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied( newPos) && world.getOccupancyCell( newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied( newPos) && world.getOccupancyCell( newPos).kind != EntityKind.STUMP) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    public void executeSaplingActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!this.transformPlant( world, scheduler, imageStore)) {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeTreeActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transformPlant( world, scheduler, imageStore)) {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeDudeNotFullActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = Functions.findNearest(world, this.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !this.moveToNotFull( world, target.get(), scheduler) || !this.transformNotFull( world, scheduler, imageStore)) {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeDudeFullActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = Functions.findNearest(world, this.position, new ArrayList<>(List.of(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && this.moveToFull( world, fullTarget.get(), scheduler)) {
            this.transformFull( world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    public double getAnimationPeriod() {
        switch (this.kind) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return this.animationPeriod;
            default:
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", this.kind));
        }
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = Functions.findNearest(world, position, new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (this.moveToFairy( world, fairyTarget.get(), scheduler)) {

                Entity sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, imageStore.getImageList( Functions.SAPLING_KEY), 0);

                world.addEntity(sapling);
                sapling.scheduleActions( scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
    }
}
