/*
 * Copyright 2017 Courtanet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.doov.core.dsl.impl;

import static io.doov.core.dsl.impl.AbstractCondition.valueModel;
import static io.doov.core.dsl.meta.predicate.LeafPredicateMetadata.notNullMetadata;
import static io.doov.core.dsl.meta.predicate.LeafPredicateMetadata.nullMetadata;

import java.util.Optional;
import java.util.function.*;

import io.doov.core.dsl.DslModel;
import io.doov.core.dsl.field.BaseFieldInfo;
import io.doov.core.dsl.lang.Context;
import io.doov.core.dsl.meta.predicate.LeafPredicateMetadata;
import io.doov.core.dsl.meta.predicate.PredicateMetadata;

public class LeafStepCondition<N> extends DefaultStepCondition {

    private LeafStepCondition(PredicateMetadata metadata, BiFunction<DslModel, Context, Optional<N>> value,
            Function<N, Boolean> predicate) {
        super(metadata, (model, context) -> value.apply(model, context).map(predicate).orElse(false));
    }

    private LeafStepCondition(PredicateMetadata metadata, BiFunction<DslModel, Context, Optional<N>> left,
            BiFunction<DslModel, Context, Optional<N>> right, BiFunction<N, N, Boolean> predicate) {
        super(metadata, (model, context) -> left.apply(model, context)
                .flatMap(l -> right.apply(model, context).map(r -> predicate.apply(l, r)))
                .orElse(false));
    }

    /**
     * Returns a step condition checking if the node value is null.
     * 
     * @param <N> the type of the node value
     * @param condition the node value to check
     * @return the step condition
     */
    public static <N> LeafStepCondition<Optional<N>> isNull(DefaultCondition<N> condition) {
        return new LeafStepCondition<>(nullMetadata(condition.getMetadata()),
                (model, context) -> Optional.of(condition.value(model, context)),
                t -> !t.isPresent());
    }

    /**
     * Returns a step condition checking if the node value is not null.
     * 
     * @param <N> the type of the node value
     * @param condition the node value to check
     * @return the step condition
     */
    public static <N> LeafStepCondition<Optional<N>> isNotNull(DefaultCondition<N> condition) {
        return new LeafStepCondition<>(notNullMetadata(condition.getMetadata()),
                (model, context) -> Optional.of(condition.value(model, context)),
                Optional::isPresent);
    }

    public static <N> LeafStepCondition<N> predicate(AbstractCondition<N> condition,
            LeafPredicateMetadata<?> mergeMetadata, Function<N, Boolean> predicate) {
        return new LeafStepCondition<>(condition.getMetadata().merge(mergeMetadata),
                condition.getFunction(), predicate);
    }

    public static <N> LeafStepCondition<N> predicate(AbstractCondition<N> condition,
            LeafPredicateMetadata<?> mergeMetadata, BaseFieldInfo<N> value, BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(condition.getMetadata().merge(mergeMetadata),
                condition.getFunction(),
                (model, context) -> valueModel(model, value),
                predicate);
    }

    public static <N> LeafStepCondition<N> predicate(AbstractCondition<N> condition,
            LeafPredicateMetadata<?> mergeMetadata, N value, BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(condition.getMetadata().merge(mergeMetadata),
                condition.getFunction(),
                (model, context) -> Optional.ofNullable(value),
                predicate);
    }

    public static <N> LeafStepCondition<N> predicate(AbstractCondition<N> condition,
            LeafPredicateMetadata<?> mergeMetadata, Supplier<N> value, BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(condition.getMetadata().merge(mergeMetadata),
                condition.getFunction(),
                (model, context) -> Optional.ofNullable(value.get()),
                predicate);
    }

    public static <N> LeafStepCondition<N> predicate(AbstractCondition<N> condition,
            LeafPredicateMetadata<?> mergeMetadata, AbstractCondition<N> value, BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(condition.getMetadata().merge(mergeMetadata),
                condition.getFunction(),
                value.getFunction(),
                predicate);
    }

}
