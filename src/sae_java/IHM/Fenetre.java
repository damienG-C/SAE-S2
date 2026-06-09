/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import sae_java_24.MonGraphe;

/**
 *
 * @author p2501262
 */
public class Fenetre extends JFrame {

    JPanel pan, panoutil, panGraph, panLabStat, panChangeGraph, panFiltre, panChemin, panOutilMilieu, panOutilMilieu2, panTournee;
    JTextField tfalea;
    JButton butcroix, butCourtChemin, butCourtTournee;
    JComboBox<Node> selecteur1, selecteur2;
    JComboBox<String> selecteurAlgo, selecteurType;
    JComboBox<Integer> selecteurNb;
    JLabel labNB1, labNB2, labNB3, labGraph, labSommet1, labSommet2, labTour1, labTour2, labTour3;
    JList<String> lstFiltre;
    JScrollPane jsp;
    DefaultListModel<String> lstmodel;
    Graph graphActuel;

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
        panGraph = new JPanel();
        panLabStat = new JPanel();
        panChangeGraph = new JPanel();
        panFiltre = new JPanel();
        panTournee = new JPanel();

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
        pan.add(panoutil, gcpan);
    }

    public void initPanTournee() {
        panTournee.setVisible(false);
        
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

        labTour1.setForeground(Color.white);
        labTour2.setForeground(Color.white);
        labTour3.setForeground(Color.white);
        panTournee.setBackground(Color.decode("#122438"));
        panTournee.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        setStyleBouton(butCourtTournee);

        panTournee.setLayout(new GridBagLayout());
        GridBagConstraints gcpanTournee = new GridBagConstraints();

        gcpanTournee.fill = GridBagConstraints.HORIZONTAL;
        gcpanTournee.insets = new Insets(2, 0, 2, 0);
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
        panTournee.add(selecteurAlgo, gcpanTournee);

        gcpanTournee.gridx = 0;
        gcpanTournee.gridy = 6;
        gcpanTournee.gridwidth = 3;
        panTournee.add(butCourtTournee, gcpanTournee);
        gcpanTournee.gridwidth = 1;
    }

    public void initPanOutil() {
        panoutil.setLayout(new BorderLayout());
        panoutil.setPreferredSize(new Dimension(200, 500)); // Ne pas retirer sinon ça fait un bug

        panFiltre = new JPanel();
        panOutilMilieu = new JPanel();
        panOutilMilieu2 = new JPanel();
        initPanOutilMilieu();
        initPanOutilMilieu2();

        // Changement de couleur du fond de panoutil.
        panoutil.setBackground(Color.decode("#1e3a5f"));

        initPanLabStat();
        initPanFiltre();
        initPanChemin();
        initPanTournee();

        panoutil.add(panLabStat, BorderLayout.NORTH);

        panoutil.add(panOutilMilieu, BorderLayout.CENTER);

        panoutil.add(panFiltre, BorderLayout.SOUTH);
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

    public void initPanOutilMilieu() {
        panOutilMilieu.setLayout(new BorderLayout());
        panOutilMilieu.setBackground(Color.decode("#1e3a5f"));
        panOutilMilieu.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panChemin = new JPanel();

        panOutilMilieu.add(panChemin, BorderLayout.NORTH);
        panOutilMilieu.add(panOutilMilieu2, BorderLayout.CENTER);
    }

    public void initPanOutilMilieu2() {
        panOutilMilieu2.setVisible(true);
        panOutilMilieu2.setLayout(new BorderLayout());
        panOutilMilieu2.setBackground(Color.decode("#1e3a5f"));
        panOutilMilieu2.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        panOutilMilieu2.add(panTournee, BorderLayout.NORTH);
    }
    
    public void initPanFiltre() {
        lstmodel = new DefaultListModel<>();
        lstFiltre = new JList<>(lstmodel);
        jsp = new JScrollPane(lstFiltre);

        String filtre[] = {"Bloc operatoire", "Centre nutrition", "Maternite", "Reset"};
        for (String string : filtre) {
            lstmodel.addElement(string);
        }

        lstFiltre.setBackground(Color.decode("#122438"));
        lstFiltre.setForeground(Color.white);
        lstFiltre.setVisibleRowCount(4);

        panFiltre.setLayout(new BorderLayout());
        panFiltre.add(jsp, BorderLayout.CENTER);

        lstFiltre.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        panFiltre.setBackground(Color.decode("#1e3a5f"));
        panFiltre.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        panFiltre.setVisible(false);
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
        panChemin.setVisible(false);
        panChemin.setLayout(new GridBagLayout());
        GridBagConstraints gcChemin = new GridBagConstraints();

        actualiseComboBox(selecteur1);
        actualiseComboBox(selecteur2);
        setStyleBouton(butCourtChemin);

        gcChemin.fill = GridBagConstraints.HORIZONTAL;
        gcChemin.gridx = 0;
        gcChemin.gridy = 0;
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

    public void actualiseComboBox(JComboBox<Node> cb) {
        cb.removeAllItems();
        if (graphActuel != null) {
            for (Node node : graphActuel) {
                cb.addItem(node);
            }
        }

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
                affichePan();
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
                colorerChemin(MonGraphe.plusCourteTournee(graphActuel, (int) selecteurNb.getSelectedItem(), (String) selecteurType.getSelectedItem()));
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

    public void affichePan() {
        panChemin.setVisible(true);
        panFiltre.setVisible(true);
        panTournee.setVisible(true);
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

        DefaultView view = (DefaultView) viewer.addDefaultView(false);
        view.setPreferredSize(new Dimension(400, 400));

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

    public void colorerChemin(Path chemin) {
        // Reset la couleur de toute les arrêtes du graphe.
        for (Edge edge : graphActuel.getEachEdge()) {
            edge.setAttribute("ui.style", "fill-color: white;");
        }
        for (Edge edge : chemin.getEachEdge()) {
            edge.setAttribute("ui.style", "fill-color: red;");
        }

    }

}
