package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Estructura de datos propia de Grafo Dirigido Ponderado implementado mediante
 * listas de adyacencia sobre mapas asociativos.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Modela una red de relaciones dirigidas donde cada vertice representa un elemento
 * (asignatura) y cada arista representa una conexion unidireccional (prerrequisito),
 * administrando inserciones, eliminaciones limpias y consultas de grado.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple estrictamente con las Secciones 10, 11, 12 y 13 de la rubrica.
 * La relacion de prerrequisitos entre asignaturas no es jerarquica ni simetrica
 * (si A es prerrequisito de B, B no lo es de A), lo cual exige un grafo dirigido
 * sin librerias externas automaticas.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Almacena los vertices y sus aristas salientes en un {@code Map<V, List<Edge<V>>>}.
 * Al eliminar un vertice, purga tanto sus conexiones de salida como todas las aristas
 * entrantes desde otros vertices para mantener consistencia absoluta en la red.</p>
 *
 * @param <V> Tipo de dato de los vertices.
 */
public class Graph<V> {

    protected final Map<V, List<Edge<V>>> adjacencyMap;

    public Graph() {
        this.adjacencyMap = new LinkedHashMap<>();
    }

    /**
     * Operacion Obligatoria 1: Agregar un vertice.
     * Registra un nuevo elemento que puede participar en la red academica.
     *
     * @param vertex Vertice a incorporar.
     * @return {@code true} si el vertice fue agregado; {@code false} si ya existia.
     */
    public boolean addVertex(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("No se puede registrar un vertice nulo en el grafo.");
        }
        if (adjacencyMap.containsKey(vertex)) {
            return false;
        }
        adjacencyMap.put(vertex, new ArrayList<>());
        return true;
    }

    /**
     * Operacion Obligatoria 2: Eliminar un vertice.
     * Elimina el vertice y controla correctamente todas las conexiones dependientes:
     * remueve sus aristas salientes y purga todas las aristas entrantes en los demas vertices.
     *
     * @param vertex Vertice a eliminar.
     * @return {@code true} si se elimino exitosamente; {@code false} si no existia.
     */
    public boolean removeVertex(V vertex) {
        V actualVertex = findVertex(vertex);
        if (actualVertex == null) {
            return false;
        }

        // 1. Remover el vertice y sus aristas salientes
        adjacencyMap.remove(actualVertex);

        // 2. Purgar todas las aristas entrantes hacia este vertice en el resto del grafo
        for (List<Edge<V>> edges : adjacencyMap.values()) {
            edges.removeIf(edge -> Objects.equals(edge.getDestination(), actualVertex));
        }

        return true;
    }

    /**
     * Operacion Obligatoria 3: Agregar una conexion (arista dirigida).
     * Crea un vinculo unidireccional respetando la naturaleza del grafo dirigido
     * (source -> destination).
     *
     * @param source Vertice origen (prerrequisito habilitador).
     * @param destination Vertice destino (asignatura dependiente).
     * @param weight Peso o costo de la conexion (creditos, dificultad, etc.).
     * @param label Etiqueta descriptiva del vinculo.
     * @return {@code true} si la arista fue anadida; {@code false} si ya existia.
     */
    public boolean addEdge(V source, V destination, double weight, String label) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Los vertices origen y destino no pueden ser nulos.");
        }
        if (source.equals(destination)) {
            throw new IllegalArgumentException("No se permiten bucles reflexivos directos en la malla curricular.");
        }

        V actualSource = findVertex(source);
        if (actualSource == null) {
            addVertex(source);
            actualSource = source;
        }

        V actualDestination = findVertex(destination);
        if (actualDestination == null) {
            addVertex(destination);
            actualDestination = destination;
        }

        List<Edge<V>> edges = adjacencyMap.get(actualSource);
        for (Edge<V> edge : edges) {
            if (Objects.equals(edge.getDestination(), actualDestination)) {
                return false; // Arista ya existente
            }
        }

        edges.add(new Edge<>(actualDestination, weight, label));
        return true;
    }

    public boolean addEdge(V source, V destination, double weight) {
        return addEdge(source, destination, weight, "Prerrequisito Obligatorio");
    }

    public boolean addEdge(V source, V destination) {
        return addEdge(source, destination, 1.0, "Prerrequisito Obligatorio");
    }

    /**
     * Operacion Obligatoria 4: Eliminar una conexion.
     * Remueve la relacion dirigida existente entre source y destination.
     *
     * @param source Vertice origen.
     * @param destination Vertice destino.
     * @return {@code true} si se elimino la arista; {@code false} si no existia.
     */
    public boolean removeEdge(V source, V destination) {
        V actualSource = findVertex(source);
        V actualDestination = findVertex(destination);
        if (actualSource == null || actualDestination == null) {
            return false;
        }

        List<Edge<V>> edges = adjacencyMap.get(actualSource);
        if (edges == null) {
            return false;
        }
        return edges.removeIf(edge -> Objects.equals(edge.getDestination(), actualDestination));
    }

    /**
     * Operacion Obligatoria 5: Consultar conexiones directas (vecinos salientes).
     * Dado un vertice, retorna la lista de elementos directamente dependientes.
     *
     * @param vertex Vertice consultado.
     * @return Lista inmutable de vertices alcanzables directamente.
     */
    public List<V> getNeighbors(V vertex) {
        V actualVertex = findVertex(vertex);
        if (actualVertex == null) {
            return Collections.emptyList();
        }
        List<Edge<V>> edges = adjacencyMap.get(actualVertex);
        List<V> neighbors = new ArrayList<>();
        for (Edge<V> edge : edges) {
            neighbors.add(edge.getDestination());
        }
        return Collections.unmodifiableList(neighbors);
    }

    /**
     * Retorna las aristas salientes completas asociadas a un vertice.
     */
    public List<Edge<V>> getEdges(V vertex) {
        V actualVertex = findVertex(vertex);
        if (actualVertex == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(adjacencyMap.get(actualVertex));
    }

    /**
     * Operacion Obligatoria 6: Verificar conexion directa.
     * Determina si existe una arista dirigida directa desde source hacia destination.
     *
     * @param source Vertice origen.
     * @param destination Vertice destino.
     * @return {@code true} si existe la conexion directa; {@code false} en caso contrario.
     */
    public boolean hasDirectEdge(V source, V destination) {
        V actualSource = findVertex(source);
        V actualDestination = findVertex(destination);
        if (actualSource == null || actualDestination == null) {
            return false;
        }

        List<Edge<V>> edges = adjacencyMap.get(actualSource);
        if (edges == null) {
            return false;
        }
        for (Edge<V> edge : edges) {
            if (Objects.equals(edge.getDestination(), actualDestination)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Operacion Obligatoria 7a: Consultar Grado de Salida (Out-Degree).
     * En un grafo dirigido, indica cuantas conexiones parten desde este vertice
     * (cuantas asignaturas tienen a esta como prerrequisito directo).
     *
     * @param vertex Vertice a evaluar.
     * @return Grado de salida (out-degree) o -1 si el vertice no existe.
     */
    public int getOutDegree(V vertex) {
        V actualVertex = findVertex(vertex);
        if (actualVertex == null) {
            return -1;
        }
        return adjacencyMap.get(actualVertex).size();
    }

    /**
     * Operacion Obligatoria 7b: Consultar Grado de Entrada (In-Degree).
     * En un grafo dirigido, indica cuantas conexiones llegan a este vertice
     * (cuantos prerrequisitos directos exige esta materia para ser cursada).
     *
     * @param vertex Vertice a evaluar.
     * @return Grado de entrada (in-degree) o -1 si el vertice no existe.
     */
    public int getInDegree(V vertex) {
        V actualVertex = findVertex(vertex);
        if (actualVertex == null) {
            return -1;
        }
        int inDegree = 0;
        for (List<Edge<V>> edges : adjacencyMap.values()) {
            for (Edge<V> edge : edges) {
                if (Objects.equals(edge.getDestination(), actualVertex)) {
                    inDegree++;
                }
            }
        }
        return inDegree;
    }

    /**
     * Retorna los vertices que son prerrequisitos directos de este vertice (entrantes).
     */
    public List<V> getIncomingVertices(V vertex) {
        V actualVertex = findVertex(vertex);
        if (actualVertex == null) {
            return Collections.emptyList();
        }
        List<V> incoming = new ArrayList<>();
        for (Map.Entry<V, List<Edge<V>>> entry : adjacencyMap.entrySet()) {
            for (Edge<V> edge : entry.getValue()) {
                if (Objects.equals(edge.getDestination(), actualVertex)) {
                    incoming.add(entry.getKey());
                    break;
                }
            }
        }
        return Collections.unmodifiableList(incoming);
    }

    /**
     * Localiza la referencia real del vertice en el grafo admitiendo busqueda
     * por identificador gracias a la sobreescritura de equals polimorfico.
     */
    public V findVertex(Object identifier) {
        if (identifier == null) {
            return null;
        }
        for (V v : adjacencyMap.keySet()) {
            if (Objects.equals(v, identifier) || v.equals(identifier)) {
                return v;
            }
        }
        return null;
    }

    public boolean containsVertex(Object identifier) {
        return findVertex(identifier) != null;
    }

    public Set<V> getVertices() {
        return Collections.unmodifiableSet(adjacencyMap.keySet());
    }

    public int getVertexCount() {
        return adjacencyMap.size();
    }

    public int getEdgeCount() {
        int count = 0;
        for (List<Edge<V>> edges : adjacencyMap.values()) {
            count += edges.size();
        }
        return count;
    }

    public boolean isEmpty() {
        return adjacencyMap.isEmpty();
    }
}
