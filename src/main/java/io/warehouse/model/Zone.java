package io.warehouse.model;

import io.warehouse.enums.ZoneType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "zones")
public class Zone {
    @Id
    private String id;
    private String name;
    private ZoneType type;
    private int capacity;

    public Zone() {
        //No argument constructor
    }

    public Zone(String name, ZoneType type, int capacity) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean hasCapacity(int units) {
        return units <= capacity;
    }

    public String getDisplayName() {
        return name +  " (" + type.name() + ")";
    }

}
