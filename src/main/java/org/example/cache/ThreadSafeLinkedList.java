package org.example.cache;

import lombok.Getter;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

public class ThreadSafeLinkedList<T> {

    private Node<T> head;

    @Getter
    private Node<T> tail;

    @Getter
    private int size;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Node<T> addFirst(T data) {
        lock.writeLock().lock();
        try {
            Node<T> newNode = new Node<>(data);
            if (isEmpty()) {
                head = tail = newNode;
            } else {
                newNode.next = head;
                head.prev = newNode;
                head = newNode;
            }
            size++;
            return head;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addLast(T data) {
        lock.writeLock().lock();
        try {
            Node<T> newNode = new Node<>(data);
            if (isEmpty()) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            size++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeFirst() {
        lock.writeLock().lock();
        try {
            if (!isEmpty()) {
                head = head.next;
                if (head != null) {
                    head.prev = null;
                } else {
                    tail = null;
                }
                size--;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeLast() {
        lock.writeLock().lock();
        try {
            if (!isEmpty()) {
                tail = tail.prev;
                if (tail != null) {
                    tail.next = null;
                } else {
                    head = null;
                }
                size--;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(T data) {
        lock.writeLock().lock();
        try {
            Node<T> current = head;
            while (current != null) {
                if (current.data.equals(data)) {
                    if (current.prev != null) {
                        current.prev.next = current.next;
                    } else {
                        head = current.next;
                    }

                    if (current.next != null) {
                        current.next.prev = current.prev;
                    } else {
                        tail = current.prev;
                    }

                    size--;
                    break;
                }
                current = current.next;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeIf(Predicate<? super T> predicate) {
        lock.writeLock().lock();
        try {
            Node<T> current = head;
            while (current != null) {
                if (predicate.test(current.data)) {
                    if (current.prev != null) {
                        current.prev.next = current.next;
                    } else {
                        head = current.next;
                    }

                    if (current.next != null) {
                        current.next.prev = current.prev;
                    } else {
                        tail = current.prev;
                    }

                    size--;
                }
                current = current.next;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return size == 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static class Node<T> {
        @Getter
        private T data;
        private Node<T> prev;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }
}
