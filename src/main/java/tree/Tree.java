package tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Estructura de datos propia de Arbol General (N-ario).
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Implementa un arbol jerarquico donde cada nodo puede tener cero, uno o multiples
 * hijos, administrando una raiz unica y un conjunto de operaciones obligatorias
 * de consulta, manipulacion y auditoria estructural.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple con las especificaciones de las Secciones 4, 5 y 7 de la rubrica.
 * En una plataforma educativa, una entidad (como una facultad o un semestre)
 * posee mas de dos dependencias directas, haciendo del arbol general la estructura
 * adecuada frente a limitaciones de arboles binarios.</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Opera mediante enlaces de memoria entre nodos {@link TreeNode}. Cada operacion
 * respeta las politicas institucionales de integridad referencial y gestion de niveles.</p>
 *
 * @param <T> Tipo de datos almacenados en los nodos.
 */
public class Tree<T> {

    private TreeNode<T> root;

    public Tree() {
        this.root = null;
    }

    public Tree(T rootData) {
        if (rootData != null) {
            this.root = new TreeNode<>(rootData);
        }
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Operacion Obligatoria 1: Insertar un nodo.
     * Permite agregar la raiz cuando el arbol esta vacio, o bien agregar un nuevo
     * elemento como hijo del nodo padre identificado.
     *
     * @param parentIdentifier Criterio o dato para localizar el padre (nulo solo si se crea la raiz).
     * @param childData Dato a insertar.
     * @return El nuevo TreeNode creado e insertado.
     */
    public TreeNode<T> insert(Object parentIdentifier, T childData) {
        if (childData == null) {
            throw new IllegalArgumentException("El dato del nuevo nodo no puede ser nulo.");
        }

        if (isEmpty()) {
            this.root = new TreeNode<>(childData);
            return this.root;
        }

        if (parentIdentifier == null) {
            throw new IllegalArgumentException("El arbol ya tiene raiz. Debe especificar el identificador del padre.");
        }

        TreeNode<T> parentNode = findNode(parentIdentifier);
        if (parentNode == null) {
            throw new IllegalArgumentException("No se encontro el nodo padre con identificador: " + parentIdentifier);
        }

        TreeNode<T> newNode = new TreeNode<>(childData);
        parentNode.addChild(newNode);
        return newNode;
    }

    /**
     * Operacion Obligatoria 2: Buscar un nodo.
     * Localiza un elemento dentro de la jerarquia a partir de un identificador
     * o del propio dato utilizando busqueda en profundidad (DFS).
     *
     * @param identifier Criterio de busqueda (soporta equals polimorfico).
     * @return El TreeNode localizado o {@code null} si no existe en el arbol.
     */
    public TreeNode<T> findNode(Object identifier) {
        if (isEmpty() || identifier == null) {
            return null;
        }
        return searchRecursive(root, identifier);
    }

    private TreeNode<T> searchRecursive(TreeNode<T> current, Object identifier) {
        if (current == null) {
            return null;
        }
        if (Objects.equals(current.getData(), identifier) || current.equals(identifier)) {
            return current;
        }
        for (TreeNode<T> child : current.getChildren()) {
            TreeNode<T> found = searchRecursive(child, identifier);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Operacion Obligatoria 3: Modificar informacion.
     * Permite modificar los atributos de la entidad contenida en un nodo
     * sin alterar sus relaciones jerarquicas (padre e hijos).
     *
     * @param identifier Identificador del nodo a modificar.
     * @param mutator Accion consumidora que aplica los cambios sobre la entidad.
     * @return {@code true} si se encontro y modifico el nodo; {@code false} en caso contrario.
     */
    public boolean modify(Object identifier, Consumer<T> mutator) {
        TreeNode<T> target = findNode(identifier);
        if (target == null || mutator == null) {
            return false;
        }
        mutator.accept(target.getData());
        return true;
    }

    /**
     * Operacion Obligatoria 4: Eliminar un nodo.
     * Aplica la politica formal documentada:
     * - Si el nodo es hoja: se elimina directamente.
     * - Si el nodo posee descendientes y {@code cascade} es {@code false}: impide la eliminacion
     *   lanzando {@link IllegalStateException} para preservar la integridad de los datos.
     * - Si {@code cascade} es {@code true}: elimina recursivamente todo el subarbol.
     *
     * @param identifier Identificador del elemento a eliminar.
     * @param cascade Si es true, autoriza la eliminacion del subarbol completo.
     * @return Lista con todos los elementos eliminados (util para desvincular dependencias en grafos).
     */
    public List<T> delete(Object identifier, boolean cascade) {
        TreeNode<T> target = findNode(identifier);
        if (target == null) {
            return Collections.emptyList();
        }

        if (!target.isLeaf() && !cascade) {
            throw new IllegalStateException("Politica de integridad: El nodo posee descendientes. " +
                    "Debe confirmar la eliminacion en cascada para remover el subarbol.");
        }

        List<T> removedItems = new ArrayList<>();
        collectSubtreeData(target, removedItems);

        if (target.isRoot()) {
            this.root = null;
        } else {
            TreeNode<T> parent = target.getParent();
            if (parent != null) {
                parent.removeChild(target);
            }
        }

        return removedItems;
    }

    private void collectSubtreeData(TreeNode<T> current, List<T> list) {
        if (current == null) {
            return;
        }
        list.add(current.getData());
        for (TreeNode<T> child : current.getChildren()) {
            collectSubtreeData(child, list);
        }
    }

    /**
     * Operacion Obligatoria 5: Consultar hijos.
     * Dado un elemento, retorna la lista de sus hijos directos.
     *
     * @param identifier Identificador del nodo a consultar.
     * @return Lista inmutable de los datos de los hijos directos.
     */
    public List<T> getChildren(Object identifier) {
        TreeNode<T> node = findNode(identifier);
        if (node == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        for (TreeNode<T> child : node.getChildren()) {
            result.add(child.getData());
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Operacion Obligatoria 6: Consultar padre.
     * Determina a que elemento pertenece directamente un nodo en la jerarquia.
     *
     * @param identifier Identificador del nodo a consultar.
     * @return El dato del nodo padre, o {@code null} si es la raiz o no existe.
     */
    public T getParent(Object identifier) {
        TreeNode<T> node = findNode(identifier);
        if (node == null || node.getParent() == null) {
            return null;
        }
        return node.getParent().getData();
    }

    /**
     * Operacion Obligatoria 7: Consultar nivel.
     * Determina en que nivel de la jerarquia se encuentra un elemento.
     * La raiz se encuentra en el nivel 0, sus hijos en el nivel 1, y asi sucesivamente.
     *
     * @param identifier Identificador del elemento.
     * @return Nivel numerico (0 para la raiz, 1 para nivel inferior, etc.) o -1 si no existe.
     */
    public int getLevel(Object identifier) {
        TreeNode<T> node = findNode(identifier);
        if (node == null) {
            return -1;
        }
        int level = 0;
        TreeNode<T> current = node.getParent();
        while (current != null) {
            level++;
            current = current.getParent();
        }
        return level;
    }

    /**
     * Operacion Obligatoria 8: Calcular altura del arbol o subarbol.
     * La altura de un arbol vacio es -1; la de una hoja es 0;
     * y la de un nodo con hijos es 1 + max(altura(hijo)).
     *
     * @return Altura global del arbol.
     */
    public int getHeight() {
        if (isEmpty()) {
            return -1;
        }
        return calculateSubtreeHeight(root);
    }

    /**
     * Calcula la altura del subarbol a partir de un nodo especifico.
     *
     * @param identifier Identificador del nodo raiz del subarbol.
     * @return Altura del subarbol o -1 si el nodo no existe.
     */
    public int getHeight(Object identifier) {
        TreeNode<T> node = findNode(identifier);
        if (node == null) {
            return -1;
        }
        return calculateSubtreeHeight(node);
    }

    private int calculateSubtreeHeight(TreeNode<T> node) {
        if (node == null || node.isLeaf()) {
            return 0;
        }
        int maxHeight = 0;
        for (TreeNode<T> child : node.getChildren()) {
            maxHeight = Math.max(maxHeight, calculateSubtreeHeight(child));
        }
        return 1 + maxHeight;
    }

    /**
     * Retorna el total de nodos presentes en la estructura jerarquica.
     */
    public int size() {
        if (isEmpty()) {
            return 0;
        }
        return countNodes(root);
    }

    private int countNodes(TreeNode<T> current) {
        int count = 1;
        for (TreeNode<T> child : current.getChildren()) {
            count += countNodes(child);
        }
        return count;
    }
}
