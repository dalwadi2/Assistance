package com.assistance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assistance.MyApplication;
import com.assistance.R;
import com.assistance.db.Salary;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;


public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ListItemViewHolder> {
    private Context mContext;
    private RealmResults<Salary> entities;


    public SalaryAdapter(Context mContext, RealmResults<Salary> modelData) {
        this.mContext = mContext;
        this.entities = modelData;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_salary, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        final Salary model = entities.get(position);

        viewHolder.tvSalaryDate.setText(MyApplication.formatted_date(model.getTimestamp()));
        viewHolder.tvSalaryAmount.setText("\u20B9 " + model.getSalaryAmount());
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_salary_date)
        TextView tvSalaryDate;
        @BindView(R.id.tv_salary_amount)
        TextView tvSalaryAmount;

        ListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
