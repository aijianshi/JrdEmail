package com.tct.fw.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tct.fw.javax.annotation.meta.TypeQualifierNickname;
import com.tct.fw.javax.annotation.meta.When;

/** Used to annotate a value of unknown sign */

@Documented
@TypeQualifierNickname
@Nonnegative(when = When.UNKNOWN)
@Retention(RetentionPolicy.RUNTIME)
public @interface Signed {

}
