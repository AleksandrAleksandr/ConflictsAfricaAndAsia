package com.example.armedconflicts;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Event>> events;

    public ViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        //events = repository.getEvents();
    }

    LiveData<List<Event>> getEventsByType(String type) { return repository.getEventsByType(type); }

    LiveData<List<Event>> getEventsByDateAndType(String date, String type) { return repository.getEventsByDateAndType(date, type); }

    //LiveData<List<Event>> getEventsByWeek(List<String> week) { return repository.getEventsByWeek(week); }

    //LiveData<List<Event>> getEventsByWeekAndType(List<String> week, String type) { return repository.getEventsByWeekAndType(week,type); }

    LiveData<List<Event>> getEvents(List<String> week, String type, String region, String fatalities) { return repository.getEvents(week, type, region, fatalities); }

    public void insert(Event event) { repository.insert(event); }
}
