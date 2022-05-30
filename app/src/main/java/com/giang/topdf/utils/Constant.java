package com.giang.topdf.utils;

import android.os.Environment;

import java.util.ArrayList;

public class Constant {
    public static final String[] MS_MIME_TYPE = new String[]{
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"

    };
    public static final ArrayList<String> MS_FILE_TYPE = new ArrayList<String>() {
        {
            add(".doc");
            add(".docx");
            add(".xls");
            add(".xlsx");
            add(".ppt");
            add(".pptx");
        }
    };
    public static final String IMAGE_LIST_URI = "uriList";
    public static final String ACTIVITY_CREATE = "activity_create";
    public static final String ACTIVITY_CONVERT = "activity_convert";
    public static final String DOCUMENTS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
    public static final int RESULT_EDIT = 100;
}
