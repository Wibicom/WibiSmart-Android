package wibicom.wibeacon3.Scanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import wibicom.wibeacon3.R;

/**
 * A fragment representing a alist of Items.
        * <p/>
        * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
        * interface.
        */
public class FragmentScanner extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    List<BluetoothDevice> deviceList = new ArrayList<>();
    List<BluetoothDevice> connectedDeviceList = new ArrayList<>();

    private RecyclerView scanRecyclerView;
    private RecyclerView connectedDeviceRecyclerView;

    private ScannerRecyclerViewAdapter scannerRecyclerViewAdapter;
    private ConnectedDeviceRecyclerViewAdapter connectedDeviceRecyclerViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final static String TAG = FragmentScanner.class.getName();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentScanner() {
    }

    public static FragmentScanner newInstance(int columnCount) {
        FragmentScanner fragment = new FragmentScanner();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateList(BluetoothDevice newDevice, int rssi)
    {
        if(!deviceList.contains(newDevice))
        {
            deviceList.add(newDevice);
            scannerRecyclerViewAdapter.insert(deviceList.size() - 1);
        }


        if(mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    public void clearList()
    {
        int pos;
        while(!deviceList.isEmpty())
        {
            pos = deviceList.size() - 1;
            deviceList.remove(pos);
            scannerRecyclerViewAdapter.remove(pos);
        }
    }

    public void clearConnectedList()
    {
        Log.d(TAG, "entering clearConnectedList()");
        int pos;
        while(!connectedDeviceList.isEmpty())
        {
            pos = connectedDeviceList.size() - 1;
            connectedDeviceList.remove(pos);
            connectedDeviceRecyclerViewAdapter.remove(pos);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        // Find the recyclers
        Context context = view.getContext();
        scanRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        connectedDeviceRecyclerView = (RecyclerView) view.findViewById(R.id.connected_device_list);

        // Set animation.
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setMoveDuration(1000);
        RecyclerView.ItemAnimator itemAnimator2 = new DefaultItemAnimator();
        itemAnimator2.setAddDuration(1000);
        itemAnimator2.setMoveDuration(1000);
        scanRecyclerView.setItemAnimator(itemAnimator);
        connectedDeviceRecyclerView.setItemAnimator(itemAnimator2);

        scanRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        connectedDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(context));


        // Set the adapters
        scannerRecyclerViewAdapter = new ScannerRecyclerViewAdapter(deviceList, mListener);
        final ScannerRecyclerViewAdapter adapter = scannerRecyclerViewAdapter;
        scanRecyclerView.setAdapter(adapter);


        connectedDeviceRecyclerViewAdapter = new ConnectedDeviceRecyclerViewAdapter(connectedDeviceList, mListener);
        connectedDeviceRecyclerView.setAdapter(connectedDeviceRecyclerViewAdapter);


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearList();
                mListener.refreshScan();

            }
        });

        return view;
    }

    public void onConnect(BluetoothDevice device, int pos)
    {
        ArrayList<BluetoothDevice> devices = (ArrayList) deviceList;
        int connectionPos = devices.indexOf(device);
        if(connectionPos != -1) {
            for(int i = 0 ; connectedDeviceList.size() > i ; i++) {
                TextView statusLabel = (TextView) connectedDeviceRecyclerView.getLayoutManager().getChildAt(i).findViewById(R.id.scanner_item_status);
                statusLabel.setText("CONNECTED");
            }
            deviceList.remove(connectionPos);
            scannerRecyclerViewAdapter.remove(pos);
            connectedDeviceList.add(device);
            connectedDeviceRecyclerViewAdapter.insert(connectedDeviceList.size() - 1);
            Log.d(TAG, ".onConnect() device " + device.getName() + "was removed from the devielist and added to the connected devices");
            clearList();
            mListener.refreshScan();
        }
        else {
            Log.d(TAG, ".onConnect() device " + device.getName() + " was not found in the device list.");
        }
    }

    public void onSelect(int pos) {
        for(int i = 0 ; connectedDeviceList.size() > i ; i++) {
            TextView statusLabel = (TextView) connectedDeviceRecyclerView.getLayoutManager().getChildAt(i).findViewById(R.id.scanner_item_status);
            statusLabel.setText("CONNECTED");
        }
        TextView statusLabel = (TextView) connectedDeviceRecyclerView.getLayoutManager().getChildAt(pos).findViewById(R.id.scanner_item_status);
        statusLabel.setText("SELECTED");
    }

    public void onDisconnect(BluetoothDevice device)
    {
        ArrayList<BluetoothDevice> connectedList = (ArrayList)connectedDeviceList;
        int listPos = connectedList.indexOf(device);
        if (listPos != -1) {
            Log.d(TAG, ".onDisconnect() device " + device.getName() + " was removed from the connectedDeviceList");
            int selectedIndex = 0;
            ArrayList<BluetoothDevice> connectedListCopy = new ArrayList<BluetoothDevice>();
            for(BluetoothDevice thisDevice : connectedDeviceList) {
                connectedListCopy.add(thisDevice);
            }
            clearConnectedList();
            for(BluetoothDevice thisDevice : connectedListCopy) {
                if(!device.getName().equals(thisDevice.getName()) && !device.getAddress().equals(thisDevice.getAddress())) {
                    Log.d(TAG, "adding device " + thisDevice.getName() + " at position " + (connectedDeviceList.size()));
                    connectedDeviceList.add(thisDevice);
                    connectedDeviceRecyclerViewAdapter.insert(connectedDeviceList.size());
                }
            }
        }
        else {
            Log.d(TAG, ".onDisconnect() device " + device.getName() + " was not found in the list of connected devices.");
        }
        //scanRecyclerView.getLayoutManager().getChildAt(0).setElevation(4);
        //scanRecyclerView.getLayoutManager().getChildAt(0).findViewById(R.id.button_scan_item).setVisibility(View.INVISIBLE);
        //TextView statusLabel = (TextView) connectedDeviceRecyclerView.getLayoutManager().getChildAt(0).findViewById(R.id.scanner_item_status);
        //statusLabel.setText("DISCONNECTED");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String localName, String adress, int position);
        //void onConnectedListFragmentInteraction(BluetoothDevice device, int position);
        void onConnectedListFragmentInteraction(String localName, String adress);
        void onDisconnectionRequest(String localName, String adress);
        void refreshScan();
    }


}
