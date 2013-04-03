package com.ntnu.tdt4215.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import com.ntnu.tdt4215.document.NLHChapter;

public class CreateFSMFromChapter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CreateFSMFromChapter createChapters = new CreateFSMFromChapter();
		
		ArrayList<NLHChapter> NLHChapters = new ArrayList<NLHChapter>();
		
		ArrayList<String> chaptersG = null;
		ArrayList<String> chaptersL = null;
		ArrayList<String> chaptersT = null;
		
		chaptersG = createChapters.getURLAddressesToChapters("Download/G/innhold.htm");
		chaptersL = createChapters.getURLAddressesToChapters("Download/L/innhold.htm");
		chaptersT = createChapters.getURLAddressesToChapters("Download/T/innhold.htm");
		
		int i = 0;
		
		for(String g : chaptersG) {
			HashSet<String> contentSet = createChapters.getContentFromURLAddress("Download/G/" + g);
			createChapters.createNLHChapter("Download/G/" + g, contentSet, NLHChapters);
		}
		
		for(String g : chaptersL) {
			HashSet<String> contentSet = createChapters.getContentFromURLAddress("Download/L/" + g);
			createChapters.createNLHChapter("Download/L/" + g, contentSet, NLHChapters);
		}
		
//		for(String g : chaptersT) {
////			i++;
////			if(i < 2) {
//			HashSet<String> contentSet = createChapters.getContentFromURLAddress("Download/T/" + g);
//			createChapters.createNLHChapter("Download/T/" + g, contentSet, NLHChapters);
////			}
//		}
		
		System.out.println("Size of chapters: " + NLHChapters.size());
		
		for(NLHChapter c : NLHChapters) {
			System.out.println("Chapter: " + c.getDocument());
		}
	}
	
	private void createNLHChapter(String address, HashSet<String> contentSet, ArrayList<NLHChapter> NLHChapters) {
		FileInputStream fis = null;
		BufferedReader reader = null;
        try {
            fis = new FileInputStream(address);
            reader = new BufferedReader(new InputStreamReader(fis));
            
            String line = reader.readLine();
            
            while(line != null) {
            	
            	if(line.contains("<h1  ")) {
            		Pattern pattern = Pattern.compile("^<h1.*\">(.*)</h.*");
            		
            		Matcher matcher = pattern.matcher(line);
            	    while (matcher.find()) {
            	    	String headLine = matcher.group(1);
            	    	
            	    	String[] parts = headLine.split("&nbsp;");
            	    	
            	    	NLHChapters.add(new NLHChapter(parts[0], parts[1], contentSet.toString()));
            	    }
            		
            	}
              line = reader.readLine();
          }
        } catch (IOException ex) {
            Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
          
        } finally {
            try {
                fis.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	}
	
	private HashSet<String> getContentFromURLAddress(String address) {
		FileInputStream fis = null;
		HashSet<String> contentSet = new HashSet<String>();
        
        try {
            fis = new FileInputStream(address);
            
            String text = Jsoup.parse(fis, "UTF-8", address).text();
            String[] temp = text.split(" ");
            
            for(String t: temp) {
            	contentSet.add(t);
            }
        } catch (IOException ex) {
            Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
          
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return contentSet;
	}
	
	private ArrayList<String> getURLAddressesToChapters(String address) {
		FileInputStream fis = null;
        BufferedReader reader = null;
        
        ArrayList<String> chapters = new ArrayList<String>();
      
        try {
            fis = new FileInputStream(address);
            reader = new BufferedReader(new InputStreamReader(fis));
          
            String line = reader.readLine();
            while(line != null){
            	
            	if(line.contains("<a class=\"menya\" href=\"")) {
            		String[] temp = line.split("href=\"");
            		String[] temp2 = temp[1].split("\"");
            		
            		chapters.add(temp2[0]);
            	}
                line = reader.readLine();
            }           
          
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
          
        } finally {
            try {
                reader.close();
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(CreateFSMFromChapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return chapters;
	}
}
