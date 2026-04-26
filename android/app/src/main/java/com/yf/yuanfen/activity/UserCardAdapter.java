package com.yf.yuanfen.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yf.yuanfen.R;
import com.yf.yuanfen.dto.UserPublicDTO;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ViewHolder> {

    private final Context context;
    private final List<UserPublicDTO> users;

    public UserCardAdapter(Context context, List<UserPublicDTO> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPublicDTO user = users.get(position);

        holder.tvNickname.setText(TextUtils.isEmpty(user.getNickname()) ? "用户" : user.getNickname());

        String city = TextUtils.isEmpty(user.getCity()) ? "未知" : user.getCity();
        String age = user.getAge() != null ? user.getAge() + "岁" : "未知";
        holder.tvCityAge.setText(city + " · " + age);

        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (user.getId() != null) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;
        TextView tvNickname, tvCityAge;

        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNickname = itemView.findViewById(R.id.tvNickname);
            tvCityAge = itemView.findViewById(R.id.tvCityAge);
        }
    }
}
