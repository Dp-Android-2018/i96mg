package m.dp.i96mg.view.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.List;

import m.dp.i96mg.R;
import m.dp.i96mg.service.model.global.BankAccountResponseModel;

public class SpinnerAdapter extends ArrayAdapter<BankAccountResponseModel> {


    public SpinnerAdapter(Context context, List<BankAccountResponseModel> countryCityPojos){
        super(context,0,countryCityPojos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_list_item, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.tv_country_name);

        BankAccountResponseModel currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
        }

        return convertView;
    }

}
