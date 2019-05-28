package m.dp.i96mg.view.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.CompoundButton;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityPayCardBinding;

public class PayCardActivity extends AppCompatActivity {

    ActivityPayCardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pay_card);
        makeActionOnClickOnRadioButtons();
    }

    private void makeActionOnClickOnRadioButtons() {
        binding.paybalRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }
}
