package com.giang.topdf.utils;

import android.os.Environment;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

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
    public static final HashMap<Rectangle, Integer> PAGE_SIZE = new HashMap<Rectangle, Integer>() {{
        put(PageSize.LETTER, 1);
        put(PageSize.LEGAL, 2);
        put(PageSize.A0, 3);
        put(PageSize.A1, 4);
        put(PageSize.A2, 5);
        put(PageSize.A3, 6);
        put(PageSize.A4, 7);
        put(PageSize.A5, 8);
        put(PageSize.A6, 9);
        put(PageSize.A7, 10);
        put(PageSize.A8, 11);
        put(PageSize.A9, 12);
        put(PageSize.A10, 13);
        put(PageSize.B0, 14);
        put(PageSize.B1, 15);
        put(PageSize.B2, 16);
        put(PageSize.B3, 17);
        put(PageSize.B4, 18);
        put(PageSize.B5, 19);
        put(PageSize.B6, 20);
        put(PageSize.B7, 21);
        put(PageSize.B8, 22);
        put(PageSize.B9, 23);
        put(PageSize.B10, 24);
    }};
}
