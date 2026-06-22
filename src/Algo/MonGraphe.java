/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Algo;

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
 * Classe utilitaire regroupant toutes les fonctions liées au graphe du district
 * : construction aléatoire, chargement depuis un fichier CSV, calcul
 * d'itinéraires (chemin le plus court / le plus sûr) et calcul de tournées pour
 * un ou deux camions (algorithmes glouton et parfait).
 *
 * @author kenzo
 */
public class MonGraphe {

    /**
     * Compteur global utilisé pour générer des identifiants uniques d'arêtes.
     */
    private static int edgeId = 0;

    /**
     * Meilleur coût trouvé par l'algorithme parfait (tournée optimale).
     */
    private static int meilleurCout;

    /**
     * Meilleur ordre de passage trouvé par l'algorithme parfait.
     */
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


   //Q2 

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
     * Calcule le chemin le plus court en durée minimale entre deux sommets, en
     * utilisant l'algorithme de Dijkstra sur l'attribut "duree" des arêtes.
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
     * Calcule le chemin le plus sûr et rapide entre deux sommets. La durée de
     * chaque arête est pondérée par sa fiabilité selon la formule dureeEstimee
     * = duree * (20 - fiabilite*10) / 10. Les routes dont la fiabilité est
     * inférieure à 0.25 sont considérées comme infranchissables (durée
     * infinie).
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

    /**
     * Trouve un ou deux chemins passant par tous les points du type demandé, en
     * minimisant la durée totale. Construit une copie du graphe filtrée selon
     * le type de dispensaire choisi, calcule les plus courts chemins entre
     * toutes les paires de sommets (APSP), puis applique l'algorithme glouton
     * ou parfait selon le nombre de camions et le choix de l'utilisateur.
     *
     * @param graphe le graphe
     * @param nbCamions le nombre de camion à notre disposition
     * @param typeDispensaire le ou les types de dispensaires à visiter
     * @param algo le type d'algorithme à éxécuter
     * @return Path le chemin de la tournée
     */
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
        Path resMauvaisGraphe2 = null;
        if (nbCamions == 1) {
            if (algo.equals("glouton")) {
                resMauvaisGraphe1 = algoGlouton(graphe2, apsp);
            } else {
                resMauvaisGraphe1 = algoParfait(graphe2, apsp);
            }
        } else {
            ArrayList<Path> temp;
            if (algo.equals("glouton")) {
                temp = algoGlouton2Camions(graphe2, apsp);
                resMauvaisGraphe1 = temp.get(0);
                resMauvaisGraphe2 = temp.get(1);
            } else {
                //temp = algoParfait2Camions(graphe2, apsp);
                resMauvaisGraphe1 = null;
                resMauvaisGraphe2 = null;
            }

        }
        ArrayList<Path> res = new ArrayList<>();
        res.add(new Path());
        res.add(new Path());
        res.get(0).setRoot(graphe.getNode(resMauvaisGraphe1.getRoot().getId()));
        for (Edge e : resMauvaisGraphe1.getEdgePath()) {
            res.get(0).add(graphe.getEdge(e.getId()));
        }
        if (resMauvaisGraphe2 != null) {
            res.get(1).setRoot(graphe.getNode(resMauvaisGraphe2.getRoot().getId()));
            for (Edge e : resMauvaisGraphe2.getEdgePath()) {
                res.get(1).add(graphe.getEdge(e.getId()));
            }
        }

