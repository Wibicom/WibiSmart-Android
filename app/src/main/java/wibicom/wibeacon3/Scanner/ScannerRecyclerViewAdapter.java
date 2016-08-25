package wibicom.wibeacon3.Scanner;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import wibicom.wibeacon3.R;

import java.util.ArrayList;
import java.util.List;


public class ScannerRecyclerViewAdapter extends RecyclerView.Adapter<ScannerRecyclerViewAdapter.ViewHolder> {

    private final List<BluetoothDevice> mValues;
    private final FragmentScanner.OnListFragmentInteractionListener mListener;


    public ScannerRecyclerViewAdapter(List<BluetoothDevice> items, FragmentScanner.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position) {
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_scanner_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getAddress());

        if(mValues.get(position).getName() != null)
            holder.mTitleView.setText(mValues.get(position).getName());
        else
            holder.mTitleView.setText("N/A");

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListPopupWindow list = new ListPopupWindow(holder.mView.getContext());


                ArrayAdapter<CharSequence> adapterBeaconType = ArrayAdapter.createFromResource(holder.mView.getContext(),
                        R.array.beacon_type, android.R.layout.simple_spinner_item);

                list.setAdapter(adapterBeaconType);
                list.setModal(true);
                //list.setAnchorView(holder.mView);
                list.show();
            }
        });


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

                    mListener.onListFragmentInteraction(mValues.get(position), position);

                    //holder.mButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

//    @Override
//    boolean animatechange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, RecyclerView.ItemAnimator.ItemHolderInfo preLayoutInfo, RecyclerView.ItemAnimator.ItemHolderInfo postLayoutInfo)
//    {
//
//    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mContentView;
        public final ImageButton mButton;
        public BluetoothDevice mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.content);
            mButton = (ImageButton) view.findViewById(R.id.overflow_menu_button);
            //mButton.setVisibility(View.INVISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
