import graph.Edge;
import graph.Graph;
import model.Asignatura;
import model.ElementoAcademico;
import model.TipoElemento;
import model.UnidadAcademica;
import service.Sistema;
import tree.Tree;
import tree.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Aplicacion principal de consola para la Plataforma Educativa.
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Proporciona una interfaz interactiva de consola contextualizada con la terminologia
 * del caso academico, implementando las 14 opciones obligatorias de la Seccion 20
 * de la rubrica mas un modo de demostracion guiada paso a paso para la grabacion del video.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple los requerimientos de interacción y demostración exigidos para la sustentacion,
 * permitiendo ejecutar en vivo recorridos, inserciones, consultas y auditorias curriculares.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Orquesta las operaciones a traves de la clase {@link Sistema}, capturando entradas
 * de usuario de forma defensiva para evitar excepciones por datos invalidos.</p>
 */
public class Main {

    private static final Sistema sistema = new Sistema();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarCaratulaInstitucional();

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero(" Seleccione una opcion (0-15): ", 0, 15);

            System.out.println();
            switch (opcion) {
                case 1 -> registrarElementoJerarquico();
                case 2 -> visualizarArbolJerarquico();
                case 3 -> buscarElementoEnArbol();
                case 4 -> mostrarRecorridosArbol();
                case 5 -> consultarNivelYAltura();
                case 6 -> registrarElementoEnRed();
                case 7 -> crearConexionPrerrequisito();
                case 8 -> eliminarConexionPrerrequisito();
                case 9 -> consultarConexionesYGrados();
                case 10 -> ejecutarBFSGrafo();
                case 11 -> ejecutarDFSGrafo();
                case 12 -> buscarCaminoEntreAsignaturas();
                case 13 -> auditarMallaCurricularCiclos();
                case 14 -> mostrarFichaAcademicaIntegral();
                case 15 -> ejecutarDemostracionGuiadaVideo();
                case 0 -> {
                    System.out.println(" Gracias por utilizar la Plataforma Educativa - Universidad de Cartagena.");
                    System.out.println(" Sesion finalizada correctamente.");
                    salir = true;
                }
            }
            if (!salir) {
                pausarConsola();
            }
        }
    }

    private static void mostrarCaratulaInstitucional() {
        System.out.println("================================================================================");
        System.out.println("                     UNIVERSIDAD DE CARTAGENA - PESAD                           ");
        System.out.println("           FACULTAD DE INGENIERIA - PROGRAMA DE INGENIERIA DE SOFTWARE          ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" Asignatura : Estructuras de Datos (Unidad IV: Arboles y Grafos)               ");
        System.out.println(" Actividad  : Actividad 3 - Protocolo Colaborativo (40%)                        ");
        System.out.println(" Caso Nro.  : 6. Plataforma Educativa (Malla Curricular y Prerrequisitos)       ");
        System.out.println(" Docente    : John Carlos Arrieta Arrieta                                       ");
        System.out.println(" Estudiante : Rosary Eilyn Carmona Navarro                                      ");
        System.out.println(" Correo     : rcarmonan@unicartagena.edu.co                                     ");
        System.out.println("================================================================================");
        System.out.println();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n================================================================================");
        System.out.println("                    MENU PRINCIPAL - PLATAFORMA EDUCATIVA                       ");
        System.out.println("================================================================================");
        System.out.println(" [COMPONENTE JERARQUICO - ARBOL GENERAL]");
        System.out.println("   1. Registrar elemento jerarquico en el plan de estudios");
        System.out.println("   2. Visualizar estructura jerarquica completa (Arbol en ASCII)");
        System.out.println("   3. Buscar elemento academico en la jerarquia");
        System.out.println("   4. Mostrar recorridos del arbol (DFS Profundidad vs BFS Niveles)");
        System.out.println("   5. Consultar nivel y altura de un elemento o subarbol");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [COMPONENTE RELACIONAL - GRAFO DIRIGIDO DE PRERREQUISITOS]");
        System.out.println("   6. Registrar nueva asignatura en la red de prerrequisitos (Vertice)");
        System.out.println("   7. Vincular conexion de prerrequisito entre asignaturas (Arista dirigida)");
        System.out.println("   8. Eliminar conexion de prerrequisito");
        System.out.println("   9. Consultar prerrequisitos y dependencias directas (In/Out Degree)");
        System.out.println("  10. Explorar cadena de dependencias en anchura (BFS por niveles)");
        System.out.println("  11. Explorar cadena de dependencias en profundidad (DFS)");
        System.out.println("  12. Trazar ruta de prerrequisitos entre dos asignaturas (Ruta y Menor Saltos)");
        System.out.println("  13. Auditar malla curricular (Deteccion de Dependencias Circulares / Ciclos)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [INTEGRACION Y DEMOSTRACION]");
        System.out.println("  14. Consultar Ficha Academica Integral (Sincronizacion Arbol + Grafo)");
        System.out.println("  15. Ejecutar Demostracion Guiada Completa (Sustentacion en Video)");
        System.out.println("   0. Salir de la plataforma");
        System.out.println("================================================================================");
    }

    // =========================================================================
    // MODULOS DE OPERACIONES DEL ARBOL
    // =========================================================================

    private static void registrarElementoJerarquico() {
        System.out.println("--- 1. REGISTRAR ELEMENTO EN LA JERARQUIA ACADEMICA ---");
        String parentCode = leerCadena(" Ingrese codigo del nodo padre (ej. SEM-05, ASIG-ED): ");
        if (sistema.buscarElemento(parentCode) == null) {
            System.out.println(" ERROR: El nodo padre con codigo '" + parentCode + "' no existe en el arbol.");
            return;
        }

        System.out.println(" Seleccione el tipo de elemento a registrar:");
        System.out.println("   1. Asignatura (Nivel 3)");
        System.out.println("   2. Unidad Tematica (Nivel 4)");
        System.out.println("   3. Otro elemento generico (Facultad, Programa, Semestre)");
        int tipo = leerEntero(" Opcion: ", 1, 3);

        String codigo = leerCadena(" Ingrese codigo unico del nuevo elemento: ");
        if (sistema.buscarElemento(codigo) != null) {
            System.out.println(" ERROR: Ya existe un elemento registrado con el codigo '" + codigo + "'.");
            return;
        }
        String nombre = leerCadena(" Ingrese nombre formal del elemento: ");
        String descripcion = leerCadena(" Ingrese breve descripcion: ");

        ElementoAcademico nuevo;
        if (tipo == 1) {
            int creditos = leerEntero(" Ingrese numero de creditos academicos (1-10): ", 1, 10);
            int semestre = leerEntero(" Ingrese numero de semestre formativo (1-12): ", 1, 12);
            String area = leerCadena(" Ingrese area de conocimiento: ");
            nuevo = new Asignatura(codigo, nombre, creditos, semestre, area, descripcion);
        } else if (tipo == 2) {
            int horas = leerEntero(" Ingrese numero de horas academicas (1-120): ", 1, 120);
            String competencia = leerCadena(" Ingrese competencia de aprendizaje: ");
            nuevo = new UnidadAcademica(codigo, nombre, horas, competencia, descripcion);
        } else {
            nuevo = new ElementoAcademico(codigo, nombre, TipoElemento.SEMESTRE, descripcion);
        }

        try {
            boolean exito = sistema.registrarElemento(parentCode, nuevo);
            if (exito) {
                System.out.println(" EXITO: Elemento '" + nombre + "' registrado correctamente bajo '" + parentCode + "'.");
                if (nuevo instanceof Asignatura) {
                    System.out.println(" NOTA: La asignatura fue sincronizada e incorporada automaticamente a la red del grafo.");
                }
            }
        } catch (Exception e) {
            System.out.println(" ERROR al registrar: " + e.getMessage());
        }
    }

    private static void visualizarArbolJerarquico() {
        System.out.println("--- 2. ESTRUCTURA JERARQUICA DEL PLAN DE ESTUDIOS (REPRESENTACION VISUAL) ---");
        System.out.println(" Total Nodos en la Jerarquia: " + sistema.getArbolAcademico().size());
        System.out.println(" Altura Maxima del Arbol     : " + sistema.getArbolAcademico().getHeight());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print(sistema.getArbolAcademico().renderAsciiTree());
    }

    private static void buscarElementoEnArbol() {
        System.out.println("--- 3. BUSCAR ELEMENTO ACADEMICO EN EL ARBOL ---");
        String codigo = leerCadena(" Ingrese codigo a buscar (ej. ASIG-ED, PROG-SOFT, SEM-03): ");
        ElementoAcademico elem = sistema.buscarElemento(codigo);
        if (elem != null) {
            System.out.println(" ELEMENTO ENCONTRADO:");
            System.out.println("   Codigo      : " + elem.getCodigo());
            System.out.println("   Nombre      : " + elem.getNombre());
            System.out.println("   Tipo        : " + elem.getTipo().getEtiqueta());
            System.out.println("   Nivel       : " + sistema.getArbolAcademico().getLevel(codigo));
            System.out.println("   Padre       : " + sistema.getArbolAcademico().getParent(codigo));
            System.out.println("   Descripcion : " + elem.getDescripcion());
        } else {
            System.out.println(" AVISO: No se encontro ningun elemento con el codigo '" + codigo + "'.");
        }
    }

    private static void mostrarRecorridosArbol() {
        System.out.println("--- 4. RECORRIDOS OBLIGATORIOS DEL ARBOL GENERAL (SECCION 8) ---");
        Tree<ElementoAcademico> arbol = sistema.getArbolAcademico();

        System.out.println("\n[A] RECORRIDO EN PROFUNDIDAD (DFS / Preorden en Arbol General):");
        System.out.println("    Estrategia: Explora verticalmente hasta la hoja antes de retroceder (backtracking).");
        List<ElementoAcademico> dfs = arbol.traverseDFS();
        System.out.println("    Total nodos recorridos: " + dfs.size());
        for (int i = 0; i < Math.min(dfs.size(), 12); i++) {
            System.out.println("    " + (i + 1) + ". " + dfs.get(i));
        }
        if (dfs.size() > 12) {
            System.out.println("    ... (" + (dfs.size() - 12) + " nodos adicionales explorados en profundidad)");
        }

        System.out.println("\n[B] RECORRIDO POR NIVELES (BFS en Arbol General mediante Queue):");
        System.out.println("    Estrategia: Explora horizontalmente anillo por anillo desde la raiz.");
        Map<Integer, List<ElementoAcademico>> niveles = arbol.getElementsByLevel();
        for (Map.Entry<Integer, List<ElementoAcademico>> entry : niveles.entrySet()) {
            System.out.println("    -> Nivel " + entry.getKey() + " (" + entry.getValue().size() + " elementos):");
            for (ElementoAcademico e : entry.getValue()) {
                System.out.println("       * " + e);
            }
        }
    }

    private static void consultarNivelYAltura() {
        System.out.println("--- 5. CONSULTAR NIVEL Y ALTURA EN EL ARBOL ---");
        String codigo = leerCadena(" Ingrese codigo del elemento a consultar: ");
        int nivel = sistema.getArbolAcademico().getLevel(codigo);
        int altura = sistema.getArbolAcademico().getHeight(codigo);

        if (nivel != -1) {
            System.out.println(" PROPIEDADES ESTRUCTURALES DE '" + codigo + "':");
            System.out.println("   Nivel en el arbol (distancia a la raiz) : " + nivel);
            System.out.println("   Altura del subarbol                      : " + altura);
            System.out.println("   Altura global del arbol institucional    : " + sistema.getArbolAcademico().getHeight());
        } else {
            System.out.println(" ERROR: El elemento '" + codigo + "' no existe en el arbol.");
        }
    }

    // =========================================================================
    // MODULOS DE OPERACIONES DEL GRAFO
    // =========================================================================

    private static void registrarElementoEnRed() {
        System.out.println("--- 6. REGISTRAR ASIGNATURA EN LA RED DE PRERREQUISITOS (VERTICE) ---");
        String codigo = leerCadena(" Ingrese codigo de la asignatura (ej. ASIG-IA): ");
        if (sistema.getGrafoPrerrequisitos().containsVertex(codigo)) {
            System.out.println(" ERROR: La asignatura ya participa en el grafo de prerrequisitos.");
            return;
        }
        String nombre = leerCadena(" Ingrese nombre de la asignatura: ");
        int creditos = leerEntero(" Creditos (1-10): ", 1, 10);
        int semestre = leerEntero(" Semestre formativo (1-12): ", 1, 12);
        String area = leerCadena(" Area de conocimiento: ");

        Asignatura nueva = new Asignatura(codigo, nombre, creditos, semestre, area, "Asignatura de red");
        boolean exito = sistema.getGrafoPrerrequisitos().addVertex(nueva);
        if (exito) {
            System.out.println(" EXITO: Asignatura registrada en la red como vertice independiente.");
            System.out.println(" Total vertices actuales en el grafo: " + sistema.getGrafoPrerrequisitos().getVertexCount());
        }
    }

    private static void crearConexionPrerrequisito() {
        System.out.println("--- 7. VINCULAR PRERREQUISITO ENTRE ASIGNATURAS (ARISTA DIRIGIDA) ---");
        System.out.println(" Formato academico: [Asignatura Habilitadora / Prerrequisito] -> [Asignatura Dependiente]");
        String origen = leerCadena(" Ingrese codigo de la asignatura prerrequisito (origen): ");
        String destino = leerCadena(" Ingrese codigo de la asignatura dependiente (destino): ");
        int creditos = leerEntero(" Ingrese creditos exigidos como peso (1-10): ", 1, 10);
        String label = leerCadena(" Etiqueta (ej. Prerrequisito Obligatorio): ");

        try {
            boolean exito = sistema.conectarPrerrequisito(origen, destino, creditos, label);
            if (exito) {
                System.out.println(" EXITO: Conexion dirigida creada: " + origen + " -> " + destino + " (" + creditos + " creditos)");
                System.out.println(" Total aristas en la red: " + sistema.getGrafoPrerrequisitos().getEdgeCount());
            } else {
                System.out.println(" AVISO: La conexion ya existia previamente en la red.");
            }
        } catch (Exception e) {
            System.out.println(" ERROR DE INTEGRIDAD: " + e.getMessage());
        }
    }

    private static void eliminarConexionPrerrequisito() {
        System.out.println("--- 8. ELIMINAR CONEXION DE PRERREQUISITO ---");
        String origen = leerCadena(" Ingrese codigo de la asignatura origen: ");
        String destino = leerCadena(" Ingrese codigo de la asignatura destino: ");

        boolean exito = sistema.eliminarPrerrequisito(origen, destino);
        if (exito) {
            System.out.println(" EXITO: Conexion de prerrequisito eliminada correctamente.");
            System.out.println(" Total aristas restantes: " + sistema.getGrafoPrerrequisitos().getEdgeCount());
        } else {
            System.out.println(" AVISO: No existia dicha conexion directa entre las asignaturas indicadas.");
        }
    }

    private static void consultarConexionesYGrados() {
        System.out.println("--- 9. CONSULTAR CONEXIONES Y GRADOS DE UNA ASIGNATURA ---");
        String codigo = leerCadena(" Ingrese codigo de la asignatura a consultar (ej. ASIG-ED): ");
        Asignatura asig = sistema.getGrafoPrerrequisitos().findVertex(codigo);

        if (asig == null) {
            System.out.println(" ERROR: La asignatura '" + codigo + "' no existe en el grafo de prerrequisitos.");
            return;
        }

        int inDegree = sistema.getGrafoPrerrequisitos().getInDegree(asig);
        int outDegree = sistema.getGrafoPrerrequisitos().getOutDegree(asig);

        System.out.println("\n METRICAS DE CONECTIVIDAD PARA: " + asig.getNombre() + " (" + asig.getCodigo() + ")");
        System.out.println("   In-Degree (Prerrequisitos previos exigidos)   : " + inDegree);
        System.out.println("   Out-Degree (Materias dependientes que habilita): " + outDegree);

        System.out.println("\n [A] PRERREQUISITOS PREVIOS REQUERIDOS (Aristas Entrantes):");
        List<Asignatura> entrantes = sistema.getGrafoPrerrequisitos().getIncomingVertices(asig);
        if (entrantes.isEmpty()) {
            System.out.println("     (Ninguno - Asignatura de nivel inicial)");
        } else {
            for (Asignatura p : entrantes) {
                System.out.println("     <- [" + p.getCodigo() + "] " + p.getNombre() + " (Semestre " + p.getNumeroSemestre() + ")");
            }
        }

        System.out.println("\n [B] ASIGNATURAS QUE HABILITA DIRECTAMENTE (Aristas Salientes):");
        List<Edge<Asignatura>> salientes = sistema.getGrafoPrerrequisitos().getEdges(asig);
        if (salientes.isEmpty()) {
            System.out.println("     (Ninguna - Asignatura terminal de la linea curricular)");
        } else {
            for (Edge<Asignatura> e : salientes) {
                System.out.println("     -> [" + e.getDestination().getCodigo() + "] " + e.getDestination().getNombre() +
                        " | Peso: " + e.getWeight() + " creditos (" + e.getLabel() + ")");
            }
        }
    }

    private static void ejecutarBFSGrafo() {
        System.out.println("--- 10. EXPLORAR CADENA DE DEPENDENCIAS EN ANCHURA (BFS - SECCION 14) ---");
        String codigo = leerCadena(" Ingrese asignatura de partida (ej. ASIG-FP, ASIG-CAL1): ");
        if (!sistema.getGrafoPrerrequisitos().containsVertex(codigo)) {
            System.out.println(" ERROR: La asignatura no existe en el grafo.");
            return;
        }

        System.out.println("\n EJECUTANDO BFS (Queue FIFO por anillos de distancia):");
        Map<Integer, List<Asignatura>> niveles = sistema.getGrafoPrerrequisitos().bfsByLevels(codigo);
        for (Map.Entry<Integer, List<Asignatura>> entry : niveles.entrySet()) {
            System.out.println("  Anillo / Nivel " + entry.getKey() + " (" + entry.getValue().size() + " asignaturas):");
            for (Asignatura a : entry.getValue()) {
                System.out.println("    * [" + a.getCodigo() + "] " + a.getNombre() + " (Semestre " + a.getNumeroSemestre() + ")");
            }
        }
    }

    private static void ejecutarDFSGrafo() {
        System.out.println("--- 11. EXPLORAR CADENA DE DEPENDENCIAS EN PROFUNDIDAD (DFS - SECCION 15) ---");
        String codigo = leerCadena(" Ingrese asignatura de partida (ej. ASIG-FP, ASIG-CAL1): ");
        if (!sistema.getGrafoPrerrequisitos().containsVertex(codigo)) {
            System.out.println(" ERROR: La asignatura no existe en el grafo.");
            return;
        }

        System.out.println("\n EJECUTANDO DFS (Recursion / Pila hasta el fondo de cada linea):");
        List<Asignatura> dfs = sistema.getGrafoPrerrequisitos().dfs(codigo);
        for (int i = 0; i < dfs.size(); i++) {
            System.out.println("  Paso " + (i + 1) + ": [" + dfs.get(i).getCodigo() + "] " + dfs.get(i).getNombre());
        }

        System.out.println("\n" + sistema.getGrafoPrerrequisitos().explainDifferenceBfsDfs(codigo));
    }

    private static void buscarCaminoEntreAsignaturas() {
        System.out.println("--- 12. TRAZAR RUTA DE PRERREQUISITOS ENTRE DOS ASIGNATURAS (SECCION 16) ---");
        String origen = leerCadena(" Ingrese codigo de la asignatura de partida (ej. ASIG-FP): ");
        String destino = leerCadena(" Ingrese codigo de la asignatura meta (ej. ASIG-IS2): ");

        List<Asignatura> ruta = sistema.getGrafoPrerrequisitos().findPath(origen, destino);
        List<Asignatura> rutaCorta = sistema.getGrafoPrerrequisitos().findShortestPathBFS(origen, destino);

        if (ruta.isEmpty()) {
            System.out.println(" AVISO: No existe ninguna ruta de prerrequisitos dirigida entre '" + origen + "' y '" + destino + "'.");
        } else {
            System.out.println("\n [A] RUTA ENCONTRADA (Cualquier camino valido):");
            imprimirRuta(ruta);

            System.out.println("\n [B] RUTA OPTIMA EN MENOR CANTIDAD DE SALTOS (Algoritmo BFS):");
            imprimirRuta(rutaCorta);
            System.out.println(" Saltos minimos necesarios: " + (rutaCorta.size() - 1));
        }
    }

    private static void imprimirRuta(List<Asignatura> ruta) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            sb.append("[").append(ruta.get(i).getCodigo()).append("] ").append(ruta.get(i).getNombre());
            if (i < ruta.size() - 1) {
                sb.append(" ---> ");
            }
        }
        System.out.println("   " + sb);
    }

    private static void auditarMallaCurricularCiclos() {
        System.out.println("--- 13. AUDITORIA DE LA MALLA CURRICULAR (DETECCION DE CICLOS - SECCION 16) ---");
        Graph<Asignatura> grafo = sistema.getGrafoPrerrequisitos();
        List<Asignatura> ciclo = grafo.detectCycle();

        if (ciclo.isEmpty()) {
            System.out.println(" ESTADO DE LA RED: ACICLICA (DAG - Grafo Dirigido Aciclico)");
            System.out.println(" DIAGNOSTICO: La malla curricular es 100% valida.");
            System.out.println(" Ninguna asignatura depende directa o indirectamente de si misma.");
        } else {
            System.out.println(" ALERTA CRITICA: Se ha detectado una dependencia circular (Ciclo):");
            System.out.println(" Cadena del ciclo: " + ciclo);
        }
    }

    private static void mostrarFichaAcademicaIntegral() {
        System.out.println("--- 14. FICHA ACADEMICA INTEGRAL (INTEGRACION ARBOL + GRAFO) ---");
        String codigo = leerCadena(" Ingrese codigo de la asignatura (ej. ASIG-ED, ASIG-PR2, ASIG-IS1): ");
        System.out.println(sistema.consultarFichaCompleta(codigo));
    }

    // =========================================================================
    // MODULO DE DEMOSTRACION GUIADA PARA SUSTENTACION EN VIDEO (SECCION 27)
    // =========================================================================

    private static void ejecutarDemostracionGuiadaVideo() {
        System.out.println("================================================================================");
        System.out.println("             DEMOSTRACION GUIADA AUTOMATIZADA PARA VIDEO DE SUSTENTACION        ");
        System.out.println("================================================================================");
        System.out.println(" Esta rutina demuestra en vivo los hitos obligatorios evaluados en la rubrica:\n");

        System.out.println("PASO 1: VERIFICACION DE ESTRUCTURA DEL ARBOL Y UMBRALES");
        System.out.println(" -> Nodos en el arbol : " + sistema.getArbolAcademico().size() + " (Exigido: >= 15)");
        System.out.println(" -> Altura del arbol  : " + sistema.getArbolAcademico().getHeight() + " niveles");
        System.out.println(" -> Raiz institucional: " + sistema.getArbolAcademico().getRoot().getData());

        System.out.println("\nPASO 2: VERIFICACION DEL GRAFO DE PRERREQUISITOS Y UMBRALES");
        System.out.println(" -> Vertices en grafo : " + sistema.getGrafoPrerrequisitos().getVertexCount() + " asignaturas (Exigido: >= 12)");
        System.out.println(" -> Aristas dirigidas : " + sistema.getGrafoPrerrequisitos().getEdgeCount() + " conexiones (Exigido: >= 18)");

        System.out.println("\nPASO 3: CASO EXACTO DE LA GUIA DOCENTE");
        Asignatura pr2 = sistema.getGrafoPrerrequisitos().findVertex("ASIG-PR2");
        Asignatura ed = sistema.getGrafoPrerrequisitos().findVertex("ASIG-ED");
        System.out.println(" -> Conexion directa Programacion II -> Estructuras de Datos: " +
                sistema.getGrafoPrerrequisitos().hasDirectEdge(pr2, ed));

        System.out.println("\nPASO 4: DISCREPANCIA DEMOSTRADA ENTRE RECORRIDOS BFS Y DFS EN EL GRAFO");
        System.out.println(" Origen: Fundamentos de Programacion (ASIG-FP)");
        System.out.println(" BFS (Anchura/Semestral) : " + sistema.getGrafoPrerrequisitos().bfs("ASIG-FP"));
        System.out.println(" DFS (Profundidad lineal): " + sistema.getGrafoPrerrequisitos().dfs("ASIG-FP"));

        System.out.println("\nPASO 5: RUTA MINIMA Y AUDITORIA DE CICLOS");
        List<Asignatura> rutaCorta = sistema.getGrafoPrerrequisitos().findShortestPathBFS("ASIG-FP", "ASIG-IS2");
        System.out.println(" -> Ruta de menor saltos FP -> IS2 : " + rutaCorta.size() + " asignaturas");
        System.out.println(" -> Auditoria de Ciclos en Malla  : " + (sistema.getGrafoPrerrequisitos().hasCycle() ? "FALLO" : "ACICLICA OK [DAG]"));

        System.out.println("\nPASO 6: PRUEBA NUEVA NO DOCUMENTADA EN VIVO (SECCION 28 DE LA RUBRICA)");
        System.out.println(" -> Operacion: Insertar la asignatura electiva 'Inteligencia Artificial' (ASIG-IA) en Semestre V.");
        System.out.println(" -> Vinculo  : Conectar prerrequisito 'Estructuras de Datos' -> 'Inteligencia Artificial'.");
        Asignatura ia = new Asignatura("ASIG-IA", "Inteligencia Artificial", 3, 5, "Ciencias de la Computacion", "Electiva");
        sistema.registrarElemento("SEM-05", ia);
        sistema.conectarPrerrequisito("ASIG-ED", "ASIG-IA", 4.0, "Prerrequisito Electiva");
        System.out.println(" -> Resultado: Asignatura registrada y conectada exitosamente.");
        System.out.println(" -> Nuevo In-Degree de IA: " + sistema.getGrafoPrerrequisitos().getInDegree(ia));
        System.out.println("================================================================================");
    }

    // =========================================================================
    // UTILIDADES DE ENTRADA DEFENSIVA EN CONSOLA
    // =========================================================================

    private static String leerCadena(String mensaje) {
        System.out.print(mensaje);
        String entrada = scanner.nextLine();
        while (entrada == null || entrada.trim().isEmpty()) {
            System.out.print(" Entrada vacia no permitida. Reintente: ");
            entrada = scanner.nextLine();
        }
        return entrada.trim();
    }

    private static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine();
            try {
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println(" Valor fuera de rango (" + min + " - " + max + "). Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println(" Entrada invalida. Ingrese un numero entero.");
            }
        }
    }

    private static void pausarConsola() {
        System.out.print("\n Presione ENTER para continuar...");
        scanner.nextLine();
    }
}
