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
 * {@link Actor}s which are able to submit requests to a consensus
 * pool.
 *
 * @param <A> Type of actions to be performed.
 * @param <R> Type of request handles.
 * @param <S> Type of session handles.
 * @param <RL> Type of request listeners.
 * @param <SL> Type of session listeners.
 */
public interface Submitter<A,
                           RL extends Submitter.Request.Listener<A>,
                           SL extends Submitter.Session.Listener,
                           R extends Submitter.Request<A, RL>,
                           S extends Submitter.Session<A, RL, SL, R>>
    extends Actor<SL, S> {

    public static interface Request<A,
                                    L extends Follower.Request.Listener<A>>
        extends Follower.Request<A, L> {
        /**
         * Attempt to cancel the {@code Request}.  Cancellations are
         * not guaranteed to be acknowledged.  If the cancellation
         * attempt succeeds, then {@link Listener#cancelled()} will be
         * called; however, it is possible that the request will be
         * committed in spite of a cancellation request.
         */
        public void requestCancel();
    }

    public static interface Session<A,
                                    RL extends Request.Listener<A>,
                                    SL extends Session.Listener,
                                    R extends Request<A, RL>>
        extends Actor.Session<SL> {
        public static interface Listener extends Request.Listener {}

        /**
         * Submit an action, along with a {@link Request.Listener} to
         * handle the result.
         *
         * @param action The action to submit.
         * @param listener The listener for events relating to the action.
         * @return The {@link Request}.
         * @see Request
         */
        public R submit(final A action,
                        final RL listener);
    }

}
