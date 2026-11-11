package net.irext.ircontrol.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import net.irext.ircontrol.ui.activity.CreateActivity;
import net.irext.ircontrol.utils.MessageUtils;

/**
 * Filename:       BaseCreateFragment.java
 * Revised:        Date: 2017-04-10
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Base Fragment class for create fragments
 * <p>
 * Revision log:
 * 2017-04-10: created by strawmanbobi
 */
@SuppressWarnings("unused")
public abstract class BaseCreateFragment extends Fragment {

    protected static final String TAG = BaseCreateFragment.class.getSimpleName();

    int mFrom;
    CreateActivity mParent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mParent = (CreateActivity) getActivity();
        return null;
    }

    // returns true if the back action is consumed by navigating to a parent page
    // within the create flow; false when already at the root page (nothing to pop)
    public boolean onBackPressed() {
        if (-1 != mFrom) {
            MessageUtils.postMessage(mParent.mMsgHandler, mFrom);
            return true;
        }
        return false;
    }

    void getFrom() {
        assert getArguments() != null;
        int from = getArguments().getInt(CreateActivity.KEY_FROM);
        if (-1 == from) {
            Log.d(TAG, "FROM IS NULL");
        } else {
            mFrom = from;
        }
    }

}
