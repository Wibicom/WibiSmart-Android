package wibicom.wibeacon3.Dashboard;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wibicom.wibeacon3.R;

/**
 * Created by Olivier on 7/6/2016.
 */
public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.ViewHolderDashboard> {

    private List<BluetoothGattService> servicesList = new ArrayList<>();

    public DashboardRecyclerViewAdapter(List<BluetoothGattService> services, FragmentDashboardMove.OnFragmentInteractionListener mListener)
    {
        servicesList = services;
    }

    @Override
    public DashboardRecyclerViewAdapter.ViewHolderDashboard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard_item, parent, false);

        return new ViewHolderDashboard(view);
    }

    @Override
    public void onBindViewHolder(DashboardRecyclerViewAdapter.ViewHolderDashboard holder, int position) {
        holder.mServiceView.setText(servicesList.get(position).getUuid().toString());
        if(servicesList.get(position).getCharacteristics().size() >= 3)
        {
            holder.mCharacteristic1View.setText(servicesList.get(position).getCharacteristics().get(0).getUuid().toString());
            holder.mCharacteristic2View.setText(servicesList.get(position).getCharacteristics().get(1).getUuid().toString());
            holder.mCharacteristic3View.setText(servicesList.get(position).getCharacteristics().get(2).getUuid().toString());
        }

    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }



    public class ViewHolderDashboard extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mServiceView;
        public final TextView mCharacteristic1View;
        public final TextView mCharacteristic2View;
        public final TextView mCharacteristic3View;

        public BluetoothDevice mItem;


        public ViewHolderDashboard(View view) {
            super(view);
            mView = view;
            mServiceView = (TextView) view.findViewById(R.id.dashboard_service);
            mCharacteristic1View = (TextView) view.findViewById(R.id.dashboard_item_char_1);
            mCharacteristic2View = (TextView) view.findViewById(R.id.dashboard_item_char_2);
            mCharacteristic3View = (TextView) view.findViewById(R.id.dashboard_item_char_3);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mServiceView.getText() + "'";
        }
    }
}
