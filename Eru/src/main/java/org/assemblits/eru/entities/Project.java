package org.assemblits.eru.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 7/30/17.
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "project", schema = "public")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String name;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    EruGroup group;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Device> devices;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Connection> connections;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Tag> tags;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<User> users;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Display> displays;

    public Project() {
        this.id = null;
        this.name = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        this.group = new EruGroup();
        this.devices = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.users = new ArrayList<>();
        this.displays = new ArrayList<>();
    }

}