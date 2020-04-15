package com.scholanova.ecommerce.order.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scholanova.ecommerce.cart.entity.Cart;
import com.scholanova.ecommerce.cart.entity.CartItem;
import com.scholanova.ecommerce.order.exception.NotAllowedException;
import com.scholanova.ecommerce.order.exception.IllegalArgException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

@Entity(name="orders")
public class Orders {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column
    private String number;

    @Column
    private Date issueDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="cart_id", referencedColumnName = "id")
    private Cart cart;

    public Orders() {
    }

    public static Orders createOrder(Cart cart) throws NotAllowedException {
        Orders order = new Orders();
        order.setCart(cart);
        order.setStatus(OrderStatus.CREATED);

        return order;
    }

    public void checkout() throws NotAllowedException, IllegalArgException {
        if (this.getStatus() == OrderStatus.CLOSED){
            throw new NotAllowedException("Order is CLOSED");
        }

        int totalQuantity = 0;
        if (this.getCart() != null) {
            for (CartItem item: this.getCart().getCartItems()) {
                totalQuantity = totalQuantity + item.getQuantity();
            }
        }

        if (this.cart != null && totalQuantity == 0) {
            throw new IllegalArgException("Order contain 0 quantity of item");
        }
        this.issueDate = new Date(System.currentTimeMillis());
        this.status = OrderStatus.PENDING;
    }

    public BigDecimal getDiscount(){
        //5/total
        BigDecimal totalPrice = this.getCart().getTotalPrice();
        if (totalPrice.compareTo(BigDecimal.valueOf(100)) < 0) {
            return BigDecimal.valueOf(0);
        }

        return BigDecimal.valueOf(5).multiply(totalPrice).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getOrderPrice(){
        return this.getCart().getTotalPrice().subtract(this.getDiscount());
    }

    public void close(){
        this.setStatus(OrderStatus.CLOSED);
    }


    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {return number;}

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getIssueDate() {return issueDate;}

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public OrderStatus getStatus() {return status;}

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Cart getCart() {return cart;}

    public void setCart(Cart cart) throws NotAllowedException {
        if (this.getStatus() == OrderStatus.CLOSED) {
            throw new NotAllowedException("closed cart");
        }
        this.cart = cart;
    }
}
