package com.lrkj.bitcoinwallet.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

/**
 * 文件工具类
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 删除文件
     */
    public static void delete(@NonNull File directoryOrFile) {
        Log.d(TAG, "delete: " + directoryOrFile);
        final File[] contents = directoryOrFile.listFiles();
        Log.d(TAG, "delete: " + contents);
        if (contents != null) {
            for (File file : contents) {
                file.delete();
            }
        }
    }

    /**
     * 校验文件是否存在
     *
     * @param directory
     * @param extension
     * @return
     */
    public static boolean isExist(@NonNull File directory, @NonNull String extension) {
        Log.d(TAG, "isExist: " + directory.getAbsolutePath() + "/" + extension);
        if (!directory.exists()) {
            throw new RuntimeException(directory.getPath() + " is not exist");
        }

        if (!directory.isDirectory()) {
            throw new RuntimeException(directory.getPath() + " is not directory");
        }

        if (!extension.isEmpty()) {
            return directory.listFiles((dir, name) -> name.toLowerCase().endsWith(extension.toLowerCase()))
                    .length > 0;
        } else {
            return directory.listFiles((dir, name) -> !name.contains("."))
                    .length > 0;
        }
    }
}
