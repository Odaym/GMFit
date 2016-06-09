package com.mcsaatchi.gmfit.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "DataChart")
public class DataChart {
    @DatabaseField(generatedId = true, index = true)
    int id;
    @DatabaseField
    String name;
    @DatabaseField
    String type;
    @DatabaseField
    int order;

    /**
     * 1 = FITNESS
     * 2 = NUTRITION
     * 3 = HEALTH
     */
    @DatabaseField
    String whichFragment;

    public DataChart() {
    }

    public DataChart(String name, String type, int order, String whichFragment) {
        this.name = name;
        this.type = type;
        this.order = order;
        this.whichFragment = whichFragment;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getWhichFragment() {
        return whichFragment;
    }

    public void setWhichFragment(String whichFragment) {
        this.whichFragment = whichFragment;
    }
}
