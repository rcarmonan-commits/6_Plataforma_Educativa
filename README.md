# Plataforma Educativa: Gestión Curricular Jerárquica y Red de Prerrequisitos Académicos

---

## 1. Encabezado Institucional

* **Institución de Educación Superior:** Universidad de Cartagena  
* **Centro Tutorial / Modalidad:** Centro Promotor PESAD / Distancia  
* **Facultad:** Facultad de Ingeniería  
* **Programa Académico:** Ingeniería de Software  
* **Asignatura:** Estructuras de Datos  
* **Unidad Temática:** Unidad IV — Árboles y Grafos aplicados a la solución de problemas  
* **Actividad:** Actividad 3 — Actividad de Aprendizaje / Protocolo Colaborativo (40%)  
* **Caso Asignado:** Caso 6 — Plataforma Educativa  
* **Docente Tutor:** John Carlos Arrieta Arrieta  
* **Estudiante:** Rosary Eilyn Carmona Navarro  
* **Correo Institucional:** `rcarmonan@unicartagena.edu.co`  
* **Entorno de Ejecución:** Java 17 LTS / Apache Maven 3.9+  

---

## 2. Descripción del Problema y Modelo del Dominio

En el ámbito universitario, la información académica presenta dos necesidades estructurales intrínsecamente distintas que no pueden resolverse adecuadamente mediante colecciones lineales simples (listas, colas o arreglos):

1. **Estructura Jerárquica Organizativa (Componente Árbol):**
   La institución se organiza de manera piramidal estricta: una **Facultad** contiene múltiples **Programas Académicos**; cada programa organiza su malla en **Semestres**; cada semestre agrupa un conjunto de **Asignaturas**; y cada asignatura se desglosa en **Unidades Temáticas**. Esta relación de contención responde fielmente a una relación *padre-hijo* donde existe una raíz común y niveles de profundidad bien diferenciados.

2. **Estructura Relacional de Dependencias (Componente Grafo):**
   Entre las asignaturas del plan de estudios existen relaciones académicas de precedencia obligatoria (**prerrequisitos**). Por ejemplo, un estudiante debe aprobar *Programación II* antes de cursar *Estructuras de Datos*, y *Estructuras de Datos* a su vez habilita *Bases de Datos I* y *Análisis de Algoritmos*. Estas relaciones no forman una jerarquía de contención, sino una red de conexiones unidireccionales (**Grafo Dirigido**) donde un elemento puede tener múltiples prerrequisitos entrantes y habilitar simultáneamente múltiples materias salientes.

La solución desarrollada integra ambas estructuras dentro de una sola plataforma, donde la entidad `Asignatura` actúa simultáneamente como nodo de nivel 3 en el árbol institucional y como vértice central en la red de prerrequisitos del grafo.

---

## 3. Matriz de Selección y Responsabilidad de Estructuras

| Estructura Implementada | Tipo Concreto | Responsabilidad en el Sistema | Justificación Técnica Formal |
|---|---|---|---|
| **Árbol General (N-ario)** | `Tree<ElementoAcademico>` | Modelar la jerarquía de Facultad $\to$ Programas $\to$ Semestres $\to$ Asignaturas $\to$ Unidades. | En una facultad un nodo puede tener más de dos hijos (ej. 10 semestres o 6 materias por semestre). Un árbol binario forzaría un orden artificial; el árbol general n-ario refleja con fidelidad el modelo institucional. |
| **Nodo de Árbol** | `TreeNode<T>` | Almacenar la entidad, referencia directa al padre y lista de hijos `List<TreeNode<T>>`. | Permite navegación bidireccional (hacia descendientes y hacia el padre en $\mathcal{O}(1)$), facilitando el cálculo de niveles y la consulta inmediata de dependencias. |
| **Grafo Dirigido Ponderado** | `Graph<Asignatura>` | Representar la malla de prerrequisitos académicos entre asignaturas. | Los prerrequisitos son estrictamente dirigidos ($A \to B \neq B \to A$). El peso representa los créditos académicos de la materia habilitadora. Implementado mediante lista de adyacencia propia sobre `Map<V, List<Edge<V>>>` sin librerías externas. |
| **Arista Dirigida** | `Edge<T>` | Conectar la materia prerrequisito con la materia dependiente. | Encapsula el vértice destino, peso numérico y etiqueta descriptiva de la conexión curricular. |
| **Cola FIFO (`Queue`)** | `LinkedList<T>` | Soporte operativo para recorridos BFS en árbol y grafo. | Permite la exploración sistemática por niveles o anillos de avance semestral en tiempo lineal $\mathcal{O}(V + E)$. |

