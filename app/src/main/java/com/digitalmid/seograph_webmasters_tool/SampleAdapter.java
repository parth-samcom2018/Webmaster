package com.digitalmid.seograph_webmasters_tool;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalmid.seograph_webmasters_tool.com.digitalmid.seograph_webmasters_tool.CurrentWeek;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ListItemViewHolder> {

    private static final String TAG = SampleAdapter.class.getSimpleName();
    private Context mContext;
    private Picasso picasso;
    private List<CurrentWeek> currentWeeks;

    public SampleAdapter(Context mContext, Picasso picasso) {
        this.mContext = mContext;
        this.picasso = picasso;
    }

    public void orderList(List<CurrentWeek> myorderlist) {
        this.currentWeeks = myorderlist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.change_one, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder viewHolder, int position) {
        CurrentWeek currentWeekList = currentWeeks.get(position);

        CurrentWeek clicks = currentWeeks.get(position);
        viewHolder.clicks.setText(clicks.getClicks());

        //viewHolder.getmBinding().packageName.setText(currentWeekList.getPackage_name());
        /*viewHolder.getmBinding().duration.setText(mypostjoblist.getMonths());
        viewHolder.getmBinding().amount.setText(mypostjoblist.getAmuont());
        viewHolder.getmBinding().transactionId.setText(mypostjoblist.getTransaction_id());
        viewHolder.getmBinding().paymentType.setText(mypostjoblist.getPayment_type());
        viewHolder.getmBinding().expireDate.setText(mypostjoblist.getExpire_date());*/
    }

    /*@Override
    public int getItemCount() {
        return 20;
    }*/

    @Override
    public int getItemCount() {
        return currentWeeks == null ? 0 : currentWeeks.size();
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        public TextView clicks, year, genre;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            clicks = (TextView) itemView.findViewById(R.id.tv_key);

        }

    }
}
