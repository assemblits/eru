/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.entities;

import javafx.beans.property.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.persistence.*;

@Entity
@Table(name = "display", schema = "public")
public class Display {

    private final IntegerProperty id;
    private StringProperty name;
    private StringProperty groupName;
    private BooleanProperty initialDisplay;
    private ObjectProperty<StageType> stageType;
    private ObjectProperty<Parent> fxNode;

    public Display() {
        this.id = new SimpleIntegerProperty(this, "display_id");
        this.name = new SimpleStringProperty(this, "name", "");
        this.groupName = new SimpleStringProperty(this, "name", "");
        this.initialDisplay = new SimpleBooleanProperty(this, "initial_display", false);
        this.stageType = new SimpleObjectProperty<>(this, "stageType", StageType.NEW);
        this.fxNode = new SimpleObjectProperty<>(this, "fxNode", null);
    }

    @Id
    @Column(name = "display_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    @Column(name = "name")
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName.get();
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    @Column(name = "initial_display")
    public boolean isInitialDisplay() {
        return initialDisplay.get();
    }

    public BooleanProperty initialDisplayProperty() {
        return initialDisplay;
    }

    public void setInitialDisplay(boolean initialDisplay) {
        this.initialDisplay.set(initialDisplay);
    }

    @Transient
    public StageType getStageType() {
        return stageType.get();
    }

    public ObjectProperty<StageType> stageTypeProperty() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType.set(stageType);
    }

    @Column(name = "stage_type")
    public String getStageTypeName() {
        return getStageType() == null ? "" : getStageType().name();
    }

    public void setStageTypeName(String name) {
        setStageType(name == null || name.isEmpty() ? StageType.REPLACE : StageType.valueOf(name));
    }

    @Transient
    public Parent getFxNode() {
        return fxNode.get();
    }

    public ObjectProperty<Parent> fxNodeProperty() {
        return fxNode;
    }

    public void setFxNode(Parent fxNode) {
        this.fxNode.set(fxNode);
    }

    @Transient
    public StageStyle stageStyle(){
        switch (getStageType()) {
            case REPLACE:
                return StageStyle.TRANSPARENT;
            case NEW:
                return StageStyle.DECORATED;
            case UTILITY:
                return  StageStyle.UTILITY;
        }
        return null;
    }

    public enum StageType {REPLACE, NEW, UTILITY}

    @Override
    public String toString() {
        return getGroupName()+":"+getName();
    }
}