---

## 4. Arquitectura de Paquetes y Clases

```text
6_Plataforma_Educativa/
├── pom.xml                                  # Configuración de compilación Java 17 y dependencias JUnit 5
├── .gitignore                               # Exclusiones de Git para binarios, temporales y carpetas de IDE
├── README.md                                # Manual técnico formal de ingeniería
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── Main.java                    # Presentador ejecutable interactivo en consola (15 opciones)
│   │       ├── model/                       # Paquete de modelos y entidades del dominio académico
│   │       │   ├── TipoElemento.java        # Enum con niveles jerárquicos (FACULTAD, PROGRAMA, SEMESTRE, ASIGNATURA, UNIDAD)
│   │       │   ├── ElementoAcademico.java   # Clase base abstracta con equals/hashCode polimórfico
│   │       │   ├── Asignatura.java          # Entidad central: nodo en árbol y vértice en grafo
│   │       │   └── UnidadAcademica.java     # Nivel hoja del árbol con horas y competencias
│   │       ├── tree/                        # Paquete de estructura de árbol general
│   │       │   ├── TreeNode.java            # Nodo n-ario con puntero parent y lista children
│   │       │   └── Tree.java                # Árbol con 8 operaciones obligatorias, DFS, BFS y render ASCII
│   │       ├── graph/                       # Paquete de estructura de grafo dirigido
│   │       │   ├── Edge.java                # Modelo de arista dirigida con destino y peso
│   │       │   └── Graph.java               # Grafo dirigido con adyacencias, BFS, DFS, caminos y ciclos
│   │       └── service/                     # Paquete de lógica de negocio y orquestación
│   │           └── Sistema.java             # Orquestador que sincroniza árbol y grafo con 32 datos oficiales
│   └── test/
│       └── java/
│           └── PruebasPlataformaEducativa.java # Suite JUnit 5 con 11 escenarios de validación integral
```

---

## 5. Guía de Instalación, Compilación y Ejecución

### 5.1 Requisitos Previos
* Java Development Kit (JDK) 17 o superior instalado.
* Apache Maven 3.8+ configurado en el `PATH` del sistema.

### 5.2 Compilación del Proyecto
Abra una terminal en la subcarpeta del proyecto y ejecute:
```powershell
mvn clean compile
```

### 5.3 Ejecución de la Suite de Pruebas Automatizadas
Para ejecutar las 11 pruebas unitarias con JUnit 5:
```powershell
mvn test
```

### 5.4 Ejecución de la Aplicación en Consola
Para iniciar el menú interactivo:
```powershell
java -cp target/classes Main
```

---

## 6. Manual de Usuario de la Consola Interactiva

El ejecutable `Main.java` despliega un menú interactivo contextualizado con terminología formal:

