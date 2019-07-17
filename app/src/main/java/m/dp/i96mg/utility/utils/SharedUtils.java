package m.dp.i96mg.utility.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import m.dp.i96mg.R;
import m.dp.i96mg.view.ui.activity.LoginActivity;
import m.dp.i96mg.view.ui.activity.ShopDetailsActivity;


public class SharedUtils {
    private Dialog dialog=null;
    private AlertDialog loginDialog=null;

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

    public void showLoginDialog(Context context,String memberType) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.choose_order_type, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        loginDialog = alertDialog.create();
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loginDialog.show();
        makeActionOnChoose(view,context,memberType);
    }

    private void makeActionOnChoose(View view, Context context, String memberType) {
        ConstraintLayout loginConstraintLayout = view.findViewById(R.id.loginConstraintLayout);
        loginConstraintLayout.setOnClickListener(v -> {
            loginDialog.cancel();
            openLoginActivity(context,memberType);
        });
        ConstraintLayout notNowConstraintLayout = view.findViewById(R.id.notNowConstraintLayout);
        notNowConstraintLayout.setOnClickListener(v -> {
            loginDialog.cancel();
        });
    }

    private void openLoginActivity(Context context, String memberType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, memberType);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

}
