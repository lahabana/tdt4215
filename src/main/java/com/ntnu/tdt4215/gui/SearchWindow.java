package com.ntnu.tdt4215.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.searchEngine.SearchEngine;

public class SearchWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchEngine searchEngine;
	JButton button;
	JTextArea searchSpace;
	JPanel results = null;
	private JTextField nbRes;
	private JPanel mainPanel;

	public SearchWindow(SearchEngine se) {
		this.searchEngine = se;
		
		mainPanel = new JPanel(new GridLayout(0, 1));
		
		JPanel topPanel = new JPanel(new GridLayout(0, 4));
		JLabel labelnbResults = new JLabel("How many results:");
		nbRes = new JTextField("5");
		topPanel.add(labelnbResults);
		topPanel.add(nbRes);
		mainPanel.add(topPanel);
		
		JLabel label = new JLabel("Your search here:");
		mainPanel.add(label);
		
		searchSpace = new JTextArea();
		searchSpace.setSize(400, 200);
		mainPanel.add(searchSpace);
		
		button = new JButton("Search");
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (results != null) {
    				mainPanel.remove(results);
    				results = null;
    			}
            	int maxResults = Integer.parseInt(nbRes.getText());
            	String query = searchSpace.getText();
            	search(maxResults, query);
            }
        });
		mainPanel.add(button);
		
		JLabel labelRes = new JLabel("Results:");
		mainPanel.add(labelRes);
		add(mainPanel);
		setVisible(true);
		setSize(mainPanel.getPreferredSize());
	}
	
	void search(int maxResults, String queryString) {
		try {
			Collection<ScoredDocument> res = searchEngine.getResults(maxResults, queryString);
			results = new JPanel();
			results.setLayout(new GridLayout(0,2));
	        
	        Iterator<ScoredDocument> it = res.iterator();
	        for (int i = 0; i < maxResults && it.hasNext(); i++) {
	        	ScoredDocument doc = it.next();
	        	results.add(new JLabel(Float.toString(doc.getScore())));
	        	results.add(new JLabel(doc.getField("title")));
	        }
	        mainPanel.add(results);
	        mainPanel.validate();
	        mainPanel.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
				    "An error occured are you sure you indexed the documents first?",
				    "Inane error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
}
