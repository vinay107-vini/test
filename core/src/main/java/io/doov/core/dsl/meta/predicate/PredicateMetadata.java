/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package io.doov.core.dsl.meta.predicate;

import java.util.concurrent.atomic.AtomicInteger;

import io.doov.core.dsl.meta.LeafMetadata;
import io.doov.core.dsl.meta.Metadata;

public interface PredicateMetadata extends Metadata {

    AtomicInteger evalTrue();

    AtomicInteger evalFalse();

    /**
     * Merges the node with the given node.
     *
     * @param other the other metadata to merge
     * @return the merged metadata
     */
    default PredicateMetadata merge(LeafMetadata<?> other) {
        throw new UnsupportedOperationException();
    }

    default int incTrueEval() {
        return evalTrue().incrementAndGet();
    }

    default int incFalseEval() {
        return evalFalse().incrementAndGet();
    }

    default int trueEvalCount() {
        return evalTrue().get();
    }

    default int falseEvalCount() {
        return evalFalse().get();
    }
    
    default void resetCounters() {
        evalTrue().set(0);
        evalFalse().set(0);
    }
}
