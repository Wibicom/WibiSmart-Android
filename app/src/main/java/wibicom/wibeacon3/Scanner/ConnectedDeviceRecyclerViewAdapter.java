package wibicom.wibeacon3.Scanner;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import wibicom.wibeacon3.R;

/**
 * Created by Olivier on 7/28/2016.
 */
public class ConnectedDeviceRecyclerViewAdapter extends RecyclerView.Adapter<ConnectedDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<BluetoothDevice> mValues;
    private final FragmentScanner.OnListFragmentInteractionListener mListener;

    private final static String TAG = ConnectedDeviceRecyclerViewAdapter.class.getName();

    public ConnectedDeviceRecyclerViewAdapter(List<BluetoothDevice> mValues, FragmentScanner.OnListFragmentInteractionListener listener) {
        this.mValues = mValues;
        this.mListener = listener;
    }

    @Override
    public ConnectedDeviceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connected_scanner_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ConnectedDeviceRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getAddress());

        if(mValues.get(position).getName() != null)
            holder.mTitleView.setText(mValues.get(position).getName());
        else
            holder.mTitleView.setText("N/A");
        holder.mStatus.setText("SELECTED");
        Log.d(TAG, ".onBindViewHolder() for device " + mValues.get(position).getName() + " at position " + position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    if(v.findViewById(R.id.button_scan_item).getVisibility() != View.VISIBLE) {
//
//
//                        holder.mView.setElevation(20f);
//                    }

                    //mListener.onConnectedListFragmentInteraction(mValues.get(position), position);
                    mListener.onConnectedListFragmentInteraction(holder.mTitleView.getText().toString(), holder.mContentView.getText().toString());

                    //holder.mButton.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDisconnectionRequest(holder.mTitleView.getText().toString(), holder.mContentView.getText().toString());
            }
        });

        /*holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onDisconnectionRequest(holder.mTitleView.getText().toString(), holder.mContentView.getText().toString());
            return  true;
            }
        });*/
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
        public final ImageButton mButton;
        public BluetoothDevice mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.content);
            mButton = (ImageButton) view.findViewById(R.id.overflow_menu_button);
            mStatus = (TextView) view.findViewById(R.id.scanner_item_status);
            //mButton.setVisibility(View.INVISIBLE);
        }



        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
