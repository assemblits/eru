package com.eru.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "display", schema = "public")
public class Display {

    @Id
    @Column(name = "display_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "deleted")
    private boolean deleted;
}
