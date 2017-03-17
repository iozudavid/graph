package student_solution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import graph_entities.IEdge;
import graph_entities.IGraph;
import graph_entities.IVertex;
import graph_entities.Label;
import graph_entities.Result;

public class Graph<T> implements IGraph<T> {

	protected HashMap<String, IVertex<T>> nodes;

	public Graph() {
		this.nodes = new HashMap<String, IVertex<T>>();
	}

	@Override
	public void addVertex(String vertexId, IVertex<T> vertex) {

		if (nodes.containsKey(vertexId) == false) {
			vertex.getLabel().setName(vertexId);
			nodes.put(vertexId, vertex);
		}

	}

	@Override
	public void addEdge(String vertexSrcId, String vertexTgtId, Float cost) {
		if (nodes.containsKey(vertexSrcId) == true && nodes.containsKey(vertexTgtId) == true) {
			Edge<T> edge = new Edge<T>(getVertex(vertexTgtId), cost);
			getVertex(vertexSrcId).addEdge(edge);
		}
	}

	@Override
	public Collection<IVertex<T>> getVertices() {
		ArrayList<IVertex<T>> vertices = new ArrayList<IVertex<T>>();
		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			vertices.add(e.getValue());
		}
		return vertices;
	}

	@Override
	public Collection<String> getVertexIds() {
		ArrayList<String> vertices = new ArrayList<String>();
		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			vertices.add(e.getKey());
		}
		return vertices;
	}

	@Override
	public IVertex<T> getVertex(String vertexId) {
		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			if (e.getKey().equals(vertexId) == true) {
				return e.getValue();
			}
		}
		return null;
	}

	public String toDotRepresentation() {
		String s = "";
		s += "digraph {";
		s += "\n";
		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			s += e.getKey();
			s += "\n";
		}
		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			for (IEdge<T> edge : e.getValue().getSuccessors()) {
				s += e.getKey() + " -> " + edge.getTgt().getLabel().getName() + "[label=" + '"' + edge.getCost() + '"'
						+ "];";
				s += "\n";
			}
		}
		s += "}";
		return s;
	}

	public void fromDotRepresentation(String dotFilePath) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(dotFilePath));
			String s;
			while ((s = read.readLine()) != null) {
				s = s.replaceAll(" ", "");
				s = s.replaceAll("	", "");

				if (s.equals("") == false && s.contains("digraph{") == false && s.contains("}") == false) {
					if (s.contains("->") == false) {
						addVertex(s, new Vertex<T>());
					} else {
						int indexLine = s.indexOf('-');
						String SrcNode = s.substring(0, indexLine);
						int indexBracket = s.indexOf('[');
						String TgtNode = s.substring(indexLine + 2, indexBracket);
						int finalIndex = s.indexOf(';');
						String cost = s.substring(indexBracket + 8, finalIndex - 2);
						addEdge(SrcNode, TgtNode, Float.parseFloat(cost));
					}
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Result<T> breadthFirstSearchFrom(String vertexId, Predicate<IVertex<T>> pred) {
		ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> queue = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
		boolean succeed = true;

		if (nodes.containsKey(vertexId) == false) {
			return new Result<T>();
		}

		getVertex(vertexId).getLabel().setCost(0.0f);
		queue.add(getVertex(vertexId));

		while (true) {
			if (queue.size() == 0) {
				succeed = false;
				break;
			}
			visited.add(queue.get(0));
			if (pred.test(queue.get(0)) == true) {
				break;
			}

			ArrayList<IVertex<T>> successors = new ArrayList<IVertex<T>>();
			for (IEdge<T> edge : queue.get(0).getSuccessors()) {
				successors.add(edge.getTgt());
			}
			while (successors.isEmpty() == false) {

				if (visited.contains(successors.get(0)) == false && queue.contains(successors.get(0)) == false) {
					queue.add(successors.get(0));
					successors.get(0).getLabel().setParentVertex(queue.get(0));
					for (IEdge<T> edge : queue.get(0).getSuccessors()) {
						if (edge.getTgt().getLabel().getName().equals(successors.get(0).getLabel().getName()) == true) {
							successors.get(0).getLabel().setCost(queue.get(0).getLabel().getCost() + edge.getCost());
							break;
						}
					}

				}
				successors.remove(0);
			}

			queue.remove(0);
		}

		Result<T> result = new Result<T>();
		result.setVisitedVertices(visited);

		if (succeed == true) {

			path.add(queue.get(0));
			int i = 0;
			while (path.get(i).getLabel().getName().equals(vertexId) == false) {
				path.add(path.get(i).getLabel().getParentVertex().get());
				i++;
			}

			ArrayList<IVertex<T>> actualPath = new ArrayList<IVertex<T>>();
			for (i = path.size() - 1; i >= 0; i--) {
				actualPath.add(path.get(i));
			}
			result.setPath(actualPath);
			result.setPathCost(queue.get(0).getLabel().getCost());
		}
	
		return result;

	}

	public Result<T> depthFirstSearchFrom(String vertexId, Predicate<IVertex<T>> pred) {
		ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> queue = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
		boolean succeed = true;

		if (nodes.containsKey(vertexId) == false) {
			return new Result<T>();
		}

		getVertex(vertexId).getLabel().setCost(0.0f);
		queue.add(getVertex(vertexId));

		while (pred.test(queue.get(0)) == false) {
			boolean hasSuccessor = false;

			if (queue.size() == 0) {
				succeed = false;
				break;
			}

			if (visited.contains(queue.get(0)) == false)
				visited.add(queue.get(0));

			ArrayList<IVertex<T>> successors = new ArrayList<IVertex<T>>();
			for (IEdge<T> edge : queue.get(0).getSuccessors()) {
				successors.add(edge.getTgt());
			}
			while (successors.isEmpty() == false) {

				if (visited.contains(successors.get(0)) == false) {
					queue.add(successors.get(0));
					successors.get(0).getLabel().setParentVertex(queue.get(0));
					for (IEdge<T> edge : queue.get(0).getSuccessors()) {
						if (edge.getTgt().getLabel().getName().equals(successors.get(0).getLabel().getName()) == true) {
							successors.get(0).getLabel().setCost(queue.get(0).getLabel().getCost() + edge.getCost());
							break;
						}
					}
					hasSuccessor = true;
					break;
				}
				successors.remove(0);
			}
			if (hasSuccessor == false && queue.get(0).getLabel().getName().equals(vertexId)) {
				succeed = false;
				break;
			}
			if (hasSuccessor == false) {
				queue.add(queue.get(0).getLabel().getParentVertex().get());
			}
			queue.remove(0);
		}

		if (succeed == true && visited.contains(queue.get(0)) == false)
			visited.add(queue.get(0));

		Result<T> result = new Result<T>();

		result.setVisitedVertices(visited);

		if (succeed == true)
			result.setPathCost(queue.get(0).getLabel().getCost());

		if (succeed == true) {
			path.add(queue.get(0));
			int i = 0;
			while (path.get(i).getLabel().getName().equals(vertexId) == false) {
				path.add(path.get(i).getLabel().getParentVertex().get());
				i++;
			}

			ArrayList<IVertex<T>> actualPath = new ArrayList<IVertex<T>>();
			for (i = path.size() - 1; i >= 0; i--) {
				actualPath.add(path.get(i));
			}
			result.setPath(actualPath);
		}
	
		return result;

	}

	public Result<T> dijkstraFrom(String vertexId, Predicate<IVertex<T>> pred) {
		ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> queue = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
		boolean succeed = true;

		if (nodes.containsKey(vertexId) == false) {
			return new Result<T>();
		}

		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			e.getValue().getLabel().setCost(Float.MAX_VALUE);
		}

		getVertex(vertexId).getLabel().setCost(0.0f);
		queue.add(getVertex(vertexId));

		while (pred.test(queue.get(0)) == false) {

			visited.add(queue.get(0));

			ArrayList<IVertex<T>> successors = new ArrayList<IVertex<T>>();
			for (IEdge<T> edge : queue.get(0).getSuccessors()) {
				successors.add(edge.getTgt());
			}
			while (successors.isEmpty() == false) {

				if (visited.contains(successors.get(0)) == false) {
					if (queue.contains(successors.get(0)) == false)
						queue.add(successors.get(0));
					for (IEdge<T> edge : queue.get(0).getSuccessors()) {
						if (edge.getTgt().getLabel().getName().equals(successors.get(0).getLabel().getName()) == true) {
							if (successors.get(0).getLabel().getCost() > queue.get(0).getLabel().getCost()
									+ edge.getCost()) {
								successors.get(0).getLabel()
										.setCost(queue.get(0).getLabel().getCost() + edge.getCost());
								successors.get(0).getLabel().setParentVertex(queue.get(0));
								break;
							}
						}
					}
				}
				successors.remove(0);
			}

			queue.remove(0);

			if (queue.size() == 0) {
				succeed = false;
				break;
			}

			selectionSortOpenList(queue);
		}

		if (succeed == true)
			visited.add(queue.get(0));

		Result<T> result = new Result<T>();

		result.setVisitedVertices(visited);

		if (succeed == true)
			result.setPathCost(queue.get(0).getLabel().getCost());

		if (succeed == true) {
			path.add(queue.get(0));
			int i = 0;
			while (path.get(i).getLabel().getName().equals(vertexId) == false) {
				path.add(path.get(i).getLabel().getParentVertex().get());
				i++;
			}

			ArrayList<IVertex<T>> actualPath = new ArrayList<IVertex<T>>();
			for (i = path.size() - 1; i >= 0; i--) {
				actualPath.add(path.get(i));
			}
			result.setPath(actualPath);
		}
	
		return result;
	}

	public Result<T> aStar(String startVertexId, String endVertexId,
			BiFunction<IVertex<T>, IVertex<T>, Float> heuristics) {

		ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> queue = new ArrayList<IVertex<T>>();
		ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
		boolean succeed = true;

		if (nodes.containsKey(startVertexId) == false || nodes.containsKey(endVertexId) == false) {
			return new Result<T>();
		}

		for (Entry<String, IVertex<T>> e : nodes.entrySet()) {
			e.getValue().getLabel().setCost(Float.MAX_VALUE);
		}

		getVertex(startVertexId).getLabel().setParentVertex(getVertex(startVertexId));
		getVertex(startVertexId).getLabel().setCost(heuristics.apply(getVertex(startVertexId), getVertex(endVertexId)));
		queue.add(getVertex(startVertexId));

		while (queue.get(0).getLabel().getName().equals(endVertexId) == false) {

			visited.add(queue.get(0));

			ArrayList<IVertex<T>> successors = new ArrayList<IVertex<T>>();
			for (IEdge<T> edge : queue.get(0).getSuccessors()) {
				successors.add(edge.getTgt());
			}
			while (successors.isEmpty() == false) {

				if (visited.contains(successors.get(0)) == false) {
					if (queue.contains(successors.get(0)) == false)
						queue.add(successors.get(0));
					for (IEdge<T> edge : queue.get(0).getSuccessors()) {
						if (edge.getTgt().getLabel().getName().equals(successors.get(0).getLabel().getName()) == true) {

							if (successors.get(0).getLabel().getCost() > queue.get(0).getLabel().getCost()
									+ edge.getCost() - heuristics.apply(queue.get(0), getVertex(endVertexId))) {
								successors.get(0).getLabel().setCost(queue.get(0).getLabel().getCost()
										- heuristics.apply(queue.get(0), getVertex(endVertexId)) + edge.getCost()
										+ heuristics.apply(successors.get(0), getVertex(endVertexId)));
								successors.get(0).getLabel().setParentVertex(queue.get(0));
								break;
							}

						}
					}
				}
				successors.remove(0);
			}

			queue.remove(0);

			if (queue.size() == 0) {
				succeed = false;
				break;
			}

			selectionSortOpenList(queue);
		}

		if (succeed == true)
			visited.add(queue.get(0));

		Result<T> result = new Result<T>();

		result.setVisitedVertices(visited);

		if (succeed == true) {
			path.add(queue.get(0));
			int i = 0;
			while (path.get(i).getLabel().getName().equals(startVertexId) == false) {
				path.add(path.get(i).getLabel().getParentVertex().get());
				i++;
			}

			ArrayList<IVertex<T>> actualPath = new ArrayList<IVertex<T>>();
			for (i = path.size() - 1; i >= 0; i--) {
				actualPath.add(path.get(i));
			}
			result.setPath(actualPath);
		}

		if (succeed == true)
			result.setPathCost(
					queue.get(0).getLabel().getCost() - heuristics.apply(queue.get(0), getVertex(endVertexId)));
	
		return result;

	}

	public void selectionSortOpenList(ArrayList<IVertex<T>> list) {
		for (int i = 0; i < list.size(); i++) {
			int index = i;
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(j).getLabel().getCost() < list.get(index).getLabel().getCost()) {
					index = j;
				}
			}
			IVertex<T> aux = list.get(index);
			list.set(index, list.get(i));
			list.set(i, aux);
		}
	}

}
