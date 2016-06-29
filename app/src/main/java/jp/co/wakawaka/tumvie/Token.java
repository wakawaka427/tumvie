package jp.co.wakawaka.tumvie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * トークン管理クラス
 * Created by wakabayashieisuke on 2016/06/29.
 */
public class Token {

    private static final String ACCESS_TOKEN_PREFERENCES_KEY = "access_token_preferences_key";
    private static final String ACCESS_TOKEN_SECRET_PREFERENCES_KEY = "access_token_secret_preferences_key";

    /**
     * アクセストークンを保存する。
     * @param context Context
     * @param token アクセストークン
     */
    public static void saveAccessToken(Context context, String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_PREFERENCES_KEY, token);
        editor.apply();
    }

    /**
     * アクセストークンを取得する。（値が存在しない場合nullを返す）
     * @param context Context
     */
    public static String getAccessToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPreferences.getString(ACCESS_TOKEN_PREFERENCES_KEY, null);
        } catch (ClassCastException e) {
            Log.e("Token", "SharedPreferences#getString(ACCESS_TOKEN_PREFERENCES_KEY) class cast Exception!");
        }
        return null;
    }

    /**
     * アクセストークンシークレットを保存する。
     * @param context Context
     * @param tokenSecret アクセストークンシークレット
     */
    public static void saveAccessSecretToken(Context context, String tokenSecret) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_SECRET_PREFERENCES_KEY, tokenSecret);
        editor.apply();
    }

    /**
     * アクセストークンシークレットを取得する。（値が存在しない場合nullを返す）
     * @param context Context
     */
    public static String getAccessTokenSecret(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPreferences.getString(ACCESS_TOKEN_SECRET_PREFERENCES_KEY, null);
        } catch (ClassCastException e) {
            Log.e("Token", "SharedPreferences#getString(ACCESS_TOKEN_SECRET_PREFERENCES_KEY) class cast Exception!");
        }
        return null;
    }

    /**
     * アクセストークンを取得済みの場合trueを返す。
     * @param context Context
     * @return アクセストークン取得済みの場合true
     */
    public static boolean existToken(Context context) {
        return getAccessToken(context) != null
                && getAccessTokenSecret(context) != null;
    }
}
