package student_solution;

import graph_entities.IVertex;
import graph_entities.IEdge;

 public class Edge<T> implements IEdge<T>{
	 
	private IVertex<T> target;
	private Float cost;

	public Edge(IVertex<T> target, Float cost){
		 this.target=target;
		 this.cost=cost;
	 }

	@Override
	public IVertex<T> getTgt() {
		return target;
	}

	@Override
	public Float getCost() {
		return cost;
	}


	 
}