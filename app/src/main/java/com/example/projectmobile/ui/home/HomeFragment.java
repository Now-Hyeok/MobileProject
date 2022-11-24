package com.example.projectmobile.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.example.projectmobile.CaptureActivity;
import com.example.projectmobile.MainActivity;
import com.example.projectmobile.R;
import com.example.projectmobile.data;
import com.example.projectmobile.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    Button insertFirebase,btn;
    private TextInputLayout ba,na,ty,ma,da,pur,dain,pl;
    int storage_place;
    public int storage=0;
    private FirebaseAuth auth;
    final private String key="6c195d37add14d8dbbd6";
    public String noData="",dateinfo="",baCode;
    public String purchasedate,prod_date="";
    public String name="",type,manufacturer="";
    public TextInputEditText product_purchaseDate,product_date;
    public TextInputEditText product_name, product_type, product_manufacturer,code_result,product_dateinfo,product_purchasedate;;
    int today_year,today_month,today_day;
    data data = null;
    //int datedif;
    DatabaseReference mDBReference = null;
    Map<String, Object> updateData =  null;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ba = (TextInputLayout)root.findViewById(R.id.bacode_layout);
        na = (TextInputLayout) root.findViewById(R.id.name_layout);
        ty = (TextInputLayout)root.findViewById(R.id.type_layout);
        da = (TextInputLayout)root.findViewById(R.id.date_layout);
        ma = (TextInputLayout)root.findViewById(R.id.manufacturer_layout);
        pur = (TextInputLayout)root.findViewById(R.id.purchasedate_layout);
        dain = (TextInputLayout)root.findViewById(R.id.dateinfo_layout);
        pl=(TextInputLayout)root.findViewById(R.id.storage_layout);
        product_name = (TextInputEditText) root.findViewById(R.id.product_name);
        product_manufacturer = (TextInputEditText) root.findViewById(R.id.product_manufacturer); //제조사
        product_type = (TextInputEditText) root.findViewById(R.id.product_type);
        code_result = (TextInputEditText) root.findViewById(R.id.codeScanner_result); //바코드 넘버
        product_dateinfo = (TextInputEditText) root.findViewById(R.id.product_dateinfo);//유통기한정보
        product_date = (TextInputEditText) root.findViewById(R.id.product_date);
        product_purchasedate = (TextInputEditText) root.findViewById(R.id.purchase_date);
        btn = (Button) root.findViewById(R.id.sebtn);
        auth = FirebaseAuth.getInstance();

        product_purchaseDate = (TextInputEditText) root.findViewById(R.id.purchase_date); //구매일자
        product_date = (TextInputEditText) root.findViewById(R.id.product_date); //유통기한
        final String[] Array = new String[]{"냉장실","냉동실","실온보관"};
        TextInputEditText place = (TextInputEditText) root.findViewById(R.id.product_place);

        place.setText("냉장실");
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setSingleChoiceItems(Array, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storage_place = which;
                    }
                });

            dlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    storage = storage_place;
                    switch (storage_place){
                        case 0:
                            place.setText("냉장실");
                            break;
                        case 1:
                            place.setText("냉동실");
                            break;
                        case 2:
                            place.setText("실온보관");
                            break;
                    }
                }
            });
            dlg.setNegativeButton("Cancel",null);
            dlg.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        GregorianCalendar toDayMan = new GregorianCalendar();

        today_year = toDayMan.get(toDayMan.YEAR);  //년
        today_month = toDayMan.get(toDayMan.MONTH)+1;//월
        today_day = toDayMan.get(toDayMan.DAY_OF_MONTH); // 일 int 값으로 불러오기
        String currentTime = String.format("%d년 %d월 %d일",today_year,today_month,today_day);
        purchasedate =  currentTime;
        product_purchaseDate.setText(purchasedate);


        product_date.setOnClickListener(new View.OnClickListener() { //유통기한 입력
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        prod_date = String.format("%d년 %d월 %d일",year,month,day);
                        product_date.setText(prod_date);
                   //     int datedif = (int) calDateBetweenAandB(currentTime,prod_date);

                    }
                },year,month,day);

                datePickerDialog.show();
            }
        });
        product_purchaseDate.setOnClickListener(new View.OnClickListener() { //구매일자 입력
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;

                        purchasedate = String.format("%d년 %d월 %d일",year,month,day);
                        product_purchaseDate.setText(purchasedate);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        insertFirebase = (Button) root.findViewById(R.id.insertButton);
        insertFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = product_name.getText().toString();
                manufacturer = product_manufacturer.getText().toString();
                type = product_type.getText().toString();

                if(name.equals("")||purchasedate.equals("")) {
                    Toast.makeText(getActivity(), "필수 값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    postFirebaseDatabase(true);
                    Toast.makeText(getActivity(),"등록 되었습니다",Toast.LENGTH_SHORT).show();
                }
               reset();

            }
        });

        ImageButton bacodeScan = (ImageButton) root.findViewById(R.id.baCodeScan); //바코드 스캐너 버튼
        bacodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                ((MainActivity)getActivity()).scanCode();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baCode = ((MainActivity)getActivity()).getbacode();
                code_result.setText(baCode);
                getData();
            }
        });



        return root;
    }
    public void postFirebaseDatabase(boolean add){ //파이어베이스 데이터베이스에 올리기
        mDBReference = FirebaseDatabase.getInstance().getReference();
        updateData = new HashMap<>();
        if(add){
            data = new data(name,purchasedate,manufacturer,type,prod_date);
        }

        String current_user = auth.getCurrentUser().getUid();
        updateData.put(name,data);
        mDBReference.child("prod").child(current_user).child(""+storage).updateChildren(updateData);
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void getData(){ //가져온 바코드로 api에서 데이터 가져오기
        new Thread(new Runnable() {
            @Override
            public void run() {

                getXmlData();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(noData.equals("해당하는 데이터가 없습니다.")){
                            Toast.makeText(getActivity(),"등록되지않은 식품 입니다. 직접 입력해주세요",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(),"스캔 성공!",Toast.LENGTH_SHORT).show();
                            product_manufacturer.setText(manufacturer);
                            product_name.setText(name);
                            product_type.setText(type);
                            product_dateinfo.setText(dateinfo);


                        }
                    }
                });
            }
        }).start();
    }
    public void getXmlData(){

        String queryUrl="http://openapi.foodsafetykorea.go.kr/api/"+key+"/C005/xml/1/1/BAR_CD="+baCode;
        try{
            URL url = new URL(queryUrl);  //문자열로 요청된 url을 URL객체로 생성
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is,"UTF-8"));

            String tag;
            xpp.next();
            int eventType=xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();
                        if(tag.equals("PRDLST_DCNM")){
                            xpp.next();
                            type = xpp.getText();
                        }
                        else if(tag.equals("PRDLST_NM")){
                            xpp.next();
                            name = xpp.getText();

                        }
                        else if(tag.equals("BSSH_NM")){
                            xpp.next();
                            manufacturer = xpp.getText();
                        }
                        else if(tag.equals("POG_DAYCNT")){
                            xpp.next();
                            dateinfo=xpp.getText();
                        }
                        else if(tag.equals("MSG")){
                            xpp.next();
                            noData=xpp.getText();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reset(){

        product_name.setText("");
        product_dateinfo.setText("");
        product_type.setText("");
        code_result.setText("");
        product_date.setText("");

        product_manufacturer.setText("");
        purchasedate = String.format("%d년 %d월 %d일",today_year,today_month,today_day);
        product_purchasedate.setText(purchasedate);

    }


}