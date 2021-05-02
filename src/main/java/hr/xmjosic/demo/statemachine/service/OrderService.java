package hr.xmjosic.demo.statemachine.service;

import hr.xmjosic.demo.statemachine.model.Order;
import hr.xmjosic.demo.statemachine.model.OrderEvents;
import hr.xmjosic.demo.statemachine.model.OrderStates;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private final StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;

    private static final String ORDER_ID_HEADER = "orderId";
    private static final String PAYMENT_CONFIRMATION_NO = "paymentConfirmationNo";

    public Order create(Date when) {
        Order order = new Order();
        order.setId(22L);
        order.setDate(when);
        order.setOrderState(OrderStates.SUBMITTED);
        return order;
    }

    public StateMachine<OrderStates, OrderEvents> pay(Long orderId, String paymentConfirmationNo) {
        StateMachine<OrderStates, OrderEvents> stateMachine = build(orderId);

        Message<OrderEvents> paymentMessage = MessageBuilder.withPayload(OrderEvents.PAY)
                .setHeader(ORDER_ID_HEADER, orderId)
                .setHeader(PAYMENT_CONFIRMATION_NO, paymentConfirmationNo)
                .build();

        stateMachine.sendEvent(paymentMessage);
        return stateMachine;
    }

    public StateMachine<OrderStates, OrderEvents> fulfill(Long orderId) {
        StateMachine<OrderStates, OrderEvents> stateMachine = build(orderId);

        Message<OrderEvents> fulfillMessage = MessageBuilder.withPayload(OrderEvents.FULFILL)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();

        stateMachine.sendEvent(fulfillMessage);
        return stateMachine;
    }

    private StateMachine<OrderStates, OrderEvents> build(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setOrderState(OrderStates.SUBMITTED);
        String orderIdKey = Long.toString(order.getId());

        StateMachine<OrderStates, OrderEvents> stateMachine = this.stateMachineFactory.getStateMachine(orderIdKey);
        stateMachine.stop();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(stateMachineAccess -> {
                    stateMachineAccess.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
                        @Override
                        public void preStateChange(State<OrderStates, OrderEvents> state, Message<OrderEvents> message, Transition<OrderStates, OrderEvents> transition, StateMachine<OrderStates, OrderEvents> stateMachine1) {
                            Optional.ofNullable(message).ifPresent(msg ->
                                    Optional.ofNullable((Long) msg.getHeaders().getOrDefault(ORDER_ID_HEADER, -1L)).ifPresent(orderId1 -> {
                                        Order order = new Order();
                                        order.setId(orderId);
                                        order.setOrderState(OrderStates.FULFILLED);
                                    }));
                        }
                    });
                    stateMachineAccess.resetStateMachine(new DefaultStateMachineContext<>(
                            order.getOrderState(),
                            null,
                            null,
                            null
                    ));
                });

        stateMachine.start();
        return stateMachine;
    }
}
