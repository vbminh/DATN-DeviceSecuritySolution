package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.devicesecuritysolution.R;

import java.util.List;

import model.DeviceInformation;
import view.DeviceDetails;

public class DeviceAdapter extends  RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{

    private static final String TAG = "DeviceAdapter";
    private List<DeviceInformation> mInformation;
    private Context mContext;

    public DeviceAdapter(List<DeviceInformation> mInformation , Context mContext) {
        this.mInformation = mInformation;
        this.mContext = mContext;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceInformation mDevice = mInformation.get(position);
        if(mDevice != null)
        {
            holder.txtserialNo.setText(mDevice.getSerialNo());
            holder.itemInfo.setOnClickListener(v -> deviceInformation(mDevice));
        }
    }

    private void deviceInformation(DeviceInformation mDevice) {
        Intent intent = new Intent(mContext , DeviceDetails.class);
        intent.putExtra("infoDevice", mDevice);
        mContext.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        if(mInformation != null)
        {
            return mInformation.size();
        }
        return 0;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        private TextView txtserialNo;
        LinearLayout itemInfo;
        private static final String TAG = "DeviceViewHolder";

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtserialNo = itemView.findViewById(R.id.txtSerialNoDevice);
            itemInfo = itemView.findViewById(R.id.itInfoDevice);
        }

    }

}
