package jp.co.wakawaka.tumvie.historylist;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 検索リストのモデルクラス
 * Created by wakabayashieisuke on 2016/07/11.
 */
public class History extends RealmObject {
    @PrimaryKey
    private long id;

    private String keyword;
    private long currentTimeMillis;

    /**
     * メンバ変数に値を設定する。
     * @param keyword 検索キーワード
     */
    public void setValue(String keyword) {
        this.id = Realm.getDefaultInstance().where(History.class).max("id").intValue() + 1;
        this.keyword = keyword;
        this.currentTimeMillis = System.currentTimeMillis();
    }

    public long getId() {
        return this.id;
    }

    public String getKeyword() {
        return this.keyword;
    }
}
