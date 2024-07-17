package com.abhijitscodelib.stateMachine;

import io.smallrye.mutiny.Uni;

/**
 * <p>
 * Add Rule with the following implementation
 * while initializing the state machine
 * </p>
 * @author Abhijit Pachpande
 * @version 2
 */
public interface StateHandler<T> {
    Uni<T> handle(T entity);
}
