package com.assistance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assistance.MyApplication;
import com.assistance.R;
import com.assistance.db.Attendance;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ListItemViewHolder> {

    private Context mContext;
    private RealmResults<Attendance> entities;


    public AttendanceAdapter(Context mContext, RealmResults<Attendance> modelData) {
        this.mContext = mContext;
        this.entities = modelData;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_attendance, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        final Attendance model = entities.get(position);

        viewHolder.tvAttDate.setText(MyApplication.formatted_date(model.getTimestamp()));
        viewHolder.tvPersonName.setText(model.getPersonName());
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_att_date)
        TextView tvAttDate;
        @BindView(R.id.tv_person_name)
        TextView tvPersonName;

        ListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
