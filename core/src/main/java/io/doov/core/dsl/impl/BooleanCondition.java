/*
 * Copyright 2017 Courtanet
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.doov.core.dsl.impl;

import static io.doov.core.dsl.impl.LeafStepCondition.predicate;
import static io.doov.core.dsl.meta.function.BooleanFunctionMetadata.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Optional;
import java.util.function.BiFunction;

import io.doov.core.dsl.DslField;
import io.doov.core.dsl.DslModel;
import io.doov.core.dsl.field.types.LogicalFieldInfo;
import io.doov.core.dsl.lang.Context;
import io.doov.core.dsl.lang.StepCondition;
import io.doov.core.dsl.meta.predicate.LeafPredicateMetadata;
import io.doov.core.dsl.meta.predicate.PredicateMetadata;

/**
 * Base class for boolean conditions.
 * <p>
 * It contains a {@link DslField} to get the value from the model, a {@link LeafPredicateMetadata} to describe this node, and a
 * {@link BiFunction} to take the value from the model and return an optional value.
 */
public class BooleanCondition extends DefaultCondition<Boolean> {

    public BooleanCondition(DslField<Boolean> field) {
        super(field);
    }

    public BooleanCondition(PredicateMetadata metadata, BiFunction<DslModel, Context, Optional<Boolean>> value) {
        super(metadata, value);
    }

    /**
     * Returns a step condition checking if the node value is not true.
     *
     * @return the step condition
     */
    public final StepCondition not() {
        return predicate(this, notMetadata(metadata), value -> !value);
    }

    /**
     * Returns a step condition checking if the node value and the given value is true.
     *
     * @param value the right value
     * @return the step condition
     */
    public final StepCondition and(boolean value) {
        return predicate(this, andMetadata(metadata, value), value, Boolean::logicalAnd);
    }

    /**
     * Returns a step condition checking if the node value and the given field value is true.
     *
     * @param value the right field value
     * @return the step condition
     */
    public final StepCondition and(LogicalFieldInfo value) {
        return predicate(this, andMetadata(metadata, value), value, Boolean::logicalAnd);
    }

    /**
     * Returns a step condition checking if the node value or the given value is true.
     *
     * @param value the right value
     * @return the step condition
     */
    public final StepCondition or(boolean value) {
        return predicate(this, orMetadata(metadata, value), value, Boolean::logicalOr);
    }

    /**
     * Returns a step condition checking if the node value or the given field value is true.
     *
     * @param value the right value
     * @return the step condition
     */
    public final StepCondition or(LogicalFieldInfo value) {
        return predicate(this, orMetadata(metadata, value), value, Boolean::logicalOr);
    }

    /**
     * Returns a step condition checking if the node value or exclusive the given value is true.
     *
     * @param value the right value
     * @return the step condition
     */
    public final StepCondition xor(boolean value) {
        return predicate(this, xorMetadata(metadata, value), value, Boolean::logicalXor);
    }

    /**
     * Returns a step condition checking if the node value or exclusive the given field value is true.
     *
     * @param value the right field value
     * @return the step condition
     */
    public final StepCondition xor(LogicalFieldInfo value) {
        return predicate(this, xorMetadata(metadata, value), value, Boolean::logicalXor);
    }

    /**
     * Returns a step condition checking if the node value is true.
     *
     * @return the step condition
     */
    public final StepCondition isTrue() {
        return predicate(this, isMetadata(metadata, true), TRUE, Boolean::equals);
    }

    /**
     * Returns a step condition checking if the node value is false.
     *
     * @return the step condition
     */
    public final StepCondition isFalse() {
        return predicate(this, isMetadata(metadata, false), FALSE, Boolean::equals);
    }

}
