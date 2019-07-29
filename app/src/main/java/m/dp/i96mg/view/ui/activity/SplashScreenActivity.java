package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.utility.utils.CustomUtils;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class SplashScreenActivity extends BaseActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
