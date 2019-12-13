package com.phmb2.nuo.bd;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phmb2 on 20/07/17.
 */

public class DBTable
{
    private String name;
    private DBColumn[] columns;
    private String[] columnsName;
    private int columnsCount;

    private String createCommandString;
    private String dropCommandString;

    public DBTable(String name, DBColumn[] columns) {
        this.name = name;
        this.columns = columns;

        if (columns != null)
        {
            this.columnsCount = columns.length;
        } else
        {
            this.columnsCount = 0;
        }

        this.columnsName = this.buildColumnsNames();

        this.createCommandString = this.buildCreateCommandString();
        this.dropCommandString = this.buildDropCommandString();
    }

    public String getName() {
        return name;
    }

    public int getColumnsCount() {
        return columnsCount;
    }

    public DBColumn[] getColumns() {
        return columns;
    }

    public String getColumnName(int index) {
        String name = null;

        try {
            name = columns[index].getName();
        } catch(ArrayIndexOutOfBoundsException e) {}

        return name;
    }

    public String getCreateCommandString() {
        return this.createCommandString;
    }

    public String getDropCommandString() {
        return this.dropCommandString;
    }

    public String[] getColumnsName() {
        return columnsName;
    }

    private String buildCreateCommandString() {
        String creation = "CREATE TABLE " + this.name + "(";
        List<String> primaryKeys = new ArrayList<String>();
        if (columnsCount > 0)
        {
            DBColumn columnTemp = columns[0];

            creation = creation + columnTemp.getName() + " " + columnTemp.getModifiers() + (columnTemp.isAutoIncrementKey()?" autoincrement":"");
            if(columnTemp.isForeignKey()){
                Pair<String,String> foreign=columnTemp.getForeignKey();
                creation = creation + " REFERENCES "+foreign.first+" ("+foreign.second+" )";

            }

            if(columnTemp.isPrimaryKey()) primaryKeys.add(columnTemp.getName());

            for (int i = 1; i < columnsCount; i++)
            {
                columnTemp = columns[i];
                creation = creation + "," +  columnTemp.getName() + " " + columnTemp.getModifiers()+(columnTemp.isAutoIncrementKey()?" autoincrement":"");
                if(columnTemp.isPrimaryKey()) primaryKeys.add(columnTemp.getName());
                if(columnTemp.isForeignKey()){
                    Pair<String,String> foreign=columnTemp.getForeignKey();
                    creation = creation + " REFERENCES "+foreign.first+" ("+foreign.second+" )";

                }

            }
            if(primaryKeys.size() > 0)
            {
                creation = creation +" , PRIMARY KEY ( "+primaryKeys.get(0);
                for (int i = 1; i < primaryKeys.size(); i++) {
                    creation = creation +" , "+primaryKeys.get(i);
                }
                creation = creation + ")";
            }
            creation = creation + ");";
        }
        return creation;
    }

    private String buildDropCommandString() {
        return "DROP TABLE IF EXISTS " + this.name;
    }

    private String[] buildColumnsNames() {
        String[] returnValue = new String[columnsCount];

        DBColumn columnTemp = null;
        for (int i = 0; i < columnsCount; i++) {
            columnTemp = columns[i];
            returnValue[i] = columnTemp.getName();
        }

        return returnValue;
    }
}