        return res;
    }

    /**
     * Algorithme glouton calculant une tournée pour un seul camion. À chaque
     * étape, le sommet non visité le plus proche (selon l'APSP) est ajouté à la
     * tournée, jusqu'à ce que tous les sommets aient été visités.
     *
     * @param graphe le graphe
     * @param apsp la liste de tout les plus courts chemin du graphe
     * @return Path le chemin de la tournée
     */
    public static Path algoGlouton(Graph graphe, APSP apsp) {
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
            if (min != null) {
                for (Edge edge : min.getEdgePath()) {
                    if (edge.getAttribute("route") != null) {
                        List< Edge> LstTemp = ((Path) edge.getAttribute("route")).getEdgePath();
                        if (res.peekNode() != LstTemp.get(0).getNode0() && res.peekNode() != LstTemp.get(0).getNode1()) {
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge edge2 : LstTemp) {
                            res.add(edge2);
                        }
                    } else {
                        res.add(edge);
                    }
                }
            }
            min = null;
            nodeMin = null;
        }
        return res;
    }

    /**
     * calcule la somme des valeurs d'un attribut des arrêtes d'un Path
     *
     * @param chemin le chemin
     * @param attribut l'attribut
     * @return int la somme
     */
    public static int calculerTotalPath(Path chemin, String attribut) {
        int total = 0;
        if (chemin != null) {
            for (Edge edge : chemin.getEachEdge()) {
                total += (int) edge.getAttribute(attribut);
            }
        }
        return total;
    }

    /**
     * supprime un noeud en sauvegardant les arrêtes du noeud supprimé la
     * fonction crée des arrêtes, ou remplace celle déjà existantes si le chemin
     * est plus court au niveau d'un attribut, pour contourner le sommet
     * supprimé et sauvegardant la route emprunté dans un attribut route
     *
     * @param g le graphe
     * @param node le noeud à supprimer
     * @param attribut l'attribut
     * @return Graph le graphe sans le noeud
     */
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
                    if (etemp.getAttribute("route") == null) {
                        temp.add(etemp);
                    } else {
                        LstTemp = ((Path) etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()) {
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    etemp = node.getEdgeBetween(node3);
                    if (etemp.getAttribute("route") == null) {
                        temp.add(etemp);
                    } else {
                        LstTemp = ((Path) etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()) {
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    g.getEdge("e" + edgeId++).setAttribute("route", temp);
                    temp = new Path();
                } else if ((int) node2.getEdgeBetween(node).getAttribute(attribut)
                        + (int) node3.getEdgeBetween(node).getAttribute(attribut)
                        < (int) node3.getEdgeBetween(node2).getAttribute(attribut)) {
                    node2.getEdgeBetween(node3).setAttribute(attribut, (int) node2.getEdgeBetween(node).getAttribute(attribut)
                            + (int) node3.getEdgeBetween(node).getAttribute(attribut));
                    temp.setRoot(node2);
                    etemp = node.getEdgeBetween(node2);
                    if (etemp.getAttribute("route") == null) {
                        temp.add(etemp);
                    } else {
                        LstTemp = ((Path) etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()) {
                            LstTemp = LstTemp.reversed();
                        }
                        for (Edge e : LstTemp) {
                            temp.add(e);
                        }
                    }
                    etemp = node.getEdgeBetween(node3);
                    if (etemp.getAttribute("route") == null) {
                        temp.add(etemp);
                    } else {
                        LstTemp = ((Path) etemp.getAttribute("route")).getEdgePath();
                        if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()) {
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

    /**
     * algorithme trouvant la solution optimale pour la tournée
     *
     * @param graphe le graphe
     * @param apsp la liste de tout les plus courts chemin du graphe
     * @return Path le chemin optimal de la tournée
     */
    public static Path algoParfait(Graph graphe, APSP apsp) {
        meilleurCout = Integer.MAX_VALUE;
        meilleurOrdre = null;
        ArrayList<Node> res = new ArrayList<>();
        Node depart = graphe.getNodeIterator().next();
        res.add(depart);
        ArrayList<Node> nonVisites = new ArrayList<>();
        for (Node n : graphe) {
            if (n != depart) {
                nonVisites.add(n);
            }
        }
        parfaitRecursif(depart, res, nonVisites, 0);
        return listeEnPath(meilleurOrdre);
    }

    /**
     * partie récursive de l'algorithme optimal
     *
     * @param courant le graphe
     * @param res la liste de tout les plus courts chemin du graphe
     * @param nonVisites les sommets pas encore visités
     * @param cout le cout de la partie déjà visitée
     */
    private static void parfaitRecursif(Node courant, ArrayList<Node> res, ArrayList<Node> nonVisites, int cout) {
        if (cout <= meilleurCout) {
            if (nonVisites.isEmpty()) {
                meilleurCout = cout;
                meilleurOrdre = new ArrayList<>(res);
            } else {
                APSPInfo info = courant.getAttribute(APSPInfo.ATTRIBUTE_NAME);
                for (Node suivant : new ArrayList<>(nonVisites)) {
                    Path p = info.getShortestPathTo(suivant.getId());
                    res.add(suivant);
                    nonVisites.remove(suivant);
                    parfaitRecursif(suivant, res, nonVisites, cout + calculerTotalPath(p, "duree"));
                    res.remove(res.size() - 1);
                    nonVisites.add(suivant);
                }
            }
        }
    }

    /**
     * transforme une liste de noeuds en Path
     *
     * @param lst une liste de noeuds
     * @return Path le chemin correspondant à la liste de noeuds
     */
    private static Path listeEnPath(ArrayList<Node> lst) {
        Path resultat = new Path();
        if (lst == null || lst.isEmpty()) {
            return resultat;
        }
        resultat.setRoot(lst.get(0));
        for (int i = 0; i < lst.size() - 1; i++) {
            APSPInfo info = lst.get(i).getAttribute(APSPInfo.ATTRIBUTE_NAME);
            Path temp = info.getShortestPathTo(lst.get(i + 1).getId());
            for (Edge e : temp.getEdgePath()) {
                if (e.getAttribute("route") != null) {
                    List<Edge> LstTemp = ((Path) e.getAttribute("route")).getEdgePath();
                    if (temp.peekNode() != LstTemp.get(0).getNode0() && temp.peekNode() != LstTemp.get(0).getNode1()) {
                        LstTemp = LstTemp.reversed();
                    }
                    for (Edge edge : LstTemp) {
                        temp.add(edge);
                    }
                } else {
                    resultat.add(e);
                }
            }
        }

        return resultat;
    }

    /**
     * Algorithme glouton calculant une tournée pour deux camions. À chaque
     * étape, on compare le coût cumulé de chaque camion si on lui ajoute son
     * meilleur prochain sommet (selon l'APSP), et on fait avancer le camion
     * dont le coût total resterait le plus faible. Le processus se répète
     * jusqu'à ce que tous les sommets aient été visités par l'un des deux
     * camions.
     *
     * @param graphe le graphe
     * @param apsp la liste de tout les plus courts chemin du graphe
     * @return ArrayList contenant les deux chemins (Path) des deux camions
     */
    public static ArrayList<Path> algoGlouton2Camions(Graph graphe, APSP apsp) {
        ArrayList<Node> nonVerifie = new ArrayList<Node>();
        for (Node node : graphe.getEachNode()) {
            nonVerifie.add(node);
        }
        APSPInfo info;
        Path min = null;
        Path min1 = null;
        Path min2 = null;
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
            if (nodeMin1 == null) {
                nonVerifie.remove(courant1);
            } else {
                nonVerifie.remove(courant2);
            }
            info = courant1.getAttribute(APSPInfo.ATTRIBUTE_NAME);
            for (Node n : nonVerifie) {
                temp = info.getShortestPathTo(n.toString());
                if (min1 == null || calculerTotalPath(temp, "duree") < calculerTotalPath(min1, "duree")) {
                    nodeMin1 = n;
                    min1 = temp;
                }
            }
            info = courant2.getAttribute(APSPInfo.ATTRIBUTE_NAME);
            for (Node n : nonVerifie) {
                temp = info.getShortestPathTo(n.toString());
                if (min2 == null || calculerTotalPath(temp, "duree") < calculerTotalPath(min2, "duree")) {
                    nodeMin2 = n;
                    min2 = temp;
                }
            }
            cond = calculerTotalPath(res1, "duree") + calculerTotalPath(min1, "duree") < calculerTotalPath(res2, "duree") + calculerTotalPath(min2, "duree");
            if (cond) {
                courant1 = nodeMin1;
                nodeMin1 = null;
                min = min1;
            } else {
                courant2 = nodeMin2;
                nodeMin2 = null;
                min = min2;
            }
            min1 = null;
            min2 = null;
            if (min != null) {
                if (cond) {
                    for (Edge edge : min.getEdgePath()) {
                        if (edge.getAttribute("route") != null) {
                            for (Edge edge2 : ((Path) edge.getAttribute("route")).getEdgePath()) {
                                res1.add(edge2);
                            }
                        } else {
                            res1.add(edge);
                        }
                    }
                } else {
                    for (Edge edge : min.getEdgePath()) {
                        if (edge.getAttribute("route") != null) {
                            for (Edge edge2 : ((Path) edge.getAttribute("route")).getEdgePath()) {
                                res2.add(edge2);
                            }
                        } else {
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

}
