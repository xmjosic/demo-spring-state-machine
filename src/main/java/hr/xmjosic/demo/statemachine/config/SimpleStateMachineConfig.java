package hr.xmjosic.demo.statemachine.config;

import hr.xmjosic.demo.statemachine.model.OrderEvents;
import hr.xmjosic.demo.statemachine.model.OrderStates;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Log
@Configuration
@EnableStateMachineFactory
class SimpleStateMachineConfig extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states
                .withStates()
                .initial(OrderStates.SUBMITTED)
//                    .stateEntry(OrderStates.SUBMITTED, new Action<OrderStates, OrderEvents>() {
//                        @Override
//                        public void execute(StateContext<OrderStates, OrderEvents> stateContext) {
//                            Long orderId = (Long) stateContext.getExtendedState().getVariables().getOrDefault("orderId ", -1L);
//                            log.info("Order ID is: " + orderId + ".");
//                            log.info("Entering submitted state!");
//                        }
//                    })
                .state(OrderStates.PAID)
                .end(OrderStates.FULFILLED)
                .end(OrderStates.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStates.SUBMITTED)
                .target(OrderStates.PAID)
                .event(OrderEvents.PAY)
                .and()
                .withExternal()
                .source(OrderStates.PAID)
                .target(OrderStates.FULFILLED)
                .event(OrderEvents.FULFILL)
                .and()
                .withExternal()
                .source(OrderStates.SUBMITTED)
                .target(OrderStates.CANCELLED)
                .event(OrderEvents.CANCEL)
                .and()
                .withExternal()
                .source(OrderStates.PAID)
                .target(OrderStates.CANCELLED)
                .event(OrderEvents.CANCEL);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {
        StateMachineListenerAdapter<OrderStates, OrderEvents> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from + "", to + ""));
            }
        };
        config
                .withConfiguration()
                .autoStartup(false)
                .listener(adapter);
    }
}