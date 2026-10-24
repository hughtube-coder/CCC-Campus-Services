package com.navigatio.campusservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CampusGraph {

    private final List<NavigationNode> nodes;
    private final List<NavigationEdge> edges;

    public CampusGraph() {

        nodes = new ArrayList<>();
        edges = new ArrayList<>();


        // PAGE 1 - ADMIN BUILDING
// From user-annotated colored dots ONLY (no extra shortcut nodes)
// Green=corridor, Pink=door, Red=room, Purple=exit/stairs
// Rule: green pathway → pink door → red room

        addNode("P001", "Corridor", 1, 0.212f, 0.072f, false);
        addNode("P002", "Corridor", 1, 0.212f, 0.098f, false);
        addNode("P003", "Corridor", 1, 0.212f, 0.166f, false);
        addNode("P004", "Corridor", 1, 0.212f, 0.314f, false);
        addNode("P005", "Corridor", 1, 0.212f, 0.468f, false);
        addNode("P006", "Corridor", 1, 0.142f, 0.468f, false);
        addNode("P007", "Corridor", 1, 0.142f, 0.635f, false);
        addNode("P008", "Corridor", 1, 0.142f, 0.678f, false);
        addNode("P009", "Corridor", 1, 0.142f, 0.810f, false);
        addNode("P010", "Corridor", 1, 0.075f, 0.810f, false);
        addNode("P011", "Corridor", 1, 0.369f, 0.165f, false);
        addNode("P012", "Corridor", 1, 0.369f, 0.206f, false);
        addNode("P013", "Corridor", 1, 0.369f, 0.266f, false);
        addNode("P014", "Corridor", 1, 0.369f, 0.349f, false);
        addNode("P015", "Corridor", 1, 0.369f, 0.435f, false);
        addNode("P016", "Corridor", 1, 0.414f, 0.435f, false);
        addNode("P017", "Corridor", 1, 0.418f, 0.270f, false);
        addNode("P018", "Corridor", 1, 0.418f, 0.468f, false);
        addNode("P019", "Corridor", 1, 0.466f, 0.468f, false);
        addNode("P020", "Corridor", 1, 0.516f, 0.266f, false);
        addNode("P021", "Corridor", 1, 0.516f, 0.389f, false);
        addNode("P022", "Corridor", 1, 0.516f, 0.468f, false);
        addNode("P023", "Corridor", 1, 0.444f, 0.633f, false);
        addNode("P024", "Corridor", 1, 0.276f, 0.634f, false);
        addNode("P025", "Corridor", 1, 0.317f, 0.633f, false);
        addNode("P026", "Corridor", 1, 0.549f, 0.633f, false);
        addNode("P027", "Corridor", 1, 0.585f, 0.633f, false);
        addNode("P028", "Corridor", 1, 0.653f, 0.633f, false);
        addNode("P029", "Corridor", 1, 0.691f, 0.634f, false);
        addNode("P030", "Corridor", 1, 0.766f, 0.633f, false);
        addNode("P031", "Corridor", 1, 0.746f, 0.104f, false);
        addNode("P032", "Corridor", 1, 0.746f, 0.166f, false);
        addNode("P033", "Corridor", 1, 0.746f, 0.205f, false);
        addNode("P034", "Corridor", 1, 0.746f, 0.268f, false);
        addNode("P035", "Corridor", 1, 0.746f, 0.436f, false);
        addNode("P036", "Corridor", 1, 0.769f, 0.436f, false);
        addNode("P037", "Corridor", 1, 0.769f, 0.548f, false);
        addNode("P038", "Corridor", 1, 0.444f, 0.765f, false);
        addNode("N001", "Admin Building Entrance", 1, 0.444f, 0.820f, true);
        addNode("N002", "Cashier Office", 1, 0.317f, 0.561f, true);
        addNode("N003", "Clinic", 1, 0.277f, 0.697f, true);
        addNode("N004", "Office of the Vice President", 1, 0.085f, 0.676f, true);
        addNode("N005", "MISD", 1, 0.151f, 0.071f, true);
        addNode("N006", "Lazaro Hall", 1, 0.466f, 0.402f, true);
        addNode("N007", "Faculty", 1, 0.643f, 0.257f, true);
        addNode("N008", "Guidance Office", 1, 0.637f, 0.575f, true);
        addNode("N009", "Dean's Office", 1, 0.690f, 0.711f, true);
        addNode("N016", "Office of the College President", 1, 0.374f, 0.088f, true);
        addNode("N017", "Supreme Student Council", 1, 0.466f, 0.199f, true);
        addNode("N018", "Administrative/Staff Room", 1, 0.799f, 0.167f, true);
        addNode("N019", "Psychology Lab Research Office", 1, 0.799f, 0.101f, true);
        addNode("N020", "Testing Room", 1, 0.799f, 0.266f, true);
        addNode("N021", "Consultation Room", 1, 0.695f, 0.575f, true);
        addNode("N022", "Speech Lab", 1, 0.819f, 0.634f, true);
        addNode("N023", "Physics Lab", 1, 0.269f, 0.098f, true);
        addNode("N024", "Biochem Lab", 1, 0.151f, 0.166f, true);
        addNode("N025", "Science Lecture Room", 1, 0.151f, 0.312f, true);
        addNode("N027", "VPS", 1, 0.585f, 0.697f, true);
        addNode("D002", "Door", 1, 0.317f, 0.596f, false);
        addNode("D003", "Door", 1, 0.276f, 0.670f, false);
        addNode("D004", "Door", 1, 0.114f, 0.676f, false);
        addNode("D005", "Door", 1, 0.181f, 0.071f, false);
        addNode("D006", "Door", 1, 0.466f, 0.499f, false);
        addNode("D007", "Door", 1, 0.569f, 0.263f, false);
        addNode("D008", "Door", 1, 0.652f, 0.596f, false);
        addNode("D009", "Door", 1, 0.690f, 0.670f, false);
        addNode("D016", "Door", 1, 0.369f, 0.121f, false);
        addNode("D017", "Door", 1, 0.521f, 0.213f, false);
        addNode("D018", "Door", 1, 0.779f, 0.167f, false);
        addNode("D019", "Door", 1, 0.779f, 0.102f, false);
        addNode("D020", "Door", 1, 0.779f, 0.266f, false);
        addNode("D021", "Door", 1, 0.672f, 0.583f, false);
        addNode("D022", "Door", 1, 0.790f, 0.634f, false);
        addNode("D023", "Door", 1, 0.250f, 0.098f, false);
        addNode("D024", "Door", 1, 0.181f, 0.166f, false);
        addNode("D025", "Door", 1, 0.182f, 0.313f, false);
        addNode("D027", "Door", 1, 0.585f, 0.670f, false);
        addNode("U001", "Entrance Stairs", 1, 0.444f, 0.809f, false);
        addNode("U002", "Exit", 1, 0.076f, 0.879f, false);
        addNode("U003", "Down Stairs", 1, 0.379f, 0.541f, false);
        addNode("P039", "Corridor", 1, 0.377f, 0.633f, false);   // corridor junction below the Underground passage
        addNode("U004", "Underground", 1, 0.377f, 0.595f, false);

        addEdge("P001", "P002", 3);
        addEdge("P002", "P003", 5);
        addEdge("P003", "P004", 11);
        addEdge("P004", "P005", 12);
        addEdge("P005", "P006", 5);
        addEdge("P006", "P007", 13);
        addEdge("P007", "P008", 3);
        addEdge("P008", "P009", 10);
        addEdge("P009", "P010", 5);
        addEdge("P011", "P012", 3);
        addEdge("P012", "P013", 4);
        addEdge("P013", "P014", 6);
        addEdge("P014", "P015", 6);
        addEdge("P015", "P016", 3);
        // REMOVED (crossed walls/rooms, not a walkable corridor): addEdge("P003", "P011", 12);
        addEdge("P016", "P018", 3);
        addEdge("P018", "P019", 3);
        addEdge("P019", "P022", 3);
        addEdge("P017", "P020", 7);
        addEdge("P020", "P021", 9);
        addEdge("P021", "P022", 6);
        // REMOVED (crossed walls/rooms, not a walkable corridor): addEdge("P015", "P017", 13);
        addEdge("P016", "P019", 4);
        addEdge("P024", "P025", 3);
        addEdge("P025", "P039", 5);   // main corridor, split at the Underground passage
        addEdge("P039", "P023", 5);
        addEdge("P023", "P026", 8);
        addEdge("P026", "P027", 3);
        addEdge("P027", "P028", 5);
        addEdge("P028", "P029", 3);
        addEdge("P029", "P030", 6);
        addEdge("P007", "P024", 10);
        addEdge("P019", "P023", 13);
        addEdge("P031", "P032", 4);
        addEdge("P032", "P033", 3);
        addEdge("P033", "P034", 5);
        addEdge("P034", "P035", 13);
        addEdge("P035", "P036", 3);
        addEdge("P036", "P037", 8);
        addEdge("P037", "P030", 6);
        // REMOVED (crossed walls/rooms, not a walkable corridor): addEdge("P020", "P034", 18);
        addEdge("P023", "P038", 10);
        addEdge("P038", "U001", 3);
        addEdge("U001", "N001", 3);
        addEdge("P010", "U002", 5);
        // Down stairs are reached from the main corridor through the Underground passage:
        // corridor -> Underground -> Down Stairs -> (page 2) AB underground
        addEdge("P039", "U004", 2);
        addEdge("U004", "U003", 3);
        addEdge("P025", "D002", 2);
        addEdge("D002", "N002", 2);
        addEdge("P024", "D003", 2);
        addEdge("D003", "N003", 2);
        addEdge("P008", "D004", 2);
        addEdge("D004", "N004", 2);
        addEdge("P001", "D005", 2);
        addEdge("D005", "N005", 2);
        addEdge("P019", "D006", 2);
        addEdge("D006", "N006", 7);
        addEdge("P020", "D007", 4);
        addEdge("D007", "N007", 5);
        addEdge("P028", "D008", 2);
        addEdge("D008", "N008", 2);
        addEdge("P029", "D009", 2);
        addEdge("D009", "N009", 3);
        addEdge("P011", "D016", 3);
        addEdge("D016", "N016", 2);
        addEdge("P020", "D017", 4);
        addEdge("D017", "N017", 4);
        addEdge("P032", "D018", 2);
        addEdge("D018", "N018", 2);
        addEdge("P031", "D019", 2);
        addEdge("D019", "N019", 2);
        addEdge("P034", "D020", 2);
        addEdge("D020", "N020", 2);
        addEdge("P028", "D021", 4);
        addEdge("D021", "N021", 2);
        addEdge("P030", "D022", 2);
        addEdge("D022", "N022", 2);
        addEdge("P002", "D023", 3);
        addEdge("D023", "N023", 2);
        addEdge("P003", "D024", 2);
        addEdge("D024", "N024", 2);
        addEdge("P004", "D025", 2);
        addEdge("D025", "N025", 2);
        addEdge("P027", "D027", 2);
        addEdge("D027", "N027", 2);

        // =========================================================
        // PAGE 2 - CAMPUS GROUND FLOOR (AB building, Rizal, JMC 1st floor)
        // Generated from the map pixels: corridors follow the dark-gray
        // walkable path, pins sit on each room's door.
        // =========================================================

        // --- corridor waypoints
        addNode("P2001", "Corridor", 2, 0.4672f, 0.0907f, false);
        addNode("P2002", "Corridor", 2, 0.4672f, 0.1306f, false);
        addNode("P2003", "Corridor", 2, 0.5062f, 0.1333f, false);
        addNode("P2004", "Corridor", 2, 0.5078f, 0.1333f, false);
        addNode("P2005", "Corridor", 2, 0.5208f, 0.1333f, false);
        addNode("P2006", "Corridor", 2, 0.5495f, 0.1556f, false);
        addNode("P2007", "Corridor", 2, 0.5615f, 0.1556f, false);
        addNode("P2008", "Corridor", 2, 0.4922f, 0.1565f, false);
        addNode("P2009", "Corridor", 2, 0.4677f, 0.1667f, false);
        addNode("P2010", "Corridor", 2, 0.4922f, 0.1667f, false);
        addNode("P2011", "Corridor", 2, 0.5057f, 0.1667f, false);
        addNode("P2012", "Corridor", 2, 0.5208f, 0.1667f, false);
        addNode("P2013", "Corridor", 2, 0.5495f, 0.1667f, false);
        addNode("P2014", "Corridor", 2, 0.5615f, 0.1667f, false);
        addNode("P2015", "Corridor", 2, 0.4672f, 0.1833f, false);
        addNode("P2016", "Corridor", 2, 0.5635f, 0.1898f, false);
        addNode("P2017", "Corridor", 2, 0.4672f, 0.2019f, false);
        addNode("P2018", "Corridor", 2, 0.5630f, 0.2111f, false);
        addNode("P2019", "Corridor", 2, 0.5599f, 0.2130f, false);
        addNode("P2020", "Corridor", 2, 0.5641f, 0.2185f, false);
        addNode("P2021", "Corridor", 2, 0.5557f, 0.2204f, false);
        addNode("P2022", "Corridor", 2, 0.5625f, 0.2204f, false);
        addNode("P2023", "Corridor", 2, 0.6156f, 0.2222f, false);
        addNode("P2024", "Corridor", 2, 0.4672f, 0.2324f, false);
        addNode("P2025", "Corridor", 2, 0.4953f, 0.2648f, false);
        addNode("P2026", "Corridor", 2, 0.4958f, 0.2648f, false);
        addNode("P2027", "Corridor", 2, 0.4891f, 0.2676f, false);
        addNode("P2028", "Corridor", 2, 0.4953f, 0.2991f, false);
        addNode("P2029", "Corridor", 2, 0.4375f, 0.3000f, false);
        addNode("P2030", "Corridor", 2, 0.4760f, 0.3000f, false);
        addNode("P2031", "Campus Main Path", 2, 0.4896f, 0.3000f, false);
        addNode("P2032", "Corridor", 2, 0.5880f, 0.3000f, false);
        addNode("P2033", "Corridor", 2, 0.4766f, 0.3213f, false);
        addNode("P2034", "Corridor", 2, 0.4760f, 0.3389f, false);
        addNode("P2035", "Corridor", 2, 0.4755f, 0.3546f, false);
        addNode("P2036", "Corridor", 2, 0.4760f, 0.3639f, false);
        addNode("P2037", "Corridor", 2, 0.5510f, 0.3667f, false);
        addNode("P2038", "Corridor", 2, 0.5458f, 0.3750f, false);
        addNode("P2039", "Corridor", 2, 0.4755f, 0.3907f, false);
        addNode("P2040", "Corridor", 2, 0.5464f, 0.4120f, false);
        addNode("P2041", "Corridor", 2, 0.5146f, 0.4343f, false);
        addNode("P2042", "Corridor", 2, 0.5458f, 0.4352f, false);
        addNode("P2043", "Corridor", 2, 0.4760f, 0.4361f, false);
        addNode("P2044", "Corridor", 2, 0.4755f, 0.4620f, false);
        addNode("P2045", "Corridor", 2, 0.5849f, 0.4833f, false);
        addNode("P2046", "Corridor", 2, 0.5901f, 0.5000f, false);
        addNode("P2047", "Corridor", 2, 0.5594f, 0.5093f, false);
        addNode("P2048", "Corridor", 2, 0.4760f, 0.5120f, false);
        addNode("P2049", "Corridor", 2, 0.5901f, 0.5185f, false);
        addNode("P2050", "Corridor", 2, 0.4760f, 0.5417f, false);
        addNode("P2051", "Corridor", 2, 0.4760f, 0.5500f, false);
        addNode("P2052", "Corridor", 2, 0.5115f, 0.5500f, false);
        addNode("P2053", "Corridor", 2, 0.5464f, 0.5500f, false);
        addNode("P2054", "Corridor", 2, 0.5469f, 0.5722f, false);
        addNode("P2055", "Corridor", 2, 0.5464f, 0.6111f, false);
        addNode("P2056", "Corridor", 2, 0.4375f, 0.6120f, false);
        addNode("P2057", "Corridor", 2, 0.4510f, 0.6120f, false);
        addNode("P2058", "Corridor", 2, 0.4521f, 0.6130f, false);
        addNode("P2059", "Corridor", 2, 0.4755f, 0.6130f, false);
        addNode("P2060", "Corridor", 2, 0.4760f, 0.6139f, false);
        addNode("P2061", "Corridor", 2, 0.5510f, 0.6167f, false);
        addNode("P2062", "Corridor", 2, 0.6130f, 0.6398f, false);
        addNode("P2063", "Corridor", 2, 0.6208f, 0.6398f, false);
        addNode("P2064", "Corridor", 2, 0.4760f, 0.6435f, false);
        addNode("P2065", "Corridor", 2, 0.6135f, 0.6537f, false);
        addNode("P2066", "Corridor", 2, 0.4630f, 0.7009f, false);
        addNode("P2067", "Corridor", 2, 0.4313f, 0.7111f, false);
        addNode("P2068", "Corridor", 2, 0.4313f, 0.7204f, false);
        addNode("P2069", "Corridor", 2, 0.4792f, 0.7250f, false);
        addNode("P2070", "Corridor", 2, 0.4661f, 0.7259f, false);
        addNode("P2071", "Corridor", 2, 0.4896f, 0.7259f, false);
        addNode("P2072", "Corridor", 2, 0.4313f, 0.7398f, false);
        addNode("P2073", "Corridor", 2, 0.4313f, 0.7685f, false);
        addNode("P2074", "Corridor", 2, 0.4307f, 0.7981f, false);
        addNode("P2075", "Corridor", 2, 0.4661f, 0.7981f, false);
        addNode("P2076", "Corridor", 2, 0.4313f, 0.8139f, false);
        addNode("P2077", "Corridor", 2, 0.4656f, 0.8139f, false);
        addNode("P2078", "Corridor", 2, 0.3839f, 0.8250f, false);
        addNode("P2079", "Corridor", 2, 0.4833f, 0.8417f, false);
        addNode("P2080", "Corridor", 2, 0.4667f, 0.8454f, false);

        // --- doors
        addNode("D2001", "Door", 2, 0.4594f, 0.2324f, false);
        addNode("D2002", "Door", 2, 0.4641f, 0.1833f, false);
        addNode("D2003", "Door", 2, 0.4599f, 0.1306f, false);
        addNode("D2004", "Door", 2, 0.5031f, 0.1185f, false);
        addNode("D2005", "Door", 2, 0.5078f, 0.1167f, false);
        addNode("D2006", "Door", 2, 0.5224f, 0.1167f, false);
        addNode("D2007", "Door", 2, 0.5240f, 0.1185f, false);
        addNode("D2008", "Door", 2, 0.5635f, 0.1481f, false);
        addNode("D2009", "Door", 2, 0.5661f, 0.2204f, false);
        addNode("D2010", "Door", 2, 0.5542f, 0.4093f, false);
        addNode("D2011", "Door", 2, 0.5547f, 0.3630f, false);
        addNode("D2012", "Door", 2, 0.5427f, 0.3750f, false);
        addNode("D2013", "Door", 2, 0.5125f, 0.4250f, false);
        addNode("D2014", "Door", 2, 0.4833f, 0.3639f, false);
        addNode("D2015", "Door", 2, 0.4703f, 0.3509f, false);
        addNode("D2016", "Door", 2, 0.4703f, 0.3944f, false);
        addNode("D2017", "Door", 2, 0.4703f, 0.4583f, false);
        addNode("D2018", "Door", 2, 0.4698f, 0.5120f, false);
        addNode("D2019", "Door", 2, 0.4792f, 0.6130f, false);
        addNode("D2020", "Door", 2, 0.5115f, 0.5611f, false);
        addNode("D2021", "Door", 2, 0.5437f, 0.6130f, false);
        addNode("D2022", "Door", 2, 0.5547f, 0.6204f, false);
        addNode("D2023", "Door", 2, 0.5542f, 0.5750f, false);
        addNode("D2024", "Door", 2, 0.4729f, 0.5417f, false);
        addNode("D2025", "Door", 2, 0.4516f, 0.6074f, false);
        addNode("D2026", "Door", 2, 0.4521f, 0.6241f, false);
        addNode("D2027", "Door", 2, 0.4729f, 0.6435f, false);
        addNode("D2028", "Door", 2, 0.4276f, 0.7093f, false);
        addNode("D2029", "Door", 2, 0.4276f, 0.7398f, false);
        addNode("D2030", "Door", 2, 0.4318f, 0.7981f, false);
        addNode("D2031", "Door", 2, 0.4333f, 0.7204f, false);
        addNode("D2032", "Door", 2, 0.4792f, 0.7231f, false);
        addNode("D2033", "Door", 2, 0.4911f, 0.7250f, false);
        addNode("D2034", "Door", 2, 0.4896f, 0.7278f, false);
        addNode("D2035", "Door", 2, 0.4682f, 0.7981f, false);
        addNode("D2036", "Door", 2, 0.4333f, 0.7685f, false);

        // --- rooms / points of interest
        addNode("N036", "1CL1", 2, 0.4469f, 0.2481f, true);
        addNode("N037", "1CL2", 2, 0.4469f, 0.1833f, true);
        addNode("N038", "1CL3", 2, 0.4469f, 0.1157f, true);
        addNode("N039", "1CL4", 2, 0.4885f, 0.1157f, true);
        addNode("N041", "JMC Female Restroom", 2, 0.5078f, 0.1000f, true);
        addNode("N040", "JMC Male Restroom", 2, 0.5224f, 0.1000f, true);
        addNode("N035", "JMC Archive Section", 2, 0.5427f, 0.1157f, true);
        addNode("N034", "Registrar", 2, 0.5823f, 0.1157f, true);
        addNode("N033", "JMC Lobby", 2, 0.5813f, 0.2296f, true);
        addNode("N046", "Rizal 1", 2, 0.5651f, 0.3981f, true);
        addNode("N047", "Rizal 2", 2, 0.5651f, 0.3491f, true);
        addNode("N048", "Rizal 3", 2, 0.5297f, 0.3750f, true);
        addNode("N049", "Rizal 4", 2, 0.5115f, 0.3750f, true);
        addNode("N050", "Rizal 5", 2, 0.4927f, 0.3750f, true);
        addNode("N051", "Rizal 6", 2, 0.4516f, 0.3491f, true);
        addNode("N052", "Rizal 7", 2, 0.4516f, 0.3963f, true);
        addNode("N053", "Rizal 8", 2, 0.4516f, 0.4472f, true);
        addNode("N054", "Rizal 9", 2, 0.4516f, 0.4972f, true);
        addNode("N055", "Rizal 10", 2, 0.4927f, 0.6130f, true);
        addNode("N056", "Rizal 11", 2, 0.5109f, 0.6130f, true);
        addNode("N057", "Rizal 12", 2, 0.5292f, 0.6130f, true);
        addNode("N058", "Rizal 13", 2, 0.5651f, 0.6389f, true);
        addNode("N059", "Rizal 14", 2, 0.5651f, 0.5852f, true);
        addNode("N060", "Rizal DCI Faculty", 2, 0.4516f, 0.5417f, true);
        addNode("N061", "Rizal Conference Room", 2, 0.4516f, 0.5815f, true);
        addNode("N062", "Rizal Male Restroom", 2, 0.4521f, 0.6435f, true);
        addNode("N063", "Rizal Female Restroom", 2, 0.4641f, 0.6435f, true);
        addNode("N082", "Psychology Laboratory", 2, 0.4089f, 0.7046f, true);
        addNode("N012", "AB7", 2, 0.4089f, 0.7519f, true);
        addNode("N011", "AB8", 2, 0.4089f, 0.7981f, true);
        addNode("N014", "AB5", 2, 0.4479f, 0.7046f, true);
        addNode("N015", "AB4", 2, 0.4792f, 0.7019f, true);
        addNode("N013", "AB6", 2, 0.5042f, 0.7019f, true);
        addNode("N028", "Sports Development Office", 2, 0.4896f, 0.7454f, true);
        addNode("N030", "AB2", 2, 0.4802f, 0.7981f, true);
        addNode("N029", "Gazebo", 2, 0.4479f, 0.7685f, true);
        addNode("N032", "JMC Building Entrance", 2, 0.6146f, 0.2222f, true);
        addNode("N044", "JMC Exit", 2, 0.4948f, 0.2685f, true);
        addNode("N045", "Rizal Building Entrance", 2, 0.5885f, 0.5185f, true);
        addNode("N064", "Rizal Stair", 2, 0.5911f, 0.5000f, false);
        addNode("N042", "JMC First Floor Left Stair", 2, 0.4948f, 0.2685f, false);
        addNode("N043", "JMC First Floor Right Stair", 2, 0.5599f, 0.2111f, false);
        addNode("N031", "Portal", 2, 0.4703f, 0.6741f, true);
        addNode("N087", "Underground Stairs", 2, 0.4661f, 0.8509f, false);
        addNode("N010", "Canteen", 2, 0.3854f, 0.8204f, true);

        // --- corridor connections
        addEdge("P2045", "P2047", 10.1);
        addEdge("P2029", "P2030", 12.3);
        addEdge("P2017", "P2015", 3.3);
        addEdge("P2017", "P2010", 14.1);
        addEdge("P2017", "P2024", 5.5);
        addEdge("P2018", "P2016", 3.9);
        addEdge("P2018", "P2019", 1.1);
        addEdge("P2018", "P2020", 1.6);
        addEdge("P2043", "P2039", 8.2);
        addEdge("P2043", "P2041", 12.5);
        addEdge("P2043", "P2044", 4.7);
        addEdge("P2008", "P2010", 1.8);
        addEdge("P2007", "P2014", 2.0);
        addEdge("P2016", "P2014", 4.5);
        addEdge("P2016", "P2023", 22.7);
        addEdge("P2006", "P2013", 2.0);
        addEdge("P2012", "P2005", 6.0);
        addEdge("P2012", "P2011", 4.8);
        addEdge("P2012", "P2013", 9.2);
        addEdge("P2032", "P2028", 29.7);
        addEdge("P2032", "P2023", 25.6);
        addEdge("P2032", "P2046", 36.3);
        addEdge("P2021", "P2019", 2.7);
        addEdge("P2022", "P2020", 0.6);
        addEdge("P2001", "P2002", 7.2);
        addEdge("P2014", "P2013", 3.8);
        addEdge("P2066", "P2070", 5.2);
        addEdge("P2067", "P2068", 1.7);
        addEdge("P2061", "P2055", 2.4);
        addEdge("P2028", "P2027", 6.7);
        addEdge("P2028", "P2031", 1.9);
        addEdge("P2010", "P2009", 7.8);
        addEdge("P2010", "P2011", 4.3);
        addEdge("P2042", "P2040", 4.2);
        addEdge("P2042", "P2041", 10.1);
        addEdge("P2042", "P2047", 15.2);
        addEdge("P2026", "P2025", 0.5);
        addEdge("P2076", "P2074", 2.9);
        addEdge("P2076", "P2077", 11.0);
        addEdge("P2076", "P2078", 19.7);
        addEdge("P2027", "P2025", 2.3);
        addEdge("P2027", "P2024", 13.0);
        addEdge("P2070", "P2069", 4.2);
        addEdge("P2070", "P2075", 13.0);
        addEdge("P2051", "P2050", 1.5);
        addEdge("P2051", "P2052", 11.3);
        addEdge("P2051", "P2059", 11.4);
        addEdge("P2059", "P2058", 7.8);
        addEdge("P2059", "P2060", 0.5);
        addEdge("P2062", "P2049", 30.5);
        addEdge("P2062", "P2063", 2.6);
        addEdge("P2062", "P2065", 2.6);
        addEdge("P2071", "P2069", 3.4);
        addEdge("P2079", "P2080", 5.9);
        addEdge("P2034", "P2035", 2.9);
        addEdge("P2034", "P2033", 3.2);
        addEdge("P2037", "P2038", 3.2);
        addEdge("P2053", "P2047", 9.1);
        addEdge("P2053", "P2052", 11.2);
        addEdge("P2053", "P2054", 4.1);
        addEdge("P2077", "P2075", 2.9);
        addEdge("P2077", "P2080", 5.8);
        addEdge("P2030", "P2031", 4.3);
        addEdge("P2030", "P2033", 3.9);
        addEdge("P2064", "P2060", 5.3);
        addEdge("P2011", "P2003", 6.1);
        addEdge("P2056", "P2057", 4.3);
        addEdge("P2009", "P2002", 6.6);
        addEdge("P2009", "P2015", 3.1);
        addEdge("P2003", "P2004", 0.5);
        addEdge("P2004", "P2005", 4.2);
        addEdge("P2040", "P2038", 6.7);
        addEdge("P2036", "P2035", 1.7);
        addEdge("P2036", "P2039", 4.9);
        addEdge("P2044", "P2048", 9.1);
        addEdge("P2048", "P2050", 5.3);
        addEdge("P2055", "P2054", 7.1);
        addEdge("P2057", "P2058", 0.5);
        addEdge("P2072", "P2068", 3.5);
        addEdge("P2072", "P2073", 5.2);
        addEdge("P2074", "P2073", 5.4);
        addEdge("P2049", "P2046", 3.5);

        // --- waypoint -> door -> room connections
        addEdge("P2024", "D2001", 2.5);
        addEdge("D2001", "N036", 2);
        addEdge("P2015", "D2002", 1.0);
        addEdge("D2002", "N037", 2);
        addEdge("P2002", "D2003", 2.3);
        addEdge("D2003", "N038", 2);
        addEdge("P2003", "D2004", 2.8);
        addEdge("D2004", "N039", 2);
        addEdge("P2004", "D2005", 3.0);
        addEdge("D2005", "N041", 2);
        addEdge("P2005", "D2006", 3.0);
        addEdge("D2006", "N040", 2);
        addEdge("P2005", "D2007", 2.8);
        addEdge("D2007", "N035", 2);
        addEdge("P2007", "D2008", 1.5);
        addEdge("D2008", "N034", 2);
        addEdge("P2020", "D2009", 0.7);
        addEdge("D2009", "N033", 2);
        addEdge("P2040", "D2010", 2.5);
        addEdge("D2010", "N046", 2);
        addEdge("P2037", "D2011", 1.3);
        addEdge("D2011", "N047", 2);
        addEdge("P2038", "D2012", 1.0);
        addEdge("D2012", "N048", 2);
        addEdge("P2041", "D2013", 1.8);
        addEdge("D2013", "N049", 2);
        addEdge("P2036", "D2014", 2.3);
        addEdge("D2014", "N050", 2);
        addEdge("P2035", "D2015", 1.8);
        addEdge("D2015", "N051", 2);
        addEdge("P2039", "D2016", 1.8);
        addEdge("D2016", "N052", 2);
        addEdge("P2044", "D2017", 1.8);
        addEdge("D2017", "N053", 2);
        addEdge("P2048", "D2018", 2.0);
        addEdge("D2018", "N054", 2);
        addEdge("P2060", "D2019", 1.0);
        addEdge("D2019", "N055", 2);
        addEdge("P2052", "D2020", 2.0);
        addEdge("D2020", "N056", 2);
        addEdge("P2055", "D2021", 0.9);
        addEdge("D2021", "N057", 2);
        addEdge("P2061", "D2022", 1.3);
        addEdge("D2022", "N058", 2);
        addEdge("P2054", "D2023", 2.4);
        addEdge("D2023", "N059", 2);
        addEdge("P2050", "D2024", 1.0);
        addEdge("D2024", "N060", 2);
        addEdge("P2057", "D2025", 0.8);
        addEdge("D2025", "N061", 2);
        addEdge("P2058", "D2026", 2.0);
        addEdge("D2026", "N062", 2);
        addEdge("P2064", "D2027", 1.0);
        addEdge("D2027", "N063", 2);
        addEdge("P2067", "D2028", 1.2);
        addEdge("D2028", "N082", 2);
        addEdge("P2072", "D2029", 1.2);
        addEdge("D2029", "N012", 2);
        addEdge("P2074", "D2030", 0.5);
        addEdge("D2030", "N011", 2);
        addEdge("P2068", "D2031", 0.7);
        addEdge("D2031", "N014", 2);
        addEdge("P2069", "D2032", 0.5);
        addEdge("D2032", "N015", 2);
        addEdge("P2071", "D2033", 0.5);
        addEdge("D2033", "N013", 2);
        addEdge("P2071", "D2034", 0.5);
        addEdge("D2034", "N028", 2);
        addEdge("P2075", "D2035", 0.7);
        addEdge("D2035", "N030", 2);
        addEdge("P2073", "D2036", 0.7);
        addEdge("D2036", "N029", 2);
        addEdge("P2023", "N032", 0.5);
        addEdge("P2025", "N044", 0.7);
        addEdge("P2049", "N045", 0.5);
        addEdge("P2046", "N064", 0.5);
        addEdge("P2025", "N042", 0.7);
        addEdge("P2019", "N043", 0.5);
        addEdge("P2064", "N031", 5.8);
        addEdge("P2080", "N087", 1.0);
        addEdge("P2078", "N010", 1.0);
        addEdge("N031", "P2066", 5.4);


        // =========================================================
        // PAGE 3 - JMC SECOND FLOOR
        // Generated from the map pixels: corridors follow the dark-gray
        // walkable path, pins sit on each room's door.
        // =========================================================

        // --- corridor waypoints
        addNode("P3001", "Corridor", 3, 0.5880f, 0.3000f, false);
        addNode("P3002", "Corridor", 3, 0.3859f, 0.3009f, false);
        addNode("P3003", "Corridor", 3, 0.4766f, 0.3713f, false);
        addNode("P3004", "Corridor", 3, 0.5099f, 0.3713f, false);
        addNode("P3005", "Corridor", 3, 0.5880f, 0.3741f, false);
        addNode("P3006", "Corridor", 3, 0.5969f, 0.3741f, false);
        addNode("P3007", "Corridor", 3, 0.4766f, 0.3898f, false);
        addNode("P3008", "Corridor", 3, 0.5094f, 0.3898f, false);
        addNode("P3009", "Corridor", 3, 0.3786f, 0.4019f, false);
        addNode("P3010", "Corridor", 3, 0.3859f, 0.4019f, false);
        addNode("P3011", "Corridor", 3, 0.5365f, 0.4278f, false);
        addNode("P3012", "Corridor", 3, 0.4432f, 0.4407f, false);
        addNode("P3013", "Corridor", 3, 0.5099f, 0.4463f, false);
        addNode("P3014", "Corridor", 3, 0.4432f, 0.4630f, false);
        addNode("P3015", "Corridor", 3, 0.4766f, 0.4630f, false);
        addNode("P3016", "Corridor", 3, 0.5208f, 0.4630f, false);
        addNode("P3017", "Corridor", 3, 0.5365f, 0.4630f, false);
        addNode("P3018", "Corridor", 3, 0.5875f, 0.4630f, false);
        addNode("P3019", "Corridor", 3, 0.3865f, 0.4639f, false);
        addNode("P3020", "Corridor", 3, 0.4859f, 0.4639f, false);
        addNode("P3021", "Corridor", 3, 0.3859f, 0.5000f, false);
        addNode("P3022", "Corridor", 3, 0.5880f, 0.5093f, false);
        addNode("P3023", "Corridor", 3, 0.5969f, 0.5093f, false);
        addNode("P3024", "Corridor", 3, 0.5880f, 0.5657f, false);
        addNode("P3025", "Corridor", 3, 0.5969f, 0.5657f, false);
        addNode("P3026", "Corridor", 3, 0.5677f, 0.5704f, false);
        addNode("P3027", "Corridor", 3, 0.5630f, 0.5713f, false);
        addNode("P3028", "Corridor", 3, 0.5875f, 0.5713f, false);
        addNode("P3029", "Corridor", 3, 0.5880f, 0.6546f, false);
        addNode("P3030", "Corridor", 3, 0.4417f, 0.6852f, false);

        // --- doors
        addNode("D3001", "Door", 3, 0.3786f, 0.5000f, false);
        addNode("D3002", "Door", 3, 0.4437f, 0.4315f, false);
        addNode("D3003", "Door", 3, 0.6047f, 0.5769f, false);
        addNode("D3004", "Door", 3, 0.6047f, 0.4981f, false);
        addNode("D3005", "Door", 3, 0.6047f, 0.3630f, false);
        addNode("D3006", "Door", 3, 0.5365f, 0.4185f, false);
        addNode("D3007", "Door", 3, 0.5099f, 0.3630f, false);
        addNode("D3008", "Door", 3, 0.4760f, 0.3630f, false);

        // --- rooms / points of interest
        addNode("N065", "Library", 3, 0.3401f, 0.5000f, true);
        addNode("N066", "Office Librarian", 3, 0.4313f, 0.3565f, true);
        addNode("N067", "2CL1", 3, 0.6474f, 0.6037f, true);
        addNode("N068", "2CL2", 3, 0.6474f, 0.4750f, true);
        addNode("N069", "2CL3", 3, 0.6474f, 0.3491f, true);
        addNode("N070", "2CL4", 3, 0.5469f, 0.3491f, true);
        addNode("N071", "CR Male 2JMC", 3, 0.5089f, 0.3204f, true);
        addNode("N072", "CR Female 2JMC", 3, 0.4766f, 0.3204f, true);
        addNode("N083", "JMC Second Floor Left Stair", 3, 0.4422f, 0.6806f, false);
        addNode("N084", "JMC Second Floor Right Stair", 3, 0.5677f, 0.5713f, false);

        // --- corridor connections
        addEdge("P3020", "P3013", 9.4);
        addEdge("P3020", "P3015", 3.1);
        addEdge("P3020", "P3016", 12.6);
        addEdge("P3024", "P3022", 10.2);
        addEdge("P3024", "P3025", 2.8);
        addEdge("P3024", "P3028", 1.1);
        addEdge("P3016", "P3013", 4.7);
        addEdge("P3016", "P3017", 5.0);
        addEdge("P3004", "P3008", 3.4);
        addEdge("P3002", "P3010", 18.2);
        addEdge("P3001", "P3005", 13.3);
        addEdge("P3011", "P3017", 6.3);
        addEdge("P3023", "P3022", 2.8);
        addEdge("P3003", "P3007", 3.3);
        addEdge("P3005", "P3006", 2.8);
        addEdge("P3005", "P3018", 16.1);
        addEdge("P3015", "P3007", 13.2);
        addEdge("P3015", "P3014", 10.8);
        addEdge("P3027", "P3026", 1.6);
        addEdge("P3009", "P3010", 2.3);
        addEdge("P3013", "P3008", 10.2);
        addEdge("P3014", "P3012", 4.0);
        addEdge("P3014", "P3019", 18.2);
        addEdge("P3029", "P3028", 15.1);
        addEdge("P3017", "P3018", 16.3);
        addEdge("P3022", "P3018", 8.4);
        addEdge("P3010", "P3019", 11.2);
        addEdge("P3019", "P3021", 6.6);
        addEdge("P3008", "P3007", 10.5);
        addEdge("P3030", "P3021", 53.5);
        addEdge("P3028", "P3026", 6.4);

        // --- waypoint -> door -> room connections
        addEdge("P3021", "D3001", 2.3);
        addEdge("D3001", "N065", 2);
        addEdge("P3012", "D3002", 1.7);
        addEdge("D3002", "N066", 2);
        addEdge("P3025", "D3003", 3.2);
        addEdge("D3003", "N067", 2);
        addEdge("P3023", "D3004", 3.2);
        addEdge("D3004", "N068", 2);
        addEdge("P3006", "D3005", 3.2);
        addEdge("D3005", "N069", 2);
        addEdge("P3011", "D3006", 1.7);
        addEdge("D3006", "N070", 2);
        addEdge("P3004", "D3007", 1.5);
        addEdge("D3007", "N071", 2);
        addEdge("P3003", "D3008", 1.5);
        addEdge("D3008", "N072", 2);
        addEdge("P3030", "N083", 0.8);
        addEdge("P3026", "N084", 0.5);


        // =========================================================
        // PAGE 4 - JMC THIRD FLOOR
        // Generated from the map pixels: corridors follow the dark-gray
        // walkable path, pins sit on each room's door.
        // =========================================================

        // --- corridor waypoints
        addNode("P4001", "Corridor", 4, 0.3818f, 0.2926f, false);
        addNode("P4002", "Corridor", 4, 0.5974f, 0.2935f, false);
        addNode("P4003", "Corridor", 4, 0.4745f, 0.3667f, false);
        addNode("P4004", "Corridor", 4, 0.5089f, 0.3667f, false);
        addNode("P4005", "Corridor", 4, 0.5974f, 0.3685f, false);
        addNode("P4006", "Corridor", 4, 0.4750f, 0.3880f, false);
        addNode("P4007", "Corridor", 4, 0.5089f, 0.3880f, false);
        addNode("P4008", "Corridor", 4, 0.3812f, 0.3944f, false);
        addNode("P4009", "Corridor", 4, 0.3740f, 0.3954f, false);
        addNode("P4010", "Corridor", 4, 0.5411f, 0.4389f, false);
        addNode("P4011", "Corridor", 4, 0.4422f, 0.4398f, false);
        addNode("P4012", "Corridor", 4, 0.5089f, 0.4435f, false);
        addNode("P4013", "Corridor", 4, 0.4422f, 0.4602f, false);
        addNode("P4014", "Corridor", 4, 0.4745f, 0.4602f, false);
        addNode("P4015", "Corridor", 4, 0.5411f, 0.4602f, false);
        addNode("P4016", "Corridor", 4, 0.3818f, 0.4611f, false);
        addNode("P4017", "Corridor", 4, 0.4766f, 0.4611f, false);
        addNode("P4018", "Corridor", 4, 0.5115f, 0.4611f, false);
        addNode("P4019", "Corridor", 4, 0.5974f, 0.4611f, false);
        addNode("P4020", "Corridor", 4, 0.5974f, 0.5028f, false);
        addNode("P4021", "Corridor", 4, 0.5823f, 0.5741f, false);
        addNode("P4022", "Corridor", 4, 0.5974f, 0.5741f, false);
        addNode("P4023", "Corridor", 4, 0.5813f, 0.5750f, false);
        addNode("P4024", "Corridor", 4, 0.5974f, 0.5935f, false);
        addNode("P4025", "Corridor", 4, 0.3818f, 0.6454f, false);
        addNode("P4026", "Corridor", 4, 0.5979f, 0.6611f, false);
        addNode("P4027", "Corridor", 4, 0.4396f, 0.6917f, false);
        addNode("P4028", "Corridor", 4, 0.4391f, 0.6963f, false);

        // --- doors
        addNode("D4001", "Door", 4, 0.3656f, 0.3833f, false);
        addNode("D4002", "Door", 4, 0.4422f, 0.4315f, false);
        addNode("D4003", "Door", 4, 0.5406f, 0.4333f, false);
        addNode("D4004", "Door", 4, 0.6094f, 0.3685f, false);
        addNode("D4005", "Door", 4, 0.6094f, 0.5028f, false);
        addNode("D4006", "Door", 4, 0.6094f, 0.5935f, false);
        addNode("D4007", "Door", 4, 0.5089f, 0.3593f, false);
        addNode("D4008", "Door", 4, 0.4745f, 0.3593f, false);
        addNode("D4009", "Door", 4, 0.3745f, 0.6454f, false);

        // --- rooms / points of interest
        addNode("N073", "3JMC7", 4, 0.3333f, 0.3519f, true);
        addNode("N074", "3JMC8", 4, 0.4281f, 0.3500f, true);
        addNode("N075", "3JMC9", 4, 0.5547f, 0.3500f, true);
        addNode("N076", "3JMC10", 4, 0.6542f, 0.3435f, true);
        addNode("N077", "3CL1", 4, 0.6542f, 0.4750f, true);
        addNode("N078", "3CL2", 4, 0.6542f, 0.6093f, true);
        addNode("N079", "CR Male 3JMC", 4, 0.5089f, 0.3130f, true);
        addNode("N080", "CR Female 3JMC", 4, 0.4755f, 0.3130f, true);
        addNode("N081", "AVR", 4, 0.3333f, 0.6454f, true);
        addNode("N085", "JMC Third Floor Left Stair", 4, 0.4396f, 0.6963f, false);
        addNode("N086", "JMC Third Floor Right Stair", 4, 0.5823f, 0.5741f, false);

        // --- corridor connections
        addEdge("P4009", "P4008", 2.4);
        addEdge("P4001", "P4008", 18.4);
        addEdge("P4027", "P4028", 0.9);
        addEdge("P4014", "P4006", 13.1);
        addEdge("P4014", "P4017", 0.7);
        addEdge("P4014", "P4013", 10.5);
        addEdge("P4006", "P4003", 3.9);
        addEdge("P4006", "P4007", 10.8);
        addEdge("P4022", "P4020", 13.0);
        addEdge("P4022", "P4021", 4.8);
        addEdge("P4022", "P4024", 3.6);
        addEdge("P4008", "P4016", 12.1);
        addEdge("P4016", "P4013", 19.4);
        addEdge("P4016", "P4025", 33.2);
        addEdge("P4004", "P4007", 3.8);
        addEdge("P4010", "P4015", 3.8);
        addEdge("P4012", "P4007", 10.0);
        addEdge("P4012", "P4017", 11.9);
        addEdge("P4012", "P4018", 3.6);
        addEdge("P4013", "P4011", 3.7);
        addEdge("P4002", "P4005", 13.5);
        addEdge("P4023", "P4021", 0.5);
        addEdge("P4017", "P4018", 12.4);
        addEdge("P4019", "P4005", 16.8);
        addEdge("P4019", "P4015", 18.1);
        addEdge("P4019", "P4020", 7.5);
        addEdge("P4015", "P4018", 9.6);
        addEdge("P4026", "P4024", 12.2);
        addEdge("P4025", "P4028", 29.0);

        // --- waypoint -> door -> room connections
        addEdge("P4009", "D4001", 3.4);
        addEdge("D4001", "N073", 2);
        addEdge("P4011", "D4002", 1.5);
        addEdge("D4002", "N074", 2);
        addEdge("P4010", "D4003", 1.0);
        addEdge("D4003", "N075", 2);
        addEdge("P4005", "D4004", 3.8);
        addEdge("D4004", "N076", 2);
        addEdge("P4020", "D4005", 3.8);
        addEdge("D4005", "N077", 2);
        addEdge("P4024", "D4006", 3.8);
        addEdge("D4006", "N078", 2);
        addEdge("P4004", "D4007", 1.3);
        addEdge("D4007", "N079", 2);
        addEdge("P4003", "D4008", 1.3);
        addEdge("D4008", "N080", 2);
        addEdge("P4025", "D4009", 2.3);
        addEdge("D4009", "N081", 2);
        addEdge("P4028", "N085", 0.5);
        addEdge("P4021", "N086", 0.5);


        // =========================================================
        // MAP / FLOOR CONNECTIONS
        // =========================================================
        // PLACEHOLDER distances - replace with your measured values.

        // Outside: Admin Building (page 1) -> Campus Main Path (page 2).
        // U001 is named "Entrance Stairs", which lets the page change pass.
        addEdge("U001", "P2031", 40);

        // Underground: Admin Building "Down" stairs -> Underground passage
        // -> AB building underground stairs (page 2).
        addEdge("U003", "N087", 60);

        // JMC left stair: 1st floor -> 2nd floor -> 3rd floor
        addEdge("N042", "N083", 20);
        addEdge("N083", "N085", 20);

        // JMC right stair: 1st floor -> 2nd floor -> 3rd floor
        addEdge("N043", "N084", 20);
        addEdge("N084", "N086", 20);
    }


    // =========================================================
    // ADD NODE
    // =========================================================

    private void addNode(
            String id,
            String name,
            int mapPage,
            float x,
            float y,
            boolean qrEnabled
    ) {

        /*
         * Page 1 coordinates were marked relative to the MAP ITSELF
         * (the cropped content area), but the app draws them over the
         * full 1920x1080 drawable, which has white margins around the
         * map. Convert them to full-image fractions so the route lands
         * on the corridors instead of crossing walls.
         *
         * Content box of map_page_1.png (pixels): x 365-1554, y 117-962.
         */
        if (mapPage == 1) {
            x = (365f + x * (1554f - 365f)) / 1920f;
            y = (117f + y * (962f - 117f)) / 1080f;
        }

        nodes.add(
                new NavigationNode(
                        id,
                        name,
                        mapPage,
                        x,
                        y,
                        qrEnabled
                )
        );
    }


    // =========================================================
    // ADD EDGE
    // =========================================================

    private void addEdge(
            String from,
            String to,
            double distance
    ) {

        edges.add(
                new NavigationEdge(
                        from,
                        to,
                        distance
                )
        );
    }


    // =========================================================
    // GET ALL NODES
    // =========================================================

    public List<NavigationNode> getNodes() {
        return nodes;
    }


    // =========================================================
    // GET ALL EDGES
    // =========================================================

    public List<NavigationEdge> getEdges() {
        return edges;
    }


    // =========================================================
    // FIND NODE BY NAME
    // =========================================================

    public NavigationNode findNodeByName(
            String name
    ) {

        if (name == null) {
            return null;
        }

        for (NavigationNode node : nodes) {

            if (node.getName()
                    .equalsIgnoreCase(name)) {

                return node;
            }
        }

        return null;
    }


    // =========================================================
    // FIND NODE BY ID
    // =========================================================

    public NavigationNode findNodeById(
            String id
    ) {

        if (id == null) {
            return null;
        }

        for (NavigationNode node : nodes) {

            if (node.getId()
                    .equalsIgnoreCase(id)) {

                return node;
            }
        }

        return null;
    }


    // =========================================================
    // GET DESTINATION NODES
    // =========================================================

    public List<NavigationNode> getDestinationNodes() {

        List<NavigationNode> destinations =
                new ArrayList<>();

        for (NavigationNode node : nodes) {

            if (node.isQrEnabled()) {

                destinations.add(node);
            }
        }

        return destinations;
    }


    // =========================================================
    // GET DESTINATION NAMES
    // =========================================================

    public List<String> getDestinationNames() {

        List<String> names =
                new ArrayList<>();

        for (NavigationNode node :
                getDestinationNodes()) {

            names.add(node.getName());
        }

        Collections.sort(
                names,
                String.CASE_INSENSITIVE_ORDER
        );

        return names;
    }
}