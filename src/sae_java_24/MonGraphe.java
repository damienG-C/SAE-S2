/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_24;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.algorithm.Dijkstra;

/**
 *
 * @author kenzo
 */
public class MonGraphe {

    private static int edgeId = 0;

    //Q1
    public static Graph construireGrapheAleatoire(int nbSommets) {
        Graph graphe = new SingleGraph("Graphe de " + nbSommets + " éléments.");
        Random rng = new Random();
        String[] TYPES = {"Maternite", "Maternite", "Maternite", "Bloc operatoire", "Centre nutrition"}; // maternité 3/5 et les deux autres 1/5 
        for (int i = 0; i < nbSommets; i++) {
            Node n = graphe.addNode("Lieu_" + i);
            String type = TYPES[rng.nextInt(5)];
            n.setAttribute("type", type);
            if ("Maternite".equals(type)) {
                n.setAttribute("ui.label", "Lieu_" + i + " (" + type.substring(0, 1) + ")");
            } else if ("Bloc operatoire".equals(type)) {
                n.setAttribute("ui.label", "Lieu_" + i + " (" + "O" + ")");
            } else {
                n.setAttribute("ui.label", "Lieu_" + i + " (" + "N" + ")");
            }

            if (graphe.getNodeCount() != 1) {
                while (n.getInDegree() == 0) {
                    for (Node n2 : graphe) {
                        if (rng.nextInt(5) == 0 && n2 != n) {
                            Edge e = graphe.addEdge("e" + edgeId++, n, n2);
                            e.setAttribute("fiabilite", (2 + rng.nextInt(9)) / 10.0);
                            e.setAttribute("distance", 10 + rng.nextInt(41));
                            e.setAttribute("duree", 10 + rng.nextInt(111));
                        }
                    }
                }
            }
        }

        return graphe;

    }

