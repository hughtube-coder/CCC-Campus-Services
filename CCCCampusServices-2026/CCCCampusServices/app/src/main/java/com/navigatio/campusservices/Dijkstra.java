
        package com.navigatio.campusservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    /*
     * =========================================================
     * INTERNAL QUEUE ITEM
     * =========================================================
     *
     * This is used by the PriorityQueue.
     * The node with the smallest current distance is processed
     * first.
     */
    private static class NodeDistance
            implements Comparable<NodeDistance> {

        private final String nodeId;
        private final double distance;

        NodeDistance(
                String nodeId,
                double distance
        ) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(
                    this.distance,
                    other.distance
            );
        }
    }


    /*
     * =========================================================
     * FIND SHORTEST PATH USING NODE IDS
     * =========================================================
     *
     * Example:
     *
     * List<NavigationNode> path =
     *     Dijkstra.findShortestPath(
     *         campusGraph,
     *         "N001",
     *         "N005"
     *     );
     */
    public static List<NavigationNode> findShortestPath(
            CampusGraph graph,
            String startNodeId,
            String destinationNodeId
    ) {

        if (graph == null) {
            return Collections.emptyList();
        }

        if (startNodeId == null
                || destinationNodeId == null) {
            return Collections.emptyList();
        }

        NavigationNode start =
                graph.findNodeById(startNodeId);

        NavigationNode destination =
                graph.findNodeById(destinationNodeId);

        if (start == null
                || destination == null) {
            return Collections.emptyList();
        }

        return findShortestPath(
                graph,
                start,
                destination
        );
    }


    /*
     * =========================================================
     * FIND SHORTEST PATH USING NAVIGATION NODES
     * =========================================================
     */
    public static List<NavigationNode> findShortestPath(
            CampusGraph graph,
            NavigationNode start,
            NavigationNode destination
    ) {

        if (graph == null
                || start == null
                || destination == null) {

            return Collections.emptyList();
        }


        /*
         * If start and destination are the same node,
         * no routing is necessary.
         */
        if (start.getId().equalsIgnoreCase(
                destination.getId()
        )) {

            List<NavigationNode> result =
                    new ArrayList<>();

            result.add(start);

            return result;
        }


        /*
         * =====================================================
         * DISTANCE TABLE
         * =====================================================
         *
         * distances[nodeId] =
         * shortest known distance from start.
         */
        Map<String, Double> distances =
                new HashMap<>();


        /*
         * =====================================================
         * PREVIOUS TABLE
         * =====================================================
         *
         * previous[nodeId] =
         * node immediately before this node in the route.
         */
        Map<String, String> previous =
                new HashMap<>();


        /*
         * Initialize every node as unreachable.
         */
        for (NavigationNode node : graph.getNodes()) {

            if (node == null
                    || node.getId() == null) {
                continue;
            }

            distances.put(
                    node.getId(),
                    Double.POSITIVE_INFINITY
            );
        }


        /*
         * Start node has distance zero.
         */
        distances.put(
                start.getId(),
                0.0
        );


        /*
         * Priority queue always gives us the node
         * with the smallest known distance.
         */
        PriorityQueue<NodeDistance> queue =
                new PriorityQueue<>();


        queue.add(
                new NodeDistance(
                        start.getId(),
                        0.0
                )
        );


        /*
         * Nodes already processed.
         */
        Set<String> visited =
                new HashSet<>();


        /*
         * =====================================================
         * DIJKSTRA LOOP
         * =====================================================
         */
        while (!queue.isEmpty()) {

            NodeDistance current =
                    queue.poll();


            String currentId =
                    current.nodeId;


            /*
             * Ignore a node if it has already been finalized.
             */
            if (visited.contains(currentId)) {
                continue;
            }


            /*
             * Ignore invalid queue entries.
             */
            if (!distances.containsKey(currentId)) {
                continue;
            }


            /*
             * Finalize this node.
             */
            visited.add(currentId);


            /*
             * Once the destination is finalized,
             * the shortest path has been found.
             */
            if (currentId.equalsIgnoreCase(
                    destination.getId()
            )) {

                break;
            }


            /*
             * Get the current node.
             */
            NavigationNode currentNode =
                    graph.findNodeById(currentId);


            if (currentNode == null) {
                continue;
            }


            /*
             * =================================================
             * CHECK EVERY EDGE
             * =================================================
             */
            for (NavigationEdge edge :
                    graph.getEdges()) {

                if (edge == null) {
                    continue;
                }


                String fromId =
                        edge.getFromNodeId();

                String toId =
                        edge.getToNodeId();


                if (fromId == null
                        || toId == null) {
                    continue;
                }


                String neighborId = null;


                /*
                 * -------------------------------------------------
                 * FORWARD DIRECTION
                 * -------------------------------------------------
                 *
                 * current -> neighbor
                 */
                if (fromId.equalsIgnoreCase(
                        currentId
                )) {

                    neighborId = toId;
                }


                /*
                 * -------------------------------------------------
                 * REVERSE DIRECTION
                 * -------------------------------------------------
                 *
                 * neighbor -> current
                 *
                 * Only allowed when the edge is bidirectional.
                 */
                else if (
                        edge.isBidirectional()
                                && toId.equalsIgnoreCase(
                                currentId
                        )
                ) {

                    neighborId = fromId;
                }


                /*
                 * This edge is not connected to
                 * the current node.
                 */
                if (neighborId == null) {
                    continue;
                }


                /*
                 * =================================================
                 * GET NEIGHBOR
                 * =================================================
                 */
                NavigationNode neighbor =
                        graph.findNodeById(
                                neighborId
                        );


                if (neighbor == null) {
                    continue;
                }


                /*
                 * =================================================
                 * PAGE / FLOOR SAFETY
                 * =================================================
                 *
                 * Nodes on different map pages must not be
                 * connected accidentally.
                 *
                 * Example:
                 *
                 * Page 1 -> Page 2
                 *
                 * is NOT allowed simply because the coordinates
                 * happen to be close.
                 *
                 * Floor changes must use the explicit stair
                 * connection that you created in CampusGraph.
                 */
                if (currentNode.getMapPage()
                        != neighbor.getMapPage()) {

                    /*
                     * The only valid way to change pages is
                     * through an explicitly named stair node.
                     */
                    if (!isFloorTransition(
                            currentNode,
                            neighbor
                    )) {

                        continue;
                    }
                }


                /*
                 * =================================================
                 * EDGE DISTANCE VALIDATION
                 * =================================================
                 *
                 * Dijkstra requires non-negative weights.
                 */
                double edgeDistance =
                        edge.getDistance();


                if (Double.isNaN(edgeDistance)
                        || Double.isInfinite(edgeDistance)
                        || edgeDistance < 0) {

                    continue;
                }


                /*
                 * =================================================
                 * CALCULATE NEW DISTANCE
                 * =================================================
                 */
                double currentDistance =
                        distances.getOrDefault(
                                currentId,
                                Double.POSITIVE_INFINITY
                        );


                if (Double.isInfinite(
                        currentDistance
                )) {

                    continue;
                }


                double newDistance =
                        currentDistance
                                + edgeDistance;


                double oldDistance =
                        distances.getOrDefault(
                                neighborId,
                                Double.POSITIVE_INFINITY
                        );


                /*
                 * =================================================
                 * FOUND A BETTER ROUTE
                 * =================================================
                 */
                if (newDistance < oldDistance) {

                    distances.put(
                            neighborId,
                            newDistance
                    );


                    previous.put(
                            neighborId,
                            currentId
                    );


                    queue.add(
                            new NodeDistance(
                                    neighborId,
                                    newDistance
                            )
                    );
                }
            }
        }


        /*
         * =========================================================
         * DESTINATION REACHABILITY CHECK
         * =========================================================
         */
        Double destinationDistance =
                distances.get(
                        destination.getId()
                );


        if (destinationDistance == null
                || Double.isInfinite(
                destinationDistance
        )) {

            return Collections.emptyList();
        }


        /*
         * =========================================================
         * RECONSTRUCT PATH
         * =========================================================
         *
         * previous[] contains the route backwards.
         *
         * Example:
         *
         * N005 <- P013 <- P003 <- P002 <- P001 <- N001
         *
         * We reverse it to:
         *
         * N001 -> P001 -> P002 -> P003 -> P013 -> N005
         */
        List<NavigationNode> path =
                new ArrayList<>();


        String currentId =
                destination.getId();


        /*
         * Safety counter prevents an accidental infinite loop
         * if the graph contains malformed previous references.
         */
        int safetyCounter = 0;

        int maximumSteps =
                graph.getNodes().size() + 1;


        while (currentId != null
                && safetyCounter <= maximumSteps) {

            NavigationNode node =
                    graph.findNodeById(
                            currentId
                    );


            if (node == null) {
                return Collections.emptyList();
            }


            path.add(node);


            /*
             * We reached the starting node.
             */
            if (currentId.equalsIgnoreCase(
                    start.getId()
            )) {

                break;
            }


            /*
             * Move backwards through the route.
             */
            currentId =
                    previous.get(currentId);


            safetyCounter++;
        }


        /*
         * If the route did not reach the start,
         * something is wrong with the graph.
         */
        if (path.isEmpty()
                || !path.get(
                path.size() - 1
        ).getId().equalsIgnoreCase(
                start.getId()
        )) {

            return Collections.emptyList();
        }


        /*
         * Reverse the backwards route.
         */
        Collections.reverse(path);


        /*
         * =========================================================
         * FINAL PATH VALIDATION
         * =========================================================
         */
        if (path.isEmpty()) {
            return Collections.emptyList();
        }


        /*
         * First node MUST be the current location.
         */
        if (!path.get(0)
                .getId()
                .equalsIgnoreCase(
                        start.getId()
                )) {

            return Collections.emptyList();
        }


        /*
         * Last node MUST be the destination.
         */
        if (!path.get(
                path.size() - 1
        ).getId().equalsIgnoreCase(
                destination.getId()
        )) {

            return Collections.emptyList();
        }


        return path;
    }


    /*
     * =========================================================
     * FLOOR TRANSITION CHECK
     * =========================================================
     *
     * A route is allowed to change map pages only when the
     * connection is explicitly associated with a stair.
     *
     * Your current graph contains nodes such as:
     *
     * N043 = JMC Second Floor Stair
     * P019 = Second Floor Stair
     * P022 = Third Floor Stair
     *
     * etc.
     */
    private static boolean isFloorTransition(
            NavigationNode current,
            NavigationNode neighbor
    ) {

        if (current == null
                || neighbor == null) {

            return false;
        }


        String currentName =
                current.getName();

        String neighborName =
                neighbor.getName();


        if (currentName == null
                || neighborName == null) {

            return false;
        }


        String currentLower =
                currentName.toLowerCase();

        String neighborLower =
                neighborName.toLowerCase();


        /*
         * A floor transition is permitted when at least
         * one side is explicitly a stair.
         */
        return currentLower.contains("stair")
                || neighborLower.contains("stair");
    }


    /*
     * =========================================================
     * GET TOTAL DISTANCE OF A ROUTE
     * =========================================================
     */
    public static double getPathDistance(
            CampusGraph graph,
            List<NavigationNode> path
    ) {

        if (graph == null
                || path == null
                || path.size() < 2) {

            return 0.0;
        }


        double total = 0.0;


        for (int i = 0;
             i < path.size() - 1;
             i++) {

            NavigationNode from =
                    path.get(i);

            NavigationNode to =
                    path.get(i + 1);


            if (from == null
                    || to == null) {

                continue;
            }


            NavigationEdge matchingEdge =
                    findEdge(
                            graph,
                            from.getId(),
                            to.getId()
                    );


            if (matchingEdge != null) {

                total +=
                        matchingEdge.getDistance();
            }
        }


        return total;
    }


    /*
     * =========================================================
     * FIND EDGE BETWEEN TWO NODES
     * =========================================================
     */
    private static NavigationEdge findEdge(
            CampusGraph graph,
            String fromId,
            String toId
    ) {

        if (graph == null
                || fromId == null
                || toId == null) {

            return null;
        }


        for (NavigationEdge edge :
                graph.getEdges()) {

            if (edge == null) {
                continue;
            }


            String edgeFrom =
                    edge.getFromNodeId();

            String edgeTo =
                    edge.getToNodeId();


            if (edgeFrom == null
                    || edgeTo == null) {

                continue;
            }


            /*
             * Normal direction.
             */
            boolean forward =
                    edgeFrom.equalsIgnoreCase(
                            fromId
                    )
                            && edgeTo.equalsIgnoreCase(
                            toId
                    );


            /*
             * Reverse direction.
             */
            boolean reverse =
                    edge.isBidirectional()
                            && edgeFrom.equalsIgnoreCase(
                            toId
                    )
                            && edgeTo.equalsIgnoreCase(
                            fromId
                    );


            if (forward || reverse) {
                return edge;
            }
        }


        return null;
    }


    /*
     * =========================================================
     * CHECK WHETHER A ROUTE EXISTS
     * =========================================================
     */
    public static boolean hasPath(
            CampusGraph graph,
            String startNodeId,
            String destinationNodeId
    ) {

        List<NavigationNode> path =
                findShortestPath(
                        graph,
                        startNodeId,
                        destinationNodeId
                );


        return !path.isEmpty();
    }


    /*
     * =========================================================
     * GET START NODE FROM A PATH
     * =========================================================
     */
    public static NavigationNode getStartNode(
            List<NavigationNode> path
    ) {

        if (path == null
                || path.isEmpty()) {

            return null;
        }


        return path.get(0);
    }


    /*
     * =========================================================
     * GET DESTINATION NODE FROM A PATH
     * =========================================================
     */
    public static NavigationNode getDestinationNode(
            List<NavigationNode> path
    ) {

        if (path == null
                || path.isEmpty()) {

            return null;
        }


        return path.get(
                path.size() - 1
        );
    }
}
