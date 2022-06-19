package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private GenesDao dao;
	private List<Adiacenza> archi;
	private List<Integer> solOttima;
	private double sommaOttima;
	
	public Model() {
		dao = new GenesDao();
		solOttima = new ArrayList<>();
		sommaOttima = 0;

	}
	
	public void creaGrafo() {
		grafo = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, dao.getAllChromosomes());
		
		archi = new ArrayList<Adiacenza>(dao.getArchi());
		
		Collections.sort(archi, Comparator.comparing(Adiacenza::getPeso));
		
		for(Adiacenza a : archi) {
			Graphs.addEdge(grafo, a.getC1(), a.getC2(), a.getPeso());
		}
		
		System.out.println("Numero vertici : "+grafo.vertexSet().size());
		System.out.println("Numero archi : "+grafo.edgeSet().size());
	}
	
	public double getPesoMaggiore() {
		return archi.get(archi.size()-1).getPeso();
	}
	
	public double getPesoMinore() {
		return archi.get(0).getPeso();
	}
	
	public boolean rientraNellIntervallo(double s) {
		if(s<=getPesoMaggiore() && s>=getPesoMinore()) {
			System.out.println(true);
			return true;
		}
		System.out.println(false);
		return false;
	}
	
	public int contaArchiMaggiori(double s) {
		int ret = 0;
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>s)
				ret++;
		}
		System.out.println(ret);
		return ret;
	}
	
	public int contaArchiMinori(double s) {
		int ret = 0;
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)<s)
				ret++;
		}
		System.out.println(ret);
		return ret;
	}

	//punto 2
	
	public List<Integer> ricerca(double s) {
		List<Integer> parziale = new ArrayList<>();
		
		ricercaRicorsiva(parziale, s, grafo.vertexSet());
		
		System.out.println("La sol ottima è : "+ solOttima+" e il suo peso è : "+sommaOttima);
		return solOttima;
		
	}

	private void ricercaRicorsiva(List<Integer> parziale, double s, Set<Integer> possibili) {
		if(somma(parziale)>sommaOttima) {		
			sommaOttima = somma(parziale);
			solOttima = new ArrayList<>(parziale);
		}
		
		for(Integer c : possibili) {
			parziale.add(c);
			ricercaRicorsiva(parziale, s, controllo(parziale, s, possibili));
			parziale.remove(parziale.size()-1);
		}
	}

	

	private Set<Integer> controllo(List<Integer> parziale, double s, Set<Integer> possibili) {
		Set<Integer> candidati = new HashSet<>();
		
		for(Integer i : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(i) && grafo.containsEdge(parziale.get(parziale.size()-1), i)) {
				double peso = grafo.getEdgeWeight(grafo.getEdge(parziale.get(parziale.size()-1), i));
				if(peso>s) {
					candidati.add(i);
				}
			}
		}
		
		return candidati;
	}

	private double somma(List<Integer> parziale) {
		double somma = 0;
		
		if(parziale.size()==0 || parziale.size()==1){
			return 0;
		}
		
		for(int i=0; i<parziale.size()-1; i++) {
			double peso = grafo.getEdgeWeight(grafo.getEdge(parziale.get(i), parziale.get(i+1)));
			somma += peso;
		}
		
		return somma;
	}

	public double getSommaOttima() {
		return sommaOttima;
	}









}