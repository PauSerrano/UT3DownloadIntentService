package es.schooleando.downloadintent;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Pau on 30/12/16.
 */

public class MiResultReceiver extends ResultReceiver {

    private Receiver miReceiver;

    public MiResultReceiver(Handler handler) {
        super(handler);
    }


    public interface Receiver{
        public void onReceiverResult(int resultCode, Bundle resultData);
    }


    public void setReceiver (Receiver receiver){
        miReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (miReceiver!=null){
            miReceiver.onReceiverResult(resultCode, resultData);
        }
    }
}
