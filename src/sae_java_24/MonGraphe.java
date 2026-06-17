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
import java.util.Iterator;
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
        //int edgeId = 0;
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

    public static ArrayList<Path> plusCourteTournee(Graph graphe, int nbCamions, String typeDispensaire, String algo) {
        Graph graphe2 = new SingleGraph("graphe de calcul");
        for (Node node : graphe) {
            Node tmp = graphe2.addNode(node.getId());
            tmp.addAttribute("type", (String) node.getAttribute("type"));
        }
        for (Edge edge : graphe.getEachEdge()) {
            Edge tmp = graphe2.addEdge(edge.getId(), edge.getNode0().getId(), edge.getNode1().getId());
            tmp.setAttribute("duree", (int) edge.getAttribute("duree"));
        }
        //modifie le graphe(à l'interieur de la fonction seulement) afin de garder uniquement les dispensaires souhaités en fonction de leur type
        if (!typeDispensaire.equals("tous")) {
            for (Node n : graphe2) {
                if (typeDispensaire != n.getAttribute("type")) {
                    graphe2 = supprimerNoeudSauvegardeRoute(graphe2, n, "duree");
                }
            }
        }
        APSP apsp = new APSP();
        apsp.init(graphe2);
        apsp.setDirected(false);
        apsp.setWeightAttributeName("duree");
        apsp.compute();
        Path resMauvaisGraphe1;
        if(algo.equals("glouton")){
            resMauvaisGraphe1 = algoGlouton(graphe2, apsp);
        }
        else{
            resMauvaisGraphe1 = algoParfait(graphe2, apsp, null, graphe2.getNode(0));
        }
        ArrayList<Path> res = new ArrayList<>();
        res.add(new Path());
        res.get(0).setRoot(graphe.getNode(resMauvaisGraphe1.getRoot().getId()));
        for (Edge e : resMauvaisGraphe1.getEdgePath()) {
            res.get(0).add(graphe.getEdge(e.getId()));
        }
        
        return res;
    }

    public static Path algoGlouton(Graph graphe, APSP apsp){
        ArrayList<Node> nonVerifie = new ArrayList<Node>();
        for (Node node : graphe.getEachNode()) {
            nonVerifie.add(node);
        }
        APSPInfo info;
        Path min = null;
        Path res = new Path();
        res.setRoot(graphe.getNode(0));
        Path temp;
        Node nodeMin = null;
        Node courant = graphe.getNode(0);
        while (!nonVerifie.isEmpty()) {
            nonVerifie.remove(courant);
            info = courant.getAttribute(APSPInfo.ATTRIBUTE_NAME);
            for (Node n : nonVerifie) {
                temp = info.getShortestPathTo(n.toString());
                if (min == null || calculerTotalPath(temp, "duree") < calculerTotalPath(min, "duree")) {
                    nodeMin = n;
                    min = temp;
                }
            }
            courant = nodeMin;
            if (min != null){
                for (Edge edge : min.getEdgePath()) {
                    if (edge.getAttribute("route") != null){
                            for (Edge edge2 : ((Path)edge.getAttribute("route")).getEdgePath()) {
                                res.add(edge2);
                        }
                    }
                    else{
                        res.add(edge);
                    }
                }
            }
            min = null;
            nodeMin = null;
        }
        return res;
    }

    public static int calculerTotalPath(Path chemin, String attribut) {
        int total = 0;
        for (Edge edge : chemin.getEachEdge()) {
            total += (int) edge.getAttribute(attribut);
        }
        return total;
    }

    public static Graph supprimerNoeudSauvegardeRoute(Graph g, Node node, String attribut) {
        List<Node> voisins = new ArrayList<>();
        List<Node> voisins2 = new ArrayList<>();
        Path temp = new Path();
        for (Edge edge : node) {
            voisins.add(edge.getOpposite(node));
            voisins2.add(edge.getOpposite(node));
        }
        for (Node node2 : voisins) {
            voisins2.remove(node2);
            for (Node node3 : voisins2) {
                if (node2.getEdgeBetween(node3) == null) {
                    g.addEdge("e" + edgeId, node2, node3);
                    g.getEdge("e" + edgeId).setAttribute(attribut, (int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut));
                    temp.setRoot(node2);
                    temp.add(node.getEdgeBetween(node2));
                    temp.add(node.getEdgeBetween(node3));
                    g.getEdge("e" + edgeId++).setAttribute("route", temp);
                    temp = new Path();
                }
                else if((int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut)
                        < (int) node3.getEdgeBetween(node2).getAttribute(attribut)){
                    node2.getEdgeBetween(node3).setAttribute(attribut, (int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut));
                }
            }
        }
        g.removeNode(node);
        return g;
    }
    
    public static Path algoParfait(Graph graphe, APSP apsp, Node origine, Node courant){
        Graph graphe2 = new SingleGraph("graphe de calcul");
        for (Node node : graphe) {
            Node tmp = graphe2.addNode(node.getId());
            tmp.addAttribute("type", (String) node.getAttribute("type"));
        }
        for (Edge edge : graphe.getEachEdge()) {
            Edge tmp = graphe2.addEdge(edge.getId(), edge.getNode0().getId(), edge.getNode1().getId());
            tmp.setAttribute("duree", (int) edge.getAttribute("duree"));
        }
        courant = graphe2.getNode(courant.getId());
        if (origine != null){
            graphe2 = supprimerNoeudSauvegardeRoute(graphe2, graphe2.getNode(origine.getId()), "duree");
        }
        Path res = new Path();
        res.setRoot(courant);
        
        if(graphe2.getNodeCount() == 2){
            res.add(courant.getEdgeBetween((Node) courant.getEdge(0).getOpposite(courant)));
        }
        else{
            Path min = null;
            Path concurent;
            for (Edge edge : courant.getEachEdge()) {
                concurent = algoParfait(graphe2, apsp, courant, edge.getOpposite(courant));
                if (min == null || calculerTotalPath(min, "duree") > calculerTotalPath(concurent, "duree")){
                    min = concurent;
                }
            }
            res.add(graphe.getNode(courant.getId()).getEdgeBetween((Node) graphe.getNode(min.getRoot().getId())));
            for (Edge e : min.getEdgePath()) {
                if (e.getAttribute("route") != null){
                        for (Edge edge2 : ((Path)e.getAttribute("route")).getEdgePath()) {
                            res.add(graphe.getEdge(edge2.getId()));
                    }
                }
                else{
                    res.add(graphe.getEdge(e.getId()));
                }
            }
        }
        return res;
    }
    
    public static ArrayList<Path> algoGlouton2Camions(Graph graphe, APSP apsp){
        ArrayList<Node> nonVerifie = new ArrayList<Node>();
        for (Node node : graphe.getEachNode()) {
            nonVerifie.add(node);
        }
        APSPInfo info;
        Path min = null;
        boolean cond;
        Path res1 = new Path();
        res1.setRoot(graphe.getNode(0));
        Path res2 = new Path();
        res2.setRoot(graphe.getNode(0));
        Path temp;
        Node nodeMin1 = null;
        Node nodeMin2 = null;
        Node courant1 = graphe.getNode(0);
        Node courant2 = graphe.getNode(0);
        while (!nonVerifie.isEmpty()) {
            if(nodeMin1 == null){
                nonVerifie.remove(courant1);
            }
            else{
                nonVerifie.remove(courant2);
            }
            info = courant1.getAttribute(APSPInfo.ATTRIBUTE_NAME);
            for (Node n : nonVerifie) {
                temp = info.getShortestPathTo(n.toString());
                if (min == null || calculerTotalPath(temp, "duree") < calculerTotalPath(min, "duree")) {
                    nodeMin1 = n;
                    min = temp;
                }
            }
            min = null;
            info = courant2.getAttribute(APSPInfo.ATTRIBUTE_NAME);
            for (Node n : nonVerifie) {
                temp = info.getShortestPathTo(n.toString());
                if (min == null || calculerTotalPath(temp, "duree") < calculerTotalPath(min, "duree")) {
                    nodeMin2 = n;
                    min = temp;
                }
            }
            cond = calculerTotalPath(res1, "duree") + (int) courant1.getEdgeBetween(nodeMin1).getAttribute("duree") < calculerTotalPath(res2, "duree") + (int) courant2.getEdgeBetween(nodeMin2).getAttribute("duree");
            if (cond){
                courant1 = nodeMin1;
                nodeMin1 = null;
            }
            else{
                courant2 = nodeMin2;
                nodeMin2 = null;
            }
            if (min != null){
                if (cond){
                    for (Edge edge : min.getEdgePath()) {
                        if (edge.getAttribute("route") != null){
                                for (Edge edge2 : ((Path)edge.getAttribute("route")).getEdgePath()) {
                                    res1.add(edge2);
                            }
                        }
                        else{
                            res1.add(edge);
                        }
                    }
                }
                else{
                    for (Edge edge : min.getEdgePath()) {
                        if (edge.getAttribute("route") != null){
                                for (Edge edge2 : ((Path)edge.getAttribute("route")).getEdgePath()) {
                                    res2.add(edge2);
                            }
                        }
                        else{
                            res2.add(edge);
                        }
                    }
                }
            }
            min = null;
        }
        ArrayList<Path> res = new ArrayList<>();
        res.add(res1);
        res.add(res2);
        return res;
    }
    
    /*public static ArrayList<Path> algoParfait2Camion(Graph graphe, APSP apsp, Node origine, int camion, Node[] courant){
        Graph graphe2 = new SingleGraph("graphe de calcul");
        for (Node node : graphe) {
            Node tmp = graphe2.addNode(node.getId());
            tmp.addAttribute("type", (String) node.getAttribute("type"));
        }
        for (Edge edge : graphe.getEachEdge()) {
            Edge tmp = graphe2.addEdge(edge.getId(), edge.getNode0().getId(), edge.getNode1().getId());
            tmp.setAttribute("duree", (int) edge.getAttribute("duree"));
        }
        courant1 = graphe2.getNode(courant1.getId());
        courant2 = graphe2.getNode(courant2.getId());
        if (origine != null && !origine.getId().equals(courant[0].getId()) && !origine.getId().equals(courant[1].getId())){
            graphe2 = supprimerNoeudSauvegardeRoute(graphe2, graphe2.getNode(origine.getId()), "duree");
        }
        ArrayList<Path> res = new ArrayList<>();
        res.add(new Path());
        res.add(new Path());
        res.get(0).setRoot(courant[0]);
        res.get(1).setRoot(courant[1]);
        
        
        if(graphe2.getNodeCount() == 3 || (graphe2.getNodeCount() == 2 && courant[1] == courant[0])){
            res.add(courant.getEdge(0).getOpposite(courant));
        }
        else{
            Path min = null;
            Path concurent;
            for (Edge edge : courant.getEachEdge()) {
                concurent = algoParfait(graphe2, apsp, courant, edge.getOpposite(courant));
                if (min == null || calculerTotalPath(min, "duree") > calculerTotalPath(concurent, "duree")){
                    min = concurent;
                }
            }
            for (Edge e : min.getEdgePath()) {
                res.add(graphe.getEdge(e.getId()));
            }
        }
        return res;
    }*/
}
