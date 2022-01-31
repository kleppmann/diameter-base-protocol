/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.linkedlogics.diameter.utility;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class UIDGenerator {

    private long value;
    private final static Lock mutex = new ReentrantLock();

    public UIDGenerator() {
        value = System.currentTimeMillis();
    }

    public UIDGenerator(long value) {
        this.value = value;
    }

    public int nextInt() {
        return (int) (0x7FFFFFFF & nextLong());
    }

    public long nextLong() {
        mutex.lock();
        value++;
        mutex.unlock();
        return value;
    }
}
