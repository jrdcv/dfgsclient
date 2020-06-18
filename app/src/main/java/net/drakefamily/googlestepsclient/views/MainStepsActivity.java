package net.drakefamily.googlestepsclient.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import net.drakefamily.googlestepsclient.R;
import net.drakefamily.googlestepsclient.model.StepsData;

public class MainStepsActivity extends AppCompatActivity {

    public static final int GOOGLE_FIT_SIGN_IN_RESULT = 1001;

    StepsListFragment slf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_steps);

        Fragment f = getSupportFragmentManager().findFragmentByTag(StepsListFragment.FRAGMENT_TAG);
        if (f == null) {
            slf = StepsListFragment.newInstance();
            getSupportFragmentManager().beginTransaction().
                    add(R.id.frame_layout_fragment_holder, slf, StepsListFragment.FRAGMENT_TAG).
                    addToBackStack(StepsListFragment.FRAGMENT_TAG).
                    commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (true || (requestCode == GOOGLE_FIT_SIGN_IN_RESULT && resultCode == RESULT_OK)) {
            StepsData.getStepData(slf);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}