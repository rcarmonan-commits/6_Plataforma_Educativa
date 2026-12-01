package tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa un nodo generico dentro de la estructura de arbol general (n-ario).
 *
 * <p><b>¿Que es y que se hizo?</b><br>
 * Modela el nodo de informacion que almacena un dato generico de tipo {@code T},
 * una referencia directa a su nodo padre y una lista dinamica de nodos hijos.</p>
 *
 * <p><b>¿Por que se uso?</b><br>
 * Cumple con el requerimiento de la rubrica (Seccion 6) de utilizar una estructura
 * de nodo para arboles generales con multiples descendientes y permite navegar
 * bidireccionalmente (hacia los hijos y hacia el padre en tiempo constante).</p>
 *
 * <p><b>¿Como funciona?</b><br>
 * Mantiene la coherencia de la jerarquia vinculando automaticamente el puntero
 * padre al momento de anadir un hijo y desvinculandolo al momento de eliminarlo.</p>
 *
 * @param <T> Tipo de dato almacenado en el nodo.
 */
public class TreeNode<T> {

    private T data;
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children;

    public TreeNode(T data) {
        if (data == null) {
            throw new IllegalArgumentException("El dato del nodo no puede ser nulo.");
        }
        this.data = data;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        if (data == null) {
            throw new IllegalArgumentException("No se puede asignar un dato nulo al nodo.");
        }
        this.data = data;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Agrega un nuevo hijo a este nodo y actualiza automaticamente la referencia
     * del padre en el hijo para mantener la integridad de la estructura.
     */
    public void addChild(TreeNode<T> child) {
        if (child == null) {
            throw new IllegalArgumentException("No se puede agregar un nodo hijo nulo.");
        }
        if (child == this) {
            throw new IllegalArgumentException("Un nodo no puede ser hijo de si mismo.");
        }
        child.setParent(this);
        this.children.add(child);
    }

    /**
     * Elimina un hijo directo y desvincula su referencia al padre.
     */
    public boolean removeChild(TreeNode<T> child) {
        if (child != null && this.children.remove(child)) {
            child.setParent(null);
            return true;
        }
        return false;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }

    public int getDegree() {
        return children.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof TreeNode<?>) {
            TreeNode<?> other = (TreeNode<?>) obj;
            return Objects.equals(this.data, other.data);
        }
        // Soporte polimorfico para comparar nodo contra el dato o identificador contenido
        return Objects.equals(this.data, obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }
}
