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
    int type;
    @DatabaseField
    int order;

    public DataChart() {
    }

    public DataChart(String name, int type, int order) {
        this.name = name;
        this.type = type;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
