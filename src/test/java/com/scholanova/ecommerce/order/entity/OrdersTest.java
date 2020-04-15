package com.scholanova.ecommerce.order.entity;

import com.scholanova.ecommerce.cart.entity.Cart;
import com.scholanova.ecommerce.cart.entity.CartItem;
import com.scholanova.ecommerce.order.exception.NotAllowedException;
import com.scholanova.ecommerce.order.exception.IllegalArgException;
import com.scholanova.ecommerce.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrdersTest {

    @Test
    public void checkout_ShouldSetTheDateAndTimeOfTodayInTheOrder() throws NotAllowedException, IllegalArgException {
        //given
        Date todayTime = new Date();
        Orders order = new Orders();
        //when
        order.checkout();
        //then
        assertThat(order.getIssueDate()).isEqualTo(todayTime);
    }

    @Test
    public void checkout_ShouldSetOrderStatusToPending() throws NotAllowedException, IllegalArgException {
        //given
        Orders order = new Orders();
        OrderStatus pendingStatus = OrderStatus.PENDING;
        order.setStatus(OrderStatus.CREATED);
        //when
        order.checkout();
        //then
        assertThat(order.getStatus()).isEqualTo(pendingStatus);
    }

    @Test
    public void checkout_ShouldThrowNotAllowedExceptionIfStatusIsClosed(){
        //given
        Orders order = new Orders();
        order.setStatus(OrderStatus.CLOSED);
        //then
        assertThrows(NotAllowedException.class, order::checkout);
    }

    @Test
    public void checkout_ShouldThrowIllegalArgExceptionIfCartTotalItemsQuantityIsZERO() throws NotAllowedException {
        //given
        Orders order = new Orders();
        Cart cart = new Cart();
        Product product1 = Product.create("test1", "test1", 12.0f, 0.2f, "EUR");
        Product product2 = Product.create("test2", "test2", 10.0f, 0.2f, "EUR");

        List<CartItem> items = new ArrayList<>();
        items.add(CartItem.create(product1, 0));
        items.add(CartItem.create(product2, 0));

        cart.setCartItems(items);
        order.setCart(cart);
        //then
        assertThrows(IllegalArgException.class, order::checkout);
    }

    @Test
    public void setCart_ShouldThrowNotAllowedExceptionIfStatusIsClosed(){
        //given
        Orders order = new Orders();
        order.setStatus(OrderStatus.CLOSED);
        Cart cart = new Cart();
        //then
        assertThrows(NotAllowedException.class, () -> {
            order.setCart(cart);
        });
    }

    @Test
    public void createOrder_ShouldSetTheCartInTheOrder() throws NotAllowedException {
        //given
        Cart cart = new Cart();
        //when
        Orders order = Orders.createOrder(cart);
        //then
        assertThat(order.getCart()).isEqualTo(cart);
    }

    @Test
    public void createOrder_ShouldSetStatusToCreated() throws NotAllowedException {
        //given
        Cart cart = new Cart();
        //when
        Orders order = Orders.createOrder(cart);
        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    public void getDiscount_shouldReturnZEROIFCartTotalPriceIsLessThan100() throws NotAllowedException {
        //given
        Orders order = new Orders();
        Cart cart = new Cart();
        Product product1 = Product.create("test1", "test1", 12.0f, 0.2f, "EUR");
        Product product2 = Product.create("test2", "test2", 10.0f, 1.6f, "EUR");

        List<CartItem> items = new ArrayList<>();
        items.add(CartItem.create(product1, 1));
        items.add(CartItem.create(product2, 2));

        cart.setCartItems(items);
        order.setCart(cart);
        //then
        assertThat(order.getDiscount()).isEqualTo(BigDecimal.valueOf(0));
    }

    @Test
    public void getDiscount_shouldReturn5percentIfCartTotalPriceIsMoreOrEqual100() throws NotAllowedException {
        //given
        Orders order = new Orders();
        Cart cart = new Cart();
        Product product1 = Product.create("test1", "test1", 120.0f, 0.0f, "EUR");

        List<CartItem> items = new ArrayList<>();
        items.add(CartItem.create(product1, 1));

        cart.setCartItems(items);
        order.setCart(cart);
        BigDecimal discount = BigDecimal.valueOf(5).multiply(cart.getTotalPrice()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        //then
        assertThat(order.getDiscount()).isEqualTo(discount);
    }

    @Test
    public void getOrderPrice_shouldReturnTotalPriceWithDiscount() throws NotAllowedException {
        //given
        Orders order = new Orders();
        Cart cart = new Cart();
        Product product1 = Product.create("test1", "test1", 12.0f, 0.0f, "EUR");

        List<CartItem> items = new ArrayList<>();
        items.add(CartItem.create(product1, 10));

        cart.setCartItems(items);
        order.setCart(cart);
        BigDecimal totalPriceWithDiscount = order.getCart().getTotalPrice().subtract(order.getDiscount());
        //then
        assertThat(order.getOrderPrice()).isEqualTo(totalPriceWithDiscount);
    }

    @Test
    public void close_ShouldSetStatusToClose(){
        //given
        Orders order = new Orders();
        //when
        order.close();
        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CLOSED);
    }

}