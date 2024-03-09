package com.abhijitscodelib.stateMachine;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

@Startup
@ApplicationScoped
public abstract class StateMachine<T,C,O> {

    protected Map<T, Map<C,C>> mapActionsToStates;

    @PostConstruct
    public void init() {
        mapActionsToStates = new HashMap<>();
        addRulesOnStartup();
    }

    protected abstract void addRulesOnStartup();

    protected StateMachine<T,C,O> addRule(C initialState, C finalState, T action) {
        if ( mapActionsToStates.containsKey(action) ) {
            mapActionsToStates.get(action).put(initialState,finalState);
        }
        else {
            Map<C,C> stateMapping = new HashMap<>();
            stateMapping.put(initialState,finalState);
            mapActionsToStates.put(action, stateMapping);
        }
        return this;
    }

    public Uni<O> performAction(O object, T action) {
        if( !isValidInitialState(object,action) ) {
            throw new UnsupportedOperationException("Unsupported Operation");
        }
        return process(object,action);
    }
    public boolean isValidInitialState(O object, T action) {
        C initialStatus = getCurrentOrderStatus(object);
        if(!mapActionsToStates.containsKey(action)) return false;
        Map<C,C> stateMap = mapActionsToStates.get(action);
        return stateMap.containsKey(initialStatus);
    }

    public C finalState(O object, T action) {
        if(!isValidInitialState(object,action))
            throw new IllegalStateException("Cannot apply operation on this state");
        C initialStatus = getCurrentOrderStatus(object);
        Map<C,C> stateMap = mapActionsToStates.get(action);
        return stateMap.get(initialStatus);
    }

    public abstract C getCurrentOrderStatus(O o);

    public abstract Uni<O> process(O o, T action);

    public abstract Map<T,Map<C,C>> getStateActionMap();
}
