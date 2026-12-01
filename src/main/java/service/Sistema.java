package service;

import graph.Edge;
import graph.Graph;
import model.Asignatura;
import model.ElementoAcademico;
import model.TipoElemento;
import model.UnidadAcademica;
import tree.Tree;
import tree.TreeNode;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Servicio central y orquestador integral de la plataforma educativa.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Unifica e integra en una sola aplicacion la estructura jerarquica del arbol
 * institucional (Facultad -> Programa -> Semestre -> Asignatura -> Unidad) y la red
 * relacional del grafo dirigido de prerrequisitos academicos, administrando la consistencia
 * transaccional y precargando los datos de prueba oficiales de la Universidad de Cartagena.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple estrictamente con la Seccion 17 ("Integracion entre Arbol y Grafo") y la
 * Seccion 21 ("Datos minimos para las pruebas") de la rubrica, evitando programas
 * separados y garantizando que las asignaturas compartan identidad en ambas estructuras.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Mantiene instancias sincronizadas de {@link Tree} y {@link Graph}. Al insertar o
 * eliminar una asignatura en la jerarquia, se actualiza automaticamente el grafo
 * para evitar vertices o aristas huerfanas.</p>
 */
public class Sistema {

    private final Tree<ElementoAcademico> arbolAcademico;
    private final Graph<Asignatura> grafoPrerrequisitos;

    public Sistema() {
        this.arbolAcademico = new Tree<>();
        this.grafoPrerrequisitos = new Graph<>();
        precargarDatosInstitucionales();
    }

    public Tree<ElementoAcademico> getArbolAcademico() {
        return arbolAcademico;
    }

    public Graph<Asignatura> getGrafoPrerrequisitos() {
        return grafoPrerrequisitos;
    }

