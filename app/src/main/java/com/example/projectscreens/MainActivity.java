package com.example.projectscreens;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TableLayout carsTable;
    private TableLayout ticketsTable;
    private DrawerLayout drawerLayout;
    int userId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = 123456789; //example until we get from intent
        drawerLayout = findViewById(R.id.drawer_layout);
        carsTable = findViewById(R.id.cars_card_table);
        ticketsTable = findViewById(R.id.history_card_table);

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

        fetchCarsData("http://192.168.0.104/carAPIs/get_customer_cars.php?userId=" + userId);
        fetchCompletedTicketsData("http://192.168.0.104/carAPIs/get_completed_tickets.php?userId=" + userId);
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
                        populateCarsTable(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching cars: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void fetchCompletedTicketsData(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        populateTicketsTable(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching tickets: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void populateCarsTable(JSONArray carsData) {
        carsTable.removeAllViews();
        int numOfViews = Math.min(carsData.length(), 2);

        for (int i = 0; i < numOfViews; i++) {
            try {
                JSONObject car = carsData.getJSONObject(i);
                String make = car.getString("make");
                String model = car.getString("model");
                String year = car.getString("year");

                TableRow row = createTableRow();
                row.addView(createImageView(R.drawable.mini_car));
                row.addView(createStyledTextView(make));
                row.addView(createStyledTextView(model));
                row.addView(createStyledTextView(year));

                carsTable.addView(row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateTicketsTable(JSONArray ticketsData) {
        ticketsTable.removeAllViews();
        int numOfViews = Math.min(ticketsData.length(), 5);

        for (int i = 0; i < numOfViews; i++) {
            try {
                JSONObject ticket = ticketsData.getJSONObject(i);
                String service = ticket.getString("service_id");
                String completedDate = ticket.getString("completed_date");

                TableRow row = createTableRow();
                row.addView(createImageView(R.drawable.ticket_icon)); // Replace with ticket icon resource
                row.addView(createStyledTextView(service));
                row.addView(createStyledTextView(completedDate));

                ticketsTable.addView(row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(rowParams);
        row.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        return row;
    }

    private ImageView createImageView(int drawableRes) {
        ImageView gifView = new ImageView(this);
        gifView.setLayoutParams(new TableRow.LayoutParams(100, 100));
        Glide.with(this).load(drawableRes).into(gifView);
        return gifView;
    }

    private TextView createStyledTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        ));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(Color.parseColor("#003049"));
        return textView;
    }

    public void showMoreCars(View view){
        Intent intent = new Intent(MainActivity.this, CustomerCars.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void homePage(View view){
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void completedTicketsPage(View view){
        Intent intent = new Intent(MainActivity.this, CompletedTicketsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void viewTicketsPage(View view){
        Intent intent = new Intent(MainActivity.this, ViewTicketsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    public void bookServicePage(View view){
        Intent intent = new Intent(MainActivity.this, BookServiceActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
