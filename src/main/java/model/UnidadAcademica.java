package model;

/**
 * Entidad que representa una unidad tematica o modulo de aprendizaje
 * dentro del plan de estudios de una asignatura.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Modela los contenidos curriculares que se ubican en el nivel hoja del arbol
 * jerarquico bajo una asignatura especifica.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple el requerimiento de la rubrica de representar unidades academicas
 * y permite dotar de profundidad real (nivel 4) a la estructura de arbol general.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Hereda de ElementoAcademico con tipo UNIDAD y registra el numero de horas
 * estimadas y la competencia o resultado de aprendizaje asociado.</p>
 */
public class UnidadAcademica extends ElementoAcademico {

    private int numeroHoras;
    private String competencia;

    public UnidadAcademica(String codigo, String nombre, int numeroHoras, String competencia, String descripcion) {
        super(codigo, nombre, TipoElemento.UNIDAD, descripcion);
        if (numeroHoras <= 0 || numeroHoras > 120) {
            throw new IllegalArgumentException("El numero de horas de la unidad debe ser positivo y razonable (1-120).");
        }
        if (competencia == null || competencia.trim().isEmpty()) {
            throw new IllegalArgumentException("La competencia o resultado de aprendizaje no puede ser vacia.");
        }
        this.numeroHoras = numeroHoras;
        this.competencia = competencia.trim();
    }

    public int getNumeroHoras() {
        return numeroHoras;
    }

    public void setNumeroHoras(int numeroHoras) {
        if (numeroHoras <= 0 || numeroHoras > 120) {
            throw new IllegalArgumentException("El numero de horas de la unidad debe ser positivo (1-120).");
        }
        this.numeroHoras = numeroHoras;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        if (competencia == null || competencia.trim().isEmpty()) {
            throw new IllegalArgumentException("La competencia no puede ser vacia.");
        }
        this.competencia = competencia.trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d hrs) - Competencia: %s",
                getCodigo(), getNombre(), numeroHoras, competencia);
    }
}
