/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package io.doov.core.dsl.mapping;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.doov.core.FieldInfo;
import io.doov.core.dsl.DslField;
import io.doov.core.dsl.DslModel;
import io.doov.core.dsl.lang.*;
import io.doov.core.dsl.mapping.converter.*;
import io.doov.core.serial.TypeAdapter;
import io.doov.core.serial.TypeAdapterRegistry;

/**
 * Factory methods of {@code TypeConverter}s
 */
public class TypeConverters {

    // Simple Converters

    /**
     * 1-to-1 converter
     *
     * @param converter   converter function
     * @param description text description
     * @param <I>         input type
     * @param <O>         output type
     * @return type converter
     */
    public static <I, O> TypeConverter<I, O> converter(Function<I, O> converter,
            String description) {
        return new DefaultTypeConverter<>(i -> i.map(converter).orElseGet(() -> converter.apply(null)), description);
    }

    /**
     * Convert the value in type {@code I} to String using {@code TypeAdapter} registry
     *
     * @param typeAdapters type adapter registry
     * @param <I>          input type
     * @return type converter that returns String representation of the value, {@code null} when the value is null,
     * type converter throws {@code IllegalStateException} when no type adapters accepts to convert value.
     */
    public static <I> TypeConverter<I, String> asString(TypeAdapterRegistry typeAdapters) {
        return new DefaultTypeConverter<>((context, i) ->
                i.map(value -> typeAdapters.stream()
                        .filter(t -> t.accept(value))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("cannot convert value " + value + " to string."))
                        .toString(value))
                        .orElse("null"), "as string");
    }

    /**
     * Convert String to {@code I} using {@code TypeAdapter} registry
     *
     * @param fieldInfo field of the
     * @param typeAdapters type adapter registry
     * @param <O> output type
     * @param <T> field type to lookup the type adapter
     * @return type converter that returns the object value from String representation.
     * @throws IllegalStateException if no type adapter is found for the given field
     */
    public static <O, T extends DslField<O> & FieldInfo> TypeConverter<String, O> fromString(T fieldInfo,
            TypeAdapterRegistry typeAdapters) {
        final TypeAdapter adapter = typeAdapters.stream()
                .filter(t -> t.accept(fieldInfo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("cannot find type adapter for field " + fieldInfo.id()));
        return new DefaultTypeConverter<>((context, i) ->
                i.map(value -> (O) adapter.fromString(fieldInfo, value))
                        .orElse(null), "from string");
    }

    /**
     * 1-to-1 converter with null case. Converter returns the nullCase value when the input is null.
     *
     * @param converter   converter function
     * @param nullCase    value to return when the input is null
     * @param description text description
     * @param <I>         input type
     * @param <O>         output type
     * @return type converter
     */
    public static <I, O> TypeConverter<I, O> converter(Function<I, O> converter, O nullCase,
            String description) {
        return new DefaultTypeConverter<>(i -> i.map(converter).orElse(nullCase), description);
    }

    // BiConverters

    /**
     * 2-to-1 converter with optional types
     *
     * @param converter   converter function with optional types as input
     * @param description text description
     * @param <I>         first input type
     * @param <J>         second input type
     * @param <O>         output type
     * @return type converter
     */
    public static <I, J, O> BiTypeConverter<I, J, O> biConverter(BiFunction<Optional<I>, Optional<J>, O> converter,
            String description) {
        return new DefaultBiTypeConverter<>(converter, description);
    }

    /**
     * 2-to-1 converter with null calse. Converter returns nullCase value when both inputs are null.
     *
     * @param converter   converter function
     * @param nullCase    value to return when inputs are null
     * @param description text description
     * @param <I>         first input type
     * @param <J>         second input type
     * @param <O>         output type
     * @return type converter
     */
    public static <I, J, O> BiTypeConverter<I, J, O> biConverter(BiFunction<I, J, O> converter, O nullCase,
            String description) {
        return new DefaultBiTypeConverter<>((i, j) -> (i.isPresent() && j.isPresent()) ?
                converter.apply(i.get(), j.get()) : nullCase, description);
    }

    /**
     * 2-to-1 converter with compensation values for null inputs.
     * Converter function will receive the corresponding nullIn value when the input is null.
     *
     * @param converter   converter function
     * @param nullIn      compensation value for first input
     * @param nullIn2     compensation value for second input
     * @param description text description
     * @param <I>         first input type
     * @param <J>         second input type
     * @param <O>         output type
     * @return type converter
     */
    public static <I, J, O> BiTypeConverter<I, J, O> biConverter(BiFunction<I, J, O> converter, I nullIn, J nullIn2,
            String description) {
        return new DefaultBiTypeConverter<>((i, j) -> converter.apply(i.orElse(nullIn), j.orElse(nullIn2)),
                description);
    }

    // NaryConverters

    /**
     * N-to-1 converter. Converter function takes {@code DslModel} and a list of {@code DslField} and
     * returns the output value
     *
     * @param function    converter function
     * @param description text description
     * @param <O>         output type
     * @return type converter
     */
    public static <O> NaryTypeConverter<O> nConverter(BiFunction<DslModel, List<DslField>, O> function,
            String description) {
        return new DefaultNaryTypeConverter<>(function, description);
    }

    // Utility converters

    /**
     * Counts the non-null fields
     *
     * @param description text description
     * @return type converter
     */
    public static NaryTypeConverter<Integer> counter(String description) {
        return nConverter((model, fieldInfos) ->
                (int) fieldInfos.stream()
                        .map(f -> model.get(f.id()))
                        .filter(Objects::nonNull)
                        .count(), description);
    }

}
