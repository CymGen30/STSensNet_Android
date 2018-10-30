package com.st.SensNet.net6LoWPAN;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class TestFragmentActivity extends Activity{

        private static final int FRAGMENT_ID = TestFragmentActivity.class.getCanonicalName().hashCode();

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FrameLayout content = new FrameLayout(this);
            content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            content.setId(FRAGMENT_ID);
            setContentView(content);
        }

        public void setFragment(Fragment fragment) {
            getFragmentManager().beginTransaction()
                    .replace(FRAGMENT_ID, fragment, "TEST")
                    .commit();
        }

}