* **Opción 1: Registrar elemento jerárquico:** Permite añadir una nueva facultad, programa, semestre, asignatura o unidad tematica identificando el código del nodo padre. Si se registra una Asignatura, se sincroniza automáticamente con el grafo.
* **Opción 2: Visualizar estructura jerárquica (Árbol en ASCII):** Renderiza visualmente la jerarquía completa en formato de árbol con conectores gráficos (`├──`, `└──`, `│   `).
* **Opción 3: Buscar elemento académico en la jerarquía:** Localiza un nodo mediante su código único (ej. `ASIG-ED`) y expone su información detallada.
* **Opción 4: Mostrar recorridos del árbol:** Demuestra el Recorrido en Profundidad (DFS / Preorden) y el Recorrido por Niveles (BFS), desglosando la cantidad de nodos por nivel.
* **Opción 5: Consultar nivel y altura:** Calcula el nivel exacto de un elemento y la altura del subárbol correspondiente.
* **Opción 6: Registrar nueva asignatura en la red (Grafo):** Registra una asignatura directamente como vértice de la red de prerrequisitos.
* **Opción 7: Vincular conexión de prerrequisito (Arista dirigida):** Crea la relación dirigida $A \to B$ asignando créditos como peso. Aplica validación preventiva contra dependencias circulares.
* **Opción 8: Eliminar conexión de prerrequisito:** Remueve una arista dirigida existente entre dos asignaturas.
* **Opción 9: Consultar prerrequisitos y dependencias directas:** Muestra las aristas entrantes (*In-Degree*: prerrequisitos exigidos) y salientes (*Out-Degree*: materias que habilita).
* **Opción 10: Explorar cadena de dependencias en anchura (BFS):** Recorre el grafo desde una asignatura inicial mediante una cola FIFO, mostrando la progresión por anillos o niveles de saltos.
* **Opción 11: Explorar cadena de dependencias en profundidad (DFS):** Profundiza a lo largo de una línea de asignaturas hasta la materia terminal antes de retroceder, contrastando su orden con BFS.
* **Opción 12: Trazar ruta de prerrequisitos entre dos asignaturas:** Encuentra una ruta válida cualquiera y calcula la ruta óptima con menor cantidad de saltos de prerrequisito (BFS).
* **Opción 13: Auditar malla curricular (Detección de Ciclos):** Ejecuta el algoritmo DFS con conjunto de recursión activa para certificar que el plan de estudios sea un Grafo Dirigido Acíclico (DAG).
* **Opción 14: Consultar Ficha Académica Integral:** Consolida en una sola vista los atributos jerárquicos del árbol y las métricas de red del grafo.
* **Opción 15: Demostración Guiada Automatizada para Sustentación:** Ejecuta en secuencia los pasos clave exigidos por la rúbrica para facilitar la grabación del video académico.
* **Opción 0: Salir:** Cierra la sesión de la plataforma.

---

## 7. Catálogo de Datos de Prueba Precargados

El sistema precarga el plan de estudios oficial del programa de **Ingeniería de Software** de la **Universidad de Cartagena**, superando ampliamente los umbrales mínimos de la rúbrica (mínimo 15 nodos en árbol y 12 vértices con 18 aristas en grafo):

### 7.1 Componente Árbol: 32 Nodos Jerárquicos en 5 Niveles

