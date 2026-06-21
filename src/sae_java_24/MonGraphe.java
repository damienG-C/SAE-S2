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
    private static double meilleurCout;
    private static ArrayList<Node> meilleurOrdre;

    //Q1

    /**
     * Construit un graphe aléatoire contenant un nombre donné de sommets. Les
     * sommets sont reliés de manière à garantir un graphe connexe.
     *
     * @param nbSommets nombre de sommets à créer
     * @return graphe généré aléatoirement
     */
    public static Graph construireGrapheAleatoire(int nbSommets) {
        Graph graphe = new SingleGraph("Graphe de " + nbSommets + " éléments.");
        Random rng = new Random();

        creerSommetsEtAretesAleatoires(graphe, nbSommets, rng);

        return graphe;
    }

    /**
     * Crée les sommets et les arêtes du graphe de manière aléatoire. Chaque
     * sommet reçoit un type et un label, et des arêtes sont ajoutées pour
     * garantir la connexité du graphe.
     *
     * @param graphe le graphe à construire
     * @param nbSommets nombre total de sommets
     * @param rng générateur aléatoire
     */
    private static void creerSommetsEtAretesAleatoires(Graph graphe, int nbSommets, Random rng) {

        String[] TYPES = {
            "Maternite", "Maternite", "Maternite",
            "Bloc operatoire", "Centre nutrition"
        };

        edgeId = 0;

        for (int i = 0; i < nbSommets; i++) {

            Node n = graphe.addNode("Lieu_" + i);

            String type = TYPES[rng.nextInt(5)];
            n.setAttribute("type", type);
            n.setAttribute("ui.label", creerLabelSommet(i, type));

            if (graphe.getNodeCount() != 1) {
                while (n.getInDegree() == 0) {
                    for (Node n2 : graphe) {
                        if (n2 != n && rng.nextInt(5) == 0) {
                            Edge e = graphe.addEdge("e" + edgeId++, n, n2);
                            ajouterAttributsArete(e, rng);
                        }
                    }
                }
            }
        }
    }

    /**
     * Génère un label pour un sommet à partir de son identifiant et de son
     * type.
     *
     * @param id identifiant du sommet
     * @param type type du sommet
     * @return label formaté du sommet
     */
    private static String creerLabelSommet(int id, String type) {
        String abreviation = obtenirAbbreviationType(type);
        return "Lieu_" + id + " (" + abreviation + ")";
    }

    /**
     * Génère un label pour un sommet issu d'un fichier CSV.
     *
     * @param nomSommet nom du sommet
     * @param type type du sommet
     * @return label formaté
     */
    private static String creerLabelSommet(String nomSommet, String type) {
        String abreviation = obtenirAbbreviationType(type);
        return nomSommet + " (" + abreviation + ")";
    }

    /**
     * Retourne l'abréviation associée à un type de sommet.
     *
     * @param type type complet du sommet
     * @return abréviation (M, O ou N)
     */
    private static String obtenirAbbreviationType(String type) {
        if (type.equals("Maternite")) {
            return "M";
        } else if (type.equals("Bloc operatoire")) {
            return "O";
        } else {
            return "N";
        }
    }

    /**
     * Ajoute des attributs aléatoires à une arête.
     *
     * @param e arête à modifier
     * @param rng générateur aléatoire
     */
    private static void ajouterAttributsArete(Edge e, Random rng) {

        double fiabilite = (2 + rng.nextInt(9)) / 10.0;
        int distance = 10 + rng.nextInt(41);
        int duree = 10 + rng.nextInt(111);

        e.setAttribute("fiabilite", fiabilite);
        e.setAttribute("distance", distance);
        e.setAttribute("duree", duree);
    }

    /* =========================
   Q2 - CSV
   ========================= */
    /**
     * Charge un graphe depuis un fichier CSV.
     *
     * @param nom_fichier fichier source
     * @return graphe construit
     * @throws FileNotFoundException si fichier introuvable
     */
    public static Graph chargerGraphe(String nom_fichier) throws FileNotFoundException {

        Graph g = new SingleGraph("map");

        List<String[]> lignes = lireCSV(nom_fichier);
        creerSommetsCSV(g, lignes);
        creerAretesCSV(g, lignes);

        return g;
    }

    /**
     * Lit un fichier CSV et retourne les lignes valides.
     *
     * @param nom_fichier fichier CSV
     * @return liste des lignes
     * @throws FileNotFoundException si fichier absent
     */
    private static List<String[]> lireCSV(String nom_fichier) throws FileNotFoundException {

        List<String[]> lignes = new ArrayList<>();
        Scanner sc = new Scanner(new File(nom_fichier));

        while (sc.hasNextLine()) {

            String line = sc.nextLine().trim();

            boolean estCommentaire = line.startsWith("//");
            boolean estVide = line.isEmpty();

            if (!estCommentaire && !estVide) {
                String[] cols = nettoyerEtDiviserLigne(line);
                lignes.add(cols);
            }
        }

        sc.close();
        return lignes;
    }

    /**
     * Nettoie une ligne CSV et la découpe en colonnes.
     *
     * @param line ligne brute
     * @return colonnes
     */
    private static String[] nettoyerEtDiviserLigne(String line) {

        String[] linespace = line.split(" ");
        String linewithoutspace = "";

        for (String ind : linespace) {
            linewithoutspace = linewithoutspace + ind;
        }

        return linewithoutspace.split(";");
    }

    /**
     * Crée les sommets à partir du CSV.
     *
     * @param g graphe
     * @param lignes données CSV
     */
    private static void creerSommetsCSV(Graph g, List<String[]> lignes) {

        for (String[] cols : lignes) {

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
            n.setAttribute("ui.label", creerLabelSommet(nomSommet, type));
        }
    }

    /**
     * Crée les arêtes à partir du CSV.
     *
     * @param g graphe
     * @param lignes données CSV
     */
    private static void creerAretesCSV(Graph g, List<String[]> lignes) {

        edgeId = 0;

        for (int i = 0; i < lignes.size(); i++) {

            String[] cols = lignes.get(i);
            String src = cols[0];

            for (int j = i + 1; j < lignes.size(); j++) {

                String dst = lignes.get(j)[0];
                String cellule = cols[j + 2];

                boolean estVide = cellule.isEmpty();
                boolean estZero = cellule.equals("0");

                if (!estVide && !estZero) {

                    Edge e = g.addEdge("e" + edgeId++, src, dst);
                    extraireAttributsArete(e, cellule);
                }
            }
        }
    }

    /**
     * Extrait et applique les attributs d'une arête depuis une cellule CSV.
     *
     * @param e arête
     * @param cellule données "fiabilite,distance,duree"
     */
    private static void extraireAttributsArete(Edge e, String cellule) {

        String[] vals = cellule.split(",");

        double fiabilite = Integer.parseInt(vals[0]) / 10.0;
        int distance = Integer.parseInt(vals[1]);
        int duree = Integer.parseInt(vals[2]);

        e.setAttribute("fiabilite", fiabilite);
        e.setAttribute("distance", distance);
        e.setAttribute("duree", duree);
    }

    /**
     * Calcule le chemin le plus court en durée minimale.
     *
     * @param graphe le graphe
     * @param depart sommet de départ
     * @param arrivee sommet d'arrivée
     * @return List contenant [Path, Double duree]
     */
    public static List<Object> calculerItinerairePlusCourt(Graph graphe, String depart, String arrivee) {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "duree");
        dijkstra.init(graphe);
        dijkstra.setSource(graphe.getNode(depart));
        dijkstra.compute();

        Path chemin = dijkstra.getPath(graphe.getNode(arrivee));
        double duree = dijkstra.getPathLength(graphe.getNode(arrivee));

        List<Object> resultat = new ArrayList<>();
        resultat.add(chemin);
        resultat.add(duree);
        System.out.println(chemin + " " + duree);
        return resultat;
    }

    /**
     * Calcule le chemin le plus sûr et rapide.
     *
     * @param graphe le graphe
     * @param depart sommet de départ
     * @param arrivee sommet d'arrivée
     * @return List contenant [Path, Double duree]
     */
    public static List<Object> calculerItineraireSecurise(Graph graphe, String depart, String arrivee) {
        double dureeEstimee;
        // Préparer les attributs dureeEstimee
        for (Edge edge : graphe.getEachEdge()) {

            double fiabilite = (double) edge.getAttribute("fiabilite");
            int duree = (int) edge.getAttribute("duree");

            if (fiabilite < 0.25) {
                dureeEstimee = Double.MAX_VALUE;
            } else {
                dureeEstimee = duree * (20 - fiabilite * 10) / 10.0;

            }
            edge.setAttribute("dureeEstimee", dureeEstimee);
        }

        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "dureeEstimee");
        dijkstra.init(graphe);
        dijkstra.setSource(graphe.getNode(depart));
        dijkstra.compute();

        Path chemin = dijkstra.getPath(graphe.getNode(arrivee));
        double duree = dijkstra.getPathLength(graphe.getNode(arrivee));

        List<Object> resultat = new ArrayList<>();
        resultat.add(chemin);
        resultat.add(duree);

        System.out.println(chemin + " " + duree);
        return resultat;
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
        if (!typeDispensaire.equals("tous")) {
            ArrayList<Node> aSupprimer = new ArrayList<>();
            for (Node n : graphe2) {
                if (!typeDispensaire.equals(n.getAttribute("type"))) {
                    aSupprimer.add(n);
                }
            }
            for (Node n : aSupprimer) {
                graphe2 = supprimerNoeudSauvegardeRoute(graphe2, n, "duree");
            }
        }
        APSP apsp = new APSP();
        apsp.init(graphe2);
        apsp.setDirected(false);
        apsp.setWeightAttributeName("duree");
        apsp.compute();
        Path resMauvaisGraphe1;
        if (nbCamions == 1){
            if(algo.equals("glouton")){
                resMauvaisGraphe1 = algoGlouton(graphe2, apsp);
            }
            else{
                resMauvaisGraphe1 = algoParfait(graphe2, apsp);
            }
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
        res.setRoot(graphe.getNodeIterator().next());
        Path temp;
        Node nodeMin = null;
        Node courant = graphe.getNodeIterator().next();
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
                        List< Edge> LstTemp = ((Path)edge.getAttribute("route")).getEdgePath();
                        if (res.peekNode() != LstTemp.get(0).getNode0() && res.peekNode() != LstTemp.get(0).getNode1()){
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge edge2 : LstTemp) {
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
        Edge etemp;
        List<Edge> LstTemp;
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
                    etemp = node.getEdgeBetween(node2);
                    if (etemp.getAttribute("route") == null){
                        temp.add(etemp);
                    }
                    else{
                        LstTemp = ((Path)etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()){
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    etemp = node.getEdgeBetween(node3);
                    if (etemp.getAttribute("route") == null){
                        temp.add(etemp);
                    }
                    else{
                        LstTemp = ((Path)etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()){
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    g.getEdge("e" + edgeId++).setAttribute("route", temp);
                    temp = new Path();
                }
                else if((int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut)
                        < (int) node3.getEdgeBetween(node2).getAttribute(attribut)){
                    node2.getEdgeBetween(node3).setAttribute(attribut, (int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut));
                    temp.setRoot(node2);
                    etemp = node.getEdgeBetween(node2);
                    if (etemp.getAttribute("route") == null){
                        temp.add(etemp);
                    }
                    else{
                        LstTemp = ((Path)etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()){
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    etemp = node.getEdgeBetween(node3);
                    if (etemp.getAttribute("route") == null){
                        temp.add(etemp);
                    }
                    else{
                        LstTemp = ((Path)etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()){
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    node2.getEdgeBetween(node3).setAttribute("route", temp);
                    temp = new Path();
                }
            }
        }
        g.removeNode(node);
        return g;
    }
    
    public static Path algoParfait(Graph graphe, APSP apsp){
        meilleurCout = Double.MAX_VALUE;
        meilleurOrdre = null;
        ArrayList<Node> res = new ArrayList<>();
        Node depart = graphe.getNodeIterator().next();
        res.add(depart);
        ArrayList<Node> nonVisites = new ArrayList<>();
        for(Node n : graphe) {
            if(n != depart) {
                nonVisites.add(n);
            }
        }
        parfaitRecursif(depart, res, nonVisites, 0);
        return listeEnPath(meilleurOrdre);
    }
    
    private static void parfaitRecursif(Node courant, ArrayList<Node> res, ArrayList<Node> nonVisites, int cout){
        if(cout <= meilleurCout) {
            if(nonVisites.isEmpty()) {
                meilleurCout = cout;
                meilleurOrdre = new ArrayList<>(res);
            }
            else{
                APSPInfo info = courant.getAttribute(APSPInfo.ATTRIBUTE_NAME);
                for(Node suivant : new ArrayList<>(nonVisites)) {
                    Path p = info.getShortestPathTo(suivant.getId());
                    res.add(suivant);
                    nonVisites.remove(suivant);
                    parfaitRecursif(suivant, res, nonVisites, cout + calculerTotalPath(p, "duree"));
                    res.remove(res.size()-1);
                    nonVisites.add(suivant);
                }
            }
        }
    }
    
        private static Path listeEnPath(ArrayList<Node> res){
        Path resultat = new Path();
        if(res == null || res.isEmpty()) {
            return resultat;
        }
        resultat.setRoot(res.get(0));
        for(int i = 0 ; i < res.size()-1 ; i++) {
            APSPInfo info = res.get(i).getAttribute(APSPInfo.ATTRIBUTE_NAME);
            Path morceau = info.getShortestPathTo(res.get(i+1).getId());
            for(Edge e : morceau.getEdgePath()) {
                resultat.add(e);
            }
        }

    return resultat;
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
        res1.setRoot(graphe.getNodeIterator().next());
        Path res2 = new Path();
        res2.setRoot(graphe.getNodeIterator().next());
        Path temp;
        Node nodeMin1 = null;
        Node nodeMin2 = null;
        Node courant1 = graphe.getNodeIterator().next();
        Node courant2 = graphe.getNodeIterator().next();
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
