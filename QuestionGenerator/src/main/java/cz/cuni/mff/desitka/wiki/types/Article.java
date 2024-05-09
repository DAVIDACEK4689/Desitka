package cz.cuni.mff.desitka.wiki.types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class represents an article in the wiki.
 * It extends the WikiObject class and includes additional functionality for managing views.
 */
public class Article extends WikiObject {
    private final int views;

    /**
     * Constructs an Article with a specified name and path.
     * The views of the Article are initialized to 0.
     * @param name The name of the Article.
     * @param path The path of the Article.
     */
    public Article(String name, Path path) {
        super(name, path);
        this.views = 0;
    }

    /**
     * Constructs an Article with a specified path and views.
     * The name of the Article is derived from the filename of the path.
     * @param path The path of the Article.
     * @param views The views of the Article.
     */
    public Article(Path path, int views) {
        super(path.getFileName().toString().split("\\.")[0], path);
        this.views = views;
    }

    /**
     * Returns the views of the Article.
     * @return The views of the Article.
     */
    public int getViews() {
        return views;
    }

    /**
     * Saves the views of the Article to a specified path.
     * The views are written to a file with the name of the Article and a ".txt" extension.
     * @param keysPath The path where the views are saved.
     */
    public void save(Path keysPath) {
        try {
            Files.write(keysPath.resolve(name + ".txt"), String.valueOf(views).getBytes());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if this Article is equal to a specified object.
     * The object is considered equal if it is an Article and has the same name as this Article.
     * @param obj The object to compare with.
     * @return True if the object is equal to this Article, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Article)) {
            return false;
        }
        Article article = (Article) obj;
        return name.equals(article.name);
    }

    /**
     * Returns the hash code of this Article.
     * The hash code is calculated based on the name of the Article.
     * @return The hash code of this Article.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}