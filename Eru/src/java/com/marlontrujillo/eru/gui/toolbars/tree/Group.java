package com.marlontrujillo.eru.gui.toolbars.tree;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mtrujillo on 7/25/17.
 */
@Entity
@Table(name = "group", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Group {

    public enum Type {ROOT, CONNECTION, DEVICE, TAG, USER}

    private int         id;
    private String      name;
    private Type        type;
    private Group       parent;
    private List<Group> children;
    private Date        lastModified;

    public Group() {
        this.children = new ArrayList<>();
    }

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }
    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    @OneToOne(cascade= CascadeType.ALL, orphanRemoval = true)
    public Group getParent() {
        return parent;
    }
    public void setParent(Group parent) {
        this.parent = parent;
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Group> getChildren() {
        return children;
    }
    public void setChildren(List<Group> children) {
        this.children = children;
    }

    @Column(name = "last_modified")
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
