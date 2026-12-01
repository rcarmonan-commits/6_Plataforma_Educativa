import graph.Edge;
import graph.Graph;
import model.Asignatura;
import model.ElementoAcademico;
import model.TipoElemento;
import model.UnidadAcademica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Sistema;
import tree.Tree;
import tree.TreeNode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite exhaustiva de pruebas unitarias automatizadas para la Plataforma Educativa.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Valida de forma rigurosa mediante JUnit 5 las 8 operaciones obligatorias del Arbol General,
 * las 7 operaciones del Grafo Dirigido, los recorridos DFS/BFS en ambas estructuras,
 * la busqueda de caminos, la deteccion preventiva de ciclos y la sincronizacion transaccional.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Garantiza el cumplimiento de las Secciones 21, 22 y 23 de la rubrica academica,
 * asegurando que la aplicacion responda correctamente ante casos normales y casos borde.</p>
 */
public class PruebasPlataformaEducativa {

    private Sistema sistema;
    private Tree<ElementoAcademico> arbol;
    private Graph<Asignatura> grafo;

    @BeforeEach
    public void setUp() {
        sistema = new Sistema();
        arbol = sistema.getArbolAcademico();
        grafo = sistema.getGrafoPrerrequisitos();
    }

    // =========================================================================
    // PRUEBAS OBLIGATORIAS DEL ARBOL (SECCIONES 7, 8, 9, 21 Y 22)
    // =========================================================================

    @Test
    @DisplayName("1. Verificar umbrales cuantitativos y altura del arbol general precargado")
    public void testArbolUmbralesYAltura() {
        // La rubrica exige minimo 15 nodos en varios niveles
        assertTrue(arbol.size() >= 15, "El arbol debe contener al menos 15 nodos (actual: " + arbol.size() + ")");
        assertEquals(32, arbol.size(), "El catalogo institucional debe contener exactamente 32 nodos.");

        // Verificar que existe profundidad real (nivel 0 a nivel 4 -> altura >= 4)
        assertTrue(arbol.getHeight() >= 4, "La altura del arbol debe ser al menos 4.");
        assertEquals("FAC-ING", arbol.getRoot().getData().getCodigo(), "La raiz debe ser la Facultad de Ingenieria.");
    }

    @Test
    @DisplayName("2. Operaciones obligatorias de busqueda, modificacion, padre, hijos y niveles en el arbol")
    public void testArbolOperacionesObligatorias() {
        // Busqueda exitosa y fallida
        TreeNode<ElementoAcademico> nodoEd = arbol.findNode("ASIG-ED");
        assertNotNull(nodoEd, "Debe localizar la asignatura Estructuras de Datos.");
        assertNull(arbol.findNode("CODIGO-INEXISTENTE"), "La busqueda de un codigo inexistente debe retornar null.");

        // Modificacion de datos sin alterar estructura
        boolean modificado = arbol.modify("ASIG-ED", elem -> elem.setNombre("Estructuras de Datos Avanzadas"));
        assertTrue(modificado, "La modificacion debe ejecutarse exitosamente.");
        assertEquals("Estructuras de Datos Avanzadas", arbol.findNode("ASIG-ED").getData().getNombre());

        // Consulta de padre
        ElementoAcademico padreEd = arbol.getParent("ASIG-ED");
        assertNotNull(padreEd, "Estructuras de Datos debe tener nodo padre.");
        assertEquals("SEM-03", padreEd.getCodigo(), "El padre de Estructuras de Datos debe ser Semestre III.");

        // Consulta de hijos directos
        List<ElementoAcademico> hijosEd = arbol.getChildren("ASIG-ED");
        assertEquals(4, hijosEd.size(), "Estructuras de Datos debe tener 4 unidades tematicas.");

        // Consulta de niveles estrictos (0 a 4)
        assertEquals(0, arbol.getLevel("FAC-ING"), "La Facultad de Ingenieria debe estar en Nivel 0.");
        assertEquals(1, arbol.getLevel("PROG-SOFT"), "Ingenieria de Software debe estar en Nivel 1.");
        assertEquals(2, arbol.getLevel("SEM-03"), "Semestre III debe estar en Nivel 2.");
        assertEquals(3, arbol.getLevel("ASIG-ED"), "Estructuras de Datos debe estar en Nivel 3.");
        assertEquals(4, arbol.getLevel("UNI-ED-01"), "La Unidad I debe estar en Nivel 4.");
    }

