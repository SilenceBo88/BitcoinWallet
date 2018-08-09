package com.lrkj.bitcoinwallet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtils {

    private static String TAG = "NewBTCWalletActivity";

    private static String sharePath = "btcwallet";

    /**
     * 存储对象
     * @param context
     * @param name
     * @param object
     */
    public static void setObject(Context context, String name, Object object){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharePath, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);

            String base64Student = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            editor.putString(name, base64Student);
            editor.apply();

            oos.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getObject(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharePath, MODE_PRIVATE);
        String studentString = sharedPreferences.getString(name, "");
        byte[] base64Student = Base64.decode(studentString, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Student);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object object = ois.readObject();

            ois.close();
            bais.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
