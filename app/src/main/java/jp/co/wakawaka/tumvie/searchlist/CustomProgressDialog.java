package jp.co.wakawaka.tumvie.searchlist;

import android.app.Dialog;
import android.content.Context;

import jp.co.wakawaka.tumvie.R;

/**
 * カスタマイズしたプログレスダイアログ
 * Created by wakabayashieisuke on 2016/07/13.
 */
public class CustomProgressDialog extends Dialog {
    /**
     * コンストラクタ
     * @param context Context
     */
    public CustomProgressDialog(Context context) {
        super(context, R.style.Theme_CustomProgressDialog);

        // レイアウトを決定
        setContentView(R.layout.custom_progress_dialog);
    }
}