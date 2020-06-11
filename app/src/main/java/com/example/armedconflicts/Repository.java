package com.example.armedconflicts;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private EventDao eventDao;
    private LiveData<List<Event>> events;

    Repository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        eventDao = db.eventDao();

        loadData(); // нужно делать раз в неделю например .. грузить все события африки за месяц

        //events = eventDao.getAllEvents();
    }

    LiveData<List<Event>> getEventsByType(String type) { return eventDao.getEventsByType(type); }

    LiveData<List<Event>> getEventsByDateAndType(String date, String type) { return eventDao.getEventsByDateAndType(date, type); }

    //LiveData<List<Event>> getEventsByWeek(List<String> week) { return eventDao.getEventsByWeek(week); }

    //LiveData<List<Event>> getEventsByWeekAndType(List<String> week, String type) { return eventDao.getEventsByWeekAndType(week, type); }

    LiveData<List<Event>> getEvents(List<String> week, String type, String region, String fatalities) {
        int fatal;
        if (fatalities.equals("all"))
            fatal = 0;
        else if (fatalities.equals(" > 10"))
            fatal = 11;
        else if (fatalities.equals(" > 100"))
            fatal = 101;
        else
            fatal = -10;

        if (fatal >= 0) {
            if (type.equals("all") & region.equals("all"))
                return eventDao.getEventsByWeek(week, fatal);           //all types and regions
            else if (type.equals("all"))
                return eventDao.getEventsByWeekAndRegion(week, region, fatal);   //all types
            else if (region.equals("all"))
                return eventDao.getEventsByWeekAndType(week, type, fatal);     //all regions
            else
                return eventDao.getEventsByWeekAndTypeAndRegion(week, type, region, fatal);
        }
        else {
            if (type.equals("all") & region.equals("all"))
                return eventDao.getEventsByWeek_(week, -fatal);           //all types and regions
            else if (type.equals("all"))
                return eventDao.getEventsByWeekAndRegion_(week, region, -fatal);   //all types
            else if (region.equals("all"))
                return eventDao.getEventsByWeekAndType_(week, type, -fatal);     //all regions
            else
                return eventDao.getEventsByWeekAndTypeAndRegion_(week, type, region, -fatal);
        }
    }

    void insert(final Event event) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insert(event);
        });

    }

    void insertAll(final Event... event) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.insertAll(event);
        });
    }

    void deleteByDate(final List<String> month) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int deleted = eventDao.deleteByDate(month);
            System.out.println("DELETED " + deleted);
        });
    }

    public void loadData() {
        int page = 1;

        NetworkService.getInstance()
                .getApiService()
                .getEventsByDate(DateInfo.month.get(4), page)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        //deleteAll()
                        deleteByDate(DateInfo.month);
                        insertAll(response.body().getData());

                        if (response.body().getCount() == 500) {
                            loadMore(page+1);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });

    }

    private void loadMore(int page) {
        NetworkService.getInstance()
                .getApiService()
                .getEventsByDate(DateInfo.month.get(4), page)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        insertAll(response.body().getData());

                        if (response.body().getCount() == 500)
                            loadMore(page+1);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
    }
}
