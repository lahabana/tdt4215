package com.ntnu.tdt4215.parser;

import java.util.ArrayList;
import java.util.Iterator;

import com.ntnu.tdt4215.document.NLHChapter.NLHChapter;

/**
 * This is an example FSM in order to show how to use it. It also enables testing
 * @author charlymolter
 *
 */
public class BasicFSM implements IndexingFSM {

	protected ArrayList<NLHChapter> elts;
	protected Iterator<NLHChapter> iterator;
	
	public BasicFSM() {
		elts = new ArrayList<NLHChapter>();
		NLHChapter doc = new NLHChapter("G1 Farmakodynamikk", "Farmakodynamikk beskriver og forklarer effekter av legemidler. Dette omfatter biokjemiske effekter i celler og fysiologiske effekter i vev og organer, men også effekter hos enkeltindivider og grupper av individer. Oftest forbinder man farmakodynamikk med ønskede effekter av legemidler, men farmakodynamikken gir også bakgrunn for å forstå bivirkninger og visse typer interaksjoner. Se også G5 Bivirkninger og legemiddelovervåking og G6 Interaksjoner.");
		elts.add(doc);
		doc = new NLHChapter("G3 Legemiddelbruk og -dosering ved nedsatt nyrefunksjon", "Nedsatt nyrefunksjonPublisert: 21.12.2012 Nedsatt nyrefunksjon (nyresvikt) medfører betydelige forandringer både i nyrenes ekskretoriske funksjon og sekundært i resten av kroppen. Dette gir endringer i farmakokinetikk og virkning av legemidler og krever dosejustering for mange legemidler. Legemidler kan også utløse eller forverre nyresvikt og kan forsterke symptomer som følge av svikten. Nyresvikt kan utvikles gradvis hos pasienter etter årelang vedlikeholdsbehandling (f.eks. nedsatt nyrefunksjon med økende alder). Mål på nyrefunksjon Serumkreatinin er et relativt grovt mål på nyrefunksjon og gjenspeiler ved normal nyrefunksjon balansen mellom endogen produksjon (muskelmasse) og renal utskillelse, hovedsakelig ved glomerulær filtrasjon (GFR). Tubulær sekresjon av kreatinin bidrar lite ved lavt nivå av kreatinin i plasma, men mer ved høyere nivå. Kreatininclearance (ClKr) er et estimat på GFR, og kan beregnes ut fra følgende formel:");		
		elts.add(doc);
		doc = new NLHChapter("L1.1 Om bruk av antimikrobielle midler", "Antibakterielle midler og soppmidler inndeles ofte i baktericide/fungicide midler som dreper mikroben, og bakteriostatiske/fungistatiske midler som bare hemmer mikrobens vekst. Dette skillet er ikke absolutt, og antimikrobielle midler vil ofte ha ulike egenskaper overfor ulike grupper av mikrober. ");
		elts.add(doc);
	}
	
	public void initialize() {
		iterator = elts.iterator();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public NLHChapter next() {
		return iterator.next();
	}

	public void finish() {
		// We have nothing to do
	}

	public void remove() {
		// We don't do this
	}

}
