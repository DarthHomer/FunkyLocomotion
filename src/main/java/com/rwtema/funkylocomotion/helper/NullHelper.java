package com.rwtema.funkylocomotion.helper;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NullHelper {
	static Object dummy;

	// Helper method that tricks IDE into thinking that an apparently null object is not null
	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T> T notNull(@Nullable T object) {
		if (dummy != null) {
			return (T) dummy;
		}
		return Validate.notNull(object);
	}

	// Helper method that tricks IDE into thinking that a 'final' field that is set later is nonnull
	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T> T notNull() {
		return (T) dummy;
	}

	@Nullable
	public static <T> T nullable(T object) {
		if (dummy != null) return null;
		return object;
	}
}
