package com.navigatio.campusservices;

public class NavigationEdge {

    private final String fromNodeId;
    private final String toNodeId;

    private final double distance;

    /*
     * true = the path can be walked in both directions.
     * false = only from -> to.
     */
    private final boolean bidirectional;


    public NavigationEdge(
            String fromNodeId,
            String toNodeId,
            double distance
    ) {

        this(
                fromNodeId,
                toNodeId,
                distance,
                true
        );
    }


    public NavigationEdge(
            String fromNodeId,
            String toNodeId,
            double distance,
            boolean bidirectional
    ) {

        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.distance = distance;
        this.bidirectional = bidirectional;
    }


    public String getFromNodeId() {
        return fromNodeId;
    }


    public String getToNodeId() {
        return toNodeId;
    }


    public double getDistance() {
        return distance;
    }


    public boolean isBidirectional() {
        return bidirectional;
    }


    /*
     * Compatibility methods in case your existing
     * Dijkstra code uses getFrom(), getTo(), etc.
     */

    public String getFrom() {
        return fromNodeId;
    }


    public String getTo() {
        return toNodeId;
    }


    public double getWeight() {
        return distance;
    }


    @Override
    public String toString() {

        return fromNodeId
                + " <-> "
                + toNodeId
                + " ("
                + distance
                + ")";
    }
}
