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
import sae_java_24.MonGraphe;

/**
 *
 * @author p2501262
 */
public class Fenetre extends JFrame implements MouseListener {

    JPanel pan, panoutil, panGraph, panLabStat, panChangeGraph, panFiltre, panChemin, panTournee, panFiabilite;
    JButton butcroix, butCourtChemin, butCourtTournee, butFiabilité;
    JComboBox<Node> selecteur1, selecteur2;
    JComboBox<String> selecteurAlgo, selecteurType;
    JComboBox<Integer> selecteurNb, ecartInfFiabilité, ecartSupFiabilité;
    JLabel labNB1, labNB2, labNB3, labGraph, labSommet1, labSommet2, labTour1, labTour2, labTour3, labFiabilite, labTourneeChemin, labStatSeparationFen, labDureeTournee;
    JList<String> lstFiltre, lstNodePathTournee, lstTourneeChemin;
    JScrollPane jsp, ScrollPaneOutil, ScrollListTournee;
    DefaultListModel<String> lstmodel, lstTourneeModel;
    Graph graphActuel;
    DefaultView view;

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

    public void initPanTournee() {
        panTournee = new JPanel();

        panTournee.setVisible(true);
        lstTourneeModel = new DefaultListModel<>();
        lstTourneeChemin = new JList<>(lstTourneeModel);
        ScrollListTournee = new JScrollPane(lstTourneeChemin);
        lstTourneeChemin.setVisibleRowCount(1);

        labStatSeparationFen = new JLabel("Stats");
        labDureeTournee = new JLabel("Durée de la Tournée : ");
        labTourneeChemin = new JLabel("Chemin");
        labTour1 = new JLabel("Type dispensaire");
        labTour2 = new JLabel("Nombre de camions");
        labTour3 = new JLabel("Type d'algorithme");
        butCourtTournee = new JButton("Calculer l'itinéraire");
        selecteurType = new JComboBox<>();
        selecteurNb = new JComboBox<>();
        selecteurAlgo = new JComboBox<>();

        selecteurType.addItem("tous");
        selecteurType.addItem("Maternite");
        selecteurType.addItem("Bloc operatoire");
        selecteurType.addItem("Centre nutrition");

        selecteurNb.addItem(1);
        selecteurNb.addItem(2);

        selecteurAlgo.addItem("glouton");
        selecteurAlgo.addItem("parfait");

        labTourneeChemin.setForeground(Color.white);
        labDureeTournee.setForeground(Color.white);
        labTour1.setForeground(Color.white);
        labTour2.setForeground(Color.white);
        labTour3.setForeground(Color.white);
        labStatSeparationFen.setForeground(Color.white);
        
        labStatSeparationFen.setBorder(BorderFactory.createLineBorder(Color.white));
        
        panTournee.setBackground(Color.decode("#122438"));
        panTournee.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        setStyleBouton(butCourtTournee);

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

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(10, 6, 0, 6);
        panTournee.add(labStatSeparationFen, gcpanTournee);
        
        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(3, 6, 3, 6);
        panTournee.add(labTourneeChemin, gcpanTournee);

        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(0, 6, 0, 6);
        panTournee.add(ScrollListTournee, gcpanTournee);
        
        gcpanTournee.gridy++;
        gcpanTournee.insets = new Insets(6, 6, 6, 6);
        panTournee.add(labDureeTournee, gcpanTournee);
    }

