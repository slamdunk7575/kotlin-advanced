package com.yanggang.advanced.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = JavaShapeContainer.class)
public @interface JavaShape {
}
