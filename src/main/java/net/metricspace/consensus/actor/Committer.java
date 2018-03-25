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
import java.security.PublicKey;

/**
 * Active participants in a consensus protocol.  {@code Committer}s
 * necessarily have the same receive-side interface as {@link
 * Auditor}s; additionally, they are able to send protocol messages
 * through the same interface.
 *
 * @param <A> Type of actions.
 * @param <SL> Type of session listeners.
 * @param <RL> Type of request listeners.
 * @param <S> Type of session handles.
 * @param <R> Type of requests.
 */
public interface Committer<A,
                           RL extends Committer.Request.Listener<A>,
                           SL extends Committer.Session.Listener<A, RL, R>,
                           R extends Committer.Request<A, RL>,
                           S extends Committer.Session<A, RL, SL, R>>
    extends Auditor<A, RL, SL, R, S> {

    /**
     * Request handle.  This is used to recieve updates about a given
     * request that has been subminted.
     *
     * @param <A> Type of actions.
     * @param <L> Type of listener for receiving updates.
     */
    public static interface Request<A,
                                    L extends Request.Listener<A>>
        extends Auditor.Request<A, L>,
                Auditor.Request.Listener<A> {}

    /**
     * Session handle for {@link Committer}s.
     */
    public static interface Session<A,
                                    RL extends Request.Listener<A>,
                                    SL extends Session.Listener<A, RL, R>,
                                    R extends Request<A, RL>>
        extends Auditor.Session<A, RL, SL, R> {
        /**
         * Listener interface for {@link Committer}.  This provides
         * the ability to respond to events representing discovery of
         * {@link Request}s by providing a {@link Request.Listener}
         * for further updates.
         *
         * @param <A> Type of actions.
         * @param <L> Listener interface for {@link Request}s.
         * @param <R> Type of requests.
         */
        public static interface Listener<A,
                                         L extends Request.Listener<A>,
                                         R extends Request<A, L>>
            extends Auditor.Session.Listener<A, L, R> {
            /**
             * Called upon learning of a request to obtain a {@link
             * Request.Listener} for events pertaining to the request.
             *
             * @param req The request.
             * @return A {@link Request.Listener} to handle any
             *         updates, or {@code null} if they are to be ignored.
             */
            public L request(final R req);
        }

        /**
         * Request eviction of a given {@link Committer} from the
         * consensus pool.
         *
         * @param id The ID of the {@link Committer} to evict.
         * @param receive The {@link Request.Listener} which will
         *                recieve events corresponding to the request.
         * @return The request handle for the eviction request.
         */
        public R requestEvict(final String id,
                              final RL receive);

        /**
         * Close the session, which will alert the consensus pool that
         * we are no longer an active {@link Committer}.
         */
        @Override
        public void close();
    }

    /**
     * Obtain the {@link PublicKey} used to verify signatures from
     * this {@code Committer}.
     *
     * @return The {@link PublicKey} used to verify signatures from
     * this {@code Committer}.
     */
    public PublicKey verifyKey();
}
