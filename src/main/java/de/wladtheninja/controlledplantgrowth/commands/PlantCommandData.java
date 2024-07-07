package de.wladtheninja.controlledplantgrowth.commands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PlantCommandData {
    String name();

    String usage() default "";

    String description() default "";

    String permission() default "";

    boolean onlyPlayerCMD() default false;
}