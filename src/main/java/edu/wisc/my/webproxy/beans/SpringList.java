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
public class SpringList implements List {
    private List wrappedList;

    public List getWrappedList() {
        return wrappedList;
    }

    public void setWrappedList(List wrappedList) {
        this.wrappedList = wrappedList;
    }

    public void add(int index, Object element) {
        wrappedList.add(index, element);
    }

    public boolean add(Object o) {
        return wrappedList.add(o);
    }

    public boolean addAll(int index, Collection c) {
        return wrappedList.addAll(index, c);
    }

    public boolean addAll(Collection c) {
        return wrappedList.addAll(c);
    }

    public void clear() {
        wrappedList.clear();
    }

    public boolean contains(Object o) {
        return wrappedList.contains(o);
    }

    public boolean containsAll(Collection c) {
        return wrappedList.containsAll(c);
    }

    public boolean equals(Object obj) {
        return wrappedList.equals(obj);
    }

    public Object get(int index) {
        return wrappedList.get(index);
    }

    public int hashCode() {
        return wrappedList.hashCode();
    }

    public int indexOf(Object o) {
        return wrappedList.indexOf(o);
    }

    public boolean isEmpty() {
        return wrappedList.isEmpty();
    }

    public Iterator iterator() {
        return wrappedList.iterator();
    }

    public int lastIndexOf(Object o) {
        return wrappedList.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return wrappedList.listIterator();
    }

    public ListIterator listIterator(int index) {
        return wrappedList.listIterator(index);
    }

    public Object remove(int index) {
        return wrappedList.remove(index);
    }

    public boolean remove(Object o) {
        return wrappedList.remove(o);
    }

    public boolean removeAll(Collection c) {
        return wrappedList.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return wrappedList.retainAll(c);
    }

    public Object set(int index, Object element) {
        return wrappedList.set(index, element);
    }

    public int size() {
        return wrappedList.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return wrappedList.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return wrappedList.toArray();
    }

    public Object[] toArray(Object[] a) {
        return wrappedList.toArray(a);
    }

    public String toString() {
        return wrappedList.toString();
    }
}