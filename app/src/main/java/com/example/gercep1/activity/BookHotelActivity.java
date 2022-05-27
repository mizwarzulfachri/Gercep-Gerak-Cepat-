package com.example.gercep1.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gercep1.R;

import java.util.Calendar;
import java.util.HashMap;

public class BookHotelActivity extends AppCompatActivity {
    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinHotel, spinHari, spinDewasa, spinAnak;
    SessionManager session;
    String email;
    int id_hotel;
    public String sHotel, sHari, sJHari, sDewasa, sAnak;
    int jmlDewasa, jmlAnak;
    int hargaDewasa, hargaAnak;
    int hargaTotalDewasa, hargaTotalAnak, hargaTotal;
    private EditText tanggalMasuk;
    private DatePickerDialog dpTanggal;
    Calendar newCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_hotel);

        dbHelper = new DatabaseHelper(BookHotelActivity.this);
        db= dbHelper.getReadableDatabase();

        final String[] hotel = {"Ayani Hotel", "UB Caisar Hotel", "Kriyad Muraya Hotel", "Ring Road Hotel", "Hermes Hotel", "Medan Hotel", "Hotel The Pade"};
        final String[] hari = {"1", "2", "3"};
        final String[] dewasa = {"0", "1", "2", "3", "4", "5"};
        final String[] anak = {"0", "1", "2", "3", "4", "5"};

        spinHotel = findViewById(R.id.hotel);
        spinHari = findViewById(R.id.hari);
        spinDewasa = findViewById(R.id.dewasa);
        spinAnak = findViewById(R.id.anak);

        ArrayAdapter<CharSequence> adapterHotel = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, hotel);
        adapterHotel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinHotel.setAdapter(adapterHotel);

        ArrayAdapter<CharSequence> adapterHari = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, hari);
        adapterHari.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinHari.setAdapter(adapterHari);

        ArrayAdapter<CharSequence> adapterDewasa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dewasa);
        adapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDewasa.setAdapter(adapterDewasa);

        ArrayAdapter<CharSequence> adapterAnak = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, anak);
        adapterAnak.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAnak.setAdapter(adapterAnak);

        spinHotel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sHotel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        spinHari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sHari = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        spinDewasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sDewasa = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinAnak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sAnak = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button book = findViewById(R.id.book);

        tanggalMasuk = findViewById(R.id.tanggal_check_in);
        tanggalMasuk.setInputType(InputType.TYPE_NULL);
        tanggalMasuk.requestFocus();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);
        setDateTimeField();

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perhitunganHarga();
                if (sHotel != null && sHari != null && sJHari != null && sDewasa != null) {
                    AlertDialog dialog = new AlertDialog.Builder(BookHotelActivity.this)
                            .setTitle("Ingin booking hotel sekarang?")
                            .setPositiveButton("Ya,", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        db.execSQL("INSERT INTO TB_HOTEL (hotel, hari, jumlah, dewasa, anak) VALUES ('" +
                                                sHotel + "','" +
                                                sJHari + "','" +
                                                sHari + "','" +
                                                sDewasa + "','" +
                                                sAnak + "');");
                                        cursor = db.rawQuery("SELECT id_hotel FROM TB_HOTEL ORDER BY id_hotel DESC", null);
                                        cursor.moveToLast();
                                        if (cursor.getCount() > 0) {
                                            cursor.moveToPosition(0);
                                            id_hotel = cursor.getInt(0);
                                        }
                                        db.execSQL("INSERT INTO TB_HARGA (username, id_hotel, harga_dewasa, harga_anak, harga_total) VALUES ('" +
                                                email + "','" +
                                                id_hotel + "','" +
                                                hargaTotalDewasa + "','" +
                                                hargaTotalAnak + "','" +
                                                hargaTotal + "');");
                                        Toast.makeText(BookHotelActivity.this, "Bookng berhasil", Toast.LENGTH_LONG).show();
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(BookHotelActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .create();
                    dialog.show();
                } else {
                    Toast.makeText(BookHotelActivity.this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle("Form Booking");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void perhitunganHarga() {
        if (sHotel.equalsIgnoreCase("ayani hotel") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 450000;
            hargaAnak = 150000;
        } else if (sHotel.equalsIgnoreCase("ayani hotel") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 900000;
            hargaAnak = 300000;
        } else if (sHotel.equalsIgnoreCase("ayani hotel") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 1350000;
            hargaAnak = 450000;
        } else if (sHotel.equalsIgnoreCase("ub caisar hotel") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 250000;
            hargaAnak = 50000;
        } else if (sHotel.equalsIgnoreCase("ub caisar hotel") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 500000;
            hargaAnak = 100000;
        } else if (sHotel.equalsIgnoreCase("ub caisar hotel") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 750000;
            hargaAnak = 150000;
        } else if (sHotel.equalsIgnoreCase("kriyad muraya hotel") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 325000;
            hargaAnak = 75000;
        } else if (sHotel.equalsIgnoreCase("kriyad muraya hotel") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 650000;
            hargaAnak = 150000;
        } else if (sHotel.equalsIgnoreCase("kriyad muraya hotel") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 975000;
            hargaAnak = 225000;
        } else if (sHotel.equalsIgnoreCase("ring road hotel") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 290000;
            hargaAnak = 100000;
        } else if (sHotel.equalsIgnoreCase("ring road hotel") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 580000;
            hargaAnak = 200000;
        } else if (sHotel.equalsIgnoreCase("ring road hotel") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 870000;
            hargaAnak = 300000;
        } else if (sHotel.equalsIgnoreCase("hermes hotel") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 600000;
            hargaAnak = 250000;
        } else if (sHotel.equalsIgnoreCase("hermes hotel") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 1200000;
            hargaAnak = 500000;
        } else if (sHotel.equalsIgnoreCase("hermes hotel") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 1800000;
            hargaAnak = 750000;
        } else if (sHotel.equalsIgnoreCase("medan hotel") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 180000;
            hargaAnak = 0;
        } else if (sHotel.equalsIgnoreCase("medan hotel") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 360000;
            hargaAnak = 0;
        } else if (sHotel.equalsIgnoreCase("medan hotel") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 540000;
            hargaAnak = 0;
        } else if (sHotel.equalsIgnoreCase("hotel the pade") && sHari.equalsIgnoreCase("1")) {
            hargaDewasa = 420000;
            hargaAnak = 125000;
        } else if (sHotel.equalsIgnoreCase("hotel the pade") && sHari.equalsIgnoreCase("2")) {
            hargaDewasa = 840000;
            hargaAnak = 250000;
        } else if (sHotel.equalsIgnoreCase("hotel the pade") && sHari.equalsIgnoreCase("3")) {
            hargaDewasa = 1260000;
            hargaAnak = 375000;
        }

        jmlDewasa = Integer.parseInt(sDewasa);
        jmlAnak = Integer.parseInt(sAnak);

        hargaTotalDewasa = jmlDewasa * hargaDewasa;
        hargaTotalAnak = jmlAnak * hargaAnak;
        hargaTotal = hargaTotalDewasa + hargaTotalAnak;
    }

    private void setDateTimeField() {
        tanggalMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpTanggal.show();
            }
        });

        dpTanggal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei",
                        "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                sJHari = dayOfMonth + " " + bulan[monthOfYear] + " " + year;
                tanggalMasuk.setText(sJHari);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
