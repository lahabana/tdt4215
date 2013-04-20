package com.ntnu.tdt4215.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ntnu.tdt4215.index.ScoredDocument;
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

	public SearchWindow(SearchEngine se) {
		this.searchEngine = se;
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel labelnbResults = new JLabel("How many results:");
		nbRes = new JTextField("   5");
		topPanel.add(labelnbResults);
		topPanel.add(nbRes);
		topPanel.setAlignmentX(0);
		add(topPanel);

		
		JLabel label = new JLabel("Your search here:");
		label.setAlignmentX(0);
		add(label);
		
		searchSpace = new JTextArea();
		searchSpace.setAlignmentX(0);
		add(searchSpace);
		
		button = new JButton("Search");
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (results != null) {
    				remove(results);
    				results = null;
    			}
            	int maxResults = Integer.parseInt(nbRes.getText().trim());
            	String query = searchSpace.getText();
            	search(maxResults, query);
            }
        });
		button.setAlignmentX(0);
		add(button);
		
		JLabel labelRes = new JLabel("Results:");
		labelRes.setAlignmentX(0);
		add(labelRes);
		setVisible(true);
		setSize(500, 500);
	}
	
	void search(int maxResults, String queryString) {
		try {
			Collection<ScoredDocument> res = searchEngine.getResults(maxResults, queryString);
			results = new JPanel();
			results.setLayout(new GridLayout(0, 2));
	        
	        Iterator<ScoredDocument> it = res.iterator();
	        for (int i = 0; i < maxResults && it.hasNext(); i++) {
	        	ScoredDocument doc = it.next();
	        	results.add(new JLabel(Float.toString(doc.getScore())));
	        	results.add(new JLabel(doc.getField("title")));
	        }
	        results.setAlignmentX(0);
	        add(results);
	        validate();
	        repaint();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
				    "An error occured are you sure you indexed the documents first?",
				    "Inane error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
}
