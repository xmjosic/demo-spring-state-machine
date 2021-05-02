package hr.xmjosic.demo.statemachine;

import hr.xmjosic.demo.statemachine.model.Order;
import hr.xmjosic.demo.statemachine.model.OrderEvents;
import hr.xmjosic.demo.statemachine.model.OrderStates;
import hr.xmjosic.demo.statemachine.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Log
@Component
@AllArgsConstructor
public class Runner implements ApplicationRunner {
    private final OrderService orderService;

    private final StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//            Long orderId = 123L;
//            StateMachine<OrderStates, OrderEvents> stateMachine = this.stateMachineFactory.getStateMachine(Long.toString(orderId));
//            stateMachine.getExtendedState().getVariables().putIfAbsent("orderId", orderId);
//            stateMachine.start();
//            log.info("Current state: " + stateMachine.getState().getId().name());
//            stateMachine.sendEvent(OrderEvents.PAY);
//            log.info("Current state: " + stateMachine.getState().getId().name());
//            Message<OrderEvents> eventsMessage = MessageBuilder
//                    .withPayload(OrderEvents.FULFILL)
//                    .setHeader("Header", "HeaderValue")
//                    .build();
//            stateMachine.sendEvent(eventsMessage);
//            log.info("Current state: " + stateMachine.getState().getId().name());

        Order order = this.orderService.create(new Date());

        StateMachine<OrderStates, OrderEvents> paymentStateMachine = this.orderService.pay(order.getId(), UUID.randomUUID().toString());
        log.info("After calling pay method: " + paymentStateMachine.getState().getId().name());

        StateMachine<OrderStates, OrderEvents> fulfillStateMachine = this.orderService.fulfill(order.getId());
        log.info("After calling fulfill method: " + fulfillStateMachine.getState().getId().name());
    }
}
