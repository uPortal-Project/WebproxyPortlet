/*******************************************************************************
 * Copyright 2004, The Board of Regents of the University of Wisconsin System.
 * All rights reserved.
 *
 * A non-exclusive worldwide royalty-free license is granted for this Software.
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose is granted
 * provided that such redistribution and use in source and binary forms, with or
 * without modification meets the following conditions:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 * acknowledgement:
 *
 * "This product includes software developed by The Board of Regents of
 * the University of Wisconsin System."
 *
 *THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
 *WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
 *BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
 *THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
 *INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 *OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

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