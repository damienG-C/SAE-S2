package Algo;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.APSP;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonGrapheTest {

    
    private Graph creerPetitGraphe() {
        Graph g = new SingleGraph("test");

        g.addNode("A");
        g.addNode("B");
        g.addNode("C");

        Edge ab = g.addEdge("AB", "A", "B");
        ab.setAttribute("duree", 10);
        ab.setAttribute("distance", 10);
        ab.setAttribute("fiabilite", 1.0);

        Edge bc = g.addEdge("BC", "B", "C");
        bc.setAttribute("duree", 20);
        bc.setAttribute("distance", 20);
        bc.setAttribute("fiabilite", 1.0);

        Edge ac = g.addEdge("AC", "A", "C");
        ac.setAttribute("duree", 50);
        ac.setAttribute("distance", 50);
        ac.setAttribute("fiabilite", 1.0);

        return g;
    }

    private void calculAPSP(Graph g) {
        APSP apsp = new APSP();
        apsp.init(g);
        apsp.setDirected(false);
        apsp.setWeightAttributeName("duree");
        apsp.compute();
    }

    @Test
    public void testConstruireGrapheAleatoire() {
        Graph g = MonGraphe.construireGrapheAleatoire(10);

        assertNotNull(g);
        assertEquals(10, g.getNodeCount());
        assertTrue(g.getEdgeCount() > 0);
    }

    @Test
    public void testCalculerTotalPath() {
        Graph g = creerPetitGraphe();

        Path p = new Path();
        p.setRoot(g.getNode("A"));
        p.add(g.getEdge("AB"));
        p.add(g.getEdge("BC"));

        int total = MonGraphe.calculerTotalPath(p, "duree");

        assertEquals(30, total);
    }

    @Test
    public void testCalculerItinerairePlusCourt() {

        Graph g = creerPetitGraphe();

        List<Object> resultat =
                MonGraphe.calculerItinerairePlusCourt(g, "S1", "S9");

        Path chemin = (Path) resultat.get(0);
        double duree = (double) resultat.get(1);

        assertNotNull(chemin);
        assertEquals(30.0, duree);
    }

    @Test
    public void testCalculerItineraireSecurise() {

        Graph g = creerPetitGraphe();

        List<Object> resultat =
                MonGraphe.calculerItineraireSecurise(g, "A", "C");

        assertNotNull(resultat);
        assertEquals(2, resultat.size());
    }

    @Test
    public void testChargerGraphe() throws Exception {

        File f = File.createTempFile("graphe", ".csv");

        try (PrintWriter pw = new PrintWriter(f)) {

            pw.println("S1;M;0;8,10,20");
            pw.println("S2;O;8,10,20;0");
        }

        Graph g = MonGraphe.chargerGraphe(f.getAbsolutePath());

        assertNotNull(g);
        assertEquals(2, g.getNodeCount());
        assertEquals(1, g.getEdgeCount());

        f.delete();
    }

    @Test
    public void testSupprimerNoeudSauvegardeRoute() {

        Graph g = creerPetitGraphe();

        Graph resultat =
                MonGraphe.supprimerNoeudSauvegardeRoute(
                        g,
                        g.getNode("B"),
                        "duree"
                );

        assertNull(resultat.getNode("B"));
        assertEquals(2, resultat.getNodeCount());
    }

    @Test
    public void testAlgoGlouton() {

        Graph g = creerPetitGraphe();
        calculAPSP(g);

        Path p = MonGraphe.algoGlouton(g, g.getNode("A"));

        assertNotNull(p);
        assertFalse(p.getEdgePath().isEmpty());
    }

    @Test
    public void testAlgoParfait() {

        Graph g = creerPetitGraphe();
        calculAPSP(g);

        Path p = MonGraphe.algoParfait(g);

        assertNotNull(p);
        assertFalse(p.getNodePath().isEmpty());
    }

    @Test
    public void testAlgoSemiParfait() {

        Graph g = creerPetitGraphe();
        calculAPSP(g);

        Path p = MonGraphe.algoSemiParfait(g, 1);

        assertNotNull(p);
    }

    @Test
    public void testAlgoGlouton2Camions() {

        Graph g = creerPetitGraphe();
        calculAPSP(g);

        ArrayList<Path> chemins =
                MonGraphe.algoGlouton2Camions(
                        g,
                        g.getNode("A"),
                        g.getNode("A")
                );

        assertNotNull(chemins);
        assertEquals(2, chemins.size());
    }

    @Test
    public void testAlgoParfait2Camions() {

        Graph g = creerPetitGraphe();
        calculAPSP(g);

        ArrayList<Path> chemins =
                MonGraphe.algoParfait2Camions(g);

        assertNotNull(chemins);
        assertEquals(2, chemins.size());
    }

    @Test
    public void testAlgoSemiParfait2Camions() {

        Graph g = creerPetitGraphe();
        calculAPSP(g);

        ArrayList<Path> chemins =
                MonGraphe.algoSemiParfait2Camions(g, 1);

        assertNotNull(chemins);
        assertEquals(2, chemins.size());
    }
}