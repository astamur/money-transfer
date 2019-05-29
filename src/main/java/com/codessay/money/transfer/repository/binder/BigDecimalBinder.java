package com.codessay.money.transfer.repository.binder;

import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

public class BigDecimalBinder extends ComparableBinding {
    public static final BigDecimalBinder BINDER = new BigDecimalBinder();

    public static BigDecimal entryToInstant(final ByteIterable entry) {
        return (BigDecimal) BINDER.entryToObject(entry);
    }

    public static ArrayByteIterable instantToEntry(final BigDecimal object) {
        return BINDER.objectToEntry(object);
    }

    @Override
    public Comparable readObject(@NotNull ByteArrayInputStream stream) {
        return new BigDecimal(BindingUtils.readString(stream));
    }

    @Override
    public void writeObject(LightOutputStream output, @NotNull Comparable object) {
        final BigDecimal number = (BigDecimal) object;
        output.writeString(number.toString());
    }
}