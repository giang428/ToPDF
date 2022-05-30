package com.giang.topdf.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.giang.topdf.BuildConfig;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class FileUtils {
    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FileUtils(Activity mContext, SharedPreferences mSharedPreferences) {
        this.mContext = mContext;
        this.mSharedPreferences = mSharedPreferences;
    }

    public static String getPath(final Context context, final Uri uri) {
        if (uri == null) return null;
        ContentResolver mContentResolver = context.getContentResolver();
        if (uri.getScheme().equals("content")) {
            return getRealPath(mContentResolver, uri, null);
        }
        if (uri.getScheme().equals("file")) {
            return uri.getPath();
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            return getPathForDocumentUri(mContentResolver, uri);
        }
        return null;
    }

    private static String getPathForDocumentUri(ContentResolver mContentResolver, Uri uri) {
        String mAuthority = uri.getAuthority();
        switch (mAuthority) {
            case "com.android.providers.media.documents":
                return getPathForMediaDoc(mContentResolver, uri);
            case "com.android.providers.downloads.documents":
                return getPathForDownloadsDoc(mContentResolver, uri);
            case "com.android.externalstorage.documents":
                return getPathForExternalStorageDoc(uri);
        }
        return null;
    }

    private static String getPathForExternalStorageDoc(Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        String[] idArr = documentId.split(":");
        if (idArr.length == 2) {
            String type = idArr[0];
            String realDocId = idArr[1];
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + realDocId;
            }
        }
        return null;
    }

    private static String getPathForDownloadsDoc(ContentResolver mContentResolver, Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        Uri downloadUri = Uri.parse("content://com.android.providers.downloads.documents/" + documentId.replace(":", "%3A"));
        // Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.parseLong(documentId));
        return getRealPath(mContentResolver, downloadUri, null);
    }

    private static String getPathForMediaDoc(ContentResolver mContentResolver, Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        String[] idArr = documentId.split(":");
        if (idArr.length == 2) {
            String docType = idArr[0];
            String realDocId = idArr[1];
            Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            switch (docType) {
                case "image":
                    mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    break;
                case "video":
                    mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    break;
                case "audio":
                    mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    break;
            }
            String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;
            return getRealPath(mContentResolver, mediaContentUri, whereClause);
        }
        return null;
    }


    private static String getRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);
        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {
                String columnName = MediaStore.Images.Media.DATA;
                int imageColumnIndex = cursor.getColumnIndex(columnName);
                if (imageColumnIndex == -1)
                    return ret;
                ret = cursor.getString(imageColumnIndex);
                cursor.close();
            }
        }
        return ret;
    }


    private static String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath;
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }
        return fullPath;
    }

    public static String getFileName(String path) {
        if (path == null)
            return null;

        int index = path.lastIndexOf("/");
        return index < path.length() ? path.substring(index + 1) : null;
    }

    public static String getFileNameWithoutExtension(String path) {
        if (path == null)
            return null;
        else {
            String fileName = getFileName(path);
            return fileName.lastIndexOf('.') < fileName.length() ? fileName.substring(0, fileName.lastIndexOf('.')) : null;
        }
    }

    public static String getFileExtension(String path) {
        if (path == null)
            return null;
        else {
            String fileName = getFileName(path);
            return fileName.lastIndexOf('.') < fileName.length() ? fileName.substring(fileName.lastIndexOf('.')) : null;
        }
    }

    public static String getFilePath(String path) {
        if (path == null)
            return null;
        int index = path.lastIndexOf("/");
        return index < path.length() ? path.substring(0, index + 1) : null;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static String getFileSize(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static void viewFile(Context mContext, String outputfile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_TEXT, FileUtils.getFileName(outputfile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri fileUri;
        fileUri = FileProvider.getUriForFile(
                mContext
                , BuildConfig.APPLICATION_ID + ".provider"
                , new File(outputfile));
        intent.setDataAndType(fileUri, "application/pdf");
        mContext.startActivity(Intent.createChooser(intent, "Share file"));
    }

    public static void shareFile(Context mContext, String outputfile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, FileUtils.getFileName(outputfile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri fileUri;
        fileUri = FileProvider.getUriForFile(
                mContext
                , BuildConfig.APPLICATION_ID + ".provider"
                , new File(outputfile));
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(intent, "Share file"));
    }
}
