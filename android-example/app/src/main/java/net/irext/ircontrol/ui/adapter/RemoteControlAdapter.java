package net.irext.ircontrol.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import net.irext.decode.sdk.utils.Constants;
import net.irext.ircontrol.R;
import net.irext.ircontrol.bean.RemoteControl;
import net.irext.ircontrol.ui.widget.SwipeRevealLayout;
import java.util.List;

/**
 * Filename:       RemoteControlAdapter.java
 * Revised:        Date: 2017-04-15
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Adapter class of Remote
 * <p>
 * Revision log:
 * 2017-04-15: created by strawmanbobi
 */
@SuppressWarnings("unused")
public class RemoteControlAdapter extends BaseAdapter {

    private List<RemoteControl> mRemoteControls;
    private LayoutInflater mInflater;
    private OnDeleteListener mOnDeleteListener;

    // only one row is allowed to stay swiped-open at a time
    private SwipeRevealLayout mOpenLayout;

    private final SwipeRevealLayout.OnRevealListener mRevealListener =
            new SwipeRevealLayout.OnRevealListener() {
                @Override
                public void onRevealed(SwipeRevealLayout layout) {
                    if (null != mOpenLayout && mOpenLayout != layout) {
                        mOpenLayout.revealClose();
                    }
                    mOpenLayout = layout;
                }
            };

    public interface OnDeleteListener {
        void onDelete(RemoteControl remoteControl);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.mOnDeleteListener = listener;
    }

    public RemoteControlAdapter(Context ctx, List<RemoteControl> list) {
        this.mRemoteControls = list;
        this.mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public RemoteControlAdapter(Context ctx) {
        this.mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRemoteControls(List<RemoteControl> remoteControls) {
        this.mRemoteControls = remoteControls;
    }

    @Override
    public int getCount() {
        return mRemoteControls.size();
    }

    @Override
    public Object getItem(int position) {
        return mRemoteControls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_remote, parent, false);
            holder.swipeLayout = (SwipeRevealLayout) convertView;
            holder.textView = (TextView)convertView.findViewById(R.id.tv_remote_name);
            holder.deleteButton = (Button)convertView.findViewById(R.id.btn_delete_remote);
            holder.swipeLayout.setOnRevealListener(mRevealListener);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = v.getTag();
                    if (tag instanceof RemoteControl && null != mOnDeleteListener) {
                        mOnDeleteListener.onDelete((RemoteControl) tag);
                    }
                }
            });
            // clicks are forwarded by SwipeRevealLayout via performClick, so keep it non-clickable
            holder.deleteButton.setClickable(false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        // reset recycled row so it never shows a stale swiped-open state
        holder.swipeLayout.resetImmediately();
        RemoteControl remoteControl = mRemoteControls.get(position);
        String remoteName;
        if (Constants.CategoryID.STB.getValue() != remoteControl.getCategoryId()) {
            remoteName = remoteControl.getCategoryName() + "-" + remoteControl.getBrandName();
        } else {
            remoteName = remoteControl.getCityName() + "-" + remoteControl.getOperatorName();
        }
        holder.textView.setText(remoteName);
        holder.deleteButton.setTag(remoteControl);
        return convertView;
    }

    private static class ViewHolder {
        SwipeRevealLayout swipeLayout;
        TextView textView;
        Button deleteButton;
    }
}
