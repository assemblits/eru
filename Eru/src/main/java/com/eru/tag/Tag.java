package com.eru.tag;

import com.eru.comm.device.Address;
import com.eru.util.MathUtil;
import javafx.beans.property.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Created by mtrujillo on 8/05/14.
 */
@Entity
@Table(name = "tag", schema = "public")
public class Tag {
    /* ********** Static Fields ********** */
    private static final int DEFAULT_DECIMALS    = 2;

    public enum Type {INPUT, MASK, MATH, STATUS, OUTPUT, LOGICAL}

    /* ********** Dynamic Fields ********** */
    private IntegerProperty                 id;
    private final StringProperty            name;
    private final BooleanProperty           enabled;
    private final StringProperty            description;
    private final StringProperty            value;
    private final IntegerProperty           decimals;
    private final ObjectProperty<Timestamp> timestamp;
    private final ObjectProperty<Type>      type;
    private final StringProperty            status;
    private final ObjectProperty<Address>   linkedAddress;
    private final ObjectProperty<Tag>       linkedTag;
    private final StringProperty            script;
    private final IntegerProperty           mask;
    private final DoubleProperty            scaleFactor;
    private final DoubleProperty            scaleOffset;
    private final BooleanProperty           alarmEnabled;
    private final StringProperty            alarmScript;
    private final BooleanProperty           alarmed;
    private final BooleanProperty           historicalEnabled;
    private StringProperty                  groupName;

    /* ********** Constructors ********** */
    public Tag() {
        this.id                 = new SimpleIntegerProperty(this, "id", 0);
        this.name               = new SimpleStringProperty(this, "name", "");
        this.enabled            = new SimpleBooleanProperty(this, "enabled", true);
        this.description        = new SimpleStringProperty(this, "description", "");
        this.value              = new SimpleStringProperty(this, "value", "");
        this.decimals           = new SimpleIntegerProperty(this, "decimals", DEFAULT_DECIMALS);
        this.timestamp          = new SimpleObjectProperty<>(this, "timestamp", Timestamp.from(Instant.now()));
        this.type               = new SimpleObjectProperty<>();
        this.status             = new SimpleStringProperty(this, "status", "");
        this.linkedAddress      = new SimpleObjectProperty<>();
        this.linkedTag          = new SimpleObjectProperty<>();
        this.script             = new SimpleStringProperty(this, "script", "");
        this.mask               = new SimpleIntegerProperty(this, "mask", 0);
        this.scaleFactor        = new SimpleDoubleProperty(this, "scaleFactor", 1.0);
        this.scaleOffset        = new SimpleDoubleProperty(this, "scaleOffset", 0.0);
        this.alarmEnabled       = new SimpleBooleanProperty(this, "alarmEnabled", false);
        this.alarmScript        = new SimpleStringProperty(this, "alarmScript", "");
        this.alarmed            = new SimpleBooleanProperty(this, "alarmed", false);
        this.historicalEnabled  = new SimpleBooleanProperty(this, "historicalEnabled", false);
        this.groupName          = new SimpleStringProperty(this, "groupName", "");
    }

