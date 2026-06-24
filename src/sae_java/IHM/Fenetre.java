/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import Algo.MonGraphe;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Fenetre est la fenetre principale de l'application.
 * <p>
 * Elle affiche un graphe (GraphStream) representant un reseau de dispensaires
 * (maternites, blocs operatoires, centres de nutrition) et propose des outils
 * pour :
 * <ul>
 * <li>charger un graphe depuis un fichier ou en generer un aleatoirement ;</li>
 * <li>calculer le plus court chemin et le chemin le plus fiable entre deux
 * sommets ;</li>
 * <li>calculer une tournee de livraison ;</li>
 * <li>filtrer les sommets par type ;</li>
 * <li>mettre en evidence les aretes selon leur fiabilite.</li>
 * </ul>
 * Le panneau principal {@code pan} utilise un GridBagLayout. La fenetre
 * implemente {@link MouseListener} pour reagir aux clics sur les sommets du
 * graphe et ouvrir une {@link FenetreDetails}.
 *
 * @author p2501262
 */
public class Fenetre extends JFrame implements MouseListener {

    /**
     * Panneaux de l'interface : panneau principal, barre d'outils, zone graphe
     * et sous-panneaux.
     */
    JPanel pan, panoutil, panGraph, panLabStat, panChangeGraph, panFiltre, panChemin, panTournee, panFiabilite;
    /**
     * Boutons : ouverture (croix), calcul plus court chemin, calcul tournee,
     * affichage fiabilite.
     */
    JButton butcroix, butCourtChemin, butCourtTournee, butFiabilité;
    /**
     * Selecteurs de sommets de depart et d'arrivee pour le calcul d'itineraire.
     */
    JComboBox<Node> selecteur1, selecteur2;
    /**
     * Selecteurs de type d'algorithme et de type de dispensaire.
     */
    JComboBox<String> selecteurAlgo, selecteurType;
    /**
     * Selecteurs du nombre de camions et des bornes inferieure/superieure de
     * fiabilite.
     */
    JComboBox<Integer> selecteurNb, ecartInfFiabilité, ecartSupFiabilité;
    /**
     * Labels d'affichage des statistiques, des sommets, des tournees et des
     * chemins.
     */
    JLabel labNB1, labNB2, labNB3, labGraph, labSommet1, labSommet2, labTour1, labTour2, labTour3, labFiabilite,
            labTourneeCheminCam1, labStatSeparationFenCam1, labDureeTourneeCam1,
            labStatSeparationFenCam2, labTourneeCheminCam2, labDureeTourneeCam2,
            labStatSeparationFenCheminCourt, labStatSeparationFenCheminFiable, labDureeCheminCourt, labCheminCourt,
            labDureeCheminFiable, labCheminFiable;
    /**
     * Listes affichant le filtre et les chemins calcules (tournees et
     * itineraires).
     */
    JList<String> lstFiltre, lstNodePathTournee, lstTourneeCheminCam1, lstTourneeCheminCam2, lstCheminCourt, lstCheminFiable;
    /**
     * Conteneurs defilables associes aux listes et a la barre d'outils.
     */
    JScrollPane jsp, ScrollPaneOutil, ScrollListTourneeCam1, ScrollListTourneeCam2, ScrollListCheminCourt, ScrollListCheminFiable;
    /**
     * Modeles de donnees des differentes JList.
     */
    DefaultListModel<String> lstmodel, lstTourneeModelCam1, lstTourneeModelCam2, lstCheminModelCourt, lstCheminModelFiable;
    /**
     * Graphe actuellement affiche et manipule par l'application.
     */
    Graph graphActuel;
    /**
     * Vue GraphStream du graphe, integree dans le panneau {@code panGraph}.
     */
    DefaultView view;
    
    String enregistrement1 = "nomFichier, algo, duree, chemin";
    String enregistrement2 = "nomFichier, algo, duree1, duree2, chemins";
    
    /**
     * Construit la fenetre principale : initialise tous les composants,
     * positionne le panneau principal, ajuste la taille a son contenu, centre
     * la fenetre et l'affiche.
     */
    public Fenetre() {
        // Attribution d'un titre à la fenêtre.
        super("MonAppli");

        // Ferme la fenêtre lorsqu'on clique sur la croix de la fenêtre.
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Création de tout les composants de la fenêtre (Panneaux + Labels + Buttons etc).
        initComponent();

        // Remplacement du contentPane par pan (panneau principal).
        this.setContentPane(pan);

        // Redimension de la fenêtre pour s'adapter à tout les composants du panneau principal.
        this.pack();

        // Positionne la fenêtre en "null".
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Initialise et positionne les composants de premier niveau de la fenetre.
     * <p>
     * Cree le panneau principal {@code pan} en GridBagLayout, appelle les
     * methodes d'initialisation des sous-panneaux ({@code initPanOutil},
     * {@code initPanGraphe}, {@code initPanChangeGraph}, {@code initEcouteur})
     * puis place la barre d'outils, la zone de graphe et la barre de titre.
     */
    public void initComponent() {
        pan = new JPanel();
        panoutil = new JPanel();
        ScrollPaneOutil = new JScrollPane(panoutil);
        panGraph = new JPanel();
        panLabStat = new JPanel();
        panChangeGraph = new JPanel();
        panFiltre = new JPanel();
        panTournee = new JPanel();
        panFiabilite = new JPanel();

        // Changement de couleur du pan (panneau principal).
        pan.setBackground(Color.decode("#1a2b3c"));

        pan.setLayout(new GridBagLayout());
        GridBagConstraints gcpan = new GridBagConstraints();

        // Initialisation de tout les panneaux pour les positionner.
        initPanOutil();
        initPanGraphe();
        initPanChangeGraph();
        initEcouteur();

        // Espacement réalisé sur tout les composants : Top / Left / Bottom / Right.
        gcpan.insets = new Insets(10, 5, 3, 5);

        // Positionnement du panGraph.
        gcpan.gridx = 1;
        gcpan.gridy = 1;
        gcpan.fill = GridBagConstraints.HORIZONTAL;
        gcpan.fill = GridBagConstraints.VERTICAL;
        pan.add(panGraph, gcpan);

        // Positionnement du panChangeGraph.
        gcpan.gridy = 0;
        gcpan.anchor = GridBagConstraints.WEST;
        pan.add(panChangeGraph, gcpan);
        gcpan.anchor = GridBagConstraints.CENTER;

        // Positionnement du panoutil.
        gcpan.gridx = 0;
        gcpan.gridy = 0;
        gcpan.gridheight = 2;
        pan.add(ScrollPaneOutil, gcpan);
    }

