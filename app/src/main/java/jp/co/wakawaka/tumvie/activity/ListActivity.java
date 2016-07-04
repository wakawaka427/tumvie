package jp.co.wakawaka.tumvie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.co.wakawaka.tumvie.R;
import jp.co.wakawaka.tumvie.fragment.ItemFragment;
import jp.co.wakawaka.tumvie.fragment.dummy.DummyContent;

public class ListActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        // TODO：FragmentからのCallback
    }
}
