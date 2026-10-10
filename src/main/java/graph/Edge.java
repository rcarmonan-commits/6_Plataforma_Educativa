package graph;

import java.util.Objects;

/**
 * Representa una arista o conexion dirigida dentro del grafo ponderado.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Modela el vinculo dirigido entre un vertice origen implicito y un vertice
 * destino explicito, asociando un peso numerico y una etiqueta descriptiva.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple con el requerimiento de la Seccion 12 de la rubrica. En una malla
 * curricular, una arista representa un prerrequisito academico hacia otra asignatura,
 * donde el peso puede representar los creditos exigidos o la dificultad relativa.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Encapsula el vertice destino y el peso, proporcionando inmutabilidad
 * y contratos consistentes de comparacion.</p>
 *
 * @param <T> Tipo de dato del vertice destino.
 */
public class Edge<T> {

    private final T destination;
    private final double weight;
    private final String label;

    public Edge(T destination, double weight, String label) {
        if (destination == null) {
            throw new IllegalArgumentException("El vertice destino de una arista no puede ser nulo.");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("El peso de la conexion no puede ser negativo.");
        }
        this.destination = destination;
        this.weight = weight;
        this.label = (label != null && !label.trim().isEmpty()) ? label.trim() : "Prerrequisito Obligatorio";
    }

    public Edge(T destination, double weight) {
        this(destination, weight, "Prerrequisito Obligatorio");
    }

    public Edge(T destination) {
        this(destination, 1.0, "Prerrequisito Obligatorio");
    }

    public T getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Edge<?> other = (Edge<?>) obj;
        return Objects.equals(destination, other.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination);
    }

    @Override
    public String toString() {
        return String.format("-> %s (peso: %.1f, tipo: %s)", destination, weight, label);
    }
}
