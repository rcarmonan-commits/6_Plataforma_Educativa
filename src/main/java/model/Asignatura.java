package model;

/**
 * Entidad principal que representa una asignatura del plan de estudios.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Modela la materia universitaria que participa simultaneamente como nodo
 * intermedio en el arbol jerarquico y como vertice central en el grafo de prerrequisitos.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Es el eje de integracion entre ambas estructuras de datos, cumpliendo la exigencia
 * de la rubrica de que un mismo elemento exista con coherencia funcional en el sistema.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Hereda de ElementoAcademico con tipo ASIGNATURA y anade atributos curriculares
 * como numero de creditos, semestre academico y area de conocimiento.</p>
 */
public class Asignatura extends ElementoAcademico {

    private int creditos;
    private int numeroSemestre;
    private String areaConocimiento;

    public Asignatura(String codigo, String nombre, int creditos, int numeroSemestre,
                      String areaConocimiento, String descripcion) {
        super(codigo, nombre, TipoElemento.ASIGNATURA, descripcion);
        if (creditos <= 0 || creditos > 10) {
            throw new IllegalArgumentException("Los creditos academicos deben estar en el rango de 1 a 10.");
        }
        if (numeroSemestre <= 0 || numeroSemestre > 12) {
            throw new IllegalArgumentException("El numero de semestre debe ser un valor valido entre 1 y 12.");
        }
        if (areaConocimiento == null || areaConocimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El area de conocimiento no puede ser nula ni vacia.");
        }
        this.creditos = creditos;
        this.numeroSemestre = numeroSemestre;
        this.areaConocimiento = areaConocimiento.trim();
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        if (creditos <= 0 || creditos > 10) {
            throw new IllegalArgumentException("Los creditos academicos deben estar en el rango de 1 a 10.");
        }
        this.creditos = creditos;
    }

    public int getNumeroSemestre() {
        return numeroSemestre;
    }

    public void setNumeroSemestre(int numeroSemestre) {
        if (numeroSemestre <= 0 || numeroSemestre > 12) {
            throw new IllegalArgumentException("El numero de semestre debe ser valido (1-12).");
        }
        this.numeroSemestre = numeroSemestre;
    }

    public String getAreaConocimiento() {
        return areaConocimiento;
    }

    public void setAreaConocimiento(String areaConocimiento) {
        if (areaConocimiento == null || areaConocimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El area de conocimiento no puede ser vacia.");
        }
        this.areaConocimiento = areaConocimiento.trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Semestre %d | %d Creditos (%s)",
                getCodigo(), getNombre(), numeroSemestre, creditos, areaConocimiento);
    }
}
