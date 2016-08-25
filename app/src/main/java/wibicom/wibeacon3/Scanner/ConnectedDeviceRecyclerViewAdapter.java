package wibicom.wibeacon3.Scanner;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import wibicom.wibeacon3.R;

/**
 * Created by Olivier on 7/28/2016.
 */
public class ConnectedDeviceRecyclerViewAdapter extends RecyclerView.Adapter<ConnectedDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<BluetoothDevice> mValues;

    public ConnectedDeviceRecyclerViewAdapter(List<BluetoothDevice> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ConnectedDeviceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_scanner_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConnectedDeviceRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getAddress());

        if(mValues.get(position).getName() != null)
            holder.mTitleView.setText(mValues.get(position).getName());
        else
            holder.mTitleView.setText("N/A");
        holder.mStatus.setText("CONNECTED");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position) {
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(int position) {
        notifyItemRemoved(position);
    }






    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mContentView;
        public final TextView mStatus;
        //public final Button mButton;
        public BluetoothDevice mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.content);
            //mButton = (Button) view.findViewById(R.id.button_scan_item);
            mStatus = (TextView) view.findViewById(R.id.scanner_item_status);
            //mButton.setVisibility(View.INVISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
