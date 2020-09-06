package com.nishant.customcache.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Documents that field is guarded by a lock mentioned in value
 */

@Documented
@Retention(value = RetentionPolicy.SOURCE)
@Target({ FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, TYPE_PARAMETER })
public @interface GuardedBy {
    String value();
}
