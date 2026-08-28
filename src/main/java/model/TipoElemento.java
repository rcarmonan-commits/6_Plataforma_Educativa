package model;

/**
 * Enumeracion que define la tipologia y jerarquia de los elementos academicos
 * que conforman la estructura organizativa de la plataforma educativa.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Define los 5 niveles canonicos de la organizacion universitaria: Facultad (raiz),
 * Programa, Semestre, Asignatura y Unidad Academica (hojas).</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Garantiza integridad en el arbol n-ario, permitiendo validar las relaciones
 * padre-hijo segun la jerarquia institucional y calcular niveles esperados.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Cada constante posee un nivel jerarquico numerico (0 a 4) y una etiqueta
 * formal para visualizacion en consola y reportes.</p>
 */
public enum TipoElemento {
    FACULTAD(0, "Facultad"),
    PROGRAMA(1, "Programa Academico"),
    SEMESTRE(2, "Semestre Academico"),
    ASIGNATURA(3, "Asignatura"),
    UNIDAD(4, "Unidad Tematica");

    private final int nivelJerarquico;
    private final String etiqueta;

    TipoElemento(int nivelJerarquico, String etiqueta) {
        this.nivelJerarquico = nivelJerarquico;
        this.etiqueta = etiqueta;
    }

    public int getNivelJerarquico() {
        return nivelJerarquico;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Valida si un tipo de elemento puede ser legalmente hijo de otro
     * respetando el orden institucional estricto.
     */
    public boolean puedeSerHijoDe(TipoElemento padre) {
        if (padre == null) {
            return this == FACULTAD;
        }
        return this.nivelJerarquico == padre.nivelJerarquico + 1;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
