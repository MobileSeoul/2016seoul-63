package com.seoulapp.sandfox.retax;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulapp.sandfox.retax.model.ClusterMapItem;

import java.util.List;

/**
 * Created by user on 2016-10-28.
 */

public class DialogListAdapter extends ArrayAdapter<ClusterMapItem> {
    public DialogListAdapter(Context context, List<ClusterMapItem> objects) {
        super(context, R.layout.cluster_dialog, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.cluster_dialog_item, parent, false);
        }
        ClusterMapItem item = getItem(position);

        TextView tv = (TextView) listItemView.findViewById(R.id.dialog_item);
        tv.setText(item.getmStore());

        ImageView iv = (ImageView) listItemView.findViewById(R.id.dialog_image);
        if(item.getmSort() != null){
            iv.setImageDrawable(getContext().getResources().getDrawable(item.getmLogoPic()));
        }else {
            iv.setImageDrawable(getContext().getResources().getDrawable(item.getmThumb()));
        }

        return listItemView;
    }
}
