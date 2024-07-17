package com.abhijitscodelib.stateMachine;

import io.quarkus.runtime.Startup;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.Data;

import java.util.*;

/**
 * <p>
 * Quarkus state machine
 * </p>
 * <a href = "https://medium.com/@abhijitpachpande/state-machine-in-quarkus-5c24e641f9cf"> Check out this blog for more information </a>
 * @author Abhijit Pachpande
 * @version 2
 */
@Startup
public abstract class StateMachine<ENTITY extends ManagedState> {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    EventBus bus;

    protected Map<String, List<Transition>> mapActionTransitions;
    protected Map<String,Map<String, List<String>>> roleBasedActions;
    protected Set<String> postProcessors;

    @PostConstruct
    @Startup
    protected void init() {
        mapActionTransitions = new HashMap<>();
        roleBasedActions = new HashMap<>();
        postProcessors = new HashSet<>();
        addRulesOnStartup();
    }

    protected abstract void addRulesOnStartup();

    protected abstract Class<ENTITY> entityClass();

    protected void addPostProcessor(String finalState) {
        postProcessors.add(createPostProcessingString(finalState));
    }

    private String createPostProcessingString(String finalState) {
        return String.join("_",entityClass().getSimpleName().toUpperCase(),finalState);
    }

    protected void addRule(List<String> roles , String initialState, String action, StateHandler<ENTITY> handler , String... finalStates) {
        if (mapActionTransitions.containsKey(action)) {
            List<Transition> transitions = mapActionTransitions.get(action);
            Transition transition = transitions.stream()
                    .filter(t -> Objects.equals(t.initialState, initialState))
                    .findFirst().orElse(null);
            if (transition == null ) {
                transition = new Transition(initialState,Arrays.asList(finalStates),roles, handler);
                transitions.add(transition);
            }
        } else {
            Transition transition = new Transition(initialState,Arrays.asList(finalStates),roles, handler);
            mapActionTransitions.put(action, new ArrayList<>(Arrays.asList(transition)));
        }
        if(roles == null) return ;
        for(String role :  roles) {
            if (!roleBasedActions.containsKey(role)) {
                Map<String, List<String>> map = new HashMap<>();
                map.put(initialState, new ArrayList<>(Collections.singletonList(action)));
                roleBasedActions.put(role , map);
            }
            else if (!roleBasedActions.get(role).containsKey(initialState)){
                roleBasedActions.get(role).put(initialState,new ArrayList<>(Collections.singletonList(action)));
            }
            else {
                roleBasedActions.get(role).get(initialState).add(action);
            }
        }
    }

    public Uni<ENTITY> performAction(ENTITY object, String action) {
        String initialState = object.getState();
        Transition transition = getTransition(object,action);
        if( !isValidForProcessing(transition)) {
            throw new UnsupportedOperationException("Unsupported Operation");
        }
        List<String> finalStatesAvailable = transition.finalStates;
        Uni<ENTITY> processingUni = null;
        StateHandler<ENTITY> stateHandler = transition.handler;
        if( finalStatesAvailable.size() > 1  ) {
            // Check factory and process
            if(stateHandler == null) throw new RuntimeException("Need to specify complex state operations");
            processingUni = stateHandler.handle(object);
        }
        else if(stateHandler != null) {
            //Check factory available
            processingUni = stateHandler.handle(object);
            //if available, delegate processing
        }
        else {
            object.setState(finalStatesAvailable.get(0));
            processingUni = (Uni<ENTITY>) object.save();
        }

        return processingUni.onItem().transform(o -> {
            // perform post processing;
            String postProcessorString = createPostProcessingString(o.getState());
            if(postProcessors.contains(postProcessorString)) bus.publish(postProcessorString,o);
            return o;
        });

    }
    public Transition getTransition(ENTITY object, String action) {
        String initialStatus = object.getState();
        if(!mapActionTransitions.containsKey(action)) return null;
        List<Transition> transitions = mapActionTransitions.get(action);
        return transitions.stream().filter(t -> initialStatus.equals(t.getInitialState())).findFirst().orElse(null)  ;
    }

    public boolean isValidForProcessing(Transition transition) {
        return transition != null;
    }
    public Map<String, List<String>> getActionStateMap() {
        String mainRole =  getMainRole(securityIdentity.getRoles());
        return roleBasedActions.get(mainRole);
    }

    protected abstract String getMainRole(Set<String> roles);

    @Data
    public class Transition {
        private String initialState;
        private List<String> finalStates;
        private List<String> roles;
        private StateHandler<ENTITY> handler;

        public Transition(String initialState, List<String> finalStates, List<String> roles, StateHandler<ENTITY> handler) {
            this.initialState = initialState;
            this.finalStates = finalStates;
            this.roles = roles;
            this.handler = handler;
        }
    }

}
