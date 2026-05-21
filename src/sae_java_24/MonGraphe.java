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
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.graphstream.algorithm.Dijkstra;

/**
 *
 * @author kenzo
 */
public class MonGraphe{
    //Q1
    public static Graph construireGrapheAleatoire(int nbSommets){
        Graph graphe = new SingleGraph("Graphe de "+nbSommets+" éléments.");
        Random rng = new Random();
        String[] TYPES = {"Maternite", "Maternite", "Maternite","Bloc operatoire","Centre nutrition"}; // maternité 3/5 et les deux autres 1/5 
        for(int i = 0; i<nbSommets ; i++){
             Node n = graphe.addNode("Lieu_" + i);
            String type = TYPES[rng.nextInt(5)];
            n.setAttribute("type", type);
            n.setAttribute("ui.label", "Lieu_" + i + " (" + type + ")");
        }
        int edgeId = 0;
        for (int i = 0; i < nbSommets; i++) {
            for (int j = i + 1; j < nbSommets; j++) {
                if (rng.nextInt(5) == 0) {
                    Edge e = graphe.addEdge("e" + edgeId++, "Lieu_" + i, "Lieu_"+ j);
                    e.setAttribute("fiabilite",(2 + rng.nextInt(9)) /10.0);
                    e.setAttribute("distance", 10 + rng.nextInt(41));
                    e.setAttribute("duree", 10 + rng.nextInt(111));
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
            if (line.startsWith("//") || line.isEmpty()) 
                continue;

            String[] linespace = line.split(" ");
            String linewithoutspace = "";
            for (String ind : linespace) {
                linewithoutspace = linewithoutspace + ind;
            }
            
            
            String[] cols = linewithoutspace.split(";");
            String nomSommet = cols[0];
            String typeCode  = cols[1];

            String type;
            if      (typeCode.equals("M")) 
                type = "Maternite";
            else if (typeCode.equals("O")) 
                type = "Bloc operatoire";
            else                           
                type = "Centre nutrition";

            Node n = g.addNode(nomSommet);
            n.setAttribute("type", type);
            n.setAttribute("ui.label", nomSommet + " (" + type + ")");

            lignes.add(cols);
    }
        sc.close();
        int edgeId = 0;
        for (int i = 0; i < lignes.size(); i++) {
            String[] cols = lignes.get(i);
            String src = cols[0];

            for (int j = 0; j < lignes.size(); j++) {
                if (j == i) 
                    continue;
                String dst = lignes.get(j)[0];
                String cellule = cols[j + 2];

                if (cellule.isEmpty() || cellule.equals("0")) 
                    continue;

                String[] vals = cellule.split(",");
                double fiabilite = Integer.parseInt(vals[0]) / 10.0;
                int    distance = Integer.parseInt(vals[1]);
                int    duree  = Integer.parseInt(vals[2]);

                if (i < j) {
                    Edge e = g.addEdge("e" + edgeId++, src, dst);
                    e.setAttribute("fiabilite",fiabilite);
                    e.setAttribute("distance",distance);
                    e.setAttribute("duree", duree);
            }
        }
    }
    return g;

}
    public static void calculerItineraires(Graph graphe, String depart, String arrivee) {
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
    for (Edge e : graphe.getEachEdge()) {{
        double fiab  = (double) e.getAttribute("fiabilite");
        int    duree = (int) e.getAttribute("duree");
        if (fiab < 0.25) {
            e.setAttribute("dureeEstimee", Double.MAX_VALUE); // route interdite
        } else {
            e.setAttribute("dureeEstimee", duree * (20 - fiab * 10) / 10.0);
        }
    }}

    //  chemin 2 plus sûr et plus rapide 
    Dijkstra dijkstra2 = new Dijkstra(Dijkstra.Element.EDGE, null, "dureeEstimee");
    dijkstra2.init(graphe);
    dijkstra2.setSource(graphe.getNode(depart));
    dijkstra2.compute();
    i++;
    
    System.out.println("Chemin le plus sûr et rapide : "+i);
    System.out.println(dijkstra2.getPath(graphe.getNode(arrivee)));
    System.out.println("Durée estimée totale : " + dijkstra2.getPathLength(graphe.getNode(arrivee)) + " min");
    }
    
    public void plusCourteTournee(Graph graphe, int nbCamions, String typeDispensaire){
        if (!typeDispensaire.equals("all")){
            for (Node n : graphe){
                if(typeDispensaire != n.getAttribute("type")){
                    graphe.removeNode(n);
                }
            }
        }
        Node n = graphe.getNode(0);
        for(Edge e : n.getEachEdge()){
            
        }
        
    }
}

