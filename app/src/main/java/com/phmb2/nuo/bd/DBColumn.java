package com.phmb2.nuo.bd;

import android.util.Pair;

/**
 * Created by phmb2 on 20/07/17.
 */

public class DBColumn
{
    public static final String TYPE_INTEGER = "INTEGER";
    public static final String TYPE_TEXT = "TEXT";
    private Pair<String,String> foreignKey = null;
    private boolean isPrimaryKey = false;
    private boolean isAutoIncrementKey = false;

    private String name;
    private String modifiers;

    public boolean isAutoIncrementKey() {
        return isAutoIncrementKey;
    }

    public DBColumn setAutoIncrementKey() {
        this.isAutoIncrementKey = true;
        return this;

    }
    public DBColumn resetAutoIncrementKey() {
        this.isAutoIncrementKey = false;
        return this;
    }

    public DBColumn (String name, String modifiers) {
        this.name = name;
        this.modifiers = modifiers;
    }
    public DBColumn setPrimaryKey(){
        isPrimaryKey = true;
        return this;
    }
    public DBColumn resetPrimaryKey(){
        isPrimaryKey = false;
        return this;
    }
    public DBColumn setForeignKey(String tableName,String columnName){
        foreignKey = new Pair<String,String>(tableName,columnName);
        return this;
    }
    public DBColumn resetForeignKey(){
        foreignKey = null;
        return this;
    }
    public boolean isForeignKey(){

        return foreignKey != null;
    }
    public Pair<String,String> getForeignKey(){

        return foreignKey;
    }
    public boolean isPrimaryKey(){

        return isPrimaryKey;
    }
    public String getName() {
        return name;
    }

    public String getModifiers() {
        return modifiers;
    }
}