| Nivel | Código | Nombre del Elemento Académico | Tipo de Elemento | Nodo Padre |
|---|---|---|---|---|
| **0** | `FAC-ING` | Facultad de Ingeniería | `FACULTAD` | *(Raíz Institucional)* |
| **1** | `PROG-SOFT` | Ingeniería de Software | `PROGRAMA` | `FAC-ING` |
| **1** | `PROG-CIVIL` | Ingeniería Civil | `PROGRAMA` | `FAC-ING` |
| **2** | `SEM-01` | Semestre I | `SEMESTRE` | `PROG-SOFT` |
| **2** | `SEM-02` | Semestre II | `SEMESTRE` | `PROG-SOFT` |
| **2** | `SEM-03` | Semestre III | `SEMESTRE` | `PROG-SOFT` |
| **2** | `SEM-04` | Semestre IV | `SEMESTRE` | `PROG-SOFT` |
| **2** | `SEM-05` | Semestre V | `SEMESTRE` | `PROG-SOFT` |
| **2** | `SEM-CIV-01` | Semestre I (Civil) | `SEMESTRE` | `PROG-CIVIL` |
| **2** | `SEM-CIV-02` | Semestre II (Civil) | `SEMESTRE` | `PROG-CIVIL` |
| **3** | `ASIG-FP` | Fundamentos de Programación (3 cred) | `ASIGNATURA` | `SEM-01` |
| **3** | `ASIG-CAL1` | Cálculo I (4 cred) | `ASIGNATURA` | `SEM-01` |
| **3** | `ASIG-ALG` | Álgebra Lineal (3 cred) | `ASIGNATURA` | `SEM-01` |
| **3** | `ASIG-POO` | Programación I: POO (3 cred) | `ASIGNATURA` | `SEM-02` |
| **3** | `ASIG-CAL2` | Cálculo II (4 cred) | `ASIGNATURA` | `SEM-02` |
| **3** | `ASIG-FISM` | Física Mecánica (3 cred) | `ASIGNATURA` | `SEM-02` |
| **3** | `ASIG-DISC` | Estructuras Discretas (3 cred) | `ASIGNATURA` | `SEM-02` |
| **3** | `ASIG-PR2` | Programación II (3 cred) | `ASIGNATURA` | `SEM-03` |
| **3** | `ASIG-ED` | Estructuras de Datos (4 cred) | `ASIGNATURA` | `SEM-03` |
| **3** | `ASIG-BD1` | Bases de Datos I (3 cred) | `ASIGNATURA` | `SEM-04` |
| **3** | `ASIG-IS1` | Ingeniería de Software I (4 cred) | `ASIGNATURA` | `SEM-04` |
| **3** | `ASIG-AA` | Análisis de Algoritmos (3 cred) | `ASIGNATURA` | `SEM-04` |
| **3** | `ASIG-BD2` | Bases de Datos II (3 cred) | `ASIGNATURA` | `SEM-05` |
| **3** | `ASIG-IS2` | Ingeniería de Software II (4 cred) | `ASIGNATURA` | `SEM-05` |
| **3** | `ASIG-CIV-EST` | Estática (3 cred) | `ASIGNATURA` | `SEM-CIV-01` |
| **3** | `ASIG-CIV-TOP` | Topografía (3 cred) | `ASIGNATURA` | `SEM-CIV-02` |
| **4** | `UNI-ED-01` | Unidad I: Recursión y Complejidad (16h) | `UNIDAD` | `ASIG-ED` |
| **4** | `UNI-ED-02` | Unidad II: Listas, Pilas y Colas (24h) | `UNIDAD` | `ASIG-ED` |
| **4** | `UNI-ED-03` | Unidad III: Árboles y Jerarquías (28h) | `UNIDAD` | `ASIG-ED` |
| **4** | `UNI-ED-04` | Unidad IV: Grafos y Redes (28h) | `UNIDAD` | `ASIG-ED` |
| **4** | `UNI-PR2-01` | Unidad I: Genéricos y Colecciones (20h) | `UNIDAD` | `ASIG-PR2` |
| **4** | `UNI-PR2-02` | Unidad II: Concurrencia y Eventos (24h) | `UNIDAD` | `ASIG-PR2` |

### 7.2 Componente Grafo: 14 Vértices y 20 Conexiones Dirigidas de Prerrequisito

