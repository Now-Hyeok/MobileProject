package com.example.projectmobile.ui.dashboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.AlteredCharSequence;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.text.AllCapsTransformationMethod;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmobile.Adapter;
import com.example.projectmobile.MainActivity;
import com.example.projectmobile.R;
import com.example.projectmobile.data;
import com.example.projectmobile.databinding.FragmentDashboardBinding;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.os.SystemClock.sleep;

public class DashboardFragment extends Fragment {

    private RecyclerView firstRecyclerView,secondRecyclerView,thirdRecyclerView;
    private Adapter firstAdapter, secondAdapter, thirdAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth auth;
    public String user;
    View dialogView;
    public TextInputLayout na, ty, ma, pur, da;
    public TextInputEditText dlg_name,dlg_type, dlg_manufacturer, dlg_purchasedate, dlg_date;
    DatabaseReference mDBReference = null;

    private ArrayList<data> dataList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mDatabase = FirebaseDatabase.getInstance();
        mDBReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid();



        firstRecyclerView = (RecyclerView) root.findViewById(R.id.firstView);
        secondRecyclerView = (RecyclerView) root.findViewById(R.id.secondView);
        thirdRecyclerView = (RecyclerView) root.findViewById(R.id.thirdView);

        firstRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        secondRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        thirdRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<data> firstDataset = new ArrayList<data>(); //냉장
        ArrayList<data> secondDataset = new ArrayList<data>(); //냉동
        ArrayList<data> thirdDataset = new ArrayList<data>(); //실온

        firstAdapter = new Adapter(firstDataset);   //냉장
        firstRecyclerView.setAdapter(firstAdapter);

        secondAdapter = new Adapter(secondDataset);   //냉동
        secondRecyclerView.setAdapter(secondAdapter);

        thirdAdapter = new Adapter(thirdDataset);   //실온
        thirdRecyclerView.setAdapter(thirdAdapter);

