package texto;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/*
 * A wrapper for System.out.
 */
public class SwingConsoleManager implements TextManager {
	Scanner reader;
	AtomicBoolean newEntry = new AtomicBoolean(false);
	JTextArea outputText;
	JTextField inputComponent;
	
	public SwingConsoleManager() {  
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		JFrame frame = new JFrame("Texto");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		outputText = new JTextArea();
		outputText.setEditable(false);
		outputText.setLineWrap(true);
		outputText.setWrapStyleWord(true);
		
		outputText.setBackground(Color.BLACK);
		Font font = new Font("Courier", Font.PLAIN, 12);
		outputText.setFont(font);
		outputText.setForeground(Color.WHITE);
		
		JScrollPane scrollPane = new JScrollPane( outputText );
		scrollPane.setPreferredSize(new Dimension(800, 580));
		
		inputComponent = new JTextField();
		inputComponent.setEditable(true);
		inputComponent.setPreferredSize(new Dimension(800, 20));
		inputComponent.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable(){ 
		            public void run(){    
		            	newEntry.set(true);
		            }
		        });
			}
		});
		
		inputComponent.setBackground(Color.BLACK);
		inputComponent.setFont(font);
		inputComponent.setForeground(Color.WHITE);
		
		mainPanel.add( scrollPane);
		mainPanel.add( inputComponent);
		
		frame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
	
	@Override
	public void print(final String text) {
		SwingUtilities.invokeLater(new Runnable(){ 
            public void run(){    
            	outputText.append(text);
            }
        });
	}

	@Override
	public void printLine(final String text) {
		SwingUtilities.invokeLater(new Runnable(){ 
            public void run(){    
            	outputText.append(text + "\n");
            }
        });
	}

	@Override
	public void clear() {
		SwingUtilities.invokeLater(new Runnable(){ 
            public void run(){    
            	outputText.setText("");
            }
        });
	}

	public String getInput() {
        if (newEntry.get()) {
        	newEntry.set(false);
        	String inputLine = inputComponent.getText();
        	
        	printLine(" > " + inputLine);
        	
        	SwingUtilities.invokeLater(new Runnable(){ 
	            public void run(){    
	            	inputComponent.setText("");
	            }
	        });
        	
        	return inputLine;
        }
        
        return null;
	}

	@Override
	public boolean isReady() {
		return outputText != null;
	}
}
