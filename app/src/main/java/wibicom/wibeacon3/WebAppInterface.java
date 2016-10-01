package wibicom.wibeacon3;

import android.content.Context;
import android.webkit.JavascriptInterface;


/**
 * Created by Olivier on 9/19/2016.
 */
public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void getTemperature(String toast) {

    }
}
