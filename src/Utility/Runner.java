package Utility;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Utility.output.HiderRecord;
import Utility.output.OutputManager;
import Utility.output.TraverserRecord;

/**
 * @author Martin
 *
 */
public class Runner extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String[] hiderTypes = 
		{ 
		  "Random",
		  "RandomVariableHidePotential",
		  "RandomDirection",
		  "RandomSet",
		  "RandomFixedDistance",
		  "LowEdgeCostRandomFixedDistance",
		  "VariableFixedDistance",
		  "LowEdgeCostVariableFixedDistance",
		  "LowEdgeCostFixedDistance",
		  "MinimumConnectivity",
		  "MaxDistance",
		  
		  "FullyBiasHider",
		  "LooselyBiasHider",
		  "VariableBiasHider" 
		};
	
	private static String[] seekerTypes = 
		{ 
		  "RandomWalk",
		  "ConstrainedRandomWalk",
		  "LowEdgeCost",
		  "DepthFirstSearch",
          "DepthFirstSearchLowCost",
	      "BreadthFirstSearch",
	      "BreadthFirstSearchLowCost",
	      "BacktrackPath",
	      "VariableBacktrackPath",
	      "OptimalBacktrackPath",
	      "LeastConnectedFirst", 
	      
          "HighProbabilitySeeker"
		};
	
	private static String[] graphTypes = 
		{
		  "random",
		  "ring",
		  "scalefree",
		  "complete"
		};
	
	// 
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) {
		
		new Runner();

	}
	
	private JComboBox<String> topologies;
	
	private JTextField numberOfNodes;
	
	private JTextField numberOfHiddenItems;
	
	private JTextField costOfEdgeTraversal;
	
	private JComboBox<String> fixedOrRandom;
	
	private JTextField edgeTraversalDecrement;
	
	private JTextField numberOfRounds;
	
	private JTextField numberOfGames;
	
	private DefaultListModel<String> simulationHidersModel;
	
	private DefaultListModel<String> simulationSeekersModel;
	
	private DefaultListModel<String> queueListModel;
	
	private JButton start;
	
	private JButton queue;
	
	private JButton startQueue;
	
	private JButton startSelected;
	
	private JList<String> queueList;
	
	/**
	 * @author Martin
	 *
	 */
	public interface PostDelete 
    {
		
		public void postDelete(String deleted);
		
    }

	/**
	 * @param list
	 * @param model
	 * @param deleted
	 */
	private void deleteOnClick(final JList<String> list, final DefaultListModel<String> model, final PostDelete deleted) {
		
		list.addMouseListener(new MouseAdapter() {
		    
			public void mouseClicked(MouseEvent evt) {
		        
		        if (evt.getClickCount() == 2) {
		       
		        	/* After something has been deleted, pass what has been deleted to the 'post delete' method
		        	 * for processing
		        	 */
		        	deleted.postDelete(model.getElementAt(list.locationToIndex(evt.getPoint())));
		        	
		        	model.remove(list.locationToIndex(evt.getPoint()));
		        	
		        } 
		        
		    }
			
		});
		
	}
	
	/**
	 * 
	 */
	private void generateGUI() {
		
		setPreferredSize(new Dimension(800, 1800));
		
		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		add(tabbedPane);
		
		//
		
		simulationsTab(tabbedPane);
		
		outputTab(tabbedPane);
		
		//
		
		pack();
		
		setVisible(true);
		
	}
	
	/**
	 * @param tabbedPane
	 */
	private void outputTab(JTabbedPane tabbedPane) {
		
		final OutputManager outputManager = new OutputManager();
		
		//////
		
		JPanel outputTab = new JPanel();
		
		outputTab.setLayout(new BorderLayout());
		
		tabbedPane.addTab("Output", outputTab);
		
		////
		
		JPanel northPane = new JPanel();
		
		northPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		outputTab.add(northPane, BorderLayout.NORTH);
		
		//
		
		JButton collateOutput = new JButton("Process Output");
		
		northPane.add(collateOutput);
		
		final DefaultListModel<HiderRecord> outputFeedback = new DefaultListModel<HiderRecord>();

		final JButton showTextStats = new JButton("Show text stats");
		
		showTextStats.setEnabled(false);
		
		collateOutput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				outputManager.processOutput();
				
				outputFeedback.clear();
				
				for (ArrayList<HiderRecord> fileHiderRecord : outputManager.getFileHiderRecords()) {
					
					for (HiderRecord hiderRecord : fileHiderRecord) {
						
						outputFeedback.addElement(hiderRecord);
						
					}
					
					// Mark end of Hiders in that game
					
					outputFeedback.addElement(new HiderRecord("") {
						
						public String toString() {
							
							return "-----";
							
						}
						
					});
					
				}
				
				showTextStats.setEnabled(true);
				
			}
			
		});
		
		//
		
		JButton deleteOutputFiles = new JButton("Delete output files");
		
		deleteOutputFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				outputManager.removeAllOutputFiles();
				
				outputFeedback.clear();
				
			}
			
		});
		
		northPane.add(deleteOutputFiles);
		
		//
		
		
		
		showTextStats.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println(outputManager.printAllStats());
				
			}
			
		});
		
		northPane.add(showTextStats);
		
		////
		
		JPanel centerPane = new JPanel();
		
		centerPane.setLayout(new BorderLayout());
		
		outputTab.add(centerPane, BorderLayout.CENTER);
		
		//
		
		final JList<HiderRecord> outputFeedbackList = new JList<HiderRecord>(outputFeedback);
		
		final JComboBox<String> measure = new JComboBox<String>();
		
		final JLabel simulationParameters = new JLabel();
		
		outputFeedbackList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				if (e.getValueIsAdjusting()) return;
				
				if (outputFeedbackList.getSelectedIndex() == -1 || outputFeedbackList.getSelectedValue().toString().equals("-----")) return;
				
				measure.removeAllItems();
				
				for ( String attribute : outputFeedbackList.getSelectedValue().getSeekerAttributes() ) {
					
					measure.addItem(attribute);
					
				}
				
				simulationParameters.setText("<html><body style='width: 100px;'>Simulation parameters: " + outputFeedbackList.getSelectedValue().getParameters() + "</body></html>");
				
			}
			
		});
		
		centerPane.add(simulationParameters, BorderLayout.NORTH);
		
		centerPane.add(outputFeedbackList, BorderLayout.CENTER);
		
		////
		
		JPanel centerPaneRight = new JPanel();
		
		centerPaneRight.setLayout(new BorderLayout());
		
		centerPane.add(centerPaneRight, BorderLayout.EAST);
		
		//
		
		JPanel centerPaneRightCenter = new JPanel(new GridLayout(10, 4));
		
		centerPaneRight.add(centerPaneRightCenter, BorderLayout.CENTER);
		
		//
		
		centerPaneRightCenter.add(new JLabel("Data for:"));
		
		final JRadioButton seekers = new JRadioButton("Seekers");
		
		centerPaneRightCenter.add(seekers);
		
		seekers.setSelected(true);
		
		final JRadioButton hiders = new JRadioButton("Hiders");
		
		centerPaneRightCenter.add(hiders);
		
		ButtonGroup hidersSeekers = new ButtonGroup();
		
		hidersSeekers.add(hiders);
		
		hidersSeekers.add(seekers);
	    
		//
		
		seekers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (outputFeedbackList.getSelectedIndex() == -1 || outputFeedbackList.getSelectedValue().toString().equals("-----")) return;
				
				measure.removeAllItems();
				
				for ( String attribute : outputFeedbackList.getSelectedValue().getSeekerAttributes() ) {
					
					measure.addItem(attribute);
					
				}
				
			}
			
		});
		
		hiders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (outputFeedbackList.getSelectedIndex() == -1 || outputFeedbackList.getSelectedValue().toString().equals("-----")) return;
				
				measure.removeAllItems();
				
				for ( String attribute : outputFeedbackList.getSelectedValue().getAttributes() ) {
					
					measure.addItem(attribute);
					
				}
				
			}
			
		});
		
		//
		
		centerPaneRightCenter.add(new JLabel("Selected measure:"));
		
		centerPaneRightCenter.add(measure);
		
		//
		
		final JComboBox<String> graphTypes = new JComboBox<String>();
		
		graphTypes.addItem("Line");
		
		graphTypes.addItem("Bar");
		
		centerPaneRightCenter.add(new JLabel("Graph types:"));
		
		centerPaneRightCenter.add(graphTypes);
		
		//
		
		JButton generateGraph = new JButton("Generate graph");
		
		generateGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (outputFeedbackList.getSelectedIndex() == -1 || outputFeedbackList.getSelectedValue().toString().equals("-----")) return;
				
				// Allows for selection of multiple hiders or multiple seekers from list
				ArrayList<TraverserRecord> selectedHiders = new ArrayList<TraverserRecord>();
				
				ArrayList<TraverserRecord> selectedSeekers = new ArrayList<TraverserRecord>();
				
				String title = "";
				
				for ( HiderRecord hider : outputFeedbackList.getSelectedValuesList()) {
					
					selectedHiders.add(hider);
					
					title += hider.toString().replace(" ", "");
					
					for ( TraverserRecord hidersSeekers : hider.getSeekersAndAttributes() ) {
						
						hidersSeekers.setTraverser(hidersSeekers.getTraverser() + " (" + hider.getTraverser() + ")");
						
						selectedSeekers.add(hidersSeekers);
						
					}
					
				}
				
				if (seekers.isSelected()) {
					
					if (graphTypes.getSelectedItem().equals("Line")) {
						
						outputManager.showLineGraphForAttribute(selectedSeekers, title, (String)measure.getSelectedItem());
				
					} else if (graphTypes.getSelectedItem().equals("Bar")) {
						
						outputManager.showBarGraphForAttribute(selectedSeekers, title, (String)measure.getSelectedItem());
						
					}
					
				} else if (hiders.isSelected()) {
					
					if (graphTypes.getSelectedItem().equals("Line")) {
						
						outputManager.showLineGraphForAttribute(selectedHiders, title, (String)measure.getSelectedItem());
						
					} else if (graphTypes.getSelectedItem().equals("Bar")) {
						
						outputManager.showBarGraphForAttribute(selectedHiders, title, (String)measure.getSelectedItem());
						
					}
					
					
				}
				
			}
		
		});
		
		centerPaneRight.add(generateGraph, BorderLayout.SOUTH);
		
	}
	
	private void simulationsTab(JTabbedPane tabbedPane) {
		
		//////
		
		JPanel simulationsTab = new JPanel();
		
		simulationsTab.setLayout(new BorderLayout());
		
		tabbedPane.addTab("Simulations", simulationsTab);
		
		////
		
		JPanel northPane = new JPanel();
		
		northPane.setLayout(new GridLayout(2, 2));
		
		simulationsTab.add(northPane, BorderLayout.NORTH);
		
		////
		
		JPanel parameters = new JPanel();
		
		parameters.setLayout(new GridLayout(6, 2));
		
		parameters.setBorder(new TitledBorder("Parameters"));
		
		//
		
		parameters.add(new JLabel("Topology:"));
		
		topologies = new JComboBox<String>();
		
		for ( String topology : graphTypes ) {
			
			topologies.addItem(topology);
		}
		
		parameters.add(topologies);
		
		//
		
		parameters.add(new JLabel("Number of nodes:"));
		
		numberOfNodes = new JTextField("100");
		
		parameters.add(numberOfNodes);
		
		//
		
		parameters.add(new JLabel("Number of hide locations:"));
		
		numberOfHiddenItems = new JTextField("5");
		
		parameters.add(numberOfHiddenItems);
		
		// 
		
		parameters.add(new JLabel("Cost of traversing an edge:"));
		
		costOfEdgeTraversal = new JTextField("10.0");
		
		parameters.add(costOfEdgeTraversal);
		
		//
		
		parameters.add(new JLabel("Fixed or random cost:"));
		
		fixedOrRandom = new JComboBox<String>();
		
		fixedOrRandom.addItem("random");
		fixedOrRandom.addItem("fixed");
		
		parameters.add(fixedOrRandom);
		
		//
		
		parameters.add(new JLabel("Edge traversal decrement:"));
		
		edgeTraversalDecrement = new JTextField("0");
		
		parameters.add(edgeTraversalDecrement);
		
		//
		
		northPane.add(parameters);
		
		////
		
		JPanel hiders = new JPanel();
		
		hiders.setLayout(new GridLayout(3, 1));
		
		hiders.setBorder(new TitledBorder("Hiders"));
		
		//
		
		hiders.add(new JLabel("Hider types:"));
		
		//
		
		JPanel hiderListAndButton = new JPanel();
		
		hiderListAndButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		final JComboBox<String> hiderList = new JComboBox<String>();
		
		ComboboxToolTipRenderer hiderListRenderer = new ComboboxToolTipRenderer();
		
		hiderList.setRenderer(hiderListRenderer);
		
		ArrayList<String> hiderTooltips = new ArrayList<String>();
		
		for ( String hiderType : hiderTypes ) {
			
			hiderList.addItem(hiderType);
			
			hiderTooltips.add(" ");
			
		}
		
		hiderListRenderer.setTooltips(hiderTooltips);
		
		hiderListAndButton.add(hiderList);
		
		JButton addHider = new JButton("Add hider");
		
		hiderListAndButton.add(addHider);
		
		hiders.add(hiderListAndButton);
		
		//

		simulationHidersModel = new DefaultListModel<String>();
		
		simulationHidersModel.addElement("Random");
		
		JList<String> simulationHiders = new JList<String>(simulationHidersModel);
		
		JScrollPane simulationHidersPane = new JScrollPane(simulationHiders);
		
		simulationHidersPane.setPreferredSize(new Dimension(100, 100));
		
		deleteOnClick(simulationHiders, simulationHidersModel, new PostDelete() {

			@Override
			public void postDelete(String deleted) {
				
				if (simulationHidersModel.toArray().length == 1) { 
					
					start.setEnabled(false);
					
					queue.setEnabled(false);
					
				}
				
			}
			
		});
		
		hiders.add(simulationHidersPane);
		
		addHider.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!simulationHidersModel.contains(hiderList.getSelectedItem().toString())) simulationHidersModel.addElement(hiderList.getSelectedItem().toString());
				
				start.setEnabled(true);
				
				queue.setEnabled(true);
				
			}
			
		});
		
		//
		
		northPane.add(hiders);
		
		////
		
		JPanel simulationParameters = new JPanel();
		
		simulationParameters.setLayout(new GridLayout(2, 2));
		
		simulationParameters.setBorder(new TitledBorder("Simulation Parameters"));
		
		//
		
		simulationParameters.add(new JLabel("Number of rounds:"));
		
		numberOfRounds = new JTextField("1");
		
		simulationParameters.add(numberOfRounds);
		
		//
		
		simulationParameters.add(new JLabel("Number of games:"));
		
		numberOfGames = new JTextField("1");
		
		simulationParameters.add(numberOfGames);
		
		//
		
		northPane.add(simulationParameters);
		
		////
		
		JPanel seekers = new JPanel();
		
		seekers.setLayout(new GridLayout(3, 1));
		
		seekers.setBorder(new TitledBorder("Seekers"));
		
		//
		
		seekers.add(new JLabel("Seeker types:"));
		
		//
		
		JPanel seekerListAndButton = new JPanel();
		
		seekerListAndButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		final JComboBox<String> seekerList = new JComboBox<String>();
		
		ComboboxToolTipRenderer seekerListRenderer = new ComboboxToolTipRenderer();
		
		seekerList.setRenderer(seekerListRenderer);
		
		ArrayList<String> seekerTooltips = new ArrayList<String>();
		
		for ( String seekerType : seekerTypes ) {
			
			seekerList.addItem(seekerType);
			
			seekerTooltips.add(" ");
			
		}
		
		seekerListRenderer.setTooltips(seekerTooltips);
		
		seekerListAndButton.add(seekerList);
		
		JButton addSeeker = new JButton("Add seeker");
		
		seekerListAndButton.add(addSeeker);
		
		seekers.add(seekerListAndButton);
		
		//

		simulationSeekersModel = new DefaultListModel<String>();
		
		simulationSeekersModel.addElement("RandomWalk");
		
		JList<String> simulationSeekers = new JList<String>(simulationSeekersModel);
		
		JScrollPane simulationSeekersPane = new JScrollPane(simulationSeekers);
		
		simulationSeekersPane.setPreferredSize(new Dimension(100, 100));
		
		deleteOnClick(simulationSeekers, simulationSeekersModel, new PostDelete() {

			@Override
			public void postDelete(String deleted) {
				
				if (simulationSeekersModel.toArray().length == 1) { 
					
					start.setEnabled(false);
					
					queue.setEnabled(false);
					
				}
				
			}
			
		});
		
		seekers.add(simulationSeekersPane);
		
		addSeeker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!simulationSeekersModel.contains(seekerList.getSelectedItem().toString())) simulationSeekersModel.addElement(seekerList.getSelectedItem().toString());
				
				start.setEnabled(true);
				
				queue.setEnabled(true);
				
			}
			
		});
		
		//
		
		northPane.add(seekers);
		
		////////
		
		queueListModel = new DefaultListModel<String>();
		
		queueList = new JList<String>(queueListModel);
		
		JScrollPane queueListScroll = new JScrollPane(queueList);
		
		queueListScroll.setPreferredSize(new Dimension(800, 200));
		
		queueList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
			
				updateUISettings(queueList.getSelectedValue());
				
				startSelected.setEnabled(true);
				
			}
			
		});
		
		deleteOnClick(queueList, queueListModel, new PostDelete() {

			@Override
			public void postDelete(String deleted) {
				
				if (queueListModel.toArray().length == 1) { 
					
					startQueue.setEnabled(false);
					
					startSelected.setEnabled(false);
					
				}
				
				simulations.remove(deleted);
				
				// Empty the file
				FileWriter writer;
				
				try {
					
					writer = new FileWriter(Utils.FILEPREFIX + "simulationSchedule.txt");
					
					writer.write("");
					
					for (String simulation : simulations) { 
						
						writer.append(simulation + "\n");
						
					}
					
					writer.close();
					
				} catch (IOException e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		});
		
		simulationsTab.add(queueListScroll, BorderLayout.CENTER);
		
		////////
		
		JPanel controls = new JPanel();
		
		controls.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton resetUI = new JButton("Reset");
		
		resetUI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				topologies.setSelectedIndex(0);
				
				numberOfNodes.setText("100");
				
				numberOfHiddenItems.setText("5");
				
				costOfEdgeTraversal.setText("10.0");
				
				fixedOrRandom.setSelectedIndex(0);
				
				edgeTraversalDecrement.setText("0.0");
				
				numberOfRounds.setText("1");
				
				numberOfGames.setText("1");
				
				simulationHidersModel.clear();
				
				simulationHidersModel.addElement("Random");
				
				simulationSeekersModel.clear();
				
				simulationSeekersModel.addElement("Random Walk");
				
			}
			
		});
		
		controls.add(resetUI);
		
		startSelected = new JButton("Start selected");
		
		startSelected.setEnabled(false);
		
		controls.add(startSelected);
		
		start = new JButton("Start setup");
		
		controls.add(start);
		
		queue = new JButton("Queue simulation");
		
		controls.add(queue);
		
		startQueue = new JButton("Start queue");
		
		startQueue.setEnabled(false);
		
		controls.add(startQueue);
		
		//
		
		simulationsTab.add(controls, BorderLayout.SOUTH);
		
		////

	}
	
	private ArrayList<String> simulations;
	
	/**
	 * 
	 */
	public Runner() {
		
		super("HANDS");
		
		generateGUI();
		
		// Collect list of simulations
        simulations = Utils.readFromFile(Utils.FILEPREFIX + "simulationSchedule.txt");
        
        for (String simulation : simulations) { 
        	
        	queueListModel.addElement(simulation);
        	
        	startQueue.setEnabled(true);
        
        }
        
        //
        
        start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println(Arrays.toString(getUISettings()));
				
				runSimulation(Integer.parseInt(numberOfGames.getText()), getUISettings());
				
			}
        	
        });
        
        //
        
        startSelected.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (String simulationFromList : queueList.getSelectedValuesList() ) {
				
					String[] simulation = simulationFromList.split(",\\s");
					
		        	runSimulation(Integer.parseInt(simulation[0]), simulation);
	        	
				}
				
			}
        	
        });
        
        //
        
        queue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!queueListModel.contains(Arrays.toString(getUISettings()).substring(1, Arrays.toString(getUISettings()).length() - 1))) {
				
					queueListModel.addElement(Arrays.toString(getUISettings()).substring(1, Arrays.toString(getUISettings()).length() - 1));
				
				}
				
				try {
					
					Utils.writeToFile(new FileWriter(Utils.FILEPREFIX + "simulationSchedule.txt", true), Arrays.toString(getUISettings()).substring(1, Arrays.toString(getUISettings()).length() - 1) + "\n");
					
					simulations = Utils.readFromFile(Utils.FILEPREFIX + "simulationSchedule.txt");
					
				} catch (IOException e1) {
				
					// TODO Auto-generated catch block
					e1.printStackTrace();
				
				}
				
				startQueue.setEnabled(true);
				
			}
        	
        });
        
        //
        
        startQueue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				runSimulationList(simulations);
				
			}
        	
        });
       
	}
	
	private void updateUISettings(String settings) {
		
		if (settings == null) return;
		
		String[] simulationParameters = settings.split(",\\s");
		
		int arrayPosition = 0;
		
		simulationHidersModel.clear();
		
		simulationSeekersModel.clear();
		
		for (String param : simulationParameters) {
      	  
			if (param.indexOf('{') != -1) {
				
				Pair<String, String> paramPair = Utils.stringToArray(param, "(\\{([0-9a-zA-Z]+),([0-9a-zA-Z.+]+)\\})").get(0);
  
				if (paramPair.getElement0().equals("Topology")) {
		  
					topologies.setSelectedItem(paramPair.getElement1());
		  
				} else if (paramPair.getElement0().equals("NumberOfNodes")) { 
		  
					numberOfNodes.setText(paramPair.getElement1());
		  
				} else if (paramPair.getElement0().equals("NumberOfHideLocations")) { 
		  
					numberOfHiddenItems.setText(paramPair.getElement1());
		  
				} else if (paramPair.getElement0().equals("Rounds")) { 
		  
					numberOfRounds.setText(paramPair.getElement1());
		  
				} else if (paramPair.getElement0().equals("EdgeWeight")) { 
		  
					costOfEdgeTraversal.setText(paramPair.getElement1());
		  
				} else if (paramPair.getElement0().equals("FixedOrUpperWeight")) { 
		  
					fixedOrRandom.setSelectedItem(paramPair.getElement1());
		  
				} else if (paramPair.getElement0().equals("EdgeTraversalDecrement")) { 
			  
					edgeTraversalDecrement.setText(paramPair.getElement1());
			  
				} 
	  
			} else if (param.indexOf('[') != -1) {
  
				ArrayList<Pair<String, String>> types = Utils.stringToArray(param, "(\\[([0-9a-zA-Z]+),([0-9]+)\\])");
  
				for (Pair<String, String> traverser : types) {
	  
					// Is Hider 
					if (arrayPosition == 1)  {
	  
						simulationHidersModel.addElement(traverser.getElement0());
	  
					// Is Seeker  
					} else {
			  
						simulationSeekersModel.addElement(traverser.getElement0());
			  
					}
		  
				}
	  
			} else {
	  
				numberOfGames.setText(param);
	  
			}
  
			arrayPosition++;
			  
		}
		
	}
	
	private String[] getUISettings() {
		
		String hiderParameter = "";
		
		for (Object hider : simulationHidersModel.toArray()) {
			
			hiderParameter += "[" + hider + ",1]";
			
		}
		
		String seekerParameter = "";
		
		for (Object seeker : simulationSeekersModel.toArray()) {
			
			seekerParameter += "[" + seeker + ",1]";
			
		}
		
		return new String[]{ 
					 
			numberOfGames.getText(),
			
			hiderParameter,
			
			seekerParameter,
			
			"{Topology," + topologies.getSelectedItem().toString() + "}", // Topology
			
			"{NumberOfNodes," + numberOfNodes.getText() + "}", // Number of nodes in graph
			
			"{NumberOfHideLocations," + numberOfHiddenItems.getText() + "}", // Number of hide locations
			
			"{Rounds," + numberOfRounds.getText() + "}", // rounds
			
			"{EdgeWeight," + costOfEdgeTraversal.getText() + "}", // cost of traversing an edge
			
			"{FixedOrUpperWeight," + fixedOrRandom.getSelectedItem().toString() + "}", // whether cost supplied is static value or the upper bound of a distribution
			
			"{EdgeTraversalDecrement," + edgeTraversalDecrement.getText() + "}" // % discount gained by an agent for having traversed an edge before (100 = no discount; < 100 = discount)
		  		 
		};  
		
	}
	
	/**
	 * 
	 */
	private void runSimulationList(ArrayList<String> simulations) {
		
		for (String currentSimulation : simulations) {
			
			String[] simulationParameters = currentSimulation.split(",\\s");
			
			// If the simulation is commented out, do not run it.
			if(		simulationParameters[0].equals("//")	) { continue; }
			
			runSimulation(Integer.parseInt(simulationParameters[0]), simulationParameters);
			
		}
		
	}
		
	private void runSimulation(int GAMES, String[] simulationParameters) {
		
		/***********/
		
		// Generate ID For this simulation
		
		String currentSimulationIdentifier = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		
		Utils.writeToFile(Utils.FILEPREFIX + "simRecordID.txt", currentSimulationIdentifier);
		
		Utils.writeToFile(Utils.FILEPREFIX + "/data/" + currentSimulationIdentifier + ".csv", "");
		
		Utils.writeToFile(Utils.FILEPREFIX + "/data/" + currentSimulationIdentifier + ".csv", Arrays.toString(simulationParameters) + "\n");
		
		/***********/
		
		String[] parameters = { "Hiders", 
				  "Seekers",
  				  "Topology", // Topology
  				  "NumberOfNodes", // Number of nodes in graph
  				  "NumberOfHideLocations", // Number of hide locations
  				  "Rounds", // rounds 
  				  "EdgeWeight", // cost of traversing an edge
  				  "FixedOrUpperWeight", // whether cost supplied is static value or the upper bound of a distribution
  				  "EdgeTraversalDecrement" // % discount gained by an agent for having traversed an edge before (100 = no discount; < 100 = discount)
  				  };
  				  
		String[] defaultParameters = { simulationParameters[1],
				 simulationParameters[2],
				 "random", // Topology
				 "100", // Number of nodes in graph
				 "5", // Number of hide locations
				 "120", // rounds
				 "100.0", // cost of traversing an edge
				 "upper", // whether cost supplied is static value or the upper bound of a distribution
				 "0"// % discount gained by an agent for having traversed an edge before (1.0 = no discount; < 1.0 = discount)
		  		  };
			
		/***********/
		
		/* As we are replacing variables in our simulation parameters with concrete values, 
		 * we must retain the original variables for future games, or they will be lost
		 * i.e. if we receive i+1 as a parameter, this will need to be changed to '1' for the
		 * first game, but a copy of i+1 will need to be retained for the second, third games etc.
		 */
		String[] simulationParametersUnchanged = Arrays.copyOf(simulationParameters, simulationParameters.length);
		
		// Run 'games' of simulation by repeat running program
		
		System.out.println("-----------------------------------------------------------------");
		
	    for(int i = 0; i < GAMES; i++) {
		     	
			System.out.println("Run: " + (i + 1));
			System.out.println("-----------------------------------------------------------------");
		    
			try {
			
				Utils.writeToFile(new FileWriter(Utils.FILEPREFIX + "/upload/index.html", false), "<html>" + ((i / GAMES) * 100) + "</html>");
			
			} catch (IOException e2) {
				
				e2.printStackTrace();
			
			}
			
			Utils.uploadToFTP(Utils.FILEPREFIX + "/upload/index.html", "ftp://%s:%s@%s/%s;type=i", "martin@martin-chapman.co.uk", "wsTrXYn/.", "ftp.martin-chapman.co.uk", "/simulation/index.html");
			
			// Remove wildcards from param string and replace with values
			
			for (int j = 0; j < simulationParameters.length; j++) {
	    		  
	    	    if (simulationParametersUnchanged[j].contains("i*")) { 
	    	    	
	    	    	simulationParameters[j] = 
	    	    	
	    	        simulationParametersUnchanged[j].replaceAll("(i\\*([0-9]+))", 
	    											   "" + (i * Integer.parseInt(
	    													     simulationParametersUnchanged[j].substring( 
														            		   Utils.startIndexOf(simulationParametersUnchanged[j], "(i\\*([0-9]+))") + 2, 
														            		   Utils.endIndexOf(simulationParametersUnchanged[j], "(i\\*([0-9]+))")
														            		   )
	    													            		  
	    													      		)
	    													)
	    	    									   );
	    	    
	    	    } else if (simulationParametersUnchanged[j].contains("i+")) { 
	    	    	
	    	    	simulationParameters[j] = 
	    	    	
	    	        simulationParametersUnchanged[j].replaceAll("(i\\+([0-9]+))", 
	    	        								   "" + (i + Integer.parseInt(
	    	        										     simulationParametersUnchanged[j].substring(
	    	        										   					Utils.startIndexOf(simulationParametersUnchanged[j], "(i\\+([0-9]+))") + 2, 
	    	        										   					Utils.endIndexOf(simulationParametersUnchanged[j], "(i\\+([0-9]+))")
	    	        										   			)
	    	        										   	  )
	    	        										)
	    	        								   ); 
	    	    
	    	    } else if (simulationParametersUnchanged[j].contains(",i")) { 
	    	  		
	    	  		simulationParameters[j] = 
	    	  				
	    	  		simulationParametersUnchanged[j].replaceAll("(\\,i)", "," + i); 
	    	  		
	    	  	}
    	   
    	    }
		    
			// Alter the default parameters to reflect those that have been input
			
            for (String param : simulationParameters) {
        	  
				  if (param.indexOf('{') != -1) {
				  
					  Pair<String, String> paramPair = Utils.stringToArray(param, "(\\{([0-9a-zA-Z]+),([0-9a-zA-Z.]+)\\})").get(0);
					  
					  defaultParameters[Arrays.asList(parameters).indexOf(paramPair.getElement0())] = paramPair.getElement1(); 
					  
				  }
        	  
            }
		    
            // Construct the param string to supply to the program
            
			String paramString = "";
			  
			for (int k = 0; k < defaultParameters.length; k++) {
				  
				paramString += defaultParameters[k] + " ";
				  
			}
			
			System.out.println(paramString);
		    
			/***********/
			
			Process proc = null;
			
			try {
				
				proc = Runtime.getRuntime().exec("java -classpath bin:lib/jgrapht-core-0.9.0.jar:lib/epsgraphics.jar HideAndSeek.Main " + i + " " + GAMES + " " + paramString);
			
			} catch (IOException e1) {
				
				// TODO Auto-generated catch block
				e1.printStackTrace();
			
			}
			
			BufferedReader outputs = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			
			BufferedReader errors = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			  
			String line = null;  
			 
			try {
				
				while ((line = outputs.readLine()) != null) {  
				
					System.out.println(line);  
				
				}
				
			} catch (IOException e1) {
				
				// TODO Auto-generated catch block
				e1.printStackTrace();
			
			}
			
			try {
			
				while ((line = errors.readLine()) != null) {  
					
					System.out.println(line);  
				
				}
				
			} catch (IOException e1) {
				
				// TODO Auto-generated catch block
				e1.printStackTrace();
			
			} 
			  
			try {
			
				proc.waitFor();
			
			} catch (InterruptedException e) { System.out.println(e); }
			 
			System.out.println("-----------------------------------------------------------------");
			
	    } // End of game run loop
		
	}
	
	/**
	 * From: http://stackoverflow.com/questions/480261/java-swing-mouseover-text-on-jcombobox-items
	 * 
	 * @author Martin
	 */
	private class ComboboxToolTipRenderer extends DefaultListCellRenderer {
	    
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * 
		 */
		ArrayList<String> tooltips;

	    /* (non-Javadoc)
	     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	     */
	    @Override
	    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

	        JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	        if (-1 < index && null != value && null != tooltips) list.setToolTipText(tooltips.get(index));
	          
	        return comp;
	        
	    }

	    /**
	     * @param tooltips
	     */
	    public void setTooltips(ArrayList<String> tooltips) {
	        
	    	this.tooltips = tooltips;
	        
	    }
	    
	}
	
}
