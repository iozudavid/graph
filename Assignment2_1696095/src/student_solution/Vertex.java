package student_solution;


import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import graph_entities.IEdge;
import graph_entities.IVertex;
import graph_entities.Label;

public class Vertex<T> implements IVertex<T>{
	
	ArrayList<IEdge<T>> edges;
	Label<T> label;
	
	public Vertex(){
		edges=new ArrayList<IEdge<T>>();
		label=new Label<T>();
	}
	
	@Override
	public int compareTo(IVertex<T> arg0) {
		if(this.getLabel().getCost().compareTo(arg0.getLabel().getCost())==1){
			return 1;
		}
		else if(this.getLabel().getCost().compareTo(arg0.getLabel().getCost())==-1){
			return -1;
		}
		else
			return 0;
	}

	@Override
	public void addEdge(IEdge<T> edge) {
		edges.add(edge);
	}

	@Override
	public Collection<IEdge<T>> getSuccessors() {
		return edges;
	}

	@Override
	public Label<T> getLabel() {
		return label;
	}

	@Override
	public void setLabel(Label<T> label) {
		this.label=label;
		
	}



}