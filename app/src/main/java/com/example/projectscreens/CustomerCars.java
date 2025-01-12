package com.example.projectscreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.Car;

public class CustomerCars extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView carsListView;
    private ArrayList<Car> carDetailsList;
    private ArrayAdapter<Car> adapter;
    int userId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cars);

        drawerLayout = findViewById(R.id.drawer_layout);
        carsListView = findViewById(R.id.cars_list_view);
        carDetailsList = new ArrayList<>();

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

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", userId);

        if (userId != -1) {
            fetchCarsData("http://192.168.0.104/carAPIs/get_customer_cars.php?userId=" + userId);
        } else {
            Toast.makeText(this, "No user ID provided!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCarsData(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        populateCarsList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CustomerCars.this, "Error fetching cars: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void populateCarsList(JSONArray carsData) {
        carDetailsList.clear();
        for (int i = 0; i < carsData.length(); i++) {
            try {
                JSONObject car = carsData.getJSONObject(i);
                String licensePlate = car.getString("license_plate");
                String make = car.getString("make");
                String model = car.getString("model");
                String year = car.getString("year");
                Car carObj = new Car(licensePlate,make,model,year);
                carDetailsList.add(carObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                carDetailsList);
        carsListView.setAdapter(adapter);
    }
    public void homePage(View view){
        Intent intent = new Intent(CustomerCars.this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void viewTicketsPage(View view){
        Intent intent = new Intent(CustomerCars.this, ViewTicketsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void bookServicePage(View view){
        Intent intent = new Intent(CustomerCars.this, BookServiceActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void addCar(View view) {
        Intent intent = new Intent(CustomerCars.this, AddCarActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

}
