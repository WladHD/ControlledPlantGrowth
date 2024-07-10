package de.wladtheninja.controlledplantgrowth.data.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ParameterRunnable<T> implements Runnable {

    private T param;

    abstract void run(T param);
}
