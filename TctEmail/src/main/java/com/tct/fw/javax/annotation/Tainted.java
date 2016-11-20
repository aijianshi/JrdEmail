package com.tct.fw.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tct.fw.javax.annotation.meta.TypeQualifierNickname;
import com.tct.fw.javax.annotation.meta.When;

@Documented
@TypeQualifierNickname
@Untainted(when = When.MAYBE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tainted {

}
