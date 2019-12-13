package com.phmb2.nuo.bd.tabelas;

import com.phmb2.nuo.bd.DBColumn;
import com.phmb2.nuo.bd.DBTable;

/**
 * Created by phmb2 on 20/07/17.
 */

public class FotoTable extends DBTable
{
    public static final String TABLE_NAME = "FOTO";

    //Columns Index
    public static final int ID_COL_INDEX = 0;
    public static final int DATA_COL_INDEX = 1;
    public static final int DESCRICAO_COL_INDEX = 2;
    public static final int IMAGEM_COL_INDEX = 3;
    public static final int IMG_PATH_COL_INDEX = 4;

    //Columns Names
    public static final String ID_COL = "Id";
    public static final String DATA_COL = "Data";
    public static final String DESCRICAO_COL = "Descricao";
    public static final String IMAGEM_COL = "Imagem";
    public static final String IMG_PATH_COL = "Imagem_Path";

    private static final DBColumn[] DB_COLUMNS = new DBColumn[] {
            new DBColumn(ID_COL, DBColumn.TYPE_INTEGER).setPrimaryKey(),
            new DBColumn(DATA_COL, DBColumn.TYPE_TEXT),
            new DBColumn(DESCRICAO_COL, DBColumn.TYPE_TEXT),
            new DBColumn(IMAGEM_COL, DBColumn.TYPE_INTEGER),
            new DBColumn(IMG_PATH_COL, DBColumn.TYPE_TEXT)
    };

    public FotoTable() {
        super(TABLE_NAME, DB_COLUMNS);
    }
}
