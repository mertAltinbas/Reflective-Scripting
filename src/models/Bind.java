package models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  // Make it available at runtime via reflection
@Target(ElementType.FIELD)  // Can be used only on fields
public @interface Bind {
}
