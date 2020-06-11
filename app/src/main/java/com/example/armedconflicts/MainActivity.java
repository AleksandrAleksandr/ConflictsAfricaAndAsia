package com.example.armedconflicts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView text9;
    List<Event> africaEvents;
    GoogleMap map;

    Spinner dateSpinner, eventTypeSpinner, regionSpinner, fatalitiesSpinner;
    List<Marker> markers = new ArrayList<>();

    SimpleDateFormat dateFormat;
    Calendar calendar;
    List<String> week = new ArrayList<>();

    List<String> weeks = new ArrayList<>();
    String[] event_types;
    String[] regions;
    String[] fatalities = {" < 10", "all", " > 10", " > 100"};

    View window;

    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text9 = findViewById(R.id.textView9);
        regions = getResources().getStringArray(R.array.regions);
        event_types = getResources().getStringArray(R.array.event_types);

        window = getLayoutInflater().inflate(R.layout.custon_info_window, null);

        setTodayDateAndMonth();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        createListOfWeeks();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createSpinners();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (!isOnline(getBaseContext())) {
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
            map.addTileOverlay(new TileOverlayOptions().tileProvider(new CustomTileProvider(getResources().getAssets())));
        }

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                //render(marker, window);
                //return window;
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                render(marker, window);
                return window;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                int event_index = (Integer) marker.getTag();

                intent.putExtra("event_date", africaEvents.get(event_index).getEvent_date());
                intent.putExtra("year", africaEvents.get(event_index).getYear());
                intent.putExtra("event_type", africaEvents.get(event_index).getEvent_type());
                intent.putExtra("sub_event_type", africaEvents.get(event_index).getSub_event_type());
                intent.putExtra("actor1", africaEvents.get(event_index).getActor1());
                intent.putExtra("actor2", africaEvents.get(event_index).getActor2());
                intent.putExtra("country", africaEvents.get(event_index).getCountry());
                intent.putExtra("location", africaEvents.get(event_index).getLocation());
                intent.putExtra("source", africaEvents.get(event_index).getSource());
                intent.putExtra("notes", africaEvents.get(event_index).getNotes());
                intent.putExtra("fatalities", africaEvents.get(event_index).getFatalities());
                startActivity(intent);
            }
        });
    }

    public void fillMap() {
        //map.clear();
        clearMap();

        for (int i = 0; i < africaEvents.size(); i++) {
            LatLng marker = new LatLng(africaEvents.get(i).getLatitude(), africaEvents.get(i).getLongitude());

            Marker mark = map.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(africaEvents.get(i).getLocation())
                    .snippet(africaEvents.get(i).getEvent_date()));

            mark.setTag(i);
            mark.setAnchor(0.5f, 0.5f);

            if (africaEvents.get(i).getEvent_type().equals("Battles"))
                mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.battle2));
            else if (africaEvents.get(i).getEvent_type().equals("Riots"))
                mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.riot));
            else if (africaEvents.get(i).getEvent_type().equals("Violence against civilians"))
                mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.violence2));
            else if (africaEvents.get(i).getEvent_type().equals("Strategic developments"))
                mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stategic));
            else if (africaEvents.get(i).getEvent_type().equals("Protests"))
                mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.protest));
            else
                mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.explosion));

            markers.add(mark);
        }
    }

    public void render(Marker marker, View view){
        int badge;

        int marker_id = (Integer) marker.getTag();
        if (africaEvents.get(marker_id).getEvent_type().equals("Battles"))
            badge = R.drawable.helm;
        else if (africaEvents.get(marker_id).getEvent_type().equals("Riots"))
            badge = R.drawable.protest_logo;
        else if (africaEvents.get(marker_id).getEvent_type().equals("Violence against civilians"))
            badge = R.drawable.crime;
        else if (africaEvents.get(marker_id).getEvent_type().equals("Strategic developments"))
            badge = R.drawable.radio;
        else if (africaEvents.get(marker_id).getEvent_type().equals("Protests"))
            badge = R.drawable.protest_logo;
        else badge = R.drawable.bomt_timer;

        ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

        String title = marker.getTitle();
        TextView titleUi = view.findViewById(R.id.title);
        if (title != null)
            titleUi.setText(title);
        else
            titleUi.setText("");

        String snippet = marker.getSnippet();
        TextView snippetUi = view.findViewById(R.id.snippet);
        if (snippet != null) {
            snippetUi.setText(snippet);
        } else {
            snippetUi.setText("");
        }
    }

    private void createSpinners() {
        dateSpinner = (Spinner)findViewById(R.id.date_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weeks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setPrompt("Week");

        eventTypeSpinner = (Spinner) findViewById(R.id.event_type_spinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, event_types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //typeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        eventTypeSpinner.setAdapter(typeAdapter);
        eventTypeSpinner.setPrompt("EventType");

        regionSpinner = (Spinner) findViewById(R.id.region_spinner);
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regions);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //typeAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        regionSpinner.setAdapter(regionAdapter);

        fatalitiesSpinner = (Spinner) findViewById(R.id.fatalities_spinner);
        ArrayAdapter<String> fatalitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fatalities);
        fatalitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fatalitiesSpinner.setAdapter(fatalitiesAdapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createWeek();
                requestDB(week, eventTypeSpinner.getSelectedItem().toString(), regionSpinner.getSelectedItem().toString(),fatalitiesSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestDB(week, eventTypeSpinner.getSelectedItem().toString(), regionSpinner.getSelectedItem().toString(), fatalitiesSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestDB(week, eventTypeSpinner.getSelectedItem().toString(), regionSpinner.getSelectedItem().toString(), fatalitiesSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fatalitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestDB(week, eventTypeSpinner.getSelectedItem().toString(), regionSpinner.getSelectedItem().toString(), fatalitiesSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestDB(List<String> week, String type, String region, String fatalities) {

        viewModel.getEvents(week, type, region, fatalities).observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                africaEvents = events;
                //updateMap
                fillMap();
            }
        });
    }

    private void createWeek() {
        try {
            calendar.setTime(dateFormat.parse(dateSpinner.getSelectedItem().toString().substring(0, 10)));
        } catch (Exception e) {
            //text8.setText("date ex");
        }

        week.clear();
        week.add(dateFormat.format(calendar.getTime())); //1
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week.add(dateFormat.format(calendar.getTime())); //2
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week.add(dateFormat.format(calendar.getTime())); //3
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week.add(dateFormat.format(calendar.getTime())); //4
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week.add(dateFormat.format(calendar.getTime())); //5
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week.add(dateFormat.format(calendar.getTime())); //6
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week.add(dateFormat.format(calendar.getTime())); //7
    }

    private void createListOfWeeks() {
        calendar = new GregorianCalendar();

        String week;
        week = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        week += " - ";
        week += dateFormat.format(calendar.getTime());
        weeks.add(week);

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        week += " - ";
        week += dateFormat.format(calendar.getTime());
        weeks.add(week);

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        week = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        week += " - ";
        week += dateFormat.format(calendar.getTime());
        weeks.add(week);
    }

    private void setTodayDateAndMonth() {
        calendar = new GregorianCalendar();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String today;
        ArrayList<String> month = new ArrayList<>();

        today = dateFormat.format(calendar.getTime());
        month.add(today);

        for (int i = 0; i < 20; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            month.add(dateFormat.format(calendar.getTime()));
        }

        DateInfo.setTodayAndMonth(today, month);
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
    private void clearMap() {
        for (Marker mark : markers) {
            mark.remove();
        }
        markers.clear();
    }
}
