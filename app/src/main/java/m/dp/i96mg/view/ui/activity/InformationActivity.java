package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityInformationBinding;

public class InformationActivity extends AppCompatActivity {

    ActivityInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_information);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    public void continueToPayActivity(View view) {
        Intent intent=new Intent(this,PayCardActivity.class);
        startActivity(intent);
    }
}
