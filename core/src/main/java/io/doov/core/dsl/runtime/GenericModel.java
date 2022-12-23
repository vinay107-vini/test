package io.doov.core.dsl.runtime;

import static io.doov.core.dsl.runtime.FieldChainBuilder.from;

import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import io.doov.core.*;
import io.doov.core.dsl.field.types.*;
import io.doov.core.serial.TypeAdapterRegistry;
import io.doov.core.serial.TypeAdapters;

public final class GenericModel implements FieldModel {

    private final List<RuntimeField<GenericModel, Object>> fields;
    private final Map<FieldId, Object> valueMap;

    private final TypeAdapterRegistry adapterRegistry;

    public GenericModel() {
        this(TypeAdapters.INSTANCE);
    }

    public GenericModel(TypeAdapterRegistry adapterRegistry) {
        this.fields = new ArrayList<>();
        this.valueMap = new HashMap<>();
        this.adapterRegistry = adapterRegistry;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(FieldId id) {
        return (T) valueMap.get(id);
    }

    @Override
    public <T> void set(FieldId id, T value) {
        valueMap.put(id, value);
    }

    @Override
    public Stream<Map.Entry<FieldId, Object>> stream() {
        return valueMap.entrySet().stream();
    }

    @Override
    public Iterator<Map.Entry<FieldId, Object>> iterator() {
        return valueMap.entrySet().iterator();
    }

    @Override
    public Spliterator<Map.Entry<FieldId, Object>> spliterator() {
        return valueMap.entrySet().spliterator();
    }

    @Override
    public Stream<Map.Entry<FieldId, Object>> parallelStream() {
        return valueMap.entrySet().parallelStream();
    }

    @Override
    public List<FieldInfo> getFieldInfos() {
        return new ArrayList<>(fields);
    }

    @Override
    public TypeAdapterRegistry getTypeAdapterRegistry() {
        return adapterRegistry;
    }

    @SuppressWarnings("unchecked")
    private <T> RuntimeField<GenericModel, T> runtimeField(T value, String readable, Class<?>... genericTypes) {
        FieldId fieldId = () -> readable;
        this.set(fieldId, value);
        return from(GenericModel.class, fieldId)
                .readable(readable)
                .field(o -> o.get(fieldId), (o, v) -> o.set(fieldId, v),
                        (Class<T>) (value == null ? Void.TYPE : value.getClass()), genericTypes)
                .register(fields);
    }

    public BooleanFieldInfo booleanField(boolean value, String readable) {
        return new BooleanFieldInfo(runtimeField(value, readable));
    }

    public CharacterFieldInfo charField(char value, String readable) {
        return new CharacterFieldInfo(runtimeField(value, readable));
    }

    public DoubleFieldInfo doubleField(double value, String readable) {
        return new DoubleFieldInfo(runtimeField(value, readable));
    }

    public <E extends Enum<E>> EnumFieldInfo<E> enumField(E value, String readable) {
        return new EnumFieldInfo<>(runtimeField(value, readable));
    }

    public FloatFieldInfo floatField(float value, String readable) {
        return new FloatFieldInfo(runtimeField(value, readable));
    }

    public IntegerFieldInfo intField(int value, String readable) {
        return new IntegerFieldInfo(runtimeField(value, readable));
    }

    public LocalDateFieldInfo localDateField(LocalDate value, String readable) {
        return new LocalDateFieldInfo(runtimeField(value, readable));
    }

    public LocalDateTimeFieldInfo localDateTimeField(LocalDateTime value, String readable) {
        return new LocalDateTimeFieldInfo(runtimeField(value, readable));
    }

    public LocalTimeFieldInfo localTimeField(LocalTime value, String readable) {
        return new LocalTimeFieldInfo(runtimeField(value, readable));
    }

    public LongFieldInfo longField(long value, String readable) {
        return new LongFieldInfo(runtimeField(value, readable));
    }

    public StringFieldInfo stringField(String value, String readable) {
        return new StringFieldInfo(runtimeField(value, readable));
    }

    public <T, C extends Iterable<T>> IterableFieldInfo<T, C> iterableField(C value, String readable) {
        return new IterableFieldInfo<>(runtimeField(value, readable));
    }

}
