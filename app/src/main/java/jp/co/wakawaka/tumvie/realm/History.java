package jp.co.wakawaka.tumvie.realm;

import io.realm.RealmObject;

/**
 * Created by wakabayashieisuke on 2016/07/11.
 */
public class History extends RealmObject {
    private String keyword;
    private long currentTimeMillis;

    /**
     * メンバ変数に値を設定する。
     * @param keyword 検索キーワード
     */
    public void setValue(String keyword) {
        this.keyword = keyword;
        this.currentTimeMillis = System.currentTimeMillis();
    }

    public String getKeyword() {
        return this.keyword;
    }
}
