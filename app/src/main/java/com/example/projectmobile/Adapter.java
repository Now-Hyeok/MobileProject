package com.example.projectmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Adapter extends RecyclerView.Adapter <Adapter.ViewHolder>{
    private ArrayList<data> mDataset;
    Context context;

    public Adapter(ArrayList<data> mDataset){this.mDataset = mDataset;}
    private OnItemClickListener mListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1,textView2,textView3;

        public ViewHolder(View itemView){
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.view_prod_name);
            textView2 = (TextView) itemView.findViewById(R.id.view_prod_purchasedate);
            textView3 = (TextView) itemView.findViewById(R.id.textView123);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener!=null){
                            mListener.onItemClick(v,pos);
                        }

                    }
                }
            });
        }
    }

    @NotNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.textView1.setText(mDataset.get(position).name);
        holder.textView2.setText(mDataset.get(position).purchase_date);



        GregorianCalendar toDayMan = new GregorianCalendar();

        int today_year = toDayMan.get(toDayMan.YEAR);  //년
        int today_month = toDayMan.get(toDayMan.MONTH)+1;//월
        int today_day = toDayMan.get(toDayMan.DAY_OF_MONTH); // 일 int 값으로 불러오기
        String currentTime = String.format("%d년 %d월 %d일",today_year,today_month,today_day);
        long count = calDateBetweenAandB(currentTime,mDataset.get(position).getDate());
        Log.d("count",String.valueOf(count));
        if(count>4){
            holder.textView1.setTextColor(Color.RED);
            holder.textView2.setTextColor(Color.RED);
            holder.textView3.setTextColor(Color.RED);
        }
        else if(4>count&& count>0){
            holder.textView2.setTextColor(Color.YELLOW);
            holder.textView1.setTextColor(Color.YELLOW);
            holder.textView3.setTextColor(Color.YELLOW);
        }




    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    public long calDateBetweenAandB (String a, String b){
        long calDateDays = 0, calDate;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy년 mm월 dd일");
            Date First = format.parse(a);
            Date second = format.parse(b);

            calDate = First.getTime() - second.getTime();
            calDateDays = calDate /(24*60*60*1000);

        }catch(ParseException e){

        }
        return calDateDays;
    }

}
