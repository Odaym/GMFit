package com.mcsaatchi.gmfit.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MealItem")

public class MealItem {
    @DatabaseField(generatedId = true, index = true)
    int id;
    @DatabaseField
    String name;
    @DatabaseField
    String type;
    @DatabaseField
    int sectionType;

    public MealItem() {
    }

    public MealItem(String name, String type, int sectionType) {
        this.name = name;
        this.type = type;
        this.sectionType = sectionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSectionType() {
        return sectionType;
    }

    public void setSectionType(int sectionType) {
        this.sectionType = sectionType;
    }
}
