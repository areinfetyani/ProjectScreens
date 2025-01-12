package com.example.projectscreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddCarActivity extends AppCompatActivity {

    private EditText licensePlateInput, makeInput, modelInput, yearInput;
    private Button addCarButton;
    private int customerId = -1; // Matches your database's `customer_id`

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        licensePlateInput = findViewById(R.id.license_plate_input);
        makeInput = findViewById(R.id.make_input);
        modelInput = findViewById(R.id.model_input);
        yearInput = findViewById(R.id.year_input);
        addCarButton = findViewById(R.id.add_car_button);

        // Get customerId from Intent
        customerId = getIntent().getIntExtra("userId", -1);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarToDatabase();
            }
        });
    }

    private void addCarToDatabase() {
        String url = "http://192.168.0.104/carAPIs/add_car.php";

        String licensePlate = licensePlateInput.getText().toString().trim();
        String make = makeInput.getText().toString().trim();
        String model = modelInput.getText().toString().trim();
        String year = yearInput.getText().toString().trim();

        if (licensePlate.isEmpty() || make.isEmpty() || model.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");

                            if (success) {
                                Toast.makeText(AddCarActivity.this, "Car added successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddCarActivity.this, CustomerCars.class);
                                intent.putExtra("userId", customerId);
                                startActivity(intent);                            } else {
                                Toast.makeText(AddCarActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddCarActivity.this, "Invalid response from server.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Toast.makeText(AddCarActivity.this, "Error adding car: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Add the parameters to be sent with the POST request
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", String.valueOf(customerId)); // Matches `customer_id` in your database
                params.put("license_plate", licensePlate);
                params.put("make", make);
                params.put("model", model);
                params.put("year", year);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
