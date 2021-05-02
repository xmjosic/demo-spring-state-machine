package hr.xmjosic.demo.statemachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;

    private Date date;

    private String state;

    public Order(Date d, OrderStates os) {
        this.date = d;
        this.setOrderState(os);
    }

    public OrderStates getOrderState() {
        return OrderStates.valueOf(this.state);
    }

    public void setOrderState(OrderStates orderState) {
        this.state = orderState.name();
    }
}
