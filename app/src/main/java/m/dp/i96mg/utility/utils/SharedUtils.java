package m.dp.i96mg.utility.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import m.dp.i96mg.R;


public class SharedUtils {
    private Dialog dialog=null;

    private static  SharedUtils sharedUtils=null;
    public static SharedUtils getInstance(){
        if(sharedUtils==null)
            sharedUtils=new SharedUtils();

        return sharedUtils;
    }

    public void showProgressDialog(Context activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        if (!dialog.isShowing())
            dialog.show();
    }

    public void cancelDialog(){
        dialog.dismiss();
    }

}
