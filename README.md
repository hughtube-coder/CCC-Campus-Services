# CCC Campus Services

An Android indoor-navigation app for City College of Calamba. A student scans a QR code at their current spot on campus, picks an office or room, and the app draws the shortest walking route on the floor plan, with step-by-step directions.

> Package: `com.navigatio.campusservices` · Language: Java · Min SDK 29 · Target/Compile SDK 37

---

## Features

- **QR-based starting point.** Scanning the QR code at your location tells the app where you are. No GPS is needed, which suits indoor use.
- **Shortest-path routing.** Dijkstra's algorithm runs over a graph of corridors, doors, rooms and stairs.
- **Multi-floor maps.** Four map pages (Admin Building, Ground Floor, Second Floor, Third Floor), with routes that continue across pages via stairs and portals.
- **Interactive map.** Pinch to zoom, drag to pan, and page controls to flip between the floors a route passes through.
- **Route overlay.** The route is drawn in red over the map, with a green start pin, a red destination pin and orange markers where the route changes floor.
- **Turn-by-turn instructions.** A collapsible panel lists the path steps and the total distance.
- **Office directory.** A side drawer lists offices; selecting one shows it on the map and fills in the destination.
- **Manual start.** If a QR code can't be scanned, the start location can be chosen from a dropdown.

## How it works

```
SplashActivity ──► SecurityScanActivity ──► DashboardActivity
   (2 s splash)     (CameraX + ML Kit        (map, search, routing,
                     QR scan, validates       overlay, instructions)
                     the code)
```

1. **Splash** shows the logo for 2 seconds, then opens the scanner.
2. **Scan** reads the QR code with CameraX and ML Kit. A valid code has the format `CCC_NODE_<number>`. For example, `CCC_NODE_001` maps to node `N001` (Admin Building Entrance). The matched node ID is passed to the dashboard as the `NODE_ID` extra.
3. **Dashboard** loads the campus graph, places the start pin, and lets the user pick a destination. `Dijkstra.findShortestPath` returns the node sequence, and `RouteOverlayView` draws it on top of the map using the same `Matrix` as the map `ImageView`, so the line follows pan and zoom.

### The campus graph

`CampusGraph` holds the whole map as data:

| Item | Meaning |
|---|---|
| `addNode(id, name, page, x, y, qrEnabled)` | A point on a map page. `x` and `y` are normalized (0.0 to 1.0) so they work at any image resolution. |
| `P###` nodes | Waypoints (corridors, doors, stairs). Not destinations. |
| `N###` nodes | Destinations with QR codes (offices, rooms, entrances). |
| `addEdge(from, to, weight)` | A walkable connection between two nodes. |

There are currently 78 QR-enabled destinations: 20 on page 1, 41 on page 2, 8 on page 3 and 9 on page 4.

## Project structure

```
CCCCampusServices/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/navigatio/campusservices/
│       │   ├── SplashActivity.java        # launcher
│       │   ├── SecurityScanActivity.java  # QR scanning + validation
│       │   ├── DashboardActivity.java     # map UI, routing, route overlay
│       │   ├── CampusGraph.java           # nodes + edges (map data)
│       │   ├── Dijkstra.java              # shortest-path search
│       │   ├── NavigationNode.java        # node model
│       │   ├── NavigationEdge.java        # edge model
│       │   ├── MainActivity.java          # earlier scanner (overlaps SecurityScanActivity)
│       │   └── QRScannerActivity.java     # stub
│       └── res/
│           ├── drawable/map_page_1..4.png # floor-plan images (1920x1080)
│           └── layout/                    # screen layouts
├── gradle/libs.versions.toml              # dependency versions
└── settings.gradle.kts
```

## Requirements

- Android Studio (a recent version that supports Android Gradle Plugin 9.x)
- JDK 11 or newer
- An Android device or emulator running Android 10 (API 29) or higher
- A camera, for QR scanning (the app still installs without one)

## Getting started

```bash
git clone https://github.com/hughtube-coder/CCC-Campus-Services.git
cd CCC-Campus-Services/CCCCampusServices
```

1. Open the `CCCCampusServices` folder in Android Studio.
2. Let Gradle sync finish.
3. Select a device and press **Run**.
4. Allow camera access when asked.

To build from the command line:

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/`.

## Testing without printed QR codes

Generate a QR code (any online QR generator works) whose text is `CCC_NODE_###`, where `###` is the number of a destination node in `CampusGraph.java`. For example:

| QR text | Location |
|---|---|
| `CCC_NODE_001` | Admin Building Entrance |
| `CCC_NODE_002` | Cashier Office |

Matching is case-insensitive. Anything else shows "Invalid campus QR code."

## Editing the map

When a floor plan image changes, the node coordinates must be updated to match it, or the red route line will drift off the corridors.

1. Keep every `map_page_N.png` at the same size (currently 1920x1080).
2. In `CampusGraph.java`, place each corridor node (`P###`) at the centre of its corridor, as normalized `x` and `y`.
3. Add an `N###` node for each destination, plus a door waypoint in front of it.
4. Connect nodes with `addEdge`. Only connect points that are actually walkable in a straight line. Don't connect across walls or rooms.
5. Run the app and check each route on each page.

The route overlay uses dark-gray pixels in the map image as "walkable", so corridors in the artwork should stay that colour.

## Dependencies

- AndroidX AppCompat, ConstraintLayout, Activity, DrawerLayout
- Material Components
- CameraX 1.4.2 (core, camera2, lifecycle, view)
- ML Kit Barcode Scanning 17.3.0

## Known issues

- Route lines can be misaligned with the corridors on a map page whose floor plan was redrawn after its nodes were placed (see "Editing the map").
- `MainActivity` and `QRScannerActivity` are leftovers from earlier iterations. `SecurityScanActivity` is the active scanner.
- `DashboardActivity.java` is very large and would benefit from being split into smaller classes.

## Team

Developed as a class project for City College of Calamba.

<!-- Add contributor names here. -->
