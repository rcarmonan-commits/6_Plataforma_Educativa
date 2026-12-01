package model;

import java.util.Objects;

/**
 * Clase base que representa cualquier elemento dentro de la jerarquia academica
 * universitaria (Facultad, Programa, Semestre, Asignatura o Unidad).
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Modela la abstraccion base de los nodos del arbol institucional con identificador
 * unico, nombre formal, tipo academico y descripcion descriptiva.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Permite genericidad y polimorfismo en la estructura de arbol, manteniendo
 * contratos rigurosos de validacion defensiva y evitando duplicidad de atributos.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Aplica inmutabilidad en el codigo de identificacion, valida campos obligatorios
 * en el constructor y sobreescribe equals() con soporte polimorfico para cadenas.</p>
 */
public class ElementoAcademico implements Comparable<ElementoAcademico> {

    private final String codigo;
    private String nombre;
    private final TipoElemento tipo;
    private String descripcion;

    public ElementoAcademico(String codigo, String nombre, TipoElemento tipo, String descripcion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo del elemento academico no puede ser nulo ni vacio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del elemento academico no puede ser nulo ni vacio.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de elemento academico es obligatorio.");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.nombre = nombre.trim();
        this.tipo = tipo;
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre modificado no puede ser vacio.");
        }
        this.nombre = nombre.trim();
    }

    public TipoElemento getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
    }

    /**
     * Comparacion polimorfica que admite tanto otra entidad academica como una
     * cadena String representativa del codigo, facilitando busquedas directas.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return this.codigo.equalsIgnoreCase(((String) obj).trim());
        }
        if (obj instanceof ElementoAcademico) {
            ElementoAcademico other = (ElementoAcademico) obj;
            return this.codigo.equalsIgnoreCase(other.codigo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.toUpperCase());
    }

    @Override
    public int compareTo(ElementoAcademico otro) {
        if (otro == null) {
            return 1;
        }
        int comparacionTipo = Integer.compare(this.tipo.getNivelJerarquico(), otro.tipo.getNivelJerarquico());
        if (comparacionTipo != 0) {
            return comparacionTipo;
        }
        return this.codigo.compareToIgnoreCase(otro.codigo);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", codigo, nombre, tipo.getEtiqueta());
    }
}
