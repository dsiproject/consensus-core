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

import java.lang.AutoCloseable;

/**
 * Followers, who are alerted to the creation of requests, and can
 * learn of decisions about those requests by the consensus protocol.
 *
 * @param <A> Type of actions.
 * @param <SL> Type of session listeners.
 * @param <RL> Type of request listeners.
 * @param <S> Type of session handles.
 * @param <R> Type of requests.
 */
public interface Follower<A,
                          RL extends Follower.Request.Listener<A>,
                          SL extends Follower.Session.Listener<A, RL, R>,
                          R extends Follower.Request<A, RL>,
                          S extends Follower.Session<A, RL, SL, R>>
    extends Actor<SL, S> {

    /**
     * Request handle.  This is used to recieve updates about a given
     * request that has been subminted.
     *
     * @param <A> Type of actions.
     * @param <L> Type of listener for receiving updates.
     */
    public static interface Request<A,
                                    L extends Request.Listener<A>>
        extends Actor.Session<L> {
        /**
         * Listener interface for {@link Request}s.
         *
         * @param <A> Type of actions corresponding to this listener.
         */
        public static interface Listener<A> extends Actor.Session.Listener {
            /**
             * Called when the request has been successfully
             * committed.  This means that the action has been
             * performed successfully.
             */
            public void committed();

            /**
             * Called when an attempt to commit the request has
             * failed, but the request will be retried.  This
             * typically indicates contention, outage, or something
             * similar.
             */
            public void delayed();

            /**
             * Called when the request can no longer be committed
             * as-is.  This typically means that some other action
             * conflicts with this one, and the conflicting action was
             * committed.  It can also mean that the request conflicts
             * with the present state.
             */
            public void aborted();

            /**
             * Called when a cancellation request was acknowledged.
             * The request will not be committed after this has been
             * called; however, cancellation requests are not
             * guaranteed to always succeed.
             */
            public void cancelled();
        }

        /**
         * Get the action associated with this request.
         *
         * @return The action associated with this request.
         */
        public A action();

        /**
         * Close the request handle, which will stop further updates
         * from being sent.
         */
        @Override
        public void close();
    }

    /**
     * Session handle for {@link Follower}s.
     *
     * @param <A> Type of actions.
     * @param <SL> Type of session listeners.
     * @param <RL> Type of request listeners.
     * @param <R> Type of requests.
     */
    public static interface Session<A,
                                    RL extends Request.Listener<A>,
                                    SL extends Session.Listener<A, RL, R>,
                                    R extends Request<A, RL>>
        extends Actor.Session<SL> {
        /**
         * Listener interface for Followers.  This provides the
         * ability to respond to events representing discovery of
         * {@link Request}s by providing a {@code Request.Listener}
         * for further updates.
         *
         * @param <A> Type of actions.
         * @param <RL> Listener interface for {@link Request}s.
         * @param <R> Type of requests.
         */
        public static interface Listener<A,
                                         RL extends Request.Listener<A>,
                                         R extends Request<A, RL>>
            extends Actor.Session.Listener {
            /**
             * Called upon learning of a request to obtain a {@link
             * Request.Listener} for events pertaining to the request.
             *
             * @param req The request.
             * @return A {@link Request.Listener} to handle any
             *         updates, or {@code null} if they are to be ignored.
             */
            public RL request(final R req);
        }
    }
}