    /**
     * Initialise le panneau de fiabilite.
     * <p>
     * Cree les bornes inferieure et superieure (valeurs de 0 a 10), le label
     * "Seuil" et le bouton "Montrer fiabilite", puis les positionne en
     * GridBagLayout. Ce panneau permet de mettre en evidence les aretes dont la
     * fiabilite est comprise entre les deux bornes choisies.
     */
    public void initPanFiabilite() {
        panFiabilite = new JPanel();
        labFiabilite = new JLabel("Seuil");
        ecartInfFiabilité = new JComboBox<>();
        ecartSupFiabilité = new JComboBox<>();
        butFiabilité = new JButton("Montrer fiabilité");

        panFiabilite.setVisible(true);
        panFiabilite.setBackground(Color.decode("#122438"));
        panFiabilite.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        panFiabilite.setLayout(new GridBagLayout());

        setStyleBouton(butFiabilité);

        labFiabilite.setForeground(Color.white);

        // Remplissage des deux selecteurs de bornes avec les valeurs 0 a 10.
        for (int i = 0; i <= 10; i++) {
            ecartInfFiabilité.addItem(i);
            ecartSupFiabilité.addItem(i);
        }

        GridBagConstraints gcpanFiabilite = new GridBagConstraints();

        gcpanFiabilite.fill = GridBagConstraints.HORIZONTAL;
        gcpanFiabilite.weightx = 1.0;
        gcpanFiabilite.gridx = 0;
        gcpanFiabilite.gridy = 0;
        gcpanFiabilite.gridwidth = 2;
        panFiabilite.add(labFiabilite, gcpanFiabilite);
        gcpanFiabilite.gridwidth = 1;

        gcpanFiabilite.insets = new Insets(5, 2, 5, 2);
        gcpanFiabilite.gridy = 1;
        panFiabilite.add(ecartInfFiabilité, gcpanFiabilite);

        gcpanFiabilite.gridx = 1;
        panFiabilite.add(ecartSupFiabilité, gcpanFiabilite);

        gcpanFiabilite.gridx = 0;
        gcpanFiabilite.gridy = 2;
        gcpanFiabilite.gridwidth = 2;
        panFiabilite.add(butFiabilité, gcpanFiabilite);
        gcpanFiabilite.gridwidth = 1;
    }