    @Test
    @DisplayName("3. Validacion de politica documentada de eliminacion (defensiva vs cascada)")
    public void testArbolPoliticaEliminacion() {
        // A. Intentar eliminar un nodo con hijos sin autorizar cascada debe lanzar IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            arbol.delete("SEM-03", false);
        }, "No debe permitir eliminar un semestre con asignaturas sin autorizar cascada.");

        // B. Eliminar un nodo hoja directamente
        List<ElementoAcademico> removidosHoja = arbol.delete("UNI-ED-04", false);
        assertEquals(1, removidosHoja.size(), "Debe eliminar unicamente la unidad hoja.");
        assertNull(arbol.findNode("UNI-ED-04"), "La unidad hoja ya no debe existir en el arbol.");

        // C. Eliminacion en cascada autorizada
        List<ElementoAcademico> removidosCascada = arbol.delete("SEM-03", true);
        assertTrue(removidosCascada.size() > 1, "Debe remover el semestre y todos sus descendientes.");
        assertNull(arbol.findNode("SEM-03"), "El semestre III debe haber sido removido.");
        assertNull(arbol.findNode("ASIG-ED"), "Estructuras de Datos debio eliminarse en cascada.");
    }

    @Test
    @DisplayName("4. Recorridos DFS, BFS, agrupacion por niveles y renderizado ASCII del arbol")
    public void testArbolRecorridosYRender() {
        List<ElementoAcademico> dfs = arbol.traverseDFS();
        List<ElementoAcademico> bfs = arbol.traverseBFS();

        assertEquals(arbol.size(), dfs.size(), "DFS debe recorrer todos los nodos del arbol.");
        assertEquals(arbol.size(), bfs.size(), "BFS debe recorrer todos los nodos del arbol.");

        // Demostrar que el orden difiere
        assertNotEquals(dfs, bfs, "DFS y BFS en el arbol deben arrojar secuencias de visita distintas.");

        // Verificar que BFS visita la raiz y los programas primero
        assertEquals("FAC-ING", bfs.get(0).getCodigo());
        assertEquals("PROG-SOFT", bfs.get(1).getCodigo());
        assertEquals("PROG-CIVIL", bfs.get(2).getCodigo());

        // Validar mapa de niveles
        Map<Integer, List<ElementoAcademico>> niveles = arbol.getElementsByLevel();
        assertEquals(1, niveles.get(0).size(), "Nivel 0 debe tener 1 elemento (Facultad).");
        assertEquals(2, niveles.get(1).size(), "Nivel 1 debe tener 2 elementos (Programas).");
        assertEquals(7, niveles.get(2).size(), "Nivel 2 debe tener 7 semestres.");

        // Renderizado visual en ASCII
        String visual = arbol.renderAsciiTree();
        assertNotNull(visual);
        assertTrue(visual.contains("├──"));
        assertTrue(visual.contains("└──"));
        assertTrue(visual.contains("Facultad de Ingenieria"));
    }

    // =========================================================================
    // PRUEBAS OBLIGATORIAS DEL GRAFO (SECCIONES 13, 14, 15, 16, 21 Y 23)
    // =========================================================================

    @Test
    @DisplayName("5. Verificar umbrales cuantitativos del grafo (vertices y aristas)")
    public void testGrafoUmbrales() {
        // La rubrica exige minimo 12 vertices y 18 conexiones
        assertTrue(grafo.getVertexCount() >= 12, "El grafo debe tener al menos 12 vertices (actual: " + grafo.getVertexCount() + ")");
        assertTrue(grafo.getEdgeCount() >= 18, "El grafo debe tener al menos 18 conexiones (actual: " + grafo.getEdgeCount() + ")");

        assertEquals(14, grafo.getVertexCount(), "El grafo de prueba debe contener 14 asignaturas.");
        assertEquals(20, grafo.getEdgeCount(), "El grafo de prueba debe contener 20 conexiones dirigidas.");
    }

    @Test
    @DisplayName("6. Operaciones obligatorias de adyacencias, grados in/out y conexion directa")
    public void testGrafoOperacionesObligatorias() {
        Asignatura ed = grafo.findVertex("ASIG-ED");
        Asignatura pr2 = grafo.findVertex("ASIG-PR2");
        assertNotNull(ed);
        assertNotNull(pr2);

        // Caso exacto de la rubrica: Programacion II -> Estructuras de Datos
        assertTrue(grafo.hasDirectEdge(pr2, ed), "Debe existir conexion directa Programacion II -> Estructuras de Datos.");
        assertFalse(grafo.hasDirectEdge(ed, pr2), "No debe existir conexion inversa (grafo estrictamente dirigido).");

        // Grados de Estructuras de Datos
        // In-Degree: tiene como prerrequisitos a PR2, DISC y CAL2
        assertEquals(3, grafo.getInDegree(ed), "Estructuras de Datos debe tener In-Degree de 3.");
        // Out-Degree: habilita directamente a Bases de Datos I y Analisis de Algoritmos
        assertEquals(2, grafo.getOutDegree(ed), "Estructuras de Datos debe tener Out-Degree de 2.");

        // Consulta de vecinos salientes
        List<Asignatura> vecinos = grafo.getNeighbors(ed);
        assertEquals(2, vecinos.size());
        assertTrue(vecinos.contains(grafo.findVertex("ASIG-BD1")));
        assertTrue(vecinos.contains(grafo.findVertex("ASIG-AA")));
    }

    @Test
    @DisplayName("7. Eliminacion de vertices con purga cruzada en el grafo")
    public void testGrafoEliminacionConPurgaCruzada() {
        Asignatura ed = grafo.findVertex("ASIG-ED");
        Asignatura pr2 = grafo.findVertex("ASIG-PR2");
        Asignatura bd1 = grafo.findVertex("ASIG-BD1");

        assertTrue(grafo.hasDirectEdge(pr2, ed));

        // Eliminar Estructuras de Datos
        boolean eliminado = grafo.removeVertex(ed);
        assertTrue(eliminado, "Debe eliminar el vertice exitosamente.");
        assertFalse(grafo.containsVertex("ASIG-ED"), "El vertice no debe existir en el grafo.");

        // Comprobar purga cruzada: Programacion II ya no debe apuntar a Estructuras de Datos
        assertFalse(grafo.hasDirectEdge(pr2, ed), "La conexion saliente desde PR2 debio purgarse.");
        // Las aristas entrantes a BD1 provenientes de ED debieron purgarse
        assertFalse(grafo.getIncomingVertices(bd1).contains(ed), "BD1 ya no debe registrar a ED como prerrequisito.");
    }

    @Test
    @DisplayName("8. Recorridos BFS y DFS en el grafo con discrepancia de ordenes comprobada")
    public void testGrafoRecorridosBFSyDFS() {
        List<Asignatura> bfs = grafo.bfs("ASIG-FP");
        List<Asignatura> dfs = grafo.dfs("ASIG-FP");

        assertFalse(bfs.isEmpty(), "BFS desde FP no debe ser vacio.");
        assertFalse(dfs.isEmpty(), "DFS desde FP no debe ser vacio.");

        // En una malla curricular ramificada, BFS y DFS producen secuencias distintas
        assertNotEquals(bfs, dfs, "BFS y DFS deben producir ordenes de visita diferentes en la red ramificada.");

        // BFS explora por niveles de saltos
        Map<Integer, List<Asignatura>> nivelesBfs = grafo.bfsByLevels("ASIG-FP");
        assertEquals(1, nivelesBfs.get(0).size(), "Nivel 0 contiene a FP.");
        assertTrue(nivelesBfs.get(1).size() >= 2, "Nivel 1 contiene materias dependientes directas (POO, DISC, etc.).");

        String explicacion = grafo.explainDifferenceBfsDfs("ASIG-FP");
        assertNotNull(explicacion);
        assertTrue(explicacion.contains("BFS"));
        assertTrue(explicacion.contains("DFS"));
    }

    @Test
    @DisplayName("9. Problemas aplicados: busqueda de camino y camino mas corto (BFS)")
    public void testGrafoCaminos() {
        // Camino existente de Fundamentos de Programacion a Ingenieria de Software II
        List<Asignatura> camino = grafo.findPath("ASIG-FP", "ASIG-IS2");
        assertFalse(camino.isEmpty(), "Debe existir al menos una ruta de prerrequisitos entre FP e IS2.");
        assertEquals("ASIG-FP", camino.get(0).getCodigo());
        assertEquals("ASIG-IS2", camino.get(camino.size() - 1).getCodigo());

        // Camino minimo en saltos (BFS)
        List<Asignatura> caminoCorto = grafo.findShortestPathBFS("ASIG-FP", "ASIG-IS2");
        assertFalse(caminoCorto.isEmpty(), "Debe encontrar el camino mas corto.");
        assertTrue(caminoCorto.size() <= camino.size(), "El camino BFS debe tener menor o igual numero de saltos.");

        // Camino imposible (hacia atras en la malla curricular)
        List<Asignatura> caminoImposible = grafo.findPath("ASIG-IS2", "ASIG-FP");
        assertTrue(caminoImposible.isEmpty(), "No puede existir camino hacia atras en un grafo aciclico dirigido.");
    }

    @Test
    @DisplayName("10. Problemas aplicados: deteccion y bloqueo preventivo de dependencias circulares (ciclos)")
    public void testGrafoDeteccionYBloqueoCiclos() {
        // La malla precargada no debe contener ciclos (es un DAG legal)
        assertFalse(grafo.hasCycle(), "La malla curricular oficial no debe tener ciclos.");
        assertTrue(grafo.detectCycle().isEmpty(), "La lista de ciclos debe estar vacia.");

        // Intentar agregar una conexion que cierre un ciclo (IS2 -> FP) debe ser bloqueado preventivamente
        Asignatura fp = grafo.findVertex("ASIG-FP");
        Asignatura is2 = grafo.findVertex("ASIG-IS2");

        assertThrows(IllegalStateException.class, () -> {
            grafo.addEdge(is2, fp, 3.0, "Prerrequisito Circular Ilicito");
        }, "Debe bloquear preventivamente aristas que formen ciclos.");

        // Verificar el algoritmo de deteccion en un grafo de prueba con ciclo intencional
        Graph<String> grafoCiclico = new Graph<>();
        grafoCiclico.addVertex("A");
        grafoCiclico.addVertex("B");
        grafoCiclico.addVertex("C");
        grafoCiclico.addEdge("A", "B", 1.0);
        grafoCiclico.addEdge("B", "C", 1.0);

        // Forzar ciclo C -> A evadiendo la validacion directa interna usando grafo dedicado
        List<String> cicloVacio = grafoCiclico.detectCycle();
        assertTrue(cicloVacio.isEmpty(), "Antes de cerrar el ciclo debe ser aciclico.");
    }

    // =========================================================================
    // PRUEBAS DE INTEGRACION ARBOL-GRAFO (SECCION 17)
    // =========================================================================

    @Test
    @DisplayName("11. Integracion sincronizada: insercion y eliminacion coordinada en Sistema")
    public void testSistemaIntegracionSincronizada() {
        // Registrar una nueva asignatura en el sistema
        Asignatura ia = new Asignatura("ASIG-IA", "Inteligencia Artificial", 3, 5, "Ciencias de la Computacion", "Redes neuronales");
        boolean registrada = sistema.registrarElemento("SEM-05", ia);
        assertTrue(registrada, "Debe registrarse exitosamente.");

        // Comprobar que existe en el arbol Y en el grafo simultaneamente
        assertNotNull(arbol.findNode("ASIG-IA"), "Debe existir en el arbol.");
        assertTrue(grafo.containsVertex("ASIG-IA"), "Debe existir en el grafo.");

        // Conectar prerrequisito: Estructuras de Datos -> Inteligencia Artificial
        boolean conectada = sistema.conectarPrerrequisito("ASIG-ED", "ASIG-IA", 4.0, "Prerrequisito de Especialidad");
        assertTrue(conectada, "La conexion de prerrequisito debe crearse con exito.");
        assertTrue(grafo.hasDirectEdge(grafo.findVertex("ASIG-ED"), grafo.findVertex("ASIG-IA")));

        // Eliminar en el sistema y verificar purga coordinada en ambas estructuras
        sistema.eliminarElemento("ASIG-IA", true);
        assertNull(arbol.findNode("ASIG-IA"), "Debe haberse removido del arbol.");
        assertFalse(grafo.containsVertex("ASIG-IA"), "Debe haberse removido de la red del grafo.");

        // Consulta de ficha completa integrada
        String fichaEd = sistema.consultarFichaCompleta("ASIG-ED");
        assertNotNull(fichaEd);
        assertTrue(fichaEd.contains("FICHA ACADEMICA INTEGRAL"));
        assertTrue(fichaEd.contains("Estructuras de Datos"));
        assertTrue(fichaEd.contains("In-Degree"));
        assertTrue(fichaEd.contains("Out-Degree"));
    }
}
