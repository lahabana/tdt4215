package com.ntnu.tdt4215.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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

		ArrayList<File> dirG = createChapters
				.getFilesInDirectory("Download/G/");
		ArrayList<File> dirL = createChapters
				.getFilesInDirectory("Download/L/");
		ArrayList<File> dirT = createChapters
				.getFilesInDirectory("Download/T/");

		int i = 0;

		for (File f : dirG) {
			createChapters.parseFile("Download/G/" + f.getName(), NLHChapters);
		}

		for (File f : dirL) {
			createChapters.parseFile("Download/L/" + f.getName(), NLHChapters);
		}

		for (File f : dirT) {
			createChapters.parseFile("Download/T/" + f.getName(), NLHChapters);
		}

		System.out.println("Size of chapters: " + NLHChapters.size());

		for (NLHChapter c : NLHChapters) {
			i++;
			if (i < 6) {
				System.out.println("Chapter: " + c.getDocument());
			}
		}
	}

	private ArrayList<File> getFilesInDirectory(String dirName) {
		ArrayList<File> files = new ArrayList<File>();

		File dir = new File(dirName);

		File[] temp = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".htm");
			}
		});

		for (File f : temp) {
			if (!(f.getName().equals("innhold.htm"))
					&& !(f.getName().equals(".htm"))) {
				files.add(f);
			}
		}

		return files;
	}

	private void parseFile(String address, ArrayList<NLHChapter> NLHChapters) {
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream(address);
			reader = new BufferedReader(new InputStreamReader(fis));

			String line = reader.readLine();

			String[] parts = new String[2];
			HashSet<String> chapterContentSet = new HashSet<String>();

			boolean newChapter = false;

			while (line != null) {

				if (line.contains("<h1  ") || line.contains("<h2  ")
						|| line.contains("<h3  ") || line.contains("<h4  ")) {

					if (!(chapterContentSet.isEmpty()) && parts != null
							&& newChapter) {

						NLHChapters.add(new NLHChapter(parts[0], parts[1],
								chapterContentSet.toString()));
						parts = null;
						chapterContentSet = new HashSet<String>();
						newChapter = false;
					}
				}

				if (line.contains("<h1  ")) {
					Pattern pattern = Pattern.compile(".*<h1.*\\\">(.*)</h.*");

					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						String headLine = matcher.group(1);

						parts = headLine.split("&nbsp;");

						if (parts.length == 2) {
							newChapter = true;
						}
					}
				} else if (line.contains("<h2  ")) {
					Pattern pattern = Pattern.compile(".*<h2.*\\\">(.*)</h.*");

					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						String headLine = matcher.group(1);

						parts = headLine.split("&nbsp;");

						if (parts.length == 2) {
							newChapter = true;
						}
					}
				} else if (line.contains("<h3  ")) {
					Pattern pattern = Pattern.compile(".*<h3.*\\\">(.*)</h.*");

					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						String headLine = matcher.group(1);

						parts = headLine.split("&nbsp;");

						if (parts.length == 2) {
							newChapter = true;
						}
					}
				} else if (line.contains("<h4  ")) {
					Pattern pattern = Pattern.compile(".*<h4.*\\\">(.*)</h.*");

					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						String headLine = matcher.group(1);

						parts = headLine.split("&nbsp;");

						if (parts.length == 2) {
							newChapter = true;
						}
					}
				} else {
					String text = Jsoup.parse(line).text();
					String[] temp = text.split(" ");

					for (String t : temp) {
						chapterContentSet.add(t);
					}
				}
				line = reader.readLine();
			}

			if (!(chapterContentSet.isEmpty()) && parts != null && newChapter) {

				NLHChapters.add(new NLHChapter(parts[0], parts[1],
						chapterContentSet.toString()));
				parts = null;
				chapterContentSet = new HashSet<String>();
				newChapter = false;
			}

		} catch (IOException ex) {
			Logger.getLogger(CreateFSMFromChapter.class.getName()).log(
					Level.SEVERE, null, ex);

		} finally {
			try {
				fis.close();
				reader.close();
			} catch (IOException ex) {
				Logger.getLogger(CreateFSMFromChapter.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}
}