    public void initPanOutil() {
        ScrollPaneOutil.setPreferredSize(new Dimension(220, 300));
        panoutil.setLayout(new GridBagLayout());
        panoutil.setPreferredSize(new Dimension(200, 620)); // Ne pas retirer sinon ça fait un bug

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

    public void initPanGraphe() {
        panGraph.setLayout(new BorderLayout());
        panGraph.setPreferredSize(new Dimension(400, 400));
        panGraph.setVisible(true);

        // Changement de couleur du fond du panGraph.
        panGraph.setBackground(Color.decode("#0d1f2d"));

        // Création d'une bordure blanche sur le panGraph.
        panGraph.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
    }

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

    public void initPanChemin() {
        panChemin = new JPanel();

        selecteur1 = new JComboBox<>();
        selecteur2 = new JComboBox<>();
        labSommet1 = new JLabel("Départ");
        labSommet2 = new JLabel("Arrivée");
        butCourtChemin = new JButton("Calculer l'itinéraire");
        butCourtChemin.setPreferredSize(new Dimension(175, 25));

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

    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------- //
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

                if (lstTemp.contains("Reset")) {
                    isReset = true;

                }

                for (Node node : graphActuel) {
                    if (lstTemp.isEmpty()) {
                        node.setAttribute("ui.style", "size:15px; text-size: 10px;");
                    } else if (lstTemp.contains(node.getAttribute("type"))) {
                        node.setAttribute("ui.style", "size:15px; text-size: 10px;");
                    } else if (!lstTemp.contains(node.getAttribute("type"))) {
                        node.setAttribute("ui.style", "size:0px; text-size: 0px;");

                    }

                }

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

                Path chemin = MonGraphe.calculerItineraires(graphActuel, "" + selecteur1.getSelectedItem(), "" + selecteur2.getSelectedItem());
                colorerChemin(chemin);
            }
        });
        butCourtTournee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Path chemin = MonGraphe.plusCourteTournee(graphActuel, (int) selecteurNb.getSelectedItem(), (String) selecteurType.getSelectedItem(), (String) selecteurAlgo.getSelectedItem()).get(0);
                colorerChemin(chemin);

                String cheminString = "" + chemin;

                String replaceChemin = cheminString.replace("[", " ");
                replaceChemin = replaceChemin.replace("]", "");
                String[] lstNodeChemin = replaceChemin.split(",");

                lstTourneeModel.removeAllElements();

                List<String> lstSupprOccu = new ArrayList<>();
                int cptNode = 1, cptDureeTournee = 0;
                
                for (Edge edge : chemin.getEachEdge()) {
                    cptDureeTournee = cptDureeTournee + (int) edge.getAttribute("duree");
                }
                
                for (String node : lstNodeChemin) {
                    if (!lstSupprOccu.contains(node)) {
                        lstTourneeModel.addElement(cptNode + ")" + node);
                        cptNode++;
                        lstSupprOccu.add(node);
                    }
                }
                lstTourneeChemin.repaint();

                labDureeTournee.setText("Durée de la Tournée : " + cptDureeTournee);
            }
        });

        butFiabilité.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!((int) ecartInfFiabilité.getSelectedItem() <= (int) ecartSupFiabilité.getSelectedItem())) {
                    ecartInfFiabilité.setBackground(Color.red);
                    ecartSupFiabilité.setBackground(Color.red);
                } else {
                    ecartInfFiabilité.setBackground(Color.white);
                    ecartSupFiabilité.setBackground(Color.white);
                    resetColorArete();
                    for (Edge edge : graphActuel.getEachEdge()) {
                        double fiabilité = edge.getAttribute("fiabilite");
                        double ecartSup = Double.parseDouble("" + ecartSupFiabilité.getSelectedItem()) / 10;
                        double ecartInf = Double.parseDouble("" + ecartInfFiabilité.getSelectedItem()) / 10;

                        if (fiabilité >= ecartInf && fiabilité <= ecartSup) {
                            colorArete(edge);
                        }
                    }
                }

            }
        });
    }

    public void GénérerGraphBoutonPressé() {
        String input = JOptionPane.showInputDialog(null, "Saisir un nombre de sommets :", "Générer Graphe Aléatoire", JOptionPane.QUESTION_MESSAGE);

        // Vérification de la valeur d'entrée.
        if (input != null && !input.isEmpty()) {
            // Conversion du String en int.
            int nb = Integer.parseInt(input);

            Graph g = MonGraphe.construireGrapheAleatoire(nb);

            // null est le nom du graphe qui signifie "graphe alea".
            afficherGraph(g, null);
        }
    }

    public void ChargerGraphBoutonPressé() throws FileNotFoundException {
        JFileChooser chooser = new JFileChooser("./data");
        chooser.showOpenDialog(null);

        // Récupération du chemin absolut.
        String chemin = chooser.getSelectedFile().getAbsolutePath();
        Graph g = MonGraphe.chargerGraphe(chemin);

        // Affichage du graphe avec le nom du graphe.
        afficherGraph(g, chooser.getSelectedFile().getName());
    }

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

    public void actualiseComboBox(JComboBox<Node> cb) {
        cb.removeAllItems();
        if (graphActuel != null) {
            for (Node node : graphActuel) {
                cb.addItem(node);
            }
        }

    }

    public void colorerChemin(Path chemin) {
        // Reset la couleur de toute les arrêtes du graphe.
        resetColorArete();
        for (Edge edge : chemin.getEachEdge()) {
            colorArete(edge);
        }
    }

    public void resetColorArete() {
        for (Edge edge : graphActuel.getEachEdge()) {
            edge.setAttribute("ui.style", "fill-color: white;");
        }
    }

    public void colorArete(Edge edge) {
        edge.setAttribute("ui.style", "fill-color: red;");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 1. On vérifie d'abord que la vue et le graphe existent
        if (view == null || graphActuel == null) {
            return;
        }

        // 2. Récupération de la configuration graphique pour gérer les écrans 
        GraphicsConfiguration gc = view.getGraphicsConfiguration();
        AffineTransform tx = gc.getDefaultTransform();

        //on prend ordonnée et abscisse
        float abscisse = (float) (e.getX() * tx.getScaleX());
        float ordonnee = (float) (e.getY() * tx.getScaleY());

        // 3. Recherche de l'élément sous le clic
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
