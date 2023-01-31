import java.util.*;

import processing.core.PImage;

public final class ImageStore {
    private Map<String, List<PImage>> images;
    private final List<PImage> defaultImages;

    public Map<String, List<PImage>> getImages() {
        return images;
    }

    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    public List<PImage> getImageList( String key) {
        return this.images.getOrDefault(key, this.defaultImages);
    }
}
