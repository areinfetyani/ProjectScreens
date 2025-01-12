package com.example.projectscreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookServiceActivity extends AppCompatActivity {

    private Spinner serviceSpinner, carSpinner, emergencySpinner;
    private Button submitButton;
    private int userId = -1;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service);
        drawerLayout = findViewById(R.id.drawer_layout);

        ImageView navigationIcon = findViewById(R.id.left_image_1);
        navigationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Initialize fields
        serviceSpinner = findViewById(R.id.service_spinner);
        carSpinner = findViewById(R.id.car_spinner);
        emergencySpinner = findViewById(R.id.emergency_spinner);
        submitButton = findViewById(R.id.submit_button);

        // Get userId from intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID is required", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Load services and cars
        loadServices();
        loadCars();

        // Set up emergency levels
        ArrayAdapter<String> emergencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Low", "Medium", "High"});
        emergencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergencySpinner.setAdapter(emergencyAdapter);

        // Submit booking
        submitButton.setOnClickListener(v -> submitBooking());
    }

    private void loadServices() {
        String url = "http://192.168.0.104/carAPIs/get_services.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> services = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject service = response.getJSONObject(i);
                            services.add(service.getString("service_name"));
                        }
                        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
                        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        serviceSpinner.setAdapter(serviceAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing services", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching services", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }

    private void loadCars() {
        String url = "http://192.168.0.104/carAPIs/get_license_plate.php?userId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> cars = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            cars.add(response.getString(i)); // Directly get the string value
                        }
                        ArrayAdapter<String> carAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
                        carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        carSpinner.setAdapter(carAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing cars", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching cars", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }

    private void submitBooking() {
        String serviceName = serviceSpinner.getSelectedItem().toString();
        String licensePlate = carSpinner.getSelectedItem().toString();
        String emergencyLevel = emergencySpinner.getSelectedItem().toString();

        String url = "http://192.168.0.104/carAPIs/book_service.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Booking successful", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error booking service", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(userId));
                params.put("serviceName", serviceName);
                params.put("licensePlate", licensePlate);
                params.put("emergencyLevel", emergencyLevel);
                params.put("status", "pending");
                params.put("submittedDate", getCurrentDate());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }
    public void homePage(View view) {
        Intent intent = new Intent(BookServiceActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void viewTicketsPage(View view){
        Intent intent = new Intent(BookServiceActivity.this, ViewTicketsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void bookServicePage(View view){
        Intent intent = new Intent(BookServiceActivity.this, BookServiceActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}