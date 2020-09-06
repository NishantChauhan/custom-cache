package com.nishant.customcache.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Documents that class is thread-safe
 */
@Documented
@Target({ TYPE})
@Retention(value = RetentionPolicy.SOURCE)
public @interface ThreadSafe {
}
