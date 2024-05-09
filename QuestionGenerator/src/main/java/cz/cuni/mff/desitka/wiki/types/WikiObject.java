package cz.cuni.mff.desitka.wiki.types;

import java.nio.file.Path;

/**
 * This abstract class represents a generic object in the wiki.
 * It provides basic properties and methods that are common to all wiki objects.
 */
public abstract class WikiObject {
    /**
     * The name of the WikiObject.
     */
    protected final String name;

    /**
     * The path of the WikiObject.
     */
    protected final Path path;

    /**
     * Constructs a WikiObject with a specified name and path.
     * The name is modified to replace spaces with underscores.
     * The path is resolved with the name of the WikiObject.
     * @param name The name of the WikiObject.
     * @param path The path of the WikiObject.
     */
    public WikiObject(String name, Path path) {
        this.name = name.replace(" ", "_");
        this.path = path.resolve(this.name);
    }

    /**
     * Constructs a WikiObject with a specified path.
     * The name of the WikiObject is derived from the filename of the path.
     * @param path The path of the WikiObject.
     */
    public WikiObject(Path path) {
        this.name = path.getFileName().toString();
        this.path = path;
    }

    /**
     * Returns the name of the WikiObject.
     * @return The name of the WikiObject.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the WikiObject in lowercase.
     * @return The name of the WikiObject in lowercase.
     */
    public String getLowerName() {
        return name.toLowerCase();
    }

    /**
     * Returns the path of the WikiObject.
     * @return The path of the WikiObject.
     */
    public Path getPath() {
        return path;
    }
}