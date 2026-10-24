package com.navigatio.campusservices;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerStartLocation;
    private AutoCompleteTextView spinnerDestination;
    private Button btnStartNavigation;
    private TextView tvPathInstructions;

    private String scannedNodeId;
    private NavigationNode scannedStartNode;

    private LinearLayout pathInstructionsPanel;
    private View pathDragHandle;

    private int collapsedHeight;
    private int expandedHeight;

    private float panelStartY;
    private boolean panelDragging = false;

    private CampusGraph campusGraph;

    private ImageView campusMap;
    private FrameLayout mainDashboard;

    private Matrix mapMatrix;
    private ScaleGestureDetector scaleGestureDetector;

    private float lastX;
    private float lastY;

    private boolean isDragging = false;

    private RouteOverlayView routeOverlay;

    private final List<NavigationNode> currentRouteNodes =
            new ArrayList<>();

    /*
     * Index of the node where the red destination pin is drawn.
     *
     * Rooms are reached through a door node. The pin goes on that door
     * (the outside of the room) rather than in the middle of the room.
     */
    private int getDestinationPinIndex() {

        int last = currentRouteNodes.size() - 1;

        if (last >= 1) {

            NavigationNode destination =
                    currentRouteNodes.get(last);

            NavigationNode before =
                    currentRouteNodes.get(last - 1);

            if (destination.isQrEnabled()
                    && before.getName() != null
                    && before.getName().equalsIgnoreCase("Door")
                    && before.getMapPage()
                    == destination.getMapPage()) {

                return last - 1;
            }
        }

        return Math.max(last, 0);
    }


    private int currentMapPage = 1;

    /*
     * Page switcher shown when a route continues on another map page.
     */
    private LinearLayout pageNavBar;
    private Button btnPrevPage;
    private Button btnNextPage;
    private TextView tvPageLabel;

    private float baseScale = 1.0f;
    private float currentScale = 1.0f;

    private final float maxZoomMultiplier = 4.0f;

    private float touchStartX;
    private float touchStartY;

    private long touchStartTime;

    private boolean mapControlsVisible = true;

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private LinearLayout officeListContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard1);

        campusGraph = new CampusGraph();

        /*
         * =====================================================
         * GET STARTING LOCATION FROM QR CODE
         * =====================================================
         */

        String rawScannedValue =
                getIntent().getStringExtra("NODE_ID");

        scannedNodeId =
                convertQrValueToNodeId(rawScannedValue);

        if (scannedNodeId != null) {

            scannedStartNode =
                    campusGraph.findNodeById(scannedNodeId);
        }


        /*
         * =====================================================
         * FIND VIEWS
         * =====================================================
         */

        spinnerStartLocation =
                findViewById(R.id.spinnerStartLocation);

        spinnerDestination =
                findViewById(R.id.spinnerDestination);

        btnStartNavigation =
                findViewById(R.id.btnStartNavigation);

        tvPathInstructions =
                findViewById(R.id.tvPathInstructions);

        campusMap =
                findViewById(R.id.campusMap);

        mainDashboard =
                findViewById(R.id.mainDashboard);

        pathInstructionsPanel =
                findViewById(R.id.pathInstructionsPanel);

        pathDragHandle =
                findViewById(R.id.pathDragHandle);

        drawerLayout =
                findViewById(R.id.drawerLayout);

        ivMenu =
                findViewById(R.id.ivMenu);

        officeListContainer =
                findViewById(R.id.officeListContainer);


        /*
         * =====================================================
         * INITIALIZE MAP / UI
         * =====================================================
         */

        setupMapTouch();

        setupPathInstructionsPanel();

        setupDrawer();


        /*
         * =====================================================
         * LOCK START LOCATION TO QR CODE
         * =====================================================
         */

        spinnerStartLocation.setKeyListener(null);
        spinnerStartLocation.setFocusable(false);
        spinnerStartLocation.setClickable(false);
        spinnerStartLocation.setCursorVisible(false);

        if (scannedStartNode != null
                && scannedStartNode.isQrEnabled()) {

            spinnerStartLocation.setText(
                    scannedStartNode.getName(),
                    false
            );

            btnStartNavigation.setEnabled(true);

        } else {

            scannedStartNode = null;
            scannedNodeId = null;

            spinnerStartLocation.setText(
                    "",
                    false
            );

            spinnerStartLocation.setHint(
                    "Scan a valid campus QR code"
            );

            btnStartNavigation.setEnabled(false);

            Toast.makeText(
                    DashboardActivity.this,
                    "Starting location could not be identified.",
                    Toast.LENGTH_LONG
            ).show();
        }


        /*
         * =====================================================
         * DESTINATION LIST
         * =====================================================
         */

        List<String> destinationNames =
                new ArrayList<>();

        for (NavigationNode node :
                campusGraph.getNodes()) {

            if (node.isQrEnabled()
                    && node.getName() != null
                    && !node.getName().trim().isEmpty()
                    && !destinationNames.contains(
                    node.getName())) {

                destinationNames.add(
                        node.getName()
                );
            }
        }

        destinationNames.sort(
                String.CASE_INSENSITIVE_ORDER
        );

        ArrayAdapter<String> destinationAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_dropdown_item,
                        destinationNames
                );

        spinnerDestination.setAdapter(
                destinationAdapter
        );

        spinnerDestination.setThreshold(1);

        spinnerDestination.setOnClickListener(
                v -> spinnerDestination.showDropDown()
        );

        spinnerDestination.post(() ->
                spinnerDestination.setDropDownWidth(
                        spinnerDestination.getWidth()
                )
        );

        spinnerDestination.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (hasFocus) {
                        spinnerDestination.showDropDown();
                    }
                }
        );


        /*
         * =====================================================
         * ROUTE OVERLAY
         * =====================================================
         */

        routeOverlay =
                new RouteOverlayView();

        FrameLayout.LayoutParams overlayParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        mainDashboard.addView(
                routeOverlay,
                Math.min(
                        2,
                        mainDashboard.getChildCount()
                ),
                overlayParams
        );


        /*
         * =====================================================
         * START NAVIGATION
         * =====================================================
         */

        btnStartNavigation.setOnClickListener(
                v -> startNavigation()
        );

        setupPageNavigation();
    }


    /*
     * =========================================================
     * START NAVIGATION
     * =========================================================
     */

    private void startNavigation() {

        if (scannedStartNode == null) {

            Toast.makeText(
                    DashboardActivity.this,
                    "Please scan a valid campus QR code.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String destinationName =
                spinnerDestination.getText()
                        .toString()
                        .trim();


        if (destinationName.isEmpty()) {

            Toast.makeText(
                    DashboardActivity.this,
                    "Please select a destination.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * Find selected destination.
         */

        NavigationNode destinationNode =
                campusGraph.findNodeByName(
                        destinationName
                );


        if (destinationNode == null
                || !destinationNode.isQrEnabled()) {

            Toast.makeText(
                    DashboardActivity.this,
                    "Please select a destination from the suggestions.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * Prevent same start and destination.
         */

        if (scannedStartNode.getId().equals(
                destinationNode.getId())) {

            Toast.makeText(
                    DashboardActivity.this,
                    "Current location and destination cannot be the same.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * =====================================================
         * DIJKSTRA
         * =====================================================
         */

        List<NavigationNode> shortestPath =
                Dijkstra.findShortestPath(
                        campusGraph,
                        scannedStartNode.getId(),
                        destinationNode.getId()
                );


        /*
         * =====================================================
         * NO ROUTE
         * =====================================================
         */

        if (shortestPath == null
                || shortestPath.isEmpty()) {

            currentRouteNodes.clear();

            if (routeOverlay != null) {
                routeOverlay.invalidate();
            }

            Toast.makeText(
                    DashboardActivity.this,
                    "No walkable route found. Check the pathway connections.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        /*
         * =====================================================
         * STORE NAVIGATION NODES
         * =====================================================
         */

        currentRouteNodes.clear();

        for (NavigationNode node :
                shortestPath) {

            if (node != null) {
                currentRouteNodes.add(node);
            }
        }


        if (currentRouteNodes.isEmpty()) {

            if (routeOverlay != null) {
                routeOverlay.invalidate();
            }

            Toast.makeText(
                    DashboardActivity.this,
                    "Unable to display the calculated route.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * =====================================================
         * BUILD PATH INSTRUCTIONS
         * =====================================================
         */

        StringBuilder route =
                new StringBuilder();

        for (int i = 0;
             i < currentRouteNodes.size();
             i++) {

            NavigationNode node =
                    currentRouteNodes.get(i);


            if (i > 0) {

                NavigationNode previousNode =
                        currentRouteNodes.get(i - 1);


                if (previousNode.getMapPage()
                        != node.getMapPage()) {

                    route.append(
                                    "\n\nContinue on Map Page "
                            )
                            .append(
                                    node.getMapPage()
                            )
                            .append(
                                    "\n\n"
                            );

                } else {

                    route.append(
                            "\n↓\n"
                    );
                }
            }


            route.append(
                    node.getName()
            );
        }


        tvPathInstructions.setText(
                route.toString()
        );


        /*
         * =====================================================
         * DISPLAY START MAP
         * =====================================================
         */

        /*
         * Start on the page where the route BEGINS (the start node),
         * not on the destination's page. The page switcher moves
         * through the other pages of the route.
         */
        displayMapForNode(
                currentRouteNodes.get(0)
        );

        updatePageNavigation();

        if (routeOverlay != null) {
            routeOverlay.invalidate();
        }

        collapsePathInstructions();
    }


    /*
     * =========================================================
     * QR VALUE → NODE ID
     * =========================================================
     */

    private String convertQrValueToNodeId(
            String qrValue) {

        if (qrValue == null) {
            return null;
        }

        qrValue =
                qrValue.trim();

        if (qrValue.isEmpty()) {
            return null;
        }


        /*
         * CCC_NODE_001 → N001
         */

        if (qrValue.startsWith(
                "CCC_NODE_")) {

            String number =
                    qrValue.substring(
                            "CCC_NODE_".length()
                    );


            if (!number.matches("\\d+")) {
                return null;
            }


            try {

                return String.format(
                        java.util.Locale.US,
                        "N%03d",
                        Integer.parseInt(number)
                );

            } catch (NumberFormatException e) {

                return null;
            }
        }


        /*
         * Already formatted:
         *
         * N001
         */

        if (qrValue.matches("N\\d+")) {
            return qrValue;
        }


        return null;
    }


    /*
     * =========================================================
     * DRAWER
     * =========================================================
     */

    private void setupDrawer() {

        ivMenu.setOnClickListener(
                v -> {

                    if (drawerLayout.isDrawerOpen(
                            GravityCompat.START)) {

                        drawerLayout.closeDrawer(
                                GravityCompat.START
                        );

                    } else {

                        drawerLayout.openDrawer(
                                GravityCompat.START
                        );
                    }
                }
        );

        createOfficeList();
    }


    private void createOfficeList() {

        officeListContainer.removeAllViews();


        addOffice(
                "Registrar",
                "JMC 1st floor",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Maintains student academic records",
                        "Processes academic documents",
                        "Handles student record requests"
                },
                new String[]{
                        "Transcript of Records",
                        "Certificate of Enrollment",
                        "Certificate of Grades"
                }
        );


        addOffice(
                "Guidance",
                "JMC 1st floor",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Provides student guidance services",
                        "Assists students with concerns",
                        "Provides counseling support"
                },
                new String[]{
                        "Guidance Consultation"
                }
        );


        addOffice(
                "Clinic",
                "Main Building",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Provides basic health services",
                        "Assists students with health concerns"
                },
                new String[]{
                        "Basic Medical Assistance"
                }
        );


        addOffice(
                "Faculty",
                "Main Building",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Provides faculty and academic assistance"
                },
                new String[]{
                        "Faculty Consultation"
                }
        );


        addOffice(
                "Library",
                "Library Building",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Provides access to learning resources",
                        "Assists students with library services"
                },
                new String[]{
                        "Book Borrowing",
                        "Library Assistance"
                }
        );


        addOffice(
                "MISD",
                "Main Building",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Provides information system services",
                        "Assists with technology-related concerns"
                },
                new String[]{
                        "System Assistance"
                }
        );


        addOffice(
                "Cashier",
                "Main Building",
                "8:00 AM - 5:00 PM",
                "Monday - Saturday",
                new String[]{
                        "Handles school payment transactions",
                        "Processes payment-related requests"
                },
                new String[]{
                        "Payment Transactions"
                }
        );
    }


    private void addOffice(
            String officeName,
            String location,
            String officeHours,
            String schedule,
            String[] functions,
            String[] services) {

        LinearLayout officeContainer =
                new LinearLayout(this);

        officeContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        officeContainer.setBackgroundColor(
                0xFFFAF9FA
        );


        LinearLayout officeRow =
                new LinearLayout(this);

        officeRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        officeRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        officeRow.setPadding(
                12, 7, 8, 7
        );


        TextView officeIcon =
                new TextView(this);

        officeIcon.setText("▣");
        officeIcon.setTextColor(0xFF00114D);
        officeIcon.setTextSize(10);


        officeRow.addView(
                officeIcon,
                new LinearLayout.LayoutParams(
                        24,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );


        TextView officeNameText =
                new TextView(this);

        officeNameText.setText(
                officeName
        );

        officeNameText.setTextColor(
                0xFF333333
        );

        officeNameText.setTextSize(10);

        officeNameText.setGravity(
                Gravity.CENTER_VERTICAL
        );


        officeRow.addView(
                officeNameText,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        TextView arrow =
                new TextView(this);

        arrow.setText("⌄");
        arrow.setTextColor(0xFF777777);
        arrow.setTextSize(12);
        arrow.setGravity(Gravity.CENTER);


        officeRow.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        25,
                        35
                )
        );


        LinearLayout details =
                new LinearLayout(this);

        details.setOrientation(
                LinearLayout.VERTICAL
        );

        details.setVisibility(
                View.GONE
        );

        details.setPadding(
                20, 8, 12, 14
        );


        addDetailTitle(
                details,
                "Office Location"
        );

        addDetailText(
                details,
                location
        );


        addDetailTitle(
                details,
                "Office Hours"
        );

        addDetailText(
                details,
                officeHours
        );

        addDetailText(
                details,
                schedule
        );


        addDetailTitle(
                details,
                "Office Functions"
        );


        for (String function :
                functions) {

            addBulletText(
                    details,
                    function
            );
        }


        addDetailTitle(
                details,
                "Service Offered & Requirements"
        );


        for (String service :
                services) {

            addBulletText(
                    details,
                    service
            );

            addRequirementText(
                    details
            );
        }


        addDetailTitle(
                details,
                "IMPORTANT REMINDERS"
        );


        addDetailText(
                details,
                "Experimental information only. Official office information will be added after the school provides the requested data."
        );


        Button viewOnMap =
                new Button(this);

        viewOnMap.setText(
                "View on Map"
        );

        viewOnMap.setTextSize(11);

        viewOnMap.setTextColor(
                0xFFFFFFFF
        );

        viewOnMap.setAllCaps(
                false
        );

        viewOnMap.setBackgroundTintList(
                ColorStateList.valueOf(
                        0xFF00114D
                )
        );


        LinearLayout.LayoutParams mapButtonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        42
                );

        mapButtonParams.topMargin = 8;


        details.addView(
                viewOnMap,
                mapButtonParams
        );


        viewOnMap.setOnClickListener(
                v -> Toast.makeText(
                        DashboardActivity.this,
                        "View on Map will be connected to the finalized campus map and Dijkstra navigation.",
                        Toast.LENGTH_SHORT
                ).show()
        );


        officeContainer.addView(
                officeRow
        );

        officeContainer.addView(
                details
        );


        View divider =
                new View(this);

        divider.setBackgroundColor(
                0xFFDDDDDD
        );


        officeContainer.addView(
                divider,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                )
        );


        officeRow.setOnClickListener(
                v -> {

                    if (details.getVisibility()
                            == View.VISIBLE) {

                        details.setVisibility(
                                View.GONE
                        );

                        arrow.setText("⌄");

                    } else {

                        details.setVisibility(
                                View.VISIBLE
                        );

                        arrow.setText("⌃");
                    }
                }
        );


        officeListContainer.addView(
                officeContainer
        );
    }


    private void addDetailTitle(
            LinearLayout container,
            String title) {

        TextView text =
                new TextView(this);

        text.setText(title);
        text.setTextColor(0xFF00114D);
        text.setTextSize(10);
        text.setTypeface(
                null,
                Typeface.BOLD
        );

        text.setPadding(
                0, 8, 0, 3
        );

        container.addView(text);
    }


    private void addDetailText(
            LinearLayout container,
            String textValue) {

        TextView text =
                new TextView(this);

        text.setText(textValue);
        text.setTextColor(0xFF333333);
        text.setTextSize(10);

        text.setPadding(
                0, 2, 0, 2
        );

        container.addView(text);
    }


    private void addBulletText(
            LinearLayout container,
            String textValue) {

        TextView text =
                new TextView(this);

        text.setText(
                "• " + textValue
        );

        text.setTextColor(
                0xFF333333
        );

        text.setTextSize(10);

        text.setPadding(
                0, 2, 0, 2
        );

        container.addView(text);
    }


    private void addRequirementText(
            LinearLayout container) {

        TextView text =
                new TextView(this);

        text.setText(
                "   Requirements: Request Form, Valid ID"
        );

        text.setTextColor(
                0xFF555555
        );

        text.setTextSize(9);

        text.setPadding(
                0, 0, 0, 4
        );

        container.addView(text);
    }


    /*
     * =========================================================
     * PATH INSTRUCTIONS PANEL
     * =========================================================
     */

    private void setupPathInstructionsPanel() {

        pathInstructionsPanel.post(() -> {

            collapsedHeight =
                    pathInstructionsPanel.getHeight();

            int screenHeight =
                    pathInstructionsPanel
                            .getRootView()
                            .getHeight();

            expandedHeight =
                    (int) (
                            screenHeight * 0.70f
                    );

            if (expandedHeight < collapsedHeight) {
                expandedHeight =
                        collapsedHeight;
            }
        });


        pathInstructionsPanel.setOnTouchListener(
                (view, event) -> {

                    switch (
                            event.getActionMasked()
                    ) {

                        case MotionEvent.ACTION_DOWN:

                            panelStartY =
                                    event.getRawY();

                            panelDragging = true;

                            return true;


                        case MotionEvent.ACTION_MOVE:

                            return true;


                        case MotionEvent.ACTION_UP:

                            if (panelDragging) {

                                float difference =
                                        event.getRawY()
                                                - panelStartY;

                                if (difference < -80) {

                                    expandPathInstructions();

                                } else if (difference > 80) {

                                    collapsePathInstructions();
                                }
                            }

                            panelDragging = false;

                            view.performClick();

                            return true;


                        case MotionEvent.ACTION_CANCEL:

                            panelDragging = false;

                            return true;
                    }

                    return true;
                }
        );


        pathDragHandle.setOnTouchListener(
                (view, event) -> {

                    switch (
                            event.getActionMasked()
                    ) {

                        case MotionEvent.ACTION_DOWN:

                            panelStartY =
                                    event.getRawY();

                            panelDragging = true;

                            return true;


                        case MotionEvent.ACTION_MOVE:

                            return true;


                        case MotionEvent.ACTION_UP:

                            if (panelDragging) {

                                float difference =
                                        event.getRawY()
                                                - panelStartY;

                                if (difference < -80) {

                                    expandPathInstructions();

                                } else if (difference > 80) {

                                    collapsePathInstructions();
                                }
                            }

                            panelDragging = false;

                            view.performClick();

                            return true;


                        case MotionEvent.ACTION_CANCEL:

                            panelDragging = false;

                            return true;
                    }

                    return true;
                }
        );
    }


    private void expandPathInstructions() {

        if (expandedHeight <= 0) {
            return;
        }

        pathInstructionsPanel
                .getLayoutParams()
                .height =
                expandedHeight;

        pathInstructionsPanel.requestLayout();
    }


    private void collapsePathInstructions() {

        if (collapsedHeight <= 0) {
            return;
        }

        pathInstructionsPanel
                .getLayoutParams()
                .height =
                collapsedHeight;

        pathInstructionsPanel.requestLayout();
    }


    /*
     * =========================================================
     * MAP TOUCH / PAN
     * =========================================================
     */

    private void setupMapTouch() {

        mapMatrix =
                new Matrix();

        campusMap.setScaleType(
                ImageView.ScaleType.MATRIX
        );

        campusMap.setImageMatrix(
                mapMatrix
        );


        scaleGestureDetector =
                new ScaleGestureDetector(
                        this,
                        new ScaleListener()
                );


        campusMap.setOnTouchListener(
                (view, event) -> {

                    scaleGestureDetector
                            .onTouchEvent(event);


                    switch (
                            event.getActionMasked()
                    ) {

                        case MotionEvent.ACTION_DOWN:

                            lastX =
                                    event.getX();

                            lastY =
                                    event.getY();

                            touchStartX =
                                    event.getX();

                            touchStartY =
                                    event.getY();

                            touchStartTime =
                                    System.currentTimeMillis();

                            isDragging = true;

                            return true;


                        case MotionEvent.ACTION_POINTER_DOWN:

                            isDragging = false;

                            return true;


                        case MotionEvent.ACTION_MOVE:

                            if (!scaleGestureDetector
                                    .isInProgress()
                                    && event.getPointerCount() == 1
                                    && isDragging) {

                                float dx =
                                        event.getX()
                                                - lastX;

                                float dy =
                                        event.getY()
                                                - lastY;


                                mapMatrix.postTranslate(
                                        dx,
                                        dy
                                );


                                constrainMapToBounds();


                                campusMap.setImageMatrix(
                                        mapMatrix
                                );


                                if (routeOverlay != null) {
                                    routeOverlay.invalidate();
                                }


                                lastX =
                                        event.getX();

                                lastY =
                                        event.getY();
                            }

                            return true;


                        case MotionEvent.ACTION_POINTER_UP:

                            isDragging = false;

                            return true;


                        case MotionEvent.ACTION_UP:

                            isDragging = false;


                            float moveX =
                                    Math.abs(
                                            event.getX()
                                                    - touchStartX
                                    );

                            float moveY =
                                    Math.abs(
                                            event.getY()
                                                    - touchStartY
                                    );


                            long touchDuration =
                                    System.currentTimeMillis()
                                            - touchStartTime;


                            if (moveX < 20
                                    && moveY < 20
                                    && touchDuration < 300) {

                                toggleMapControls();
                            }


                            view.performClick();

                            return true;


                        case MotionEvent.ACTION_CANCEL:

                            isDragging = false;

                            return true;
                    }

                    return true;
                }
        );
    }


    private void toggleMapControls() {

        View navigationControls =
                findViewById(
                        R.id.navigationControls
                );

        View pathInstructions =
                findViewById(
                        R.id.pathInstructionsPanel
                );


        if (mapControlsVisible) {

            navigationControls.setVisibility(
                    View.GONE
            );

            pathInstructions.setVisibility(
                    View.GONE
            );

            mapControlsVisible = false;

        } else {

            navigationControls.setVisibility(
                    View.VISIBLE
            );

            pathInstructions.setVisibility(
                    View.VISIBLE
            );

            mapControlsVisible = true;
        }
    }


    /*
     * =========================================================
     * FIT MAP
     * =========================================================
     */

    private void fitMapToDashboard() {

        Drawable drawable =
                campusMap.getDrawable();

        if (drawable == null) {
            return;
        }


        int viewWidth =
                campusMap.getWidth();

        int viewHeight =
                campusMap.getHeight();


        if (viewWidth <= 0
                || viewHeight <= 0) {

            return;
        }


        float imageWidth =
                drawable.getIntrinsicWidth();

        float imageHeight =
                drawable.getIntrinsicHeight();


        if (imageWidth <= 0
                || imageHeight <= 0) {

            return;
        }


        /*
         * IMPORTANT:
         *
         * The Matrix works in the drawable's
         * intrinsic coordinate system.
         */

        baseScale =
                Math.max(
                        viewWidth / imageWidth,
                        viewHeight / imageHeight
                );


        currentScale =
                baseScale;


        mapMatrix.reset();


        mapMatrix.setScale(
                baseScale,
                baseScale
        );


        float scaledWidth =
                imageWidth * baseScale;

        float scaledHeight =
                imageHeight * baseScale;


        float dx =
                (viewWidth - scaledWidth) / 2f;

        float dy =
                (viewHeight - scaledHeight) / 2f;


        mapMatrix.postTranslate(
                dx,
                dy
        );


        constrainMapToBounds();


        campusMap.setImageMatrix(
                mapMatrix
        );


        if (routeOverlay != null) {
            routeOverlay.invalidate();
        }
    }


    /*
     * =========================================================
     * MAP BOUNDARIES
     * =========================================================
     */

    private void constrainMapToBounds() {

        Drawable drawable =
                campusMap.getDrawable();

        if (drawable == null) {
            return;
        }


        int viewWidth =
                campusMap.getWidth();

        int viewHeight =
                campusMap.getHeight();


        if (viewWidth <= 0
                || viewHeight <= 0) {

            return;
        }


        float imageWidth =
                drawable.getIntrinsicWidth();

        float imageHeight =
                drawable.getIntrinsicHeight();


        RectF imageRect =
                new RectF(
                        0,
                        0,
                        imageWidth,
                        imageHeight
                );


        mapMatrix.mapRect(
                imageRect
        );


        float dx = 0;
        float dy = 0;


        if (imageRect.width()
                <= viewWidth) {

            dx =
                    (viewWidth / 2f)
                            - imageRect.centerX();

        } else {

            if (imageRect.left > 0) {
                dx =
                        -imageRect.left;
            }

            if (imageRect.right < viewWidth) {
                dx =
                        viewWidth
                                - imageRect.right;
            }
        }


        if (imageRect.height()
                <= viewHeight) {

            dy =
                    (viewHeight / 2f)
                            - imageRect.centerY();

        } else {

            if (imageRect.top > 0) {
                dy =
                        -imageRect.top;
            }

            if (imageRect.bottom < viewHeight) {
                dy =
                        viewHeight
                                - imageRect.bottom;
            }
        }


        mapMatrix.postTranslate(
                dx,
                dy
        );
    }


    /*
     * =========================================================
     * PINCH ZOOM
     * =========================================================
     */

    private class ScaleListener
            extends ScaleGestureDetector
            .SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(
                ScaleGestureDetector detector) {

            float scaleFactor =
                    detector.getScaleFactor();


            float newScale =
                    currentScale
                            * scaleFactor;


            float minScale =
                    baseScale;


            float maxScale =
                    baseScale
                            * maxZoomMultiplier;


            if (newScale < minScale) {

                scaleFactor =
                        minScale / currentScale;

                newScale =
                        minScale;
            }


            if (newScale > maxScale) {

                scaleFactor =
                        maxScale / currentScale;

                newScale =
                        maxScale;
            }


            mapMatrix.postScale(
                    scaleFactor,
                    scaleFactor,
                    detector.getFocusX(),
                    detector.getFocusY()
            );


            currentScale =
                    newScale;


            constrainMapToBounds();


            campusMap.setImageMatrix(
                    mapMatrix
            );


            if (routeOverlay != null) {
                routeOverlay.invalidate();
            }


            return true;
        }
    }


    /*
     * =========================================================
     * DISPLAY CORRECT MAP PAGE
     * =========================================================
     */

    private void displayMapForNode(
            NavigationNode node) {

        if (node == null) {
            return;
        }

        displayMapPage(node.getMapPage());
    }


    private void displayMapPage(
            int mapPage) {

        currentMapPage = mapPage;


        switch (currentMapPage) {

            case 1:

                campusMap.setImageResource(
                        R.drawable.map_page_1
                );

                break;


            case 2:

                campusMap.setImageResource(
                        R.drawable.map_page_2
                );

                break;


            case 3:

                campusMap.setImageResource(
                        R.drawable.map_page_3
                );

                break;


            case 4:

                campusMap.setImageResource(
                        R.drawable.map_page_4
                );

                break;


            default:

                currentMapPage = 1;

                campusMap.setImageResource(
                        R.drawable.map_page_1
                );

                break;
        }


        campusMap.post(() -> {

            fitMapToDashboard();

            if (routeOverlay != null) {
                routeOverlay.invalidate();
            }
        });
    }


    /*
     * =========================================================
     * PAGE SWITCHER (route that spans several map pages)
     * =========================================================
     */
    private void setupPageNavigation() {

        float d = getResources().getDisplayMetrics().density;

        pageNavBar = new LinearLayout(this);
        pageNavBar.setOrientation(LinearLayout.HORIZONTAL);
        pageNavBar.setGravity(Gravity.CENTER_VERTICAL);
        pageNavBar.setPadding(
                (int) (8 * d), (int) (4 * d),
                (int) (8 * d), (int) (4 * d)
        );
        pageNavBar.setBackgroundColor(Color.parseColor("#E6FFFFFF"));
        pageNavBar.setVisibility(View.GONE);

        btnPrevPage = new Button(this);
        btnPrevPage.setText("\u25C0 Back");
        btnPrevPage.setAllCaps(false);
        btnPrevPage.setOnClickListener(v -> switchRoutePage(-1));

        tvPageLabel = new TextView(this);
        tvPageLabel.setTextColor(Color.parseColor("#00114D"));
        tvPageLabel.setTypeface(Typeface.DEFAULT_BOLD);
        tvPageLabel.setGravity(Gravity.CENTER);
        tvPageLabel.setPadding(
                (int) (12 * d), 0, (int) (12 * d), 0
        );

        btnNextPage = new Button(this);
        btnNextPage.setText("Next \u25B6");
        btnNextPage.setAllCaps(false);
        btnNextPage.setOnClickListener(v -> switchRoutePage(1));

        for (Button b : new Button[]{btnPrevPage, btnNextPage}) {
            b.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#00114D"))
            );
            b.setTextColor(Color.WHITE);
            b.setMinHeight(0);
            b.setMinimumHeight(0);
            b.setMinWidth(0);
            b.setMinimumWidth(0);
            b.setPadding(
                    (int) (12 * d), (int) (6 * d),
                    (int) (12 * d), (int) (6 * d)
            );
        }

        pageNavBar.addView(btnPrevPage);
        pageNavBar.addView(tvPageLabel);
        pageNavBar.addView(btnNextPage);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        /* just below the Start Navigation button */
        params.topMargin = (int) (258 * d);

        mainDashboard.addView(pageNavBar, params);
    }


    /* Map pages the current route passes through, in order. */
    private List<Integer> getRoutePages() {

        List<Integer> pages = new ArrayList<>();

        for (NavigationNode node : currentRouteNodes) {

            int page = node.getMapPage();

            if (pages.isEmpty()
                    || pages.get(pages.size() - 1) != page) {

                pages.add(page);
            }
        }

        return pages;
    }


    private void updatePageNavigation() {

        if (pageNavBar == null) {
            return;
        }

        List<Integer> pages = getRoutePages();

        if (pages.size() <= 1) {
            pageNavBar.setVisibility(View.GONE);
            return;
        }

        int index = pages.indexOf(currentMapPage);

        if (index < 0) {
            index = 0;
        }

        tvPageLabel.setText(
                "Map " + currentMapPage
                        + "  (" + (index + 1)
                        + "/" + pages.size() + ")"
        );

        btnPrevPage.setEnabled(index > 0);
        btnNextPage.setEnabled(index < pages.size() - 1);

        pageNavBar.setVisibility(View.VISIBLE);
        pageNavBar.bringToFront();
    }


    private void switchRoutePage(int direction) {

        List<Integer> pages = getRoutePages();

        int index = pages.indexOf(currentMapPage);

        int target = index + direction;

        if (index < 0
                || target < 0
                || target >= pages.size()) {
            return;
        }

        displayMapPage(pages.get(target));

        updatePageNavigation();

        if (routeOverlay != null) {
            routeOverlay.invalidate();
        }
    }


    private void displayMapForDestination(
            String destinationName) {

        NavigationNode destinationNode =
                campusGraph.findNodeByName(
                        destinationName
                );


        if (destinationNode != null) {

            displayMapForNode(
                    destinationNode
            );
        }
    }


    /*
     * =========================================================
     * ROUTE OVERLAY
     * =========================================================
     */

    private class RouteOverlayView
            extends View {

        private final Paint routePaint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        private final Paint markerPaint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        private final Paint startOuterPaint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        private final float density;

        /*
         * Walkability grid is built ONCE per map bitmap and reused
         * (it used to be rebuilt for every route piece on every
         * redraw, which made panning / zooming slow).
         */
        private Bitmap cachedGridBitmap;
        private boolean[][] cachedOpenMap;


        RouteOverlayView() {

            super(
                    DashboardActivity.this
            );


            density =
                    getResources()
                            .getDisplayMetrics()
                            .density;


            setBackgroundColor(
                    Color.TRANSPARENT
            );

            setClickable(false);
            setFocusable(false);


            /*
             * Route line.
             */

            routePaint.setColor(
                    Color.rgb(
                            220,
                            45,
                            45
                    )
            );

            routePaint.setStyle(
                    Paint.Style.STROKE
            );

            routePaint.setStrokeWidth(
                    5f * density
            );

            routePaint.setStrokeCap(
                    Paint.Cap.ROUND
            );

            routePaint.setStrokeJoin(
                    Paint.Join.ROUND
            );


            markerPaint.setStyle(
                    Paint.Style.FILL
            );


            startOuterPaint.setStyle(
                    Paint.Style.FILL
            );

            startOuterPaint.setColor(
                    Color.WHITE
            );
        }


        @Override
        protected void onDraw(
                Canvas canvas) {

            super.onDraw(canvas);


            if (mapMatrix == null
                    || campusMap.getDrawable() == null
                    || currentRouteNodes.isEmpty()) {

                return;
            }


            Drawable drawable =
                    campusMap.getDrawable();


            if (!(drawable
                    instanceof BitmapDrawable)) {

                return;
            }


            Bitmap bitmap =
                    ((BitmapDrawable) drawable)
                            .getBitmap();


            int bitmapW =
                    bitmap.getWidth();

            int bitmapH =
                    bitmap.getHeight();


            if (bitmapW <= 0
                    || bitmapH <= 0) {

                return;
            }


            /*
             * =================================================
             * IMPORTANT COORDINATE FIX
             * =================================================
             *
             * campusMap's Matrix operates using the
             * drawable's intrinsic dimensions.
             *
             * The bitmap used by A* uses bitmap dimensions.
             *
             * Therefore:
             *
             * normalized node
             *       ↓
             * bitmap coordinate
             *       ↓
             * intrinsic drawable coordinate
             *       ↓
             * mapMatrix
             *       ↓
             * screen
             *
             * This prevents the route from being offset.
             */

            float intrinsicW =
                    drawable.getIntrinsicWidth();

            float intrinsicH =
                    drawable.getIntrinsicHeight();


            if (intrinsicW <= 0
                    || intrinsicH <= 0) {

                return;
            }


            /*
             * Find the position of the ImageView
             * relative to the overlay.
             */

            int[] mapLoc =
                    new int[2];

            int[] overlayLoc =
                    new int[2];


            campusMap.getLocationOnScreen(
                    mapLoc
            );

            routeOverlay.getLocationOnScreen(
                    overlayLoc
            );


            float ox =
                    mapLoc[0]
                            - overlayLoc[0];

            float oy =
                    mapLoc[1]
                            - overlayLoc[1];


            /*
             * =================================================
             * DRAW ROUTE
             * =================================================
             */

            /*
             * The destination pin sits OUTSIDE the room, on its door,
             * so the route line stops there instead of going inside.
             */
            final int pinIndex = getDestinationPinIndex();

            for (int i = 0;
                 i < pinIndex;
                 i++) {

                NavigationNode from =
                        currentRouteNodes.get(i);

                NavigationNode to =
                        currentRouteNodes.get(i + 1);


                /*
                 * Do not connect two points from
                 * different map pages.
                 */

                if (from.getMapPage()
                        != currentMapPage
                        || to.getMapPage()
                        != currentMapPage) {

                    continue;
                }


                /*
                 * Convert normalized node coordinates
                 * to BITMAP coordinates.
                 */

                float startBitmapX =
                        from.getX()
                                * bitmapW;

                float startBitmapY =
                        from.getY()
                                * bitmapH;


                float endBitmapX =
                        to.getX()
                                * bitmapW;

                float endBitmapY =
                        to.getY()
                                * bitmapH;


                /*
                 * If the straight line between these two
                 * navigation points stays on gray walkable
                 * pixels, draw it directly.
                 *
                 * Navigation points sit on the CENTRE of the
                 * corridor, so the route stays centred and
                 * follows thin corridors / strips (for example
                 * the narrow walkway under AB4) exactly.
                 *
                 * The pixel search (A*) hugs the walls and can
                 * lose thin walkways, so it is only used when the
                 * straight line is blocked.
                 */

                if (isClearStraightLine(
                        bitmap,
                        startBitmapX,
                        startBitmapY,
                        endBitmapX,
                        endBitmapY)) {

                    drawSegmentFromBitmap(
                            canvas,
                            ox,
                            oy,
                            startBitmapX,
                            startBitmapY,
                            endBitmapX,
                            endBitmapY,
                            bitmapW,
                            bitmapH,
                            intrinsicW,
                            intrinsicH
                    );

                    continue;
                }


                /*
                 * Snap only for the walkable-path
                 * calculation.
                 */

                float[] walkableStart =
                        snapToWalkable(
                                bitmap,
                                startBitmapX,
                                startBitmapY
                        );


                float[] walkableEnd =
                        snapToWalkable(
                                bitmap,
                                endBitmapX,
                                endBitmapY
                        );


                /*
                 * Find A* path through actual gray
                 * walkable pixels.
                 */

                List<float[]> bitmapPath =
                        findWalkablePath(
                                bitmap,
                                walkableStart,
                                walkableEnd
                        );


                /*
                 * If A* cannot find a pixel route,
                 * use a straight line between the
                 * actual navigation nodes.
                 */

                if (bitmapPath.size() < 2) {

                    /*
                     * Between two WAYPOINTS (corridor points, doors,
                     * stairs, portal) the node coordinates were placed
                     * by hand on the path, so when the pixel search
                     * cannot connect them (for example across the
                     * lobby doors or the stairs) join the two nodes
                     * with a straight line.
                     */
                    if (isWaypointNode(from)
                            && isWaypointNode(to)) {

                        drawSegmentFromBitmap(
                                canvas,
                                ox,
                                oy,
                                startBitmapX,
                                startBitmapY,
                                endBitmapX,
                                endBitmapY,
                                bitmapW,
                                bitmapH,
                                intrinsicW,
                                intrinsicH
                        );

                        continue;
                    }


                    /*
                     * Anything involving a ROOM is never drawn
                     * straight between the node coordinates (it could
                     * cut across rooms and walls). Only join the two
                     * snapped WALKABLE points when they are close
                     * (a doorway / small gap).
                     */
                    if (walkableStart != null
                            && walkableEnd != null
                            && Math.hypot(
                            walkableStart[0] - walkableEnd[0],
                            walkableStart[1] - walkableEnd[1]
                    ) <= 50f
                            && Math.hypot(
                            walkableStart[0] - walkableEnd[0],
                            walkableStart[1] - walkableEnd[1]
                    ) > 0.5f) {

                        drawSegmentFromBitmap(
                                canvas,
                                ox,
                                oy,
                                walkableStart[0],
                                walkableStart[1],
                                walkableEnd[0],
                                walkableEnd[1],
                                bitmapW,
                                bitmapH,
                                intrinsicW,
                                intrinsicH
                        );
                    }

                    continue;
                }


                Path pathObject =
                        new Path();


                for (int j = 0;
                     j < bitmapPath.size();
                     j++) {

                    float[] point =
                            bitmapPath.get(j);


                    /*
                     * Convert bitmap coordinates
                     * to drawable intrinsic coordinates.
                     */

                    float intrinsicX =
                            point[0]
                                    * intrinsicW
                                    / bitmapW;


                    float intrinsicY =
                            point[1]
                                    * intrinsicH
                                    / bitmapH;


                    float[] screenPoint =
                            {
                                    intrinsicX,
                                    intrinsicY
                            };


                    /*
                     * Apply EXACTLY the same Matrix
                     * as the campusMap.
                     */

                    mapMatrix.mapPoints(
                            screenPoint
                    );


                    screenPoint[0] += ox;
                    screenPoint[1] += oy;


                    if (j == 0) {

                        pathObject.moveTo(
                                screenPoint[0],
                                screenPoint[1]
                        );

                    } else {

                        pathObject.lineTo(
                                screenPoint[0],
                                screenPoint[1]
                        );
                    }
                }


                canvas.drawPath(
                        pathObject,
                        routePaint
                );
            }


            /*
             * =================================================
             * DRAW START + DESTINATION MARKERS
             * =================================================
             */

            for (int i = 0;
                 i < currentRouteNodes.size();
                 i++) {

                NavigationNode node =
                        currentRouteNodes.get(i);


                if (node.getMapPage()
                        != currentMapPage) {

                    continue;
                }


                boolean start =
                        i == 0;

                boolean destination =
                        i == pinIndex;

                /*
                 * Orange marker where the route leaves this page
                 * or arrives on it (stairs / next map).
                 */
                boolean transition =
                        (i + 1 < currentRouteNodes.size()
                                && currentRouteNodes.get(i + 1)
                                .getMapPage()
                                != node.getMapPage())
                                || (i > 0
                                && currentRouteNodes.get(i - 1)
                                .getMapPage()
                                != node.getMapPage());

                if (!start && !destination && !transition) {
                    continue;
                }


                /*
                 * IMPORTANT:
                 *
                 * Markers use the ORIGINAL node
                 * coordinates.
                 *
                 * They are NOT snapped.
                 *
                 * Therefore the green marker is
                 * exactly where the Admin Building
                 * Entrance node was defined.
                 */

                float bitmapX =
                        node.getX()
                                * bitmapW;

                float bitmapY =
                        node.getY()
                                * bitmapH;


                float intrinsicX =
                        bitmapX
                                * intrinsicW
                                / bitmapW;


                float intrinsicY =
                        bitmapY
                                * intrinsicH
                                / bitmapH;


                float[] point =
                        {
                                intrinsicX,
                                intrinsicY
                        };


                mapMatrix.mapPoints(
                        point
                );


                point[0] += ox;
                point[1] += oy;


                /*
                 * White outline.
                 */

                canvas.drawCircle(
                        point[0],
                        point[1],
                        11f * density,
                        startOuterPaint
                );


                /*
                 * Green start marker.
                 *
                 * Red destination marker.
                 */

                markerPaint.setColor(
                        start
                                ? Color.rgb(30, 160, 75)
                                : destination
                                  ? Color.rgb(220, 45, 45)
                                  : Color.rgb(245, 140, 20)
                );


                canvas.drawCircle(
                        point[0],
                        point[1],
                        8f * density,
                        markerPaint
                );
            }
        }


        /*
         * =====================================================
         * WAYPOINT NODE (not a room)
         * =====================================================
         */

        private boolean isWaypointNode(
                NavigationNode node) {

            String name =
                    node.getName();

            if (name == null) {
                return false;
            }

            String lower =
                    name.toLowerCase(java.util.Locale.US);

            return lower.equals("corridor")
                    || lower.equals("underground corridor")
                    || lower.equals("door")
                    || lower.equals("portal")
                    || lower.contains("stair");
        }


        /*
         * =====================================================
         * IS THE STRAIGHT LINE FULLY ON WALKABLE PIXELS?
         * =====================================================
         *
         * Samples about once per pixel of the original
         * 1920x1080 map. The first and last few pixels are
         * ignored because doors and stairs sit on the edge of
         * the walkway, not on it.
         */

        private boolean isClearStraightLine(
                Bitmap bitmap,
                float x1,
                float y1,
                float x2,
                float y2) {

            /*
             * Works for any bitmap size: 1.0 when the bitmap is
             * 1920 px wide, larger if Android scaled it up.
             */
            float scale =
                    bitmap.getWidth() / 1920f;

            float length =
                    (float) Math.hypot(
                            x2 - x1,
                            y2 - y1
                    );

            float ignore =
                    3f * scale;

            int samples =
                    Math.max(
                            1,
                            (int) (length / scale)
                    );

            for (int i = 0;
                 i <= samples;
                 i++) {

                float t =
                        i / (float) samples;

                float x =
                        x1 + (x2 - x1) * t;

                float y =
                        y1 + (y2 - y1) * t;

                if (Math.hypot(x - x1, y - y1) < ignore
                        || Math.hypot(x - x2, y - y2) < ignore) {

                    continue;
                }

                int px =
                        Math.max(
                                0,
                                Math.min(
                                        bitmap.getWidth() - 1,
                                        Math.round(x)
                                )
                        );

                int py =
                        Math.max(
                                0,
                                Math.min(
                                        bitmap.getHeight() - 1,
                                        Math.round(y)
                                )
                        );

                if (!isWalkable(
                        bitmap.getPixel(
                                px,
                                py
                        ))) {

                    return false;
                }
            }

            return true;
        }


        /*
         * =====================================================
         * DRAW SEGMENT
         * =====================================================
         */

        private void drawSegmentFromBitmap(
                Canvas canvas,
                float ox,
                float oy,
                float x1,
                float y1,
                float x2,
                float y2,
                float bitmapW,
                float bitmapH,
                float intrinsicW,
                float intrinsicH) {

            float[] points =
                    {
                            x1 * intrinsicW / bitmapW,
                            y1 * intrinsicH / bitmapH,

                            x2 * intrinsicW / bitmapW,
                            y2 * intrinsicH / bitmapH
                    };


            mapMatrix.mapPoints(
                    points
            );


            canvas.drawLine(
                    points[0] + ox,
                    points[1] + oy,
                    points[2] + ox,
                    points[3] + oy,
                    routePaint
            );
        }


        /*
         * =====================================================
         * GRAY WALKABLE PIXEL TEST
         * =====================================================
         */

        private boolean isWalkable(
                int color) {

            int r =
                    Color.red(color);

            int g =
                    Color.green(color);

            int b =
                    Color.blue(color);


            /*
             * Current campus map walkway:
             *
             * dark gray
             *
             * R/G/B approximately equal.
             */

            return r >= 70
                    && r <= 105
                    && Math.abs(r - g) <= 6
                    && Math.abs(g - b) <= 6;
        }


        /*
         * =====================================================
         * SNAP NODE TO NEAREST WALKABLE PIXEL
         * =====================================================
         */

        private float[] snapToWalkable(
                Bitmap bitmap,
                float x,
                float y) {

            final int step = 4;


            int gw =
                    Math.max(
                            1,
                            bitmap.getWidth()
                                    / step
                    );

            int gh =
                    Math.max(
                            1,
                            bitmap.getHeight()
                                    / step
                    );


            int tx =
                    Math.max(
                            0,
                            Math.min(
                                    gw - 1,
                                    Math.round(
                                            x / step
                                    )
                            )
                    );


            int ty =
                    Math.max(
                            0,
                            Math.min(
                                    gh - 1,
                                    Math.round(
                                            y / step
                                    )
                            )
                    );


            int bestX = -1;
            int bestY = -1;
            int best = Integer.MAX_VALUE;


            /*
             * Search outward for the nearest
             * gray walkable pixel.
             */

            for (int radius = 0;
                 radius <= 60;
                 radius++) {

                int minX =
                        Math.max(
                                0,
                                tx - radius
                        );

                int maxX =
                        Math.min(
                                gw - 1,
                                tx + radius
                        );

                int minY =
                        Math.max(
                                0,
                                ty - radius
                        );

                int maxY =
                        Math.min(
                                gh - 1,
                                ty + radius
                        );


                for (int gy = minY;
                     gy <= maxY;
                     gy++) {

                    for (int gx = minX;
                         gx <= maxX;
                         gx++) {

                        int px =
                                Math.min(
                                        bitmap.getWidth() - 1,
                                        gx * step
                                                + step / 2
                                );


                        int py =
                                Math.min(
                                        bitmap.getHeight() - 1,
                                        gy * step
                                                + step / 2
                                );


                        if (!isWalkable(
                                bitmap.getPixel(
                                        px,
                                        py
                                ))) {

                            continue;
                        }


                        int distance =
                                Math.abs(
                                        gx - tx
                                )
                                        +
                                        Math.abs(
                                                gy - ty
                                        );


                        if (distance < best) {

                            best =
                                    distance;

                            bestX =
                                    gx;

                            bestY =
                                    gy;
                        }
                    }
                }


                if (bestX >= 0) {
                    break;
                }
            }


            if (bestX < 0) {

                return new float[]{
                        x,
                        y
                };
            }


            return new float[]{
                    bestX * step
                            + step / 2f,

                    bestY * step
                            + step / 2f
            };
        }


        /*
         * =====================================================
         * WALKABILITY GRID (built once per bitmap)
         * =====================================================
         */

        private boolean[][] getOpenMap(
                Bitmap bitmap,
                int step,
                int gw,
                int gh) {

            if (bitmap == cachedGridBitmap
                    && cachedOpenMap != null
                    && cachedOpenMap.length == gh
                    && cachedOpenMap[0].length == gw) {

                return cachedOpenMap;
            }


            boolean[][] openMap =
                    new boolean[gh][gw];


            for (int y = 0;
                 y < gh;
                 y++) {

                int py =
                        Math.min(
                                bitmap.getHeight() - 1,
                                y * step + 2
                        );


                for (int x = 0;
                     x < gw;
                     x++) {

                    int px =
                            Math.min(
                                    bitmap.getWidth() - 1,
                                    x * step + 2
                            );


                    openMap[y][x] =
                            isWalkable(
                                    bitmap.getPixel(
                                            px,
                                            py
                                    )
                            );
                }
            }


            cachedGridBitmap = bitmap;
            cachedOpenMap = openMap;

            return openMap;
        }


        /*
         * =====================================================
         * FIND ROUTE THROUGH GRAY WALKABLE PIXELS
         * =====================================================
         */

        private List<float[]> findWalkablePath(
                Bitmap bitmap,
                float[] start,
                float[] end) {

            List<float[]> empty =
                    new ArrayList<>();


            if (bitmap == null) {
                return empty;
            }


            final int step = 4;


            final int gw =
                    Math.max(
                            1,
                            bitmap.getWidth()
                                    / step
                    );

            final int gh =
                    Math.max(
                            1,
                            bitmap.getHeight()
                                    / step
                    );


            /*
             * Walkability grid (cached per bitmap).
             */

            boolean[][] openMap =
                    getOpenMap(
                            bitmap,
                            step,
                            gw,
                            gh
                    );


            int sx =
                    nearestX(
                            start[0],
                            gw,
                            step
                    );

            int sy =
                    nearestY(
                            start[1],
                            gh,
                            step
                    );

            int ex =
                    nearestX(
                            end[0],
                            gw,
                            step
                    );

            int ey =
                    nearestY(
                            end[1],
                            gh,
                            step
                    );


            /*
             * The snapping operation normally guarantees
             * these are walkable, but check again.
             */

            if (!openMap[sy][sx]
                    || !openMap[ey][ex]) {

                return empty;
            }


            float[][] g =
                    new float[gh][gw];


            for (int y = 0;
                 y < gh;
                 y++) {

                java.util.Arrays.fill(
                        g[y],
                        Float.POSITIVE_INFINITY
                );
            }


            int[][] parentX =
                    new int[gh][gw];

            int[][] parentY =
                    new int[gh][gw];


            for (int y = 0;
                 y < gh;
                 y++) {

                java.util.Arrays.fill(
                        parentX[y],
                        -1
                );

                java.util.Arrays.fill(
                        parentY[y],
                        -1
                );
            }


            java.util.PriorityQueue<GridPoint>
                    queue =
                    new java.util.PriorityQueue<>(
                            (a, b) ->
                                    Float.compare(
                                            a.f,
                                            b.f
                                    )
                    );


            g[sy][sx] = 0;


            queue.add(
                    new GridPoint(
                            sx,
                            sy,
                            heuristic(
                                    sx,
                                    sy,
                                    ex,
                                    ey
                            )
                    )
            );


            int[] dx =
                    {
                            1,
                            -1,
                            0,
                            0
                    };


            int[] dy =
                    {
                            0,
                            0,
                            1,
                            -1
                    };


            boolean found = false;


            while (!queue.isEmpty()) {

                GridPoint current =
                        queue.poll();


                if (current.x == ex
                        && current.y == ey) {

                    found = true;

                    break;
                }


                for (int i = 0;
                     i < 4;
                     i++) {

                    int nx =
                            current.x + dx[i];

                    int ny =
                            current.y + dy[i];


                    if (nx < 0
                            || nx >= gw
                            || ny < 0
                            || ny >= gh
                            || !openMap[ny][nx]) {

                        continue;
                    }


                    float newG =
                            g[current.y][current.x]
                                    + 1f;


                    if (newG < g[ny][nx]) {

                        g[ny][nx] =
                                newG;


                        parentX[ny][nx] =
                                current.x;

                        parentY[ny][nx] =
                                current.y;


                        queue.add(
                                new GridPoint(
                                        nx,
                                        ny,
                                        newG
                                                + heuristic(
                                                nx,
                                                ny,
                                                ex,
                                                ey
                                        )
                                )
                        );
                    }
                }
            }


            if (!found) {
                return empty;
            }


            /*
             * =================================================
             * RECONSTRUCT PATH
             * =================================================
             */

            ArrayList<float[]> result =
                    new ArrayList<>();


            int x = ex;
            int y = ey;


            while (!(x == sx
                    && y == sy)) {

                result.add(
                        new float[]{
                                x * step + 2f,
                                y * step + 2f
                        }
                );


                int px =
                        parentX[y][x];

                int py =
                        parentY[y][x];


                if (px < 0
                        || py < 0) {

                    return empty;
                }


                x = px;
                y = py;
            }


            result.add(
                    new float[]{
                            sx * step + 2f,
                            sy * step + 2f
                    }
            );


            java.util.Collections.reverse(
                    result
            );


            /*
             * =================================================
             * REMOVE UNNECESSARY POINTS
             * =================================================
             */

            ArrayList<float[]> corners =
                    new ArrayList<>();


            for (int i = 0;
                 i < result.size();
                 i++) {

                if (i == 0
                        || i == result.size() - 1) {

                    corners.add(
                            result.get(i)
                    );

                    continue;
                }


                float[] a =
                        result.get(i - 1);

                float[] b =
                        result.get(i);

                float[] c =
                        result.get(i + 1);


                boolean directionChanged =
                        Math.signum(
                                b[0] - a[0]
                        )
                                !=
                                Math.signum(
                                        c[0] - b[0]
                                )
                                ||
                                Math.signum(
                                        b[1] - a[1]
                                )
                                        !=
                                        Math.signum(
                                                c[1] - b[1]
                                        );


                if (directionChanged) {

                    corners.add(
                            b
                    );
                }
            }


            return corners;
        }


        private int nearestX(
                float value,
                int max,
                int step) {

            return Math.max(
                    0,
                    Math.min(
                            max - 1,
                            (int) Math.floor(
                                    value / step
                            )
                    )
            );
        }


        private int nearestY(
                float value,
                int max,
                int step) {

            return Math.max(
                    0,
                    Math.min(
                            max - 1,
                            (int) Math.floor(
                                    value / step
                            )
                    )
            );
        }


        private float heuristic(
                int x1,
                int y1,
                int x2,
                int y2) {

            return Math.abs(
                    x1 - x2
            )
                    +
                    Math.abs(
                            y1 - y2
                    );
        }


        private class GridPoint {

            final int x;
            final int y;
            final float f;


            GridPoint(
                    int x,
                    int y,
                    float f) {

                this.x = x;
                this.y = y;
                this.f = f;
            }
        }
    }
}