    /**
     * Initialise le panneau de tournee de livraison.
     * <p>
     * Cree les selecteurs (type de dispensaire, nombre de camions, type
     * d'algorithme), le bouton de calcul et les zones d'affichage des
     * statistiques pour le camion 1 et le camion 2 (chemin parcouru et duree).
     * L'ensemble est dispose en GridBagLayout.
     */
    public void initPanTournee() {
        panTournee = new JPanel();

        panTournee.setVisible(true);

        // camion 1
        lstTourneeModelCam1 = new DefaultListModel<>();
        lstTourneeCheminCam1 = new JList<>(lstTourneeModelCam1);
        ScrollListTourneeCam1 = new JScrollPane(lstTourneeCheminCam1);
        lstTourneeCheminCam1.setVisibleRowCount(1);
        labStatSeparationFenCam1 = new JLabel("Stats camion 1");
        labDureeTourneeCam1 = new JLabel("Durée de la Tournée : ");
        labTourneeCheminCam1 = new JLabel("Chemin");

        // camion 2
        lstTourneeModelCam2 = new DefaultListModel<>();
        lstTourneeCheminCam2 = new JList<>(lstTourneeModelCam2);
        ScrollListTourneeCam2 = new JScrollPane(lstTourneeCheminCam2);
        lstTourneeCheminCam2.setVisibleRowCount(1);
        labStatSeparationFenCam2 = new JLabel("Stats camion 2");
        labDureeTourneeCam2 = new JLabel("Durée de la Tournée : ");
        labTourneeCheminCam2 = new JLabel("Chemin");

        labTour1 = new JLabel("Type dispensaire");
        labTour2 = new JLabel("Nombre de camions");
        labTour3 = new JLabel("Type d'algorithme");
        butCourtTournee = new JButton("Calculer l'itinéraire");
        selecteurType = new JComboBox<>();
        selecteurNb = new JComboBox<>();
        selecteurAlgo = new JComboBox<>();

        // Remplissage du selecteur de type de dispensaire.
        selecteurType.addItem("tous");
        selecteurType.addItem("Maternite");
        selecteurType.addItem("Bloc operatoire");
        selecteurType.addItem("Centre nutrition");

        // Remplissage du selecteur de nombre de camions.
        selecteurNb.addItem(1);
        selecteurNb.addItem(2);

        // Remplissage du selecteur d'algorithme.
        selecteurAlgo.addItem("glouton");
        selecteurAlgo.addItem("Xparfait puis glouton");
        selecteurAlgo.addItem("parfait");

        labTourneeCheminCam1.setForeground(Color.white);
        labTourneeCheminCam2.setForeground(Color.white);
        labDureeTourneeCam1.setForeground(Color.white);
        labDureeTourneeCam2.setForeground(Color.white);
        labTour1.setForeground(Color.white);
        labTour2.setForeground(Color.white);
        labTour3.setForeground(Color.white);
        labStatSeparationFenCam1.setForeground(Color.white);
        labStatSeparationFenCam2.setForeground(Color.white);

        labStatSeparationFenCam1.setBorder(BorderFactory.createLineBorder(Color.white));
        labStatSeparationFenCam2.setBorder(BorderFactory.createLineBorder(Color.white));

        panTournee.setBackground(Color.decode("#122438"));
        panTournee.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        setStyleBouton(butCourtTournee);

        // Cache les stats camions 2.
        labDureeTourneeCam2.setVisible(false);
        labStatSeparationFenCam2.setVisible(false);
        ScrollListTourneeCam2.setVisible(false);
        labTourneeCheminCam2.setVisible(false);

        panTournee.setLayout(new GridBagLayout());
        GridBagConstraints gcpanTournee = new GridBagConstraints();

        gcpanTournee.fill = GridBagConstraints.HORIZONTAL;
        gcpanTournee.weightx = 1.0;
        gcpanTournee.insets = new Insets(2, 6, 2, 6);
        gcpanTournee.gridx = 0;
        gcpanTournee.gridy = 0;
        panTournee.add(labTour1, gcpanTournee);

        gcpanTournee.gridy = 1;
        panTournee.add(selecteurType, gcpanTournee);

        gcpanTournee.gridy = 2;
        panTournee.add(labTour2, gcpanTournee);

        gcpanTournee.gridy = 3;
        panTournee.add(selecteurNb, gcpanTournee);

        gcpanTournee.gridy = 4;
        panTournee.add(labTour3, gcpanTournee);

        gcpanTournee.gridy = 5;
        gcpanTournee.insets = new Insets(2, 6, 0, 6);
        panTournee.add(selecteurAlgo, gcpanTournee);

        gcpanTournee.gridx = 0;
        gcpanTournee.gridy = 6;
        gcpanTournee.gridwidth = 3;
        gcpanTournee.insets = new Insets(0, 6, 0, 6);
        panTournee.add(butCourtTournee, gcpanTournee);
        gcpanTournee.gridwidth = 1;

        // camion 1 Stat placement
        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(10, 6, 0, 6);
        panTournee.add(labStatSeparationFenCam1, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(3, 6, 3, 6);
        panTournee.add(labTourneeCheminCam1, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(0, 6, 0, 6);
        panTournee.add(ScrollListTourneeCam1, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(6, 6, 6, 6);
        panTournee.add(labDureeTourneeCam1, gcpanTournee);

        // camion 2 Stat placement
        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(10, 6, 0, 6);
        panTournee.add(labStatSeparationFenCam2, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(3, 6, 3, 6);
        panTournee.add(labTourneeCheminCam2, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(0, 6, 0, 6);
        panTournee.add(ScrollListTourneeCam2, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(6, 6, 6, 6);
        panTournee.add(labDureeTourneeCam2, gcpanTournee);
    }

    /**
     * Initialise la barre d'outils laterale.
     * <p>
     * Place dans un JScrollPane une pile verticale (GridBagLayout) regroupant,
     * dans l'ordre : le panneau de statistiques, le panneau d'itineraire, le
     * panneau de tournee, le panneau de fiabilite et le panneau de filtre.
     */
    public void initPanOutil() {
        ScrollPaneOutil.setPreferredSize(new Dimension(220, 300));
        panoutil.setLayout(new GridBagLayout());
        panoutil.setPreferredSize(new Dimension(200, 950)); // Ne pas retirer sinon ça fait un bug

        // Changement de couleur du fond de panoutil.
        panoutil.setBackground(Color.decode("#1e3a5f"));

        initPanLabStat();
        initPanChemin();
        initPanTournee();
        initPanFiabilite();
        initPanFiltre();

        GridBagConstraints gcpanOutil = new GridBagConstraints();
        gcpanOutil.fill = GridBagConstraints.BOTH;
        gcpanOutil.weightx = 1.0;
        gcpanOutil.weighty = 1.0;
        gcpanOutil.gridx = 0;
        gcpanOutil.gridy = 0;
        panoutil.add(panLabStat, gcpanOutil);
        gcpanOutil.gridy++;
        panoutil.add(panChemin, gcpanOutil);
        gcpanOutil.gridy++;
        panoutil.add(panTournee, gcpanOutil);
        gcpanOutil.gridy++;
        panoutil.add(panFiabilite, gcpanOutil);
        gcpanOutil.gridy++;
        panoutil.add(panFiltre, gcpanOutil);
        panoutil.setVisible(true);
    }

    /**
     * Initialise le panneau d'affichage du graphe.
     * <p>
     * Configure un BorderLayout, une taille preferee de 400x400, une couleur de
     * fond sombre et une bordure blanche. La vue GraphStream y sera ajoutee
     * plus tard par {@link #afficherGraph(Graph, String)}.
     */
    public void initPanGraphe() {
        panGraph.setLayout(new BorderLayout());
        panGraph.setPreferredSize(new Dimension(400, 400));
        panGraph.setVisible(true);

        // Changement de couleur du fond du panGraph.
        panGraph.setBackground(Color.decode("#0d1f2d"));

        // Création d'une bordure blanche sur le panGraph.
        panGraph.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
    }

    /**
     * Initialise le panneau des statistiques du graphe.
     * <p>
     * Cree trois labels affichant le nombre de maternites, de blocs operatoires
     * et de centres de nutrition. Leurs valeurs sont mises a jour par
     * {@link #afficherGraph(Graph, String)}.
     */
    public void initPanLabStat() {
        panLabStat = new JPanel();
        labNB1 = new JLabel("Maternités : ");
        labNB2 = new JLabel("Blocs opératoire : ");
        labNB3 = new JLabel("Centres de nutrition : ");

        // Changement de couleur du fond du panLabStat.
        panLabStat.setBackground(Color.decode("#122438"));

        // Création d'une bordure blanche sur le panLabStat.
        panLabStat.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));

        panLabStat.setLayout(new BorderLayout());

        // Création d'une bordure invisible en guise de padding et changement de couleur du texte.
        labNB1.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 0));
        labNB1.setForeground(Color.white);
        labNB2.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 0));
        labNB2.setForeground(Color.white);
        labNB3.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 0));
        labNB3.setForeground(Color.white);

        panLabStat.add(labNB3, BorderLayout.NORTH);
        panLabStat.add(labNB2, BorderLayout.CENTER);
        panLabStat.add(labNB1, BorderLayout.SOUTH);

        panLabStat.setVisible(true);
    }

    /**
     * Initialise le panneau de filtre des sommets.
     * <p>
     * Cree une JList contenant les options "Bloc operatoire", "Centre
     * nutrition", "Maternite" et "Reset". La selection declenche l'ecouteur
     * defini dans {@link #initEcouteur()}, qui masque ou affiche les sommets et
     * aretes selon leur type.
     */
    public void initPanFiltre() {
        panFiltre = new JPanel();
        lstmodel = new DefaultListModel<>();
        lstFiltre = new JList<>(lstmodel);

        String filtre[] = {"Bloc operatoire", "Centre nutrition", "Maternite", "Reset"};
        for (String string : filtre) {
            lstmodel.addElement(string);
        }

        lstFiltre.setBackground(Color.decode("#122438"));
        lstFiltre.setForeground(Color.white);
        lstFiltre.setVisibleRowCount(4);

        panFiltre.setLayout(new BorderLayout());
        panFiltre.add(lstFiltre, BorderLayout.CENTER);

        lstFiltre.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        panFiltre.setBackground(Color.decode("#1e3a5f"));

        panFiltre.setVisible(true);
    }

    /**
     * Initialise la barre superieure de gestion du graphe.
     * <p>
     * Cree le label du nom du graphe et le bouton "+" (croix) qui ouvre la
     * boite de dialogue de chargement ou de generation de graphe.
     */
    public void initPanChangeGraph() {
        labGraph = new JLabel("Nom du graphe");
        butcroix = new JButton("+");

        butcroix.setPreferredSize(new Dimension(25, 25));

        // Changement du style du bouton "croix".
        setStyleBouton(butcroix);
        butcroix.setFont(new Font("Arial", Font.BOLD, 18));

        // Changement de la couleur du texte de labGraphe.
        labGraph.setForeground(Color.white);

        // Ajout dans le panChangeGraph.
        panChangeGraph.add(labGraph);
        panChangeGraph.add(butcroix);

        // Changement de la couleur du fond du panChangeGraph.
        panChangeGraph.setBackground(Color.decode("#122438"));

        // Création d'une bordure sur le panChangeGraph.
        panChangeGraph.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));

        // Le panneau est rendu visible.
        panChangeGraph.setVisible(true);
    }

    /**
     * Initialise le panneau de calcul d'itineraire entre deux sommets.
     * <p>
     * Cree les selecteurs de depart et d'arrivee, le bouton de calcul, ainsi
     * que deux zones de resultats : une pour le plus court chemin et une pour
     * le chemin le plus fiable (chemin parcouru et duree). Disposition en
     * GridBagLayout.
     */
    public void initPanChemin() {
        panChemin = new JPanel();

        selecteur1 = new JComboBox<>();
        selecteur2 = new JComboBox<>();
        labSommet1 = new JLabel("Départ");
        labSommet2 = new JLabel("Arrivée");
        butCourtChemin = new JButton("Calculer l'itinéraire");
        butCourtChemin.setPreferredSize(new Dimension(175, 25));

        // Chemin le plus court
        labStatSeparationFenCheminCourt = new JLabel("Stats plus court chemin");
        lstCheminModelCourt = new DefaultListModel<>();
        lstCheminCourt = new JList<>(lstCheminModelCourt);
        ScrollListCheminCourt = new JScrollPane(lstCheminCourt);
        lstCheminCourt.setVisibleRowCount(1);
        labDureeCheminCourt = new JLabel("Durée du chemin : ");
        labCheminCourt = new JLabel("Chemin");

        // Chemin le plus fiable
        labStatSeparationFenCheminFiable = new JLabel("Stats plus fiable chemin");
        lstCheminModelFiable = new DefaultListModel<>();
        lstCheminFiable = new JList<>(lstCheminModelFiable);
        ScrollListCheminFiable = new JScrollPane(lstCheminFiable);
        lstCheminFiable.setVisibleRowCount(1);
        labDureeCheminFiable = new JLabel("Durée du chemin : ");
        labCheminFiable = new JLabel("Chemin");

        labStatSeparationFenCheminCourt.setForeground(Color.white);
        labStatSeparationFenCheminCourt.setBorder(BorderFactory.createLineBorder(Color.white));
        labDureeCheminCourt.setForeground(Color.white);
        labCheminCourt.setForeground(Color.white);

        labStatSeparationFenCheminFiable.setForeground(Color.white);
        labStatSeparationFenCheminFiable.setBorder(BorderFactory.createLineBorder(Color.white));
        labDureeCheminFiable.setForeground(Color.white);
        labCheminFiable.setForeground(Color.white);

        labSommet1.setForeground(Color.white);
        labSommet1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
        labSommet2.setForeground(Color.white);
        labSommet2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

        panChemin.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        panChemin.setBackground(Color.decode("#122438"));
        panChemin.setLayout(new GridBagLayout());
        panChemin.setVisible(true);
        GridBagConstraints gcChemin = new GridBagConstraints();

        actualiseComboBox(selecteur1);
        actualiseComboBox(selecteur2);
        setStyleBouton(butCourtChemin);

        gcChemin.fill = GridBagConstraints.HORIZONTAL;
        gcChemin.weightx = 1.0;
        gcChemin.gridx = 0;
        gcChemin.gridy = 0;
        gcChemin.insets = new Insets(0, 6, 3, 6);
        panChemin.add(labSommet1, gcChemin);

        gcChemin.gridx = 1;
        panChemin.add(selecteur1, gcChemin);

        gcChemin.gridx = 0;
        gcChemin.gridy = 1;
        panChemin.add(labSommet2, gcChemin);

        gcChemin.gridx = 1;
        panChemin.add(selecteur2, gcChemin);

        gcChemin.gridx = 0;
        gcChemin.gridy = 2;
        gcChemin.gridwidth = 2;
        panChemin.add(butCourtChemin, gcChemin);
        gcChemin.gridwidth = 1;

        // Chemin le plus court Stat Placement
        gcChemin.gridwidth = 2;
        gcChemin.gridy++;
        gcChemin.insets = new Insets(10, 6, 0, 6);
        panChemin.add(labStatSeparationFenCheminCourt, gcChemin);

        gcChemin.gridy++;
        gcChemin.insets = new Insets(3, 6, 3, 6);
        panChemin.add(labCheminCourt, gcChemin);

        gcChemin.gridy++;
        gcChemin.insets = new Insets(0, 6, 0, 6);
        panChemin.add(ScrollListCheminCourt, gcChemin);

        gcChemin.gridy++;
        gcChemin.insets = new Insets(6, 6, 6, 6);
        panChemin.add(labDureeCheminCourt, gcChemin);

        // Chemin le plus fiable Stat Placement
        gcChemin.gridwidth = 2;
        gcChemin.gridy++;
        gcChemin.insets = new Insets(10, 6, 0, 6);
        panChemin.add(labStatSeparationFenCheminFiable, gcChemin);

        gcChemin.gridy++;
        gcChemin.insets = new Insets(3, 6, 3, 6);
        panChemin.add(labCheminFiable, gcChemin);

        gcChemin.gridy++;
        gcChemin.insets = new Insets(0, 6, 0, 6);
        panChemin.add(ScrollListCheminFiable, gcChemin);

        gcChemin.gridy++;
        gcChemin.insets = new Insets(6, 6, 6, 6);
        panChemin.add(labDureeCheminFiable, gcChemin);

    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------- //
    /**
     * Installe tous les ecouteurs d'evenements de l'interface.
     * <p>
     * Associe :
     * <ul>
     * <li>au bouton "+" : une boite de dialogue de chargement ou de generation
     * de graphe, suivie de l'actualisation des selecteurs ;</li>
     * <li>a la liste de filtre : le masquage/affichage des sommets et aretes
     * selon les types selectionnes ;</li>
     * <li>au bouton "Calculer l'itineraire" (chemin) : le calcul et l'affichage
     * du plus court chemin et du chemin le plus fiable ;</li>
     * <li>au bouton "Calculer l'itineraire" (tournee) : le calcul de la tournee
     * et l'affichage du chemin et de sa duree ;</li>
     * <li>au bouton "Montrer fiabilite" : la mise en evidence des aretes dont
     * la fiabilite est dans l'intervalle choisi.</li>
     * </ul>
     */
    public void initEcouteur() {
        butcroix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Charger graphe", "Générer Graphe"};
                int choix = JOptionPane.showOptionDialog(
                        null,
                        "Que voulez-vous faire ?",
                        "MonAppli",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                // Si le choix est le premier bouton.
                if (choix == 0) {
                    try {
                        ChargerGraphBoutonPressé();
                    } catch (FileNotFoundException ex) {
                        System.getLogger(Fenetre.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }

                    // Si le choix est le deuxième bouton.
                } else if (choix == 1) {
                    GénérerGraphBoutonPressé();
                }
                actualiseComboBox(selecteur1);
                actualiseComboBox(selecteur2);
            }
        });

        lstFiltre.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                List<String> lstTemp = lstFiltre.getSelectedValuesList();
                boolean isReset = false;

                // Detection de l'option "Reset" pour reinitialiser la selection en fin de traitement.
                if (lstTemp.contains("Reset")) {
                    isReset = true;

                }

                // Affichage ou masquage de chaque sommet selon son type et la selection.
                for (Node node : graphActuel) {
                    if (lstTemp.isEmpty()) {
                        node.setAttribute("ui.style", "size:15px; text-size: 10px;");
                    } else if (lstTemp.contains(node.getAttribute("type"))) {
                        node.setAttribute("ui.style", "size:15px; text-size: 10px;");
                    } else if (!lstTemp.contains(node.getAttribute("type"))) {
                        node.setAttribute("ui.style", "size:0px; text-size: 0px;");

                    }

                }

                // Une arete n'est visible que si ses deux extremites sont des types selectionnes.
                for (Edge edge : graphActuel.getEachEdge()) {
                    String type0 = edge.getNode0().getAttribute("type");
                    String type1 = edge.getNode1().getAttribute("type");
                    if (lstTemp.isEmpty() || (lstTemp.contains(type0) && lstTemp.contains(type1))) {
                        edge.setAttribute("ui.style", "fill-color: white;");
                    } else {
                        edge.setAttribute("ui.style", "fill-color: rgba(0,0,0,0);");
                    }
                }

                if (isReset) {
                    lstFiltre.clearSelection();
                }
            }
        });

        butCourtChemin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lstCheminModelCourt.clear();
                lstCheminModelFiable.clear();
                // Calcul du plus court chemin et du chemin le plus securise/fiable.
                List lstReturnPC = MonGraphe.calculerItinerairePlusCourt(graphActuel, "" + selecteur1.getSelectedItem(), "" + selecteur2.getSelectedItem());
                List lstReturnS = MonGraphe.calculerItineraireSecurise(graphActuel, "" + selecteur1.getSelectedItem(), "" + selecteur2.getSelectedItem());
                Path cheminPC = (Path) lstReturnPC.get(0);
                Path cheminS = (Path) lstReturnS.get(0);
                // Remplissage des listes avec les sommets de chaque chemin.
                for (Node n : cheminPC.getEachNode()) {
                    lstCheminModelCourt.addElement(n.getId());
                }
                for (Node n : cheminS.getEachNode()) {
                    lstCheminModelFiable.addElement(n.getId());
                }

                labDureeCheminCourt.setText("Durée du chemin : " + lstReturnPC.get(1));
                labDureeCheminFiable.setText("Durée du chemin : " + lstReturnS.get(1));
                colorerChemin(cheminPC, cheminS);
            }
        });
        butCourtTournee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Apparition des stats Camion 2.
                if ((int) selecteurNb.getSelectedItem() == 2) {
                    labDureeTourneeCam2.setVisible(true);
                    labStatSeparationFenCam2.setVisible(true);
                    ScrollListTourneeCam2.setVisible(true);
                    labTourneeCheminCam2.setVisible(true);
                } else {
                    labDureeTourneeCam2.setVisible(false);
                    labStatSeparationFenCam2.setVisible(false);
                    ScrollListTourneeCam2.setVisible(false);
                    labTourneeCheminCam2.setVisible(false);
                }
                ArrayList<Path> chemins;
                if (selecteurAlgo.getSelectedItem().equals("Xparfait puis glouton")) {
                    String valRetour = JOptionPane.showInputDialog(pan, "Saisir le nombre de sommet parfait", "Sélecteur de Sommet");
                    chemins = MonGraphe.plusCourteTournee(graphActuel, (int) selecteurNb.getSelectedItem(), (String) selecteurType.getSelectedItem(), valRetour);
                }
                else{
                    // Calcul de la tournee selon le nombre de camions, le type de dispensaire et l'algorithme choisis.
                    chemins = MonGraphe.plusCourteTournee(graphActuel, (int) selecteurNb.getSelectedItem(), (String) selecteurType.getSelectedItem(), (String) selecteurAlgo.getSelectedItem());
                }
                
                
                Path chemin1 = chemins.get(0);
                Path chemin2 = chemins.get(1);
                
                if (chemin2 == null){
                    try{
                        FileWriter fichier = new FileWriter("resultat.csv");
                        enregistrement1 += "\n" + graphActuel.getId() + "," + selecteurAlgo.getSelectedItem() + "," + MonGraphe.calculerTotalPath(chemin1, "duree") + "," + chemin1.toString().replace("[", "").replace("]", "");
                        fichier.write(enregistrement1);
                    }
                    catch(IOException ex){
                        File f = new File("resultat.csv");
                        f.delete();
                        try {
                            FileWriter fichier = new FileWriter("resultat.csv");
                            enregistrement1 += "\n" + graphActuel.getId() + "," + selecteurAlgo.getSelectedItem() + "," + MonGraphe.calculerTotalPath(chemin1, "duree") + "," + chemin1.toString().replace("[", "").replace("]", "");
                            fichier.write(enregistrement1);
                        } catch (IOException ex2) {
                            System.out.println("euh.....");
                        }
                    }
                }
                else{
                    try{
                        FileWriter fichier = new FileWriter("resultat2.csv");
                        enregistrement2 += "\n" + graphActuel.getId() + "," + selecteurAlgo.getSelectedItem() + "," + MonGraphe.calculerTotalPath(chemin1, "duree") + "," + MonGraphe.calculerTotalPath(chemin2, "duree") + "," + chemin1.toString().replace("[", "").replace("]", "") + "," + chemin2.toString().replace("[", "").replace("]", "");
                        fichier.write(enregistrement1);
                    }
                    catch(IOException ex){
                        File f = new File("resultat2.csv");
                        f.delete();
                        try {
                            FileWriter fichier = new FileWriter("resultat2.csv");
                            enregistrement2 += "\n" + graphActuel.getId() + "," + selecteurAlgo.getSelectedItem() + "," + MonGraphe.calculerTotalPath(chemin1, "duree") + "," + MonGraphe.calculerTotalPath(chemin2, "duree") + "," + chemin1.toString().replace("[", "").replace("]", "") + "," + chemin2.toString().replace("[", "").replace("]", "");
                            fichier.write(enregistrement1);
                        } catch (IOException ex2) {
                            System.out.println("euh.....");
                        }
                    }
                }
                
                
                if (chemin2 == null){
                    colorerChemin(chemin1);
                }
                else{
                    colorerChemin(chemin1, chemin2);
                    
                    String cheminString = "" + chemin2;

                    // Nettoyage de la chaine du chemin pour extraire la liste des sommets.
                    String replaceChemin = cheminString.replace("[", " ");
                    replaceChemin = replaceChemin.replace("]", "");
                    String[] lstNodeChemin = replaceChemin.split(",");

                    lstTourneeModelCam2.removeAllElements();

                    List<String> lstSupprOccu = new ArrayList<>();
                    int cptNode = 1, cptDureeTournee = 0;

                    // Somme des durees de toutes les aretes du chemin.
                    for (Edge edge : chemin2.getEachEdge()) {
                        cptDureeTournee = cptDureeTournee + (int) edge.getAttribute("duree");
                    }

                    // Ajout des sommets dans la liste en evitant les doublons.
                    for (String node : lstNodeChemin) {
                        if (!lstSupprOccu.contains(node)) {
                            lstTourneeModelCam2.addElement(cptNode + ")" + node);
                            cptNode++;
                            lstSupprOccu.add(node);
                        }
                    }
                    lstTourneeCheminCam2.repaint();

                    labDureeTourneeCam2.setText("Durée de la Tournée : " + cptDureeTournee);
                }
                

                String cheminString = "" + chemin1;

                // Nettoyage de la chaine du chemin pour extraire la liste des sommets.
                String replaceChemin = cheminString.replace("[", " ");
                replaceChemin = replaceChemin.replace("]", "");
                String[] lstNodeChemin = replaceChemin.split(",");

                lstTourneeModelCam1.removeAllElements();

                List<String> lstSupprOccu = new ArrayList<>();
                int cptNode = 1, cptDureeTournee = 0;

                // Somme des durees de toutes les aretes du chemin.
                for (Edge edge : chemin1.getEachEdge()) {
                    cptDureeTournee = cptDureeTournee + (int) edge.getAttribute("duree");
                }

                // Ajout des sommets dans la liste en evitant les doublons.
                for (String node : lstNodeChemin) {
                    if (!lstSupprOccu.contains(node)) {
                        lstTourneeModelCam1.addElement(cptNode + ")" + node);
                        cptNode++;
                        lstSupprOccu.add(node);
                    }
                }
                lstTourneeCheminCam1.repaint();

                labDureeTourneeCam1.setText("Durée de la Tournée : " + cptDureeTournee);
            }
        });

        butFiabilité.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // La borne inferieure doit etre <= a la borne superieure, sinon signalement en rouge.
                if (!((int) ecartInfFiabilité.getSelectedItem() <= (int) ecartSupFiabilité.getSelectedItem())) {
                    ecartInfFiabilité.setBackground(Color.red);
                    ecartSupFiabilité.setBackground(Color.red);
                } else {
                    ecartInfFiabilité.setBackground(Color.white);
                    ecartSupFiabilité.setBackground(Color.white);
                    resetColorArete();
                    // Mise en evidence des aretes dont la fiabilite est dans l'intervalle [ecartInf, ecartSup].
                    for (Edge edge : graphActuel.getEachEdge()) {
                        double fiabilité = edge.getAttribute("fiabilite");
                        double ecartSup = Double.parseDouble("" + ecartSupFiabilité.getSelectedItem()) / 10;
                        double ecartInf = Double.parseDouble("" + ecartInfFiabilité.getSelectedItem()) / 10;

                        if (fiabilité >= ecartInf && fiabilité <= ecartSup) {
                            colorArete(edge, "red");
                        }
                    }
                }

            }
        });
    }

    /**
     * Gere l'action "Generer Graphe".
     * <p>
     * Demande a l'utilisateur un nombre de sommets via une boite de dialogue,
     * genere un graphe aleatoire de cette taille avec
     * {@link MonGraphe#construireGrapheAleatoire(int)} puis l'affiche.
     */
    public void GénérerGraphBoutonPressé() {
        String input = JOptionPane.showInputDialog(null, "Saisir un nombre de sommets :", "Générer Graphe Aléatoire", JOptionPane.QUESTION_MESSAGE);

        // Vérification de la valeur d'entrée.
        if (Integer.parseInt(input) > 0 && !input.isEmpty()) {
            // Conversion du String en int.
            int nb = Integer.parseInt(input);

            Graph g = MonGraphe.construireGrapheAleatoire(nb);

            // null est le nom du graphe qui signifie "graphe alea".
            afficherGraph(g, null);
        } 
    }

    /**
     * Gere l'action "Charger graphe".
     * <p>
     * Ouvre un selecteur de fichier dans le dossier {@code ./data}, charge le
     * graphe correspondant avec {@link MonGraphe#chargerGraphe(String)} puis
     * l'affiche.
     *
     * @throws FileNotFoundException si le fichier selectionne est introuvable
     */
    public void ChargerGraphBoutonPressé() throws FileNotFoundException {
        JFileChooser chooser = new JFileChooser("./data");
        chooser.showOpenDialog(null);

        // Récupération du chemin absolut.
        String chemin = chooser.getSelectedFile().getAbsolutePath();
        Graph g = MonGraphe.chargerGraphe(chemin);

        // Affichage du graphe avec le nom du graphe.
        afficherGraph(g, chooser.getSelectedFile().getName());
    }

    /**
     * Affiche un graphe dans le panneau {@code panGraph} et met a jour
     * l'interface.
     * <p>
     * Colore les sommets selon leur type (rouge = maternite, bleu = bloc
     * operatoire, vert = centre de nutrition), applique la feuille de style
     * GraphStream, cree une vue avec auto-layout, l'enregistre comme ecouteur
     * de souris, met a jour le titre du graphe et les compteurs de
     * statistiques, puis memorise le graphe dans {@link #graphActuel}.
     *
     * @param g le graphe a afficher
     * @param fileName le nom du fichier source, ou {@code null} pour un graphe
     * genere aleatoirement
     */
    public void afficherGraph(Graph g, String fileName) {
        // Suppression de l'ancien graphe.
        panGraph.removeAll();

        // Création des compteurs de statistiques.
        int nbOperatoire = 0, nbMaternite = 0, nbNutrition = 0;

        // Changement de couleur des sommets.
        for (Node node : g) {
            String type = (String) node.getAttribute("type");

            if (type.equals("Maternite")) {
                node.setAttribute("ui.style", "fill-color: red;");
                nbMaternite++;
            } else if (type.equals("Bloc operatoire")) {
                node.setAttribute("ui.style", "fill-color: #7EB8F7 ;");
                nbOperatoire++;
            } else if (type.equals("Centre nutrition")) {
                node.setAttribute("ui.style", "fill-color: green;");
                nbNutrition++;
            }
        }

        // Changement des couleurs du graphe, textes des sommets et arrêtes.
        g.setAttribute("ui.stylesheet", "graph { fill-color: #122438; }"
                + "node { size: 15px; text-color: #ffffff;}"
                + "edge { fill-color: #ffffff; }"
        );
        // Rend les arrêtes plus nettes.
        g.setAttribute("ui.antialias");

        Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        this.view = (DefaultView) viewer.addDefaultView(false);
        view.setPreferredSize(new Dimension(400, 400));
        view.addMouseListener(this);

        // Changement du nom du Graphe.
        if (fileName != null) {
            labGraph.setText(fileName);
        } else {
            labGraph.setText("Graphe Aléa");
        }

        // Actualisation des statistiques du graphe.
        labNB1.setText("Maternités : " + nbMaternite);
        labNB2.setText("Blocs opératoire : " + nbOperatoire);
        labNB3.setText("Centres de nutrition : " + nbNutrition);

        // Ajout du viewer au panneau.
        panGraph.add(view);
        panGraph.repaint(); // Réactualisation du panGraph.
        this.graphActuel = g;

    }

    /**
     * Applique le style visuel commun a un bouton.
     * <p>
     * Fixe la couleur de fond, la couleur du texte, supprime la marge et le
     * contour de focus.
     *
     * @param but le bouton a styliser
     */
    public void setStyleBouton(JButton but) {
        // Changement de couleur du fond du bouton passé en paramètre.
        but.setBackground(Color.decode("#2e86c1"));

        // Changement de couleur du texte du bouton passé en paramètre.
        but.setForeground(Color.white);

        // Enlève le margin du bouton.
        but.setMargin(new Insets(2, 0, 0, 0));

        // Enlève le contour de focus qui apparaît autour du bouton quand on clique dessus.
        but.setFocusPainted(false);
    }

    /**
     * Vide puis remplit un selecteur avec les sommets du graphe courant.
     * <p>
     * Si {@link #graphActuel} est {@code null}, le selecteur reste vide.
     *
     * @param cb le selecteur (JComboBox) a actualiser
     */
    public void actualiseComboBox(JComboBox<Node> cb) {
        cb.removeAllItems();
        if (graphActuel != null) {
            for (Node node : graphActuel) {
                cb.addItem(node);
            }
        }

    }

    /**
     * Reinitialise puis colore en rouge les aretes d'un chemin.
     *
     * @param chemin le chemin a mettre en evidence
     */
    public void colorerChemin(Path chemin) {
        // Reset la couleur de toute les arrêtes du graphe.
        resetColorArete();
        for (Edge edge : chemin.getEachEdge()) {
            colorArete(edge, "red");
        }
    }

    /**
     * Reinitialise puis colore deux chemins de couleurs differentes.
     * <p>
     * Le premier chemin est colore en rouge, le second en vert. Sert a comparer
     * visuellement le plus court chemin et le chemin le plus fiable.
     *
     * @param chemin1 premier chemin (colore en rouge)
     * @param chemin2 second chemin (colore en vert)
     */
    public void colorerChemin(Path chemin1, Path chemin2) {
        // Reset la couleur de toute les arrêtes du graphe.
        resetColorArete();
        for (Edge edge : chemin1.getEachEdge()) {
            colorArete(edge, "red");
        }
        for (Edge edge : chemin2.getEachEdge()) {
            colorArete(edge, "green");
        }
    }

    /**
     * Remet toutes les aretes du graphe courant a la couleur blanche.
     */
    public void resetColorArete() {
        for (Edge edge : graphActuel.getEachEdge()) {
            edge.setAttribute("ui.style", "fill-color: white;");
        }
    }

    /**
     * Colore une arete avec la couleur indiquee.
     *
     * @param edge l'arete a colorer
     * @param couleur le nom ou code de la couleur (syntaxe CSS GraphStream)
     */
    public void colorArete(Edge edge, String couleur) {
        edge.setAttribute("ui.style", "fill-color: " + couleur + ";");
    }

    /**
     * Gere le clic de souris sur la vue du graphe.
     * <p>
     * Convertit les coordonnees du clic en tenant compte de la configuration
     * graphique de l'ecran, recherche le sommet situe sous le curseur et, s'il
     * en existe un, ouvre une {@link FenetreDetails} pour ce sommet.
     *
     * @param e l'evenement de clic souris
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // vérifie que la vue et le graphe existent
        if (view == null || graphActuel == null) {
            return;
        }

        // Recupere la configuration graphique pour gérer les écrans 
        GraphicsConfiguration gc = view.getGraphicsConfiguration();
        AffineTransform tx = gc.getDefaultTransform();

        //on prend ordonnée et abscisse
        float abscisse = (float) (e.getX() * tx.getScaleX());
        float ordonnee = (float) (e.getY() * tx.getScaleY());

        // recherche de l'élément sous le clic
        GraphicElement element = view.findNodeOrSpriteAt(e.getX(), e.getY());

        if (element != null) {
            Node n = graphActuel.getNode(element.getId());

            if (n != null) {
                // ENVOI DU NŒUD : On passe le nœud 'n' à la fenêtre de détails
                // On peut aussi lui passer les coordonnées pour la positionner (e.getXOnScreen(), e.getYOnScreen())
                FenetreDetails fenetre = new FenetreDetails(n, graphActuel);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