    /* ********** Properties ********** */
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(int id) {
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

    @Column(name = "enabled")
    public boolean getEnabled() {
        return enabled.get();
    }
    public BooleanProperty enabledProperty() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    @Column(name = "description")
    public String getDescription() {
        return description.get();
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    public void setDescription(String description) {
        this.description.set(description);
    }

    @Column(name = "value")
    public String getValue() {
        return value.get();
    }
    public StringProperty valueProperty() {
        return value;
    }
    public void setValue(String value) {
        try{
            // Is a number
            double numericValue = Double.parseDouble(value);
            double roundedValue = MathUtil.round(numericValue * getScaleFactor() + getScaleOffset(), getDecimals());
            this.value.set(String.valueOf(roundedValue));
        } catch (NumberFormatException e){
            // Is not a number
            this.value.set(value);
        } catch (Exception e){
            // Is an error
            this.status.set(e.getLocalizedMessage());
            this.value.set("????");
        }
    }

    @Column(name = "decimals")
    public int getDecimals() {
        return decimals.get();
    }
    public IntegerProperty decimalsProperty() {
        return decimals;
    }
    public void setDecimals(int decimals) {
        this.decimals.set(decimals);
    }

    @Column(name = "time_stamp")
    public Timestamp getTimestamp() {
        return timestamp.get();
    }
    public ObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp.set(timestamp);
    }

    @Column(name = "tag_type")
    public String getTypeName() {
        return getType() == null ? "" : getType().name();
    }
    public void setTypeName(String name){
        type.set(name == null  || name.isEmpty() ? Type.INPUT : Type.valueOf(name));
    }
    @Transient
    public Type getType() {
        return type.get();
    }
    public ObjectProperty<Type> typeProperty() {
        return type;
    }
    public void setType(Type type) {
        this.type.set(type);
    }

    @Column(name = "status")
    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }
    public void setStatus(String statusName) {
        this.status.set(statusName);
    }

    @OneToOne(cascade = CascadeType.ALL)
    public Address getLinkedAddress() {
        return linkedAddress.get();
    }
    public ObjectProperty<Address> linkedAddressProperty() {
        return linkedAddress;
    }
    public void setLinkedAddress(Address linkedAddress) {
        this.linkedAddress.set(linkedAddress);
    }

    @OneToOne
    public Tag getLinkedTag() {
        return linkedTag.get();
    }
    public ObjectProperty<Tag> linkedTagProperty() {
        return linkedTag;
    }
    public void setLinkedTag(Tag linkedTag) {
        this.linkedTag.set(linkedTag);
    }

    @Column(name = "script", length = 1024)
    public String getScript() {
        return script.get();
    }
    public StringProperty scriptProperty() {
        return script;
    }
    public void setScript(String script) {
        this.script.set(script);
    }

    @Column(name = "mask")
    public int getMask() {
        return mask.get();
    }
    public IntegerProperty maskProperty() {
        return mask;
    }
    public void setMask(int mask) {
        this.mask.set(mask);
    }

    @Column(name = "scale_factor")
    public double getScaleFactor() {
        return scaleFactor.get();
    }
    public DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor.set(scaleFactor);
    }

    @Column(name = "scale_offset")
    public double getScaleOffset() {
        return scaleOffset.get();
    }
    public DoubleProperty scaleOffsetProperty() {
        return scaleOffset;
    }
    public void setScaleOffset(double scaleOffset) {
        this.scaleOffset.set(scaleOffset);
    }

    @Column(name = "alarm_enabled")
    public boolean getAlarmEnabled() {
        return alarmEnabled.get();
    }
    public BooleanProperty alarmEnabledProperty() {
        return alarmEnabled;
    }
    public void setAlarmEnabled(boolean alarmEnabled) {
        this.alarmEnabled.set(alarmEnabled);
    }

    @Column(name = "alarm_script")
    public String getAlarmScript() {
        return alarmScript.get();
    }
    public StringProperty alarmScriptProperty() {
        return alarmScript;
    }
    public void setAlarmScript(String alarmScript) {
        this.alarmScript.set(alarmScript);
    }

    @Column(name = "alarmed")
    public boolean getAlarmed() {
        return alarmed.get();
    }
    public BooleanProperty alarmedProperty() {
        return alarmed;
    }
    public void setAlarmed(boolean alarmed) {
        this.alarmed.set(alarmed);
    }

    @Column(name = "historical_enabled")
    public boolean getHistoricalEnabled() {
        return historicalEnabled.get();
    }
    public BooleanProperty historicalEnabledProperty() {
        return historicalEnabled;
    }
    public void setHistoricalEnabled(boolean historicalEnabled) {
        this.historicalEnabled.set(historicalEnabled);
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

    /* ********** Getters and Setters ********** */
    @Override
    public String toString() {
        return getName();
    }

}