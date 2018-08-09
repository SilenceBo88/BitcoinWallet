package com.lrkj.bitcoinwallet.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

public class FileUtils {

  private static final String TAG = "FileUtils";

  /**
   * 删除文件
   */
  public static void delete(@NonNull File directoryOrFile) {
    Log.d(TAG, "delete: ");
    final File[] contents = directoryOrFile.listFiles();
    if (contents != null) {
      for (File file : contents) {
        delete(file);
      }
    }
    directoryOrFile.delete();
  }

  /**
   * 校验文件是否存在
   * @param directory
   * @param extension
   * @return
   */
  public static boolean isExist(@NonNull File directory, @NonNull String extension) {
    Log.d(TAG, "isExist: ");
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
