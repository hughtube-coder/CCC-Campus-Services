package com.navigatio.campusservices;

public class NavigationNode {

    private final String id;
    private final String name;

    /*
     * Map page:
     * 1 = Admin Building
     * 2 = Ground Floor
     * 3 = Second Floor
     * 4 = Third Floor
     */
    private final int mapPage;

    /*
     * Normalized map coordinates.
     *
     * X:
     * 0.0 = left
     * 1.0 = right
     *
     * Y:
     * 0.0 = top
     * 1.0 = bottom
     */
    private final float x;
    private final float y;

    /*
     * true  = actual destination / QR location
     * false = waypoint used only for navigation
     */
    private final boolean qrEnabled;


    public NavigationNode(
            String id,
            String name,
            int mapPage,
            float x,
            float y,
            boolean qrEnabled
    ) {

        this.id = id;
        this.name = name;
        this.mapPage = mapPage;
        this.x = x;
        this.y = y;
        this.qrEnabled = qrEnabled;
    }


    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public int getMapPage() {
        return mapPage;
    }


    public float getX() {
        return x;
    }


    public float getY() {
        return y;
    }


    public boolean isQrEnabled() {
        return qrEnabled;
    }


    @Override
    public String toString() {
        return id + " - " + name;
    }
}
