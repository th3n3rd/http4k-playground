package com.example.architecture;

import java.lang.annotation.*;

public class Architecture {
    @Inherited
    @Target(ElementType.PACKAGE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Core {}

    @Inherited
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UseCase {}

    @Inherited
    @Target(ElementType.PACKAGE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Infra {}

    @Inherited
    @Target({ElementType.PACKAGE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Shared {}

    @Inherited
    @Target({ElementType.PACKAGE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Excluded {}
}
