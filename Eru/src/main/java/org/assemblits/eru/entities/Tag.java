package org.assemblits.eru.entities;

import org.assemblits.eru.util.MathUtil;
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
    private static final int DEFAULT_DECIMALS = 2;
    private final StringProperty name;
    private final BooleanProperty enabled;
    private final StringProperty description;
    private final StringProperty value;
    private final IntegerProperty decimals;
    private final ObjectProperty<Timestamp> timestamp;
    private final ObjectProperty<Type> type;
    private final StringProperty status;
    private final ObjectProperty<Address> linkedAddress;
    private final ObjectProperty<Tag> linkedTag;
    private final StringProperty script;
    private final IntegerProperty mask;
    private final DoubleProperty scaleFactor;
    private final DoubleProperty scaleOffset;
    private final BooleanProperty alarmEnabled;
    private final StringProperty alarmScript;
    private final BooleanProperty alarmed;
    private final BooleanProperty historicalEnabled;
    /* ********** Dynamic Fields ********** */
    private IntegerProperty id;
    private StringProperty groupName;

    /* ********** Constructors ********** */
    public Tag() {
        this.id = new SimpleIntegerProperty(this, "id", 0);
        this.name = new SimpleStringProperty(this, "name", "");
        this.enabled = new SimpleBooleanProperty(this, "enabled", true);
        this.description = new SimpleStringProperty(this, "description", "");
        this.value = new SimpleStringProperty(this, "value", "");
        this.decimals = new SimpleIntegerProperty(this, "decimals", DEFAULT_DECIMALS);
        this.timestamp = new SimpleObjectProperty<>(this, "timestamp", Timestamp.from(Instant.now()));
        this.type = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty(this, "status", "");
        this.linkedAddress = new SimpleObjectProperty<>();
        this.linkedTag = new SimpleObjectProperty<>();
        this.script = new SimpleStringProperty(this, "script", "");
        this.mask = new SimpleIntegerProperty(this, "mask", 0);
        this.scaleFactor = new SimpleDoubleProperty(this, "scaleFactor", 1.0);
        this.scaleOffset = new SimpleDoubleProperty(this, "scaleOffset", 0.0);
        this.alarmEnabled = new SimpleBooleanProperty(this, "alarmEnabled", false);
        this.alarmScript = new SimpleStringProperty(this, "alarmScript", "");
        this.alarmed = new SimpleBooleanProperty(this, "alarmed", false);
        this.historicalEnabled = new SimpleBooleanProperty(this, "historicalEnabled", false);
        this.groupName = new SimpleStringProperty(this, "groupName", "");
    }

    /* ********** Properties ********** */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    @Column(name = "name")
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Column(name = "enabled")
    public boolean getEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    @Column(name = "description")
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Column(name = "value")
    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        try {
            // Is a number
            double numericValue = Double.parseDouble(value);
            double roundedValue = MathUtil.round(numericValue * getScaleFactor() + getScaleOffset(), getDecimals());
            this.value.set(String.valueOf(roundedValue));
        } catch (NumberFormatException e) {
            // Is not a number
            this.value.set(value);
        } catch (Exception e) {
            // Is an error
            this.status.set(e.getLocalizedMessage());
            this.value.set("????");
        }
    }

    public StringProperty valueProperty() {
        return value;
    }

    @Column(name = "decimals")
    public int getDecimals() {
        return decimals.get();
    }

    public void setDecimals(int decimals) {
        this.decimals.set(decimals);
    }

    public IntegerProperty decimalsProperty() {
        return decimals;
    }

    @Column(name = "time_stamp")
    public Timestamp getTimestamp() {
        return timestamp.get();
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp.set(timestamp);
    }

    public ObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }

    @Column(name = "tag_type")
    public String getTypeName() {
        return getType() == null ? "" : getType().name();
    }

    public void setTypeName(String name) {
        type.set(name == null || name.isEmpty() ? Type.INPUT : Type.valueOf(name));
    }

    @Transient
    public Type getType() {
        return type.get();
    }

    public void setType(Type type) {
        this.type.set(type);
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    @Column(name = "status")
    public String getStatus() {
        return status.get();
    }

    public void setStatus(String statusName) {
        this.status.set(statusName);
    }

    public StringProperty statusProperty() {
        return status;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public Address getLinkedAddress() {
        return linkedAddress.get();
    }

    public void setLinkedAddress(Address linkedAddress) {
        this.linkedAddress.set(linkedAddress);
    }

    public ObjectProperty<Address> linkedAddressProperty() {
        return linkedAddress;
    }

    @OneToOne
    public Tag getLinkedTag() {
        return linkedTag.get();
    }

    public void setLinkedTag(Tag linkedTag) {
        this.linkedTag.set(linkedTag);
    }

    public ObjectProperty<Tag> linkedTagProperty() {
        return linkedTag;
    }

    @Column(name = "script", length = 1024)
    public String getScript() {
        return script.get();
    }

    public void setScript(String script) {
        this.script.set(script);
    }

    public StringProperty scriptProperty() {
        return script;
    }

    @Column(name = "mask")
    public int getMask() {
        return mask.get();
    }

    public void setMask(int mask) {
        this.mask.set(mask);
    }

    public IntegerProperty maskProperty() {
        return mask;
    }

    @Column(name = "scale_factor")
    public double getScaleFactor() {
        return scaleFactor.get();
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor.set(scaleFactor);
    }

    public DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }

    @Column(name = "scale_offset")
    public double getScaleOffset() {
        return scaleOffset.get();
    }

    public void setScaleOffset(double scaleOffset) {
        this.scaleOffset.set(scaleOffset);
    }

    public DoubleProperty scaleOffsetProperty() {
        return scaleOffset;
    }

    @Column(name = "alarm_enabled")
    public boolean getAlarmEnabled() {
        return alarmEnabled.get();
    }

    public void setAlarmEnabled(boolean alarmEnabled) {
        this.alarmEnabled.set(alarmEnabled);
    }

    public BooleanProperty alarmEnabledProperty() {
        return alarmEnabled;
    }

    @Column(name = "alarm_script")
    public String getAlarmScript() {
        return alarmScript.get();
    }

    public void setAlarmScript(String alarmScript) {
        this.alarmScript.set(alarmScript);
    }

    public StringProperty alarmScriptProperty() {
        return alarmScript;
    }

    @Column(name = "alarmed")
    public boolean getAlarmed() {
        return alarmed.get();
    }

    public void setAlarmed(boolean alarmed) {
        this.alarmed.set(alarmed);
    }

    public BooleanProperty alarmedProperty() {
        return alarmed;
    }

    @Column(name = "historical_enabled")
    public boolean getHistoricalEnabled() {
        return historicalEnabled.get();
    }

    public void setHistoricalEnabled(boolean historicalEnabled) {
        this.historicalEnabled.set(historicalEnabled);
    }

    public BooleanProperty historicalEnabledProperty() {
        return historicalEnabled;
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName.get();
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    /* ********** Getters and Setters ********** */
    @Override
    public String toString() {
        return getName();
    }

    public enum Type {INPUT, MASK, MATH, STATUS, OUTPUT, LOGICAL}

}