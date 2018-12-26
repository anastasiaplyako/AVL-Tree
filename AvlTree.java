import java.util.*;

public class AvlTree<T extends Comparable<T>> extends AbstractCollection<T> implements SortedSet<T> {
    class Node<T extends Comparable<T>> {
        int height;
        Node<T> right;
        Node<T> left;
        T value;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;
    List list = new ArrayList();
    private int size = 0;

    private Node<T> find(T value) {
        if (root == null) return null;
        else return findNext(root, value);
    }

    private Node<T> findNext(Node<T> start, T value) {
        int comparison = start == null ? -1 : value.compareTo(start.value);
        if (comparison == 0) return start;
        else if (comparison > 0) {
            if (start.right == null) return start;
            return findNext(start.right, value);
        } else {
            if (start.left == null) return start;
            return findNext(start.right, value);
        }
    }

    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return null;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return null;
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return null;
    }

    @Override
    public T first() {
        Node<T> current = root;
        if (current == null) throw new NoSuchElementException();
        else {
            while (current.left != null) {
                current = current.left;
            }
        }
        return current.value;
    }

    @Override
    public T last() {
        Node<T> current = root;
        if (current == null) throw new NoSuchElementException();
        else {
            while (current.right != null) {
                current = current.right;
            }
        }
        return current.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        T t = (T) o;
        Node<T> node = find(t);
        return node != null && t.compareTo(node.value) == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorAVL();
    }

    public class IteratorAVL implements Iterator<T> {
        private Node<T> current = null;
        List<Node<T>> list = new ArrayList<Node<T>>();
        private int index = 0;

        IteratorAVL() {
            toList(root);
        }

        private void toList(Node<T> node) {
            if (node != null) {
                list.add(node.left);
                list.add(node);
                list.add(node.right);
            }
        }

        public Node<T> findNext() {
            return list.get(index++);
        }

        @Override
        public boolean hasNext() {
            return findNext() != null;
        }

        @Override
        public T next() {
            current = findNext();
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }
    }

    private int height(Node<T> node) {
        if (node == null) return -1;
        return node.height;
    }

    private int balance(Node<T> node) {
        if (node == null) return 0;
        return height(node.right) - height(node.left);
    }

    private int newHeight(Node<T> node) {
        int r = height(node.right);
        int l = height(node.left);
        return Math.max(r, l) + 1;
    }

    private Node<T> smallLeftRotation(Node<T> current) {
        Node<T> newNode = current.right;
        current.right = newNode.left;
        newNode.left = current;
        newNode.height = newHeight(newNode);
        current.height = newHeight(current);
        return newNode;
    }

    private Node<T> smallRightRotation(Node<T> current) {
        Node<T> newNode = current.left;
        current.left = newNode.right;
        newNode.right = current;
        newNode.height = newHeight(newNode);
        current.height = newHeight(current);
        return newNode;
    }

    private Node<T> rotateLeftThenRight(Node<T> node) {
        node.left = smallLeftRotation(node.left);
        return smallRightRotation(node);
    }

    private Node<T> rotateRightThenLeft(Node<T> node) {
        node.right = smallRightRotation(node.right);
        return smallLeftRotation(node);
    }

    private Node<T> balancing(Node<T> node) {
        newHeight(node);
        if (balance(node) == 2) {
            if (balance(node.right) < 0) {
                rotateRightThenLeft(node);
            } else smallLeftRotation(node);
        }
        if (balance(node) == -2) {
            if (balance(node.left) > 0) {
                rotateLeftThenRight(node);
            } else smallRightRotation(node);
        }
        return node;
    }

    private Node<T> balancingForRemove(Node<T> current) {
        int balance = balance(current);
        if (balance > 1) {
            if (balance(current.left) < 0) {
                current.left = smallLeftRotation(current.left);
            }
            return smallRightRotation(current);
        }
        if (balance < -1) {
            if (balance(current.right) > 0) {
                current.right = smallRightRotation(current.right);
            }
            return smallLeftRotation(current);
        }
        return current;
    }

    @Override
    public boolean add(T t) {
        Node<T> node = find(t);
        int comparison = node == null ? -1 : t.compareTo(node.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (node == null) root = newNode;
        else {
            if (comparison == 0) return false;
            else {
                if (comparison > 0) {
                    assert node.right == null;
                    node.right = newNode;
                } else {
                    assert node.left == null;
                    node.left = newNode;
                }
            }
        }
        balancing(newNode);
        newNode.height = newHeight(newNode);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        T t = (T) o;
        Node<T> res = deleted(root, t);
        size--;
        return (res != null);
    }

    private Node<T> deleted(Node<T> root, T t) {
        Node<T> node = root;
        int compare = t.compareTo(root.value);
        if (compare < 0) {
            node.left = deleted(node.left, t);
        } else if (compare > 0) {
            node.right = deleted(node.right, t);
        } else if (node.right != null) {
            node.value = firstNode(node.right).value;
            node.right = deleted(node.right, node.value);
        } else {
            if (node.left != null) {
                node.value = lastNode(node.left).value;
                node.left = deleted(node.left, node.value);
            } else {
                node = node.right;
            }
        }
        return balancingForRemove(node);
    }

    private Node<T> firstNode(Node<T> node) {
        if (node.left == null) return node;
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node<T> lastNode(Node<T> node) {
        if (node.right == null) return node;
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c.isEmpty()) return false;
        for (Object element : c) {
            if (contains(element)) ;
            else return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        else {
            for (T element : c) {
                add(element);
            }
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) return false;
        for (Object element : c) {
            if (contains(element)) {
                remove(element);
            } else return false;
        }
        return true;
    }

    @Override
    public void clear() {

    }
}
