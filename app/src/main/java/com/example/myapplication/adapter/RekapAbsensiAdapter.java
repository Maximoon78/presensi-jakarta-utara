package com.example.myapplication.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.POJO.Absensi;
import com.example.myapplication.R;
import com.example.myapplication.Utility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Calendar;

public class RekapAbsensiAdapter extends FirebaseRecyclerAdapter<Absensi, RekapAbsensiAdapter.ViewHolder> {

    //used for in case you need to click it
    Activity activity;

    public RekapAbsensiAdapter(@NonNull FirebaseRecyclerOptions<Absensi> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Absensi model) {
        Log.d("RekapAbsensiAdapter", "onBindViewHolder: " + model);
        int tanggalAbsen = Utility.ConvertEpochToCalendar(model.getWaktuAbsen()).get(Calendar.DATE);
        int bulanAbsen = Utility.ConvertEpochToCalendar(model.getWaktuAbsen()).get(Calendar.MONTH) + 1;
        int tahunAbsen = Utility.ConvertEpochToCalendar(model.getWaktuAbsen()).get(Calendar.YEAR);
        int jamAbsen = Utility.ConvertEpochToCalendar(model.getWaktuAbsen()).get(Calendar.HOUR_OF_DAY);
        int menitAbsen = Utility.ConvertEpochToCalendar(model.getWaktuAbsen()).get(Calendar.MINUTE);
        int dayOfWeekAbsen = Utility.ConvertEpochToCalendar(model.getWaktuAbsen()).get(Calendar.DAY_OF_WEEK);
        String hariAbsen = Utility.convertDayOfWeekToHari(dayOfWeekAbsen);
        String namaBulanAbsen = Utility.convertMonthToBulan(bulanAbsen);

        String tanggalAbsenUI = hariAbsen + ", " + tanggalAbsen + " " + namaBulanAbsen + " " + tahunAbsen;
        String jamAbsenUI = jamAbsen + " : " + menitAbsen;

        holder.tanggalAbsen.setText(tanggalAbsenUI);
        holder.jamAbsen.setText(jamAbsenUI);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_rekapabsensi, parent, false);

        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jamAbsen, tanggalAbsen;

        public ViewHolder(View view) {
            super(view);

            //initialize UI
            jamAbsen = view.findViewById(R.id.jamAbsen);
            tanggalAbsen = view.findViewById(R.id.tanggalAbsen);
        }
    }
}
