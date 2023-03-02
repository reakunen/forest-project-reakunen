import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public abstract class Entity {
//    private EntityKind kind;
    private String id;
    private Point position;
    private final List<PImage> images;
    public List<PImage> getImages() {
        return images;
    }
    private int imageIndex;
    private final int resourceLimit;
    public int getResourceLimit() {
        return this.resourceLimit;
    }
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


    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

//    public int getImageIndex() {
//        return this.imageIndex;
//    }
    public double getActionPeriod() {
        return this.actionPeriod;
    }
    public int getHealthLimit() {
        return this.healthLimit;
    }

    public Entity( String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
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
    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    public double getAnimationPeriod() {
            return this.animationPeriod;
    }
    public void increaseResourceCount(int i) {
        this.resourceCount += i;
    }
    public int getResourceCount() {
        return this.resourceCount;
    }
    public void increaseHealth(int i) {
        this.health += i;
    }
    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

}