    /**
     * Precarga los datos iniciales de prueba de la Facultad de Ingenieria (Universidad de Cartagena).
     * Supera con holgura los minimos exigidos por la rubrica:
     * - Arbol: 32 nodos distribuidos en 5 niveles (minimo exigido: 15).
     * - Grafo: 14 asignaturas (vertices) y 20 conexiones de prerrequisito (minimo: 12 vertices y 18 aristas).
     */
    public final void precargarDatosInstitucionales() {
        // 1. RAIZ (Nivel 0)
        ElementoAcademico facultad = new ElementoAcademico(
                "FAC-ING", "Facultad de Ingenieria", TipoElemento.FACULTAD,
                "Unidad academica superior de la Universidad de Cartagena");
        arbolAcademico.insert(null, facultad);

        // 2. PROGRAMAS ACADEMICOS (Nivel 1)
        ElementoAcademico progSoftware = new ElementoAcademico(
                "PROG-SOFT", "Ingenieria de Software", TipoElemento.PROGRAMA,
                "Programa acreditado en alta calidad - Modalidad Distancia/PESAD");
        ElementoAcademico progCivil = new ElementoAcademico(
                "PROG-CIVIL", "Ingenieria Civil", TipoElemento.PROGRAMA,
                "Programa de infraestructura y construccion");
        arbolAcademico.insert("FAC-ING", progSoftware);
        arbolAcademico.insert("FAC-ING", progCivil);

        // 3. SEMESTRES ACADEMICOS (Nivel 2)
        ElementoAcademico sem1 = new ElementoAcademico("SEM-01", "Semestre I", TipoElemento.SEMESTRE, "Primer nivel formativo");
        ElementoAcademico sem2 = new ElementoAcademico("SEM-02", "Semestre II", TipoElemento.SEMESTRE, "Segundo nivel formativo");
        ElementoAcademico sem3 = new ElementoAcademico("SEM-03", "Semestre III", TipoElemento.SEMESTRE, "Tercer nivel formativo");
        ElementoAcademico sem4 = new ElementoAcademico("SEM-04", "Semestre IV", TipoElemento.SEMESTRE, "Cuarto nivel formativo");
        ElementoAcademico sem5 = new ElementoAcademico("SEM-05", "Semestre V", TipoElemento.SEMESTRE, "Quinto nivel formativo");

        arbolAcademico.insert("PROG-SOFT", sem1);
        arbolAcademico.insert("PROG-SOFT", sem2);
        arbolAcademico.insert("PROG-SOFT", sem3);
        arbolAcademico.insert("PROG-SOFT", sem4);
        arbolAcademico.insert("PROG-SOFT", sem5);

        ElementoAcademico semCiv1 = new ElementoAcademico("SEM-CIV-01", "Semestre I (Civil)", TipoElemento.SEMESTRE, "Nivel inicial civil");
        ElementoAcademico semCiv2 = new ElementoAcademico("SEM-CIV-02", "Semestre II (Civil)", TipoElemento.SEMESTRE, "Nivel secundario civil");
        arbolAcademico.insert("PROG-CIVIL", semCiv1);
        arbolAcademico.insert("PROG-CIVIL", semCiv2);

        // 4. ASIGNATURAS DE INGENIERIA DE SOFTWARE (Nivel 3)
        // Semestre I
        Asignatura fp = new Asignatura("ASIG-FP", "Fundamentos de Programacion", 3, 1, "Ciencias de la Computacion", "Logica y algoritmos");
        Asignatura cal1 = new Asignatura("ASIG-CAL1", "Calculo I", 4, 1, "Ciencias Basicas", "Diferencial e integral basico");
        Asignatura alg = new Asignatura("ASIG-ALG", "Algebra Lineal", 3, 1, "Ciencias Basicas", "Matrices y espacios vectoriales");

        // Semestre II
        Asignatura poo = new Asignatura("ASIG-POO", "Programacion I: POO", 3, 2, "Ciencias de la Computacion", "Paradigmas orientados a objetos");
        Asignatura cal2 = new Asignatura("ASIG-CAL2", "Calculo II", 4, 2, "Ciencias Basicas", "Integrales y series");
        Asignatura fism = new Asignatura("ASIG-FISM", "Fisica Mecanica", 3, 2, "Ciencias Basicas", "Leyes de Newton y cinematica");
        Asignatura disc = new Asignatura("ASIG-DISC", "Estructuras Discretas", 3, 2, "Matematicas Computacionales", "Conjuntos, logica y grafos base");

        // Semestre III (¡Incluye el caso exacto de la rubrica: Programacion II -> Estructuras de Datos!)
        Asignatura pr2 = new Asignatura("ASIG-PR2", "Programacion II", 3, 3, "Ciencias de la Computacion", "Patrones y diseno avanzado");
        Asignatura ed = new Asignatura("ASIG-ED", "Estructuras de Datos", 4, 3, "Ciencias de la Computacion", "Arboles, grafos y memoria");

        // Semestre IV
        Asignatura bd1 = new Asignatura("ASIG-BD1", "Bases de Datos I", 3, 4, "Sistemas de Informacion", "Modelado relacional y SQL");
        Asignatura is1 = new Asignatura("ASIG-IS1", "Ingenieria de Software I", 4, 4, "Ingenieria de Software", "Ciclos de vida y requisitos");
        Asignatura aa = new Asignatura("ASIG-AA", "Analisis de Algoritmos", 3, 4, "Ciencias de la Computacion", "Complejidad asintotica y diseno algoritmico");

        // Semestre V
        Asignatura bd2 = new Asignatura("ASIG-BD2", "Bases de Datos II", 3, 5, "Sistemas de Informacion", "NoSQL y optimizacion transaccional");
        Asignatura is2 = new Asignatura("ASIG-IS2", "Ingenieria de Software II", 4, 5, "Ingenieria de Software", "Arquitectura, pruebas y DevOps");

        // Insertar en Arbol
        arbolAcademico.insert("SEM-01", fp);
        arbolAcademico.insert("SEM-01", cal1);
        arbolAcademico.insert("SEM-01", alg);

        arbolAcademico.insert("SEM-02", poo);
        arbolAcademico.insert("SEM-02", cal2);
        arbolAcademico.insert("SEM-02", fism);
        arbolAcademico.insert("SEM-02", disc);

        arbolAcademico.insert("SEM-03", pr2);
        arbolAcademico.insert("SEM-03", ed);

        arbolAcademico.insert("SEM-04", bd1);
        arbolAcademico.insert("SEM-04", is1);
        arbolAcademico.insert("SEM-04", aa);

        arbolAcademico.insert("SEM-05", bd2);
        arbolAcademico.insert("SEM-05", is2);

        // Asignaturas complementarias en Civil
        ElementoAcademico estCivil = new Asignatura("ASIG-CIV-EST", "Estatica", 3, 1, "Estructuras", "Equilibrio mecanico");
        ElementoAcademico topCivil = new Asignatura("ASIG-CIV-TOP", "Topografia", 3, 2, "Geodesia", "Levantamientos de terrenos");
        arbolAcademico.insert("SEM-CIV-01", estCivil);
        arbolAcademico.insert("SEM-CIV-02", topCivil);

        // 5. UNIDADES ACADEMICAS TEMATICAS (Nivel 4)
        UnidadAcademica u1Ed = new UnidadAcademica("UNI-ED-01", "Unidad I: Recursion y Complejidad", 16, "Analizar algoritmos en Big-O", "Fundamentos");
        UnidadAcademica u2Ed = new UnidadAcademica("UNI-ED-02", "Unidad II: Listas, Pilas y Colas", 24, "Implementar estructuras dinamicas lineales", "Lineales");
        UnidadAcademica u3Ed = new UnidadAcademica("UNI-ED-03", "Unidad III: Arboles y Jerarquias", 28, "Modelar problemas con arboles generales y recorridos", "Jerarquias");
        UnidadAcademica u4Ed = new UnidadAcademica("UNI-ED-04", "Unidad IV: Grafos y Redes de Dependencia", 28, "Resolver problemas de conexiones y caminos", "Grafos");

        arbolAcademico.insert("ASIG-ED", u1Ed);
        arbolAcademico.insert("ASIG-ED", u2Ed);
        arbolAcademico.insert("ASIG-ED", u3Ed);
        arbolAcademico.insert("ASIG-ED", u4Ed);

        UnidadAcademica u1Pr2 = new UnidadAcademica("UNI-PR2-01", "Unidad I: Genericos y Java Collections", 20, "Aplicar tipado fuerte y colecciones avanzadas", "Genericos");
        UnidadAcademica u2Pr2 = new UnidadAcademica("UNI-PR2-02", "Unidad II: Concurrencia y Eventos", 24, "Programar hilos y flujos de ejecucion", "Hilos");
        arbolAcademico.insert("ASIG-PR2", u1Pr2);
        arbolAcademico.insert("ASIG-PR2", u2Pr2);

        // 6. INCORPORAR ASIGNATURAS AL GRAFO DE PRERREQUISITOS
        Asignatura[] listaAsignaturas = {fp, cal1, alg, poo, cal2, fism, disc, pr2, ed, bd1, is1, aa, bd2, is2};
        for (Asignatura a : listaAsignaturas) {
            grafoPrerrequisitos.addVertex(a);
        }

        // 7. CONEXIONES DE PRERREQUISITOS DIRIGIDOS (20 Conexiones)
        // Origen -> Destino (peso en creditos academicos)
        grafoPrerrequisitos.addEdge(fp, poo, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(fp, disc, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(cal1, cal2, 4.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(cal1, fism, 4.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(alg, disc, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(poo, pr2, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(pr2, ed, 3.0, "Prerrequisito Obligatorio"); // <-- Caso de la guia
        grafoPrerrequisitos.addEdge(disc, ed, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(cal2, ed, 4.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(pr2, is1, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(ed, bd1, 4.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(ed, aa, 4.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(poo, bd1, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(bd1, bd2, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(is1, is2, 4.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(bd1, is2, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(disc, aa, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(fp, pr2, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(bd2, is2, 3.0, "Prerrequisito Obligatorio");
        grafoPrerrequisitos.addEdge(aa, is2, 3.0, "Prerrequisito Obligatorio");
    }

    /**
     * Operacion Sincronizada 1: Registrar un nuevo elemento en la plataforma.
     * Si el elemento es una Asignatura, se incorpora tanto al arbol jerarquico
     * como a la red del grafo de prerrequisitos de manera coordinada.
     */
    public boolean registrarElemento(String parentCode, ElementoAcademico elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento a registrar no puede ser nulo.");
        }
        TreeNode<ElementoAcademico> inserted = arbolAcademico.insert(parentCode, elemento);
        if (inserted != null && elemento instanceof Asignatura) {
            grafoPrerrequisitos.addVertex((Asignatura) elemento);
        }
        return inserted != null;
    }

    /**
     * Operacion Sincronizada 2: Eliminar un elemento de la plataforma.
     * Remueve el elemento del arbol y purga en cascada cualquier asignatura afectada
     * de la red del grafo de prerrequisitos para evitar inconsistencias o enlaces huerfanos.
     */
    public List<ElementoAcademico> eliminarElemento(String code, boolean cascade) {
        List<ElementoAcademico> removidos = arbolAcademico.delete(code, cascade);
        for (ElementoAcademico elem : removidos) {
            if (elem instanceof Asignatura) {
                grafoPrerrequisitos.removeVertex((Asignatura) elem);
            }
        }
        return removidos;
    }

    /**
     * Localiza un elemento academico en el arbol por su codigo unico.
     */
    public ElementoAcademico buscarElemento(String code) {
        TreeNode<ElementoAcademico> node = arbolAcademico.findNode(code);
        return (node != null) ? node.getData() : null;
    }

    /**
     * Modifica los datos de un elemento sin comprometer su posicion jerarquica.
     */
    public boolean modificarElemento(String code, Consumer<ElementoAcademico> mutator) {
        return arbolAcademico.modify(code, mutator);
    }

    /**
     * Vincula un prerrequisito dirigido entre dos asignaturas del plan de estudios.
     */
    public boolean conectarPrerrequisito(String codeSource, String codeDest, double weight, String label) {
        Asignatura source = grafoPrerrequisitos.findVertex(codeSource);
        Asignatura dest = grafoPrerrequisitos.findVertex(codeDest);
        if (source == null || dest == null) {
            throw new IllegalArgumentException("Ambas asignaturas deben existir en la plataforma para crear la conexion.");
        }
        return grafoPrerrequisitos.addEdge(source, dest, weight, label);
    }

    /**
     * Elimina una conexion de prerrequisito existente.
     */
    public boolean eliminarPrerrequisito(String codeSource, String codeDest) {
        Asignatura source = grafoPrerrequisitos.findVertex(codeSource);
        Asignatura dest = grafoPrerrequisitos.findVertex(codeDest);
        if (source == null || dest == null) {
            return false;
        }
        return grafoPrerrequisitos.removeEdge(source, dest);
    }

    /**
     * Genera una ficha academica integral uniendo la informacion del arbol y del grafo.
     */
    public String consultarFichaCompleta(String code) {
        ElementoAcademico elem = buscarElemento(code);
        if (elem == null) {
            return "No se encontro ningun elemento academico con el codigo: " + code;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("                  FICHA ACADEMICA INTEGRAL                      \n");
        sb.append("=================================================================\n");
        sb.append(" Codigo      : ").append(elem.getCodigo()).append("\n");
        sb.append(" Nombre      : ").append(elem.getNombre()).append("\n");
        sb.append(" Tipo        : ").append(elem.getTipo().getEtiqueta()).append("\n");
        sb.append(" Descripcion : ").append(elem.getDescripcion()).append("\n");
        sb.append(" Nivel Arbol : ").append(arbolAcademico.getLevel(code)).append("\n");
        sb.append(" Padre       : ").append(arbolAcademico.getParent(code)).append("\n");
        sb.append(" Altura Sub. : ").append(arbolAcademico.getHeight(code)).append("\n");

        List<ElementoAcademico> hijos = arbolAcademico.getChildren(code);
        sb.append(" Hijos Directos (").append(hijos.size()).append("):\n");
        for (ElementoAcademico h : hijos) {
            sb.append("   - ").append(h).append("\n");
        }

        if (elem instanceof Asignatura) {
            Asignatura asig = (Asignatura) elem;
            sb.append("\n---------------- RELACIONES EN LA RED DE GRAFOS ----------------\n");
            sb.append(" Creditos    : ").append(asig.getCreditos()).append("\n");
            sb.append(" Semestre    : ").append(asig.getNumeroSemestre()).append("\n");
            sb.append(" Area        : ").append(asig.getAreaConocimiento()).append("\n");
            sb.append(" In-Degree (Prerrequisitos exigidos)   : ").append(grafoPrerrequisitos.getInDegree(asig)).append("\n");
            sb.append(" Out-Degree (Materias dependientes)   : ").append(grafoPrerrequisitos.getOutDegree(asig)).append("\n");

            List<Asignatura> prerrequisitos = grafoPrerrequisitos.getIncomingVertices(asig);
            sb.append(" Prerrequisitos Previos Necesarios:\n");
            if (prerrequisitos.isEmpty()) {
                sb.append("   (Ninguno - Asignatura de nivel inicial)\n");
            } else {
                for (Asignatura p : prerrequisitos) {
                    sb.append("   <- ").append(p.getCodigo()).append(" : ").append(p.getNombre()).append("\n");
                }
            }

            List<Edge<Asignatura>> dependientes = grafoPrerrequisitos.getEdges(asig);
            sb.append(" Materias que Habilita Directamente:\n");
            if (dependientes.isEmpty()) {
                sb.append("   (Ninguna - Asignatura terminal o electiva)\n");
            } else {
                for (Edge<Asignatura> e : dependientes) {
                    sb.append("   -> ").append(e.getDestination().getCodigo())
                            .append(" : ").append(e.getDestination().getNombre())
                            .append(" (").append(e.getWeight()).append(" creditos)\n");
                }
            }
        }
        sb.append("=================================================================\n");

        return sb.toString();
    }
}
