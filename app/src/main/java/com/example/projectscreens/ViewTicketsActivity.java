package com.example.projectscreens;

import model.Ticket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class ViewTicketsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewTicketsAdapter adapter;
    private List<Ticket> tickets;
    private int userId = -1;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tickets); // Use the new layout
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

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.view_requests_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        tickets = new ArrayList<>();
        adapter = new ViewTicketsAdapter(tickets);
        recyclerView.setAdapter(adapter);

        // Get userId from Intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", userId);

        // Fetch tickets data if userId is valid
        if (userId != -1) {
            fetchTicketsData("http://192.168.0.104/carAPIs/get_tickets.php?userId=" + userId);
        } else {
            Toast.makeText(this, "No user ID provided!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchTicketsData(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseTicketsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewTicketsActivity.this, "Error fetching tickets: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void parseTicketsResponse(JSONArray response) {
        tickets.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject ticketJson = response.getJSONObject(i);
                int ticketId = ticketJson.getInt("ticket_id");
                String serviceName = ticketJson.getString("service_provided");
                String submittedDate = ticketJson.getString("submitted_date");
                String customerName = ticketJson.getString("customer_name");
                String emergencyLevel = ticketJson.getString("emergency_level");
                String status = ticketJson.getString("status");

                tickets.add(new Ticket(ticketId, serviceName, submittedDate, customerName, emergencyLevel, status));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    public void homePage(View view) {
        Intent intent = new Intent(ViewTicketsActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void viewTicketsPage(View view) {
        Intent intent = new Intent(ViewTicketsActivity.this, ViewTicketsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void bookServicePage(View view){
        Intent intent = new Intent(ViewTicketsActivity.this, BookServiceActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