    //Q2
    public static Graph chargerGraphe(String nom_fichier) throws FileNotFoundException {
        Graph g = new SingleGraph("map");
        Scanner sc = new Scanner(new File(nom_fichier));
        List<String[]> lignes = new ArrayList<>();

        // lecture csv et création des sommets
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim(); // trim sert à enlever les espaces dans la ligne
            if (line.startsWith("//") || line.isEmpty()) {
                continue;
            }

            String[] linespace = line.split(" ");
            String linewithoutspace = "";
            for (String ind : linespace) {
                linewithoutspace = linewithoutspace + ind;
            }

            String[] cols = linewithoutspace.split(";");
            String nomSommet = cols[0];
            String typeCode = cols[1];

            String type;
            if (typeCode.equals("M")) {
                type = "Maternite";
            } else if (typeCode.equals("O")) {
                type = "Bloc operatoire";
            } else {
                type = "Centre nutrition";
            }

            Node n = g.addNode(nomSommet);
            n.setAttribute("type", type);
            if ("Maternite".equals(type)) {
                n.setAttribute("ui.label", nomSommet + " (" + "M" + ")");
            } else if ("Bloc operatoire".equals(type)) {
                n.setAttribute("ui.label", nomSommet + " (" + "O" + ")");
            } else {
                n.setAttribute("ui.label", nomSommet + " (" + "N" + ")");
            }
            lignes.add(cols);
        }
        sc.close();
        int edgeId = 0;
        for (int i = 0; i < lignes.size(); i++) {
            String[] cols = lignes.get(i);
            String src = cols[0];

            for (int j = 0; j < lignes.size(); j++) {
                if (j == i) {
                    continue;
                }
                String dst = lignes.get(j)[0];
                String cellule = cols[j + 2];

                if (cellule.isEmpty() || cellule.equals("0")) {
                    continue;
                }

                String[] vals = cellule.split(",");
                double fiabilite = Integer.parseInt(vals[0]) / 10.0;
                int distance = Integer.parseInt(vals[1]);
                int duree = Integer.parseInt(vals[2]);

                if (i < j) {
                    Edge e = g.addEdge("e" + edgeId++, src, dst);
                    e.setAttribute("fiabilite", fiabilite);
                    e.setAttribute("distance", distance);
                    e.setAttribute("duree", duree);
                }
            }
        }
        return g;

    }

    public static Path calculerItineraires(Graph graphe, String depart, String arrivee) {
        int i = 0;
        // Chemin 1 durée minimale 
        Dijkstra dijkstra1 = new Dijkstra(Dijkstra.Element.EDGE, null, "duree");
        dijkstra1.init(graphe);
        dijkstra1.setSource(graphe.getNode(depart));
        dijkstra1.compute();

        System.out.println("Chemin le plus court en durée : ");
        System.out.println(dijkstra1.getPath(graphe.getNode(arrivee)));
        System.out.println("Durée totale : " + dijkstra1.getPathLength(graphe.getNode(arrivee)) + " min");

        // calcul de dureeEstimee sur chaque arête 
        for (Edge e : graphe.getEachEdge()) {
            {
                double fiab = (double) e.getAttribute("fiabilite");
                int duree = (int) e.getAttribute("duree");
                if (fiab < 0.25) {
                    e.setAttribute("dureeEstimee", Double.MAX_VALUE); // route interdite
                } else {
                    e.setAttribute("dureeEstimee", duree * (20 - fiab * 10) / 10.0);
                }
            }
        }

        //  chemin 2 plus sûr et plus rapide 
        Dijkstra dijkstra2 = new Dijkstra(Dijkstra.Element.EDGE, null, "dureeEstimee");
        dijkstra2.init(graphe);
        dijkstra2.setSource(graphe.getNode(depart));
        dijkstra2.compute();
        i++;

        //System.out.println("Chemin le plus sûr et rapide : " + i);
        Path retour = dijkstra2.getPath(graphe.getNode(arrivee));

        // System.out.println("Durée estimée totale : " + dijkstra2.getPathLength(graphe.getNode(arrivee)) + " min");
        return retour;
    }

    public static Path plusCourteTournee(Graph graphe, int nbCamions, String typeDispensaire) {
        APSP apsp = new APSP();
        apsp.init(graphe);
        apsp.setDirected(false);
        apsp.setWeightAttributeName("duree");
        apsp.compute();
        //modifie le graphe(à l'interieur de la fonction seulement) afin de garder uniquement les dispensaires souhaités en fonction de leur type
        if (!typeDispensaire.equals("tous")) {
            for (Node n : graphe) {
                if (typeDispensaire != n.getAttribute("type")) {
                    graphe = supprimerNoeudSauvegardeRoute(graphe, n, "duree");
                }
            }
        }
        Path res = algoGlouton(graphe, apsp);
        return res;
    }

    public Path algoGlouton(Graph graphe, APSP apsp){
        ArrayList<Node> nonVerifie = (ArrayList<Node>) graphe.getEachNode();
        APSPInfo info;
        Path min = null;
        Path res = null;
        Path temp;
        Node courant;
        for (int i = 0; i < graphe.getNodeCount(); i++) {
            courant = graphe.getNode(i);
            nonVerifie.remove(courant);
            info = courant.getAttribute(APSPInfo.ATTRIBUTE_NAME);
            for (Node n : nonVerifie) {
                temp = info.getShortestPathTo(n.toString());
                if (min == null || calculerTotalPath(temp, "duree") < calculerTotalPath(min, "duree")) {
                    min = temp;
                }
            }
            for (Edge edge : min.getEdgePath()) {
                res.add(edge);
            }
        }
        return res;
    }

    public int calculerTotalPath(Path chemin, String attribut) {
        int total = 0;
        for (Edge edge : chemin.getEachEdge()) {
            total += (int) edge.getAttribute(attribut);
        }
        return total;
    }

    public Graph supprimerNoeudSauvegardeRoute(Graph g, Node node, String attribut) {
        List<Node> voisins = new ArrayList<>();
        for (Edge edge : node) {
            voisins.add(edge.getOpposite(node));
        }
        for (Node node2 : voisins) {
            for (Node node3 : voisins) {
                if (node2 != node3 && (node2.getEdgeBetween(node3) == null
                        || (int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut)
                        < (int) node3.getEdgeBetween(node2).getAttribute(attribut))) {
                    g.addEdge("e" + edgeId++, node2, node3);
                }
            }
            voisins.remove(node2);
        }
        g.removeNode(node);
        return g;
    }

}
