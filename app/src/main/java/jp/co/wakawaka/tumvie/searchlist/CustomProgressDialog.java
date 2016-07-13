package jp.co.wakawaka.tumvie.searchlist;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import jp.co.wakawaka.tumvie.R;

/**
 * Created by wakabayashieisuke on 2016/07/13.
 */
public class CustomProgressDialog extends Dialog {
    /**
     * コンストラクタ
     * @param context
     */
    public CustomProgressDialog(Context context) {
        super(context, R.style.Theme_CustomProgressDialog);

        // レイアウトを決定
        setContentView(R.layout.custom_progress_dialog);
    }
}