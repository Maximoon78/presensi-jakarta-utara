package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.myapplication.POJO.DataAbsen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class Utility {
    //Utility class
    //-------------
    //for utilities purposes, such as getting today date, and jam absen stuff

    //remove *1000 if u want in seconds
    static Long startDate = getTodayEpoch(getTodayDate());
    static Long startAbsenPagi = startDate + 60 * 60 * 7 * 1000;
    static Long endAbsenPagi = startDate + 60 * 60 * 10 * 1000 - 1000;
    static Long startAbsenSore = startDate + 60 * 60 * 15 * 1000;
    static Long endAbsenSore = startDate + 60 * 60 * 17 * 1000 - 1000;

    public static Calendar ConvertEpochToCalendar(Long epoch) {
        Date fromEpoch = new Date(epoch);
        Calendar calendar = Calendar.getInstance();
        //set time from epoch
        calendar.setTime(fromEpoch);
        return calendar;
    }

    public static String convertMonthToBulan(int bulan) {
        String bulan1;
        if (bulan == 1) {
            bulan1 = "Januari";
        } else if (bulan == 2) {
            bulan1 = "Februari";
        } else if (bulan == 3) {
            bulan1 = "Maret";
        } else if (bulan == 4) {
            bulan1 = "April";
        } else if (bulan == 5) {
            bulan1 = "Mei";
        } else if (bulan == 6) {
            bulan1 = "Juni";
        } else if (bulan == 7) {
            bulan1 = "Juli";
        } else if (bulan == 8) {
            bulan1 = "Agustus";
        } else if (bulan == 9) {
            bulan1 = "September";
        } else if (bulan == 10) {
            bulan1 = "Oktober";
        } else if (bulan == 11) {
            bulan1 = "November";
        } else {
            bulan1 = "Desember";
        }
        return bulan1;
    }

    public static String convertDayOfWeekToHari(int dayOfWeek) {
        String hari;

        switch (dayOfWeek) {
            case 1:
                hari = "Minggu";
                break;
            case 2:
                hari = "Senin";
                break;
            case 3:
                hari = "Selasa";
                break;
            case 4:
                hari = "Rabu";
                break;
            case 5:
                hari = "Kamis";
                break;
            case 6:
                hari = "Jumat";
                break;
            case 7:
                hari = "Sabtu";
                break;
            default:
                hari = "";
                break;
        }

        return hari;
    }

    //return 00:00 that day
    public static Long getTodayEpoch(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Date getTodayDate() {
        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        return today.getTime();
    }

    public static boolean isNoon(int hours) {
        boolean isNoon = false;
        if (hours >= 15) {
            if (hours < 17) {
                isNoon = true;
            }
        }
        return isNoon;
    }

    public static boolean isDay(int hours) {
        boolean isDay = false;
        if (hours >= 7) {
            if (hours < 10) {
                isDay = true;
            }
        }
        return isDay;
    }

    public static boolean isAbsenceDay(int dayOfWeek) {
        if (dayOfWeek >= 1) {
            return dayOfWeek <= 5;
        }
        return false;
    }

    public static void getUserAbsenceStatus(String userID, TextView statusAbsensiPagi, TextView statusAbsensiSore, Context context) {
        DatabaseReference absensiDatabaseReference = FirebaseDatabase.getInstance().getReference().child("absensi");

        //get status absen pagi
        absensiDatabaseReference.child(String.valueOf(String.valueOf(Utility.startAbsenPagi))).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataAbsen dataAbsen = snapshot.getValue(DataAbsen.class);
                if (dataAbsen == null) {
                    statusAbsensiPagi.setText("Belum Absen Pagi");
                    statusAbsensiPagi.setBackground(ContextCompat.getDrawable(context, R.drawable.button_red));
                } else {
                    statusAbsensiPagi.setText("Sudah Absen Pagi");
                    statusAbsensiPagi.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                }
                statusAbsensiPagi.setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get status absen sore
        absensiDatabaseReference.child(String.valueOf(String.valueOf(Utility.startAbsenSore))).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataAbsen dataAbsen = snapshot.getValue(DataAbsen.class);
                if (dataAbsen == null) {
                    statusAbsensiSore.setText("Belum Absen Sore");
                    statusAbsensiSore.setBackground(ContextCompat.getDrawable(context, R.drawable.button_red));
                } else {
                    statusAbsensiSore.setText("Sudah Absen Sore");
                    statusAbsensiSore.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                }
                statusAbsensiSore.setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

