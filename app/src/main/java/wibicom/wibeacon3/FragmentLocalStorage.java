package wibicom.wibeacon3;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Michael Vaquier on 2017-07-18.
 */

public class FragmentLocalStorage extends Fragment {

    private OnLocalStorageInteractionListener mListener;

    private Switch switchLocalStorage;
    static boolean switchState = false;

    private TextView messageTextView = null;

    public FragmentLocalStorage() {
        // Required empty public constructor
    }


    public static FragmentLocalStorage newInstance(boolean switchOn) {
        FragmentLocalStorage fragment = new FragmentLocalStorage();
        switchState = switchOn;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_storage, container, false);
        Button buttonOk = (Button)view.findViewById(R.id.button_ok_local_storage);
        switchLocalStorage = (Switch)view.findViewById(R.id.switch_local_storage);
        switchLocalStorage.setChecked(switchState);

        Button buttonPushData = (Button)view.findViewById(R.id.button_push_data);
        TextView message = (TextView)view.findViewById(R.id.data_count);
        messageTextView = message;
        Button buttonDeleteData = (Button)view.findViewById(R.id.button_delete_data);
        final EditText inputDatabaseName = (EditText)view.findViewById(R.id.database_name_input);

        message.setText("Your current storage is " + DataHandler.getInstance().getDataCount() + " files.");

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finnish();
            }
        });

        buttonPushData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputDatabaseName.getText().toString().length() > 3 && inputDatabaseName.getText().toString().indexOf("iotp_4rxa4d_default_") == -1) {
                    MainActivity.getInstance().displaySnackbar("Transmitting data to " + inputDatabaseName.getText().toString() + " database...");
                    DataHandler.getInstance().pushAllFilesInLocalStorage(inputDatabaseName.getText().toString());
                }
                else {
                    MainActivity.getInstance().displaySnackbar("A valid database name needs to be more than 3 characters long");
                }
            }
        });

        buttonDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().displaySnackbar("Deleting local files...");
                DataHandler.getInstance().deleteAllStoredDocuments();
                messageTextView.setText("Your current storage is 0 files.");
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocalStorageInteractionListener) {
            mListener = (OnLocalStorageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLocalStorageInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnLocalStorageInteractionListener) {
            mListener = (OnLocalStorageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLocalStorageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void finnish()
    {
        mListener.onSwitchInteractionLocalStorage(switchLocalStorage.isChecked());
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }



    public interface OnLocalStorageInteractionListener {
        void onSwitchInteractionLocalStorage(boolean state);
    }
}
