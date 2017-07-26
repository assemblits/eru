package com.marlontrujillo.eru.gui.toolbars.tag;

import com.marlontrujillo.eru.tag.Tag;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.util.Date;

/**
 * Created by mtrujillo on 7/25/17.
 */
public class TagGroup {
    private StringProperty         name;
    private ListProperty<Tag>      tags;
    private ObjectProperty<Long>   size;
    private ListProperty<TagGroup> childs;
    private ObjectProperty<Date>   lastModified;

    public TagGroup() {
        this("new");
    }

    public TagGroup(String name) {
        this.name         = new SimpleStringProperty(name);
        this.tags         = new SimpleListProperty<>();
        this.size         = new SimpleObjectProperty<>(0L);
        this.childs       = new SimpleListProperty<>();
        this.lastModified = new SimpleObjectProperty<>(Date.from(Instant.now()));

        this.tags.addListener((InvalidationListener) o -> {
            size.setValue((long) getTags().size());
            lastModified.setValue(Date.from(Instant.now()));
        });
    }

    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableList<Tag> getTags() {
        return tags.get();
    }
    public ListProperty<Tag> tagsProperty() {
        return tags;
    }
    public void setTags(ObservableList<Tag> tags) {
        this.tags.set(tags);
    }

    public Long getSize() {
        return size.get();
    }
    public ObjectProperty<Long> sizeProperty() {
        return size;
    }
    public void setSize(Long size) {
        this.size.set(size);
    }

    public ObservableList<TagGroup> getChilds() {
        return childs.get();
    }
    public ListProperty<TagGroup> childsProperty() {
        return childs;
    }
    public void setChilds(ObservableList<TagGroup> childs) {
        this.childs.set(childs);
    }

    public Date getLastModified() {
        return lastModified.get();
    }
    public ObjectProperty<Date> lastModifiedProperty() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified.set(lastModified);
    }
}
