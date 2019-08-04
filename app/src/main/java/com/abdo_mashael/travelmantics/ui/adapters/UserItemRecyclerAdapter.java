package com.abdo_mashael.travelmantics.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdo_mashael.travelmantics.R;
import com.abdo_mashael.travelmantics.data.UserListItem;
import com.abdo_mashael.travelmantics.ui.utils.SelectDeal;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserItemRecyclerAdapter extends RecyclerView.Adapter<UserItemRecyclerAdapter.ViewHolder> {

    private static final String TAG = UserItemRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private List<UserListItem> mUserListItemList;
    private final SelectDeal mSelectDeal;

    public UserItemRecyclerAdapter(Context context, List<UserListItem> userListItemList, SelectDeal selectDeal) {
        mContext = context;
        mUserListItemList = userListItemList;
        mSelectDeal = selectDeal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).
                inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserListItem mUserListItem = mUserListItemList.get(position);

        Log.d(TAG, "onBindViewHolder: " + mUserListItem.toString());
        Glide.with(mContext).
                load(mUserListItem.getTvImageUrl()).
                fitCenter().
                into(holder.imageView);

        holder.tvTitle.setText(mUserListItem.getTvTitle());
        holder.tvDescription.setText(mUserListItem.getTvDescription());
        holder.tvPrice.setText(mUserListItem.getTvPrice());
        holder.listItemLayout.setOnClickListener(view -> mSelectDeal.onDealClick(mUserListItem));

    }

    @Override
    public int getItemCount() {
        if (mUserListItemList == null)
            // return 10;
            return 0;

        return mUserListItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvDescription)
        TextView tvDescription;
        @BindView(R.id.tvPrice)
        TextView tvPrice;
        @BindView(R.id.list_item_layout)
        LinearLayout listItemLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