        mDatabase.getReference().child("prod").child(user).child("0").addValueEventListener(new ValueEventListener() {
            @Override //냉장
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    data get_Data = dataSnapshot.getValue(data.class);
                    dataList.add(get_Data);
                }
                firstDataset.addAll(dataList);
                firstAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mDatabase.getReference().child("prod").child(user).child("1").addValueEventListener(new ValueEventListener() {
            @Override //냉동
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    data get_Data = dataSnapshot.getValue(data.class);
                    dataList.add(get_Data);
                }
                secondDataset.addAll(dataList);
                secondAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        mDatabase.getReference().child("prod").child(user).child("2").addValueEventListener(new ValueEventListener() {
            @Override //실온보관
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    data get_Data = dataSnapshot.getValue(data.class);
                    dataList.add(get_Data);
                }
                thirdDataset.addAll(dataList);
                thirdAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        dialogView = (View) View.inflate(getActivity(),R.layout.recycleritem,null); // 리사이클러 클릭시 나오는 dialog view
        na = (TextInputLayout) dialogView.findViewById(R.id.dlg_name_layout);
        ty = (TextInputLayout) dialogView.findViewById(R.id.dlg_type_layout);
        ma = (TextInputLayout) dialogView.findViewById(R.id.dlg_manufacturer_layout);
        pur = (TextInputLayout) dialogView.findViewById(R.id.dlg_purchasedate_layout);
        da = (TextInputLayout) dialogView.findViewById(R.id.dlg_date_layout);
        dlg_name = (TextInputEditText) dialogView.findViewById(R.id.dlg_name);
        dlg_type = (TextInputEditText) dialogView.findViewById(R.id.dlg_type);
        dlg_manufacturer = (TextInputEditText) dialogView.findViewById(R.id.dlg_manufacturer);
        dlg_purchasedate = (TextInputEditText) dialogView.findViewById(R.id.dlg_purchasedate);
        dlg_date = (TextInputEditText) dialogView.findViewById(R.id.dlg_date);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        firstAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() { //냉장실 제품 상세보기
            @Override
            public void onItemClick(View v, int position) {
                data data = firstDataset.get(position);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                AlertDialog ad = dlg.create();
                dlg.setTitle("상품 상세정보");
                dlg.setView(dialogView);
                dlg_name.setText(data.getName());
                dlg_type.setText(data.getType());
                dlg_manufacturer.setText(data.getManufacturer());
                dlg_date.setText(data.getDate());
                dlg_purchasedate.setText(data.getPurchase_date());
                String str = data.getName();

                dlg.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mDBReference.child("prod").child(user).child("0").child(str).removeValue();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        firstDataset.remove(position);
                                        firstAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).start();

                        Toast.makeText(getActivity(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlg_name.setText(data.getName());
                        dlg_type.setText(data.getType());
                        dlg_manufacturer.setText(data.getManufacturer());
                        dlg_date.setText(data.getDate());
                        dlg_purchasedate.setText(data.getPurchase_date());
                    }
                });

                dlg.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        data.setName(dlg_name.getText().toString());
                        data.setType(dlg_type.getText().toString());
                        data.setManufacturer(dlg_manufacturer.getText().toString());
                        data.setDate(dlg_date.getText().toString());
                        data.setPurchase_date(dlg_purchasedate.getText().toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(data.getName() != dlg_name.getText().toString()){
                                    mDBReference.child("prod").child(user).child("0").child(str).removeValue();
                                }
                                mDBReference.child("prod").child(user).child("0").child(data.getName()).setValue(data);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        firstDataset.set(position,data);
                                        firstAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }).start();


                        Toast.makeText(getActivity(),"수정되었습니다.",Toast.LENGTH_SHORT).show();

                    }
                });
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((ViewGroup) dialogView.getParent()).removeView(dialogView);

                    }
                });
                dlg_purchasedate.setOnClickListener(new View.OnClickListener() { //구매날짜 입력
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month+1;
                                data.setPurchase_date(String.format("%d년 %d월 %d일",year,month,day));
                                dlg_purchasedate.setText(String.format("%d년 %d월 %d일",year,month,day));

                            }
                        },year,month,day);
                        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                        datePickerDialog.show();
                    }
                });
                dlg_date.setOnClickListener(new View.OnClickListener() { //유통기한입력
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month+1;
                                data.setDate(String.format("%d년 %d월 %d일",year,month,day));
                                dlg_date.setText(String.format("%d년 %d월 %d일",year,month,day));
                            }
                        },year,month,day);
                        datePickerDialog.show();
                    }
                });
                dlg.show();

            }
        });

        secondAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() { //냉동실 제품 상세보기
            @Override
            public void onItemClick(View v, int position) {
                data data = secondDataset.get(position);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("상품 상세정보");
                dlg.setView(dialogView);

                dlg_name.setText(data.getName());
                dlg_type.setText(data.getType());
                dlg_manufacturer.setText(data.getManufacturer());
                dlg_date.setText(data.getDate());
                dlg_purchasedate.setText(data.getPurchase_date());

                String str = data.getName();

                dlg.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        secondDataset.remove(position);
                                        secondAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                mDBReference.child("prod").child(user).child("1").child(str).removeValue();
                            }
                        }).start();



                        Toast.makeText(getActivity(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlg_name.setText(data.getName());
                        dlg_type.setText(data.getType());
                        dlg_manufacturer.setText(data.getManufacturer());
                        dlg_date.setText(data.getDate());
                        dlg_purchasedate.setText(data.getPurchase_date());
                    }
                });

                dlg.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.setName(dlg_name.getText().toString());
                        data.setType(dlg_type.getText().toString());
                        data.setManufacturer(dlg_manufacturer.getText().toString());
                        data.setDate(dlg_date.getText().toString());
                        data.setDate(dlg_date.getText().toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(data.getName() != dlg_name.getText().toString()){
                                    mDBReference.child("prod").child(user).child("1").child(str).removeValue();
                                }
                                mDBReference.child("prod").child(user).child("1").child(data.getName()).setValue(data);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        secondDataset.set(position,data);
                                        secondAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });


                            }

                        }).start();



                        Toast.makeText(getActivity(),"수정되었습니다.",Toast.LENGTH_SHORT).show();


                    }
                });
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                    }
                });

                dlg_purchasedate.setOnClickListener(new View.OnClickListener() { //구매날짜 입력
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month+1;
                                data.setPurchase_date(String.format("%d년 %d월 %d일",year,month,day));
                                dlg_purchasedate.setText(String.format("%d년 %d월 %d일",year,month,day));

                            }
                        },year,month,day);
                        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                        datePickerDialog.show();
                    }
                });
                dlg_date.setOnClickListener(new View.OnClickListener() { //유통기한입력
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month+1;
                                data.setDate(String.format("%d년 %d월 %d일",year,month,day));
                                dlg_date.setText(String.format("%d년 %d월 %d일",year,month,day));
                            }
                        },year,month,day);
                        datePickerDialog.show();
                    }
                });
                dlg.show();
            }
        });

        thirdAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() { //실온보관 제품 상세보기
            @Override
            public void onItemClick(View v, int position) {
                data data = thirdDataset.get(position);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                dlg.setTitle("상품 상세정보");
                dlg.setView(dialogView);
                dlg_name.setText(data.getName());
                dlg_type.setText(data.getType());
                dlg_manufacturer.setText(data.getManufacturer());
                dlg_date.setText(data.getDate());
                dlg_purchasedate.setText(data.getPurchase_date());
                String str = data.getName();
                dlg.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mDBReference.child("prod").child(user).child("2").child(str).removeValue();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        thirdDataset.remove(position);
                                        thirdAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).start();
                        Toast.makeText(getActivity(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlg_name.setText(data.getName());
                        dlg_type.setText(data.getType());
                        dlg_manufacturer.setText(data.getManufacturer());
                        dlg_date.setText(data.getDate());
                        dlg_purchasedate.setText(data.getPurchase_date());
                    }
                });

                dlg.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        data.setName(dlg_name.getText().toString());
                        data.setType(dlg_type.getText().toString());
                        data.setManufacturer(dlg_manufacturer.getText().toString());
                        data.setDate(dlg_date.getText().toString());
                        data.setDate(dlg_date.getText().toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(data.getName() != dlg_name.getText().toString()){
                                    mDBReference.child("prod").child(user).child("2").child(str).removeValue();
                                }
                                mDBReference.child("prod").child(user).child("2").child(data.getName()).setValue(data);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        thirdDataset.set(position,data);
                                        thirdAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }).start();

                        Toast.makeText(getActivity(),"수정되었습니다.",Toast.LENGTH_SHORT).show();
                    }

                });
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                    }
                });

                dlg_purchasedate.setOnClickListener(new View.OnClickListener() { //구매날짜 입력
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month+1;
                                data.setPurchase_date(String.format("%d년 %d월 %d일",year,month,day));
                                dlg_purchasedate.setText(String.format("%d년 %d월 %d일",year,month,day));

                            }
                        },year,month,day);
                        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                        datePickerDialog.show();
                    }
                });
                dlg_date.setOnClickListener(new View.OnClickListener() { //유통기한입력
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month+1;
                                data.setDate(String.format("%d년 %d월 %d일",year,month,day));
                                dlg_date.setText(String.format("%d년 %d월 %d일",year,month,day));
                            }
                        },year,month,day);
                        datePickerDialog.show();
                    }
                });
                dlg.show();
            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}