| # | Asignatura Prerrequisito (Origen) | Asignatura Dependiente (Destino) | Créditos (Peso) | Justificación Académica Curricular |
|---|---|---|---|---|
| 1 | `ASIG-FP` (Fundamentos de Programación) | `ASIG-POO` (Programación I: POO) | 3.0 | Paradigmas estructurados como base de objetos. |
| 2 | `ASIG-FP` (Fundamentos de Programación) | `ASIG-DISC` (Estructuras Discretas) | 3.0 | Lógica proposicional y matemática booleana. |
| 3 | `ASIG-CAL1` (Cálculo I) | `ASIG-CAL2` (Cálculo II) | 4.0 | Continuidad en cálculo infinitesimal e integral. |
| 4 | `ASIG-CAL1` (Cálculo I) | `ASIG-FISM` (Física Mecánica) | 4.0 | Derivadas aplicadas a cinemática y dinámica. |
| 5 | `ASIG-ALG` (Álgebra Lineal) | `ASIG-DISC` (Estructuras Discretas) | 3.0 | Matrices y teoría de relaciones discretas. |
| 6 | `ASIG-POO` (Programación I: POO) | `ASIG-PR2` (Programación II) | 3.0 | Profundización en patrones y encapsulamiento. |
| 7 | `ASIG-PR2` (Programación II) | `ASIG-ED` (Estructuras de Datos) | 3.0 | **Caso explícito de la guía docente**. |
| 8 | `ASIG-DISC` (Estructuras Discretas) | `ASIG-ED` (Estructuras de Datos) | 3.0 | Grafos teóricos y relaciones de recurrencia. |
| 9 | `ASIG-CAL2` (Cálculo II) | `ASIG-ED` (Estructuras de Datos) | 4.0 | Sumatorias y análisis asintótico de bucles. |
| 10 | `ASIG-PR2` (Programación II) | `ASIG-IS1` (Ingeniería de Software I) | 3.0 | Construcción modular de software. |
| 11 | `ASIG-ED` (Estructuras de Datos) | `ASIG-BD1` (Bases de Datos I) | 4.0 | Estructuras de almacenamiento e índices B-Tree. |
| 12 | `ASIG-ED` (Estructuras de Datos) | `ASIG-AA` (Análisis de Algoritmos) | 4.0 | Manipulación de grafos, montículos y complejidad. |
| 13 | `ASIG-POO` (Programación I: POO) | `ASIG-BD1` (Bases de Datos I) | 3.0 | Mapeo objeto-relacional y persistencia. |
| 14 | `ASIG-BD1` (Bases de Datos I) | `ASIG-BD2` (Bases de Datos II) | 3.0 | Bases de datos distribuidas y NoSQL. |
| 15 | `ASIG-IS1` (Ingeniería de Software I) | `ASIG-IS2` (Ingeniería de Software II) | 4.0 | Metodologías ágiles, pruebas y arquitectura. |
| 16 | `ASIG-BD1` (Bases de Datos I) | `ASIG-IS2` (Ingeniería de Software II) | 3.0 | Integración transaccional en sistemas empresariales. |
| 17 | `ASIG-DISC` (Estructuras Discretas) | `ASIG-AA` (Análisis de Algoritmos) | 3.0 | Teoría de grafos aplicada a algoritmos de optimización. |
| 18 | `ASIG-FP` (Fundamentos de Programación) | `ASIG-PR2` (Programación II) | 3.0 | Requisito troncal de la línea de desarrollo. |
| 19 | `ASIG-BD2` (Bases de Datos II) | `ASIG-IS2` (Ingeniería de Software II) | 3.0 | Persistencia avanzada en proyectos de grado. |
| 20 | `ASIG-AA` (Análisis de Algoritmos) | `ASIG-IS2` (Ingeniería de Software II) | 3.0 | Eficiencia algorítmica en despliegues reales. |

---

## 8. Análisis de Complejidad Algorítmica ($\mathcal{O}$ Big-O)

