/* Copyright (c) 2018, Eric L. McCorkle. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.metricspace.consensus.actor;

/**
 * Observers who see only committed actions.
 *
 * @param <A> Type of actions.
 * @param <L> Type of session listeners.
 * @param <S> Type of session handles.
 */
public interface Observer<A,
                          L extends Observer.Session.Listener,
                          S extends Observer.Session<A, L>>
    extends Actor<L, S> {

    /**
     * Session interface for {@link Observer}s.
     */
    public static interface Session<A,
                                    L extends Session.Listener>
        extends Actor.Session<L> {
        public static interface Listener<A> extends Actor.Session.Listener {
            /**
             * Called when a given action is committed.
             *
             * @param action The action that was committed.
             */
            public void commit(final A action);
        }
    }
}
