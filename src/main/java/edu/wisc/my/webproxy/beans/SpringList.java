/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.my.webproxy.beans;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author dgrimwood
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SpringList<E> implements List<E> {
    private List<E> wrappedList;

    public List<E> getWrappedList() {
        return wrappedList;
    }

    public void setWrappedList(List<E> wrappedList) {
        this.wrappedList = wrappedList;
    }

    public boolean add(E o) {
        return this.wrappedList.add(o);
    }

    public void add(int index, E element) {
        this.wrappedList.add(index, element);
    }

    public boolean addAll(Collection<? extends E> c) {
        return this.wrappedList.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return this.wrappedList.addAll(index, c);
    }

    public void clear() {
        this.wrappedList.clear();
    }

    public boolean contains(Object o) {
        return this.wrappedList.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return this.wrappedList.containsAll(c);
    }

    public boolean equals(Object o) {
        return this.wrappedList.equals(o);
    }

    public E get(int index) {
        return this.wrappedList.get(index);
    }

    public int hashCode() {
        return this.wrappedList.hashCode();
    }

    public int indexOf(Object o) {
        return this.wrappedList.indexOf(o);
    }

    public boolean isEmpty() {
        return this.wrappedList.isEmpty();
    }

    public Iterator<E> iterator() {
        return this.wrappedList.iterator();
    }

    public int lastIndexOf(Object o) {
        return this.wrappedList.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return this.wrappedList.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return this.wrappedList.listIterator(index);
    }

    public E remove(int index) {
        return this.wrappedList.remove(index);
    }

    public boolean remove(Object o) {
        return this.wrappedList.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return this.wrappedList.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return this.wrappedList.retainAll(c);
    }

    public E set(int index, E element) {
        return this.wrappedList.set(index, element);
    }

    public int size() {
        return this.wrappedList.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return this.wrappedList.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return this.wrappedList.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return this.wrappedList.toArray(a);
    }

    @Override
    public String toString() {
        return this.wrappedList.toString();
    }
}