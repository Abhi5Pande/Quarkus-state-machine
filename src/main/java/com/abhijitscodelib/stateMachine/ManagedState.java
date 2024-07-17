package com.abhijitscodelib.stateMachine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smallrye.mutiny.Uni;

/**
 * <p>
 * Extend the entities that you want to
 * manage states using stateMachine
 * </p>
 * @see StateMachine
 * @author Abhijit Pachpande
 * @version 2
 */
public interface ManagedState {
    @JsonIgnore
    String getState();
    @JsonIgnore
    void setState(String state);

    Uni<?> save();

}
