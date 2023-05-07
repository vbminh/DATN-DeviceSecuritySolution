package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.dssclient.R;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;

import module.BlackListApp;
import view.AppInBlackList;

public class BlacklistAppAdapter extends  RecyclerView.Adapter<BlacklistAppAdapter.BlackListAppViewHolder>{

    private static final String TAG = "BlacklistAppAdapter1";
    private List<BlackListApp> mApp;
    private Context mContext;

    public BlacklistAppAdapter(List<BlackListApp> mInformation , Context mContext) {
        this.mApp = mInformation;
        this.mContext = mContext;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BlackListAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.black_list_app_item,parent,false);
        return new BlackListAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlackListAppViewHolder holder, int position) {
        BlackListApp data = mApp.get(position);
        if(data != null)
        {
            Log.e(TAG, "onBindViewHolder: "+ position );
            initView(holder, data);
            holder.itemInfo.setOnClickListener(v -> appInformation(data));
        }
    }

    private void appInformation(BlackListApp mAppIBL) {
        Intent intent = new Intent(mContext , AppInBlackList.class);
        intent.putExtra("infoApp", mAppIBL);
        mContext.startActivity(intent);
    }

    private void initView(BlackListAppViewHolder holder, BlackListApp data) {
        holder.txtNameApp.setText(data.getName());
        Glide.with(mContext).load(data.getImageUrl()).into(holder.imgApp);
    }


    @Override
    public int getItemCount() {
        return mApp.size();
    }

    public  class  BlackListAppViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNameApp;
        LinearLayout itemInfo;
        private ImageView imgApp;
        public BlackListAppViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameApp = itemView.findViewById(R.id.txtNameApp);
            imgApp = itemView.findViewById(R.id.imgBlackApp);
            itemInfo = itemView.findViewById(R.id.iteminfo);
        }
    }

}
