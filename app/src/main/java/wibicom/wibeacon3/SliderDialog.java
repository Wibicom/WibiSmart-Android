package wibicom.wibeacon3;

import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @desc This class holds the logic of the preference slider dialog.
 * It inflates the layout slider_dialog.
 * @author Olivier Tessier-Lariviere
 */
public class SliderDialog extends DialogPreference {

    String message = "not working";
    TextView mTextValue;
    Integer mSeekBarValue = 10;


    public SliderDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        //mSeekBarValue = Integer.valueOf(getPersistedString("10"));

        setDialogLayoutResource(R.layout.slider_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        //setPersistent(false);

        for (int i=0;i<attrs.getAttributeCount();i++) {
            String attr = attrs.getAttributeName(i);
            String val  = attrs.getAttributeValue(i);
            if (attr.equals("dialogMessage")) {

                message = val;
                setDialogMessage(message);
            }
        }


    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        //mSeekBarValue = Integer.valueOf(getPersistedString("10"));

        TextView messageDialog = (TextView) view.findViewById(R.id.dialog_message);
        messageDialog.setText(message);

        mTextValue = (TextView) view.findViewById(R.id.test_value);
        updateTextValue();

        SeekBar seekbar = (SeekBar) view.findViewById(R.id.seek_bar);
        seekbar.setMax(150);
        seekbar.setProgress(mSeekBarValue);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SliderDialog.this.mSeekBarValue = progress;
                    updateTextValue();

                }
            }
        });

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
           persistString(mSeekBarValue.toString());
        }
        super.onDialogClosed(positiveResult);
    }

    private void updateTextValue()
    {
        String text;
        if(mSeekBarValue >= 10) {
            text = Float.toString((float) mSeekBarValue / 10) + " seconds";
        }
        else {
            text = Float.toString((float) mSeekBarValue * 100) + " milliseconds";
        }
        SliderDialog.this.mTextValue.setText(text);
    }

    public Integer getSliderValue()
    {
        return mSeekBarValue;
    }

//    @Override
//    protected View onCreateView(ViewGroup parent )
//    {
//        super.onCreateView(parent);
//        LayoutInflater li = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//
//        TextView messageDialog = (TextView)getView(parent, parent).findViewById(R.id.dialog_message);
//
//        return li.inflate( R.layout.slider_dialog, parent, false);
//
//    }

}
