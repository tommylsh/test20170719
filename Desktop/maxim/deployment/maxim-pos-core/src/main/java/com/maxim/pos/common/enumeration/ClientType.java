package com.maxim.pos.common.enumeration;

public enum ClientType {

    SQLSERVER(""),
    SQLPOS(""),
    SQLSVRPOS(""),
//    SQLPOS2(""),
//    SQLPOS3(""),
//    SQLPOS4(""),
//    SQLPOS5(""),
//    SQLPOS6(""),
//    SQLPOS7(""),
//    SQLPOS8(""),
//    SQLPOS9(""),
    ORACLE(""),
    WEBSERVICE(""),
    FOLDER_COPY(""),
    DBF(".dbf"),
    CSV(".txt"),
    TEXT(".txt");

    private final String fileExt;

    ClientType(String newFileExte) {
        fileExt = newFileExte;
    }

    public String getFileExt() {
        return fileExt;
    }

}
