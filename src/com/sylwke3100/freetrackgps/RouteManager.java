package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.Date;
import java.util.List;


public class RouteManager {
    private DefaultValues.areaStatus currentStatus;
    private Context context;
    private DefaultValues.routeStatus status;
    private long startTime;
    private Location lastPosition;
    private double distance;
    private AreaNotificationManager areaNotification;
    private DatabaseManager currentDB;
    private long currentId;
    private List<IgnorePointsListElement> globalIgnorePointsList;
    private VibrateNotificationManager vibrateNotificationManager;

    public RouteManager(Context globalContext) {
        context = globalContext;
        status = DefaultValues.routeStatus.stop;
        currentDB = new DatabaseManager(globalContext);
        currentStatus = DefaultValues.areaStatus.ok;
        vibrateNotificationManager = new VibrateNotificationManager(globalContext);
    }

    public DefaultValues.areaStatus getPointStatus() {
        return currentStatus;
    }

    public boolean findPointInIgnore(Location cLocation) {
        for (IgnorePointsListElement element : globalIgnorePointsList) {
            Location current = new Location(LocationManager.GPS_PROVIDER);
            current.setLatitude(element.latitude);
            current.setLongitude(element.longitude);
            float distance = current.distanceTo(cLocation);
            if (distance < 100)
                return true;
        }
        return false;
    }

    public void start() {
        globalIgnorePointsList = currentDB.getIgnorePointsList();
        startTime = System.currentTimeMillis();
        currentId = currentDB.startWorkout(startTime);
        status = DefaultValues.routeStatus.start;
        distance = 0.0;
        areaNotification.setContent(context.getString(R.string.workoutDistanceLabel) + ": " + String
                .format("%.2f km", getDistanceInKm()));
        areaNotification.sendNotify();
    }

    public void addPoint(Location currentLocation) {
        Date currentDate = new Date();
        if (status == DefaultValues.routeStatus.start) {
            long currentTime = currentDate.getTime();
            if (findPointInIgnore(currentLocation) == false) {
                RouteElement routePoint =
                        new RouteElement(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                currentLocation.getAltitude(), currentTime);
                if (lastPosition != null)
                    distance += lastPosition.distanceTo(currentLocation);
                vibrateNotificationManager.proccesNotify(currentLocation);
                currentDB.addPoint(currentId, routePoint, distance);
                currentStatus = DefaultValues.areaStatus.ok;
                lastPosition = currentLocation;
            } else
                currentStatus = DefaultValues.areaStatus.prohibited;
        }
    }

    public void pause() {
        status = DefaultValues.routeStatus.pause;
    }

    public void unPause() {
        status = DefaultValues.routeStatus.start;
        lastPosition = null;
    }

    public double getDistanceInKm() {
        return distance / 1000;
    }

    public DefaultValues.routeStatus getStatus() {
        return status;
    }

    public void stop() {
        status = DefaultValues.routeStatus.stop;
        distance = 0.0;
        lastPosition = null;
        areaNotification.deleteNotify();
        currentId = -1;
        vibrateNotificationManager.clear(true);
    }

    public void setAreaNotifyInstance(AreaNotificationManager notify) {
        this.areaNotification = notify;
    }
}