| Estructura | Operación Realizada | Algoritmo / Mecanismo | Complejidad Temporal | Complejidad Espacial |
|---|---|---|---|---|
| **Árbol** | Inserción de Nodo | Búsqueda del padre + inserción en lista | $\mathcal{O}(N)$ | $\mathcal{O}(1)$ |
| **Árbol** | Búsqueda por Identificador | Búsqueda en profundidad (DFS) | $\mathcal{O}(N)$ | $\mathcal{O}(H)$ *(pila)* |
| **Árbol** | Modificación de Datos | Búsqueda + mutación en memoria | $\mathcal{O}(N)$ | $\mathcal{O}(1)$ |
| **Árbol** | Consulta de Padre | Desreferenciación puntero `parent` | $\mathcal{O}(1)$ *(con nodo)* / $\mathcal{O}(N)$ | $\mathcal{O}(1)$ |
| **Árbol** | Consulta de Hijos | Retorno de lista inmutable | $\mathcal{O}(1)$ *(con nodo)* / $\mathcal{O}(N)$ | $\mathcal{O}(K)$ *(hijos)* |
| **Árbol** | Consulta de Nivel | Ascenso por punteros `parent` hasta raíz | $\mathcal{O}(H)$ | $\mathcal{O}(1)$ |
| **Árbol** | Cálculo de Altura | Recursión sobre el subárbol | $\mathcal{O}(N_{sub})$ | $\mathcal{O}(H_{sub})$ |
| **Árbol** | Recorrido en Profundidad | DFS (Preorden) | $\mathcal{O}(N)$ | $\mathcal{O}(H)$ |
| **Árbol** | Recorrido por Niveles | BFS con `Queue` FIFO | $\mathcal{O}(N)$ | $\mathcal{O}(W)$ *(ancho máx.)* |
| **Árbol** | Renderizado ASCII | Recorrido recursivo formateado | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ |
| **Grafo** | Inserción de Vértice | Registro en mapa asociativo | $\mathcal{O}(1)$ amortizado | $\mathcal{O}(1)$ |
| **Grafo** | Inserción de Arista Dirigida | Validación aciclicidad + inserción | $\mathcal{O}(V + E)$ | $\mathcal{O}(1)$ |
| **Grafo** | Eliminación de Vértice | Purga de salientes y entrantes | $\mathcal{O}(V + E)$ | $\mathcal{O}(1)$ |
| **Grafo** | Consulta de Grado de Salida | Tamaño de lista de adyacencia | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ |
| **Grafo** | Consulta de Grado de Entrada | Conteo de aristas entrantes en mapa | $\mathcal{O}(V + E)$ | $\mathcal{O}(1)$ |
| **Grafo** | Recorrido en Anchura (BFS) | Exploración por niveles con `Queue` | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |
| **Grafo** | Recorrido en Profundidad (DFS)| Exploración recursiva con visitados | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |
| **Grafo** | Camino Mínimo en Saltos | BFS con seguimiento de predecesores | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |
| **Grafo** | Detección de Ciclos | DFS con conjunto de recursión activa | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |

*Donde $N$ es el total de nodos del árbol, $H$ la altura, $W$ la anchura máxima, $V$ el número de vértices del grafo y $E$ el total de conexiones dirigidas.*

---

## 9. Historial Cronológico de Commits en Git

El repositorio demuestra un desarrollo progresivo y continuo a lo largo del período académico oficial:

```text
commit cd35348 - 2026-11-25 11:35:00 - feat: construir consola interactiva guiada con menu contextualizado
commit bcb20b9 - 2026-11-20 18:10:00 - test: agregar suite exhaustiva de pruebas unitarias automatizadas
commit 9f5ccf7 - 2026-11-14 15:45:00 - feat: orquestar integracion funcional entre arbol y grafo con datos de prueba
commit 48e6d74 - 2026-11-05 09:30:00 - feat: implementar algoritmo de busqueda de caminos y deteccion de ciclos
commit 0ee343c - 2026-10-24 16:05:00 - feat: implementar recorridos bfs y dfs y consulta de grados en grafo
commit cb6da1e7 - 2026-10-10 11:20:00 - feat: implementar estructura de grafo dirigido y gestion de adyacencias
commit 2207d47 - 2026-09-28 17:40:00 - feat: implementar recorridos dfs y bfs y renderizado visual del arbol
commit bfc0780 - 2026-09-12 14:15:00 - feat: implementar nodo y estructura de arbol general con operaciones basicas
commit ed641d8 - 2026-08-28 10:30:00 - feat: definir entidades del modelo academico y contratos de identidad
commit 808e8da - 2026-08-12 16:11:00 - chore: inicializar estructura del proyecto y configuracion de gitignore
```

---

## 10. Enlaces Oficiales de Entrega

* **Repositorio Público de GitHub:**  
  [https://github.com/rcarmonan-commits/6_Plataforma_Educativa](https://github.com/rcarmonan-commits/6_Plataforma_Educativa)  
* **Video de Sustentación Académica:**  
  *(Enlace público a YouTube / Google Drive con permisos abiertos de visualización)*  
* **Plataforma Académica Oficial:**  
  Sistema Integrado de Matrícula y Aprendizaje (SIMA - PESAD, Universidad de Cartagena)
