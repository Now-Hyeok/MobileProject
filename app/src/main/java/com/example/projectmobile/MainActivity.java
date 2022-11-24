package com.example.projectmobile;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;

import android.widget.Toast;

import com.example.projectmobile.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectmobile.databinding.ActivityMainBinding;

import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String baCode;
    private int place=0;//0:냉장 1: 냉동 2: 실온
    final private String key="6c195d37add14d8dbbd6";
    private FirebaseAuth auth;
    private boolean flag; //true = 등록 false = 삭제

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        auth = FirebaseAuth.getInstance();

    }
    public void scanCode(){ //바코드 스캐너
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("바코드를 화면 중앙에 위치시켜주세요");
        integrator.initiateScan();
    }
   @Override //바코드 스캐너 실행 결과 return
   protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null){
                baCode = result.getContents();
            }
            else
                Toast.makeText(getApplicationContext(),"다시 시도해주세요",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(),"다시 시도해주세요",Toast.LENGTH_SHORT).show();
    }

    public String getbacode(){
        return baCode;
    }

}