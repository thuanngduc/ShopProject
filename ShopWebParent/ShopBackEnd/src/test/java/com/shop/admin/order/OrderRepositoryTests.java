package com.shop.admin.order;

import com.shop.common.entity.*;
import com.shop.common.entity.order.*;
import com.shop.common.entity.product.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {
    @Autowired private OrderRepository repo;
    @Autowired private TestEntityManager entityManager;

    @Test
    public void testCreateNewOrderWithSingleProduct()
    {
        Customer customer = entityManager.find(Customer.class, 1);
        Product product = entityManager.find(Product.class, 1);
        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice()+10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(1);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);
        Order savedOrder = repo.save(mainOrder);

        Assertions.assertThat(savedOrder.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateNewOrderWithMultipleProducts()
    {
        Customer customer = entityManager.find(Customer.class, 2);
        Product product1 = entityManager.find(Product.class, 3);
        Product product2 = entityManager.find(Product.class, 5);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(20);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubtotal(product2.getPrice() * 2);
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        float subtotal =product1.getPrice() + product2.getPrice() *2;
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + 30);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.PROCESSING);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(3);

        Order savedOrder = repo.save(mainOrder);

        Assertions.assertThat(savedOrder.getId()).isGreaterThan(0);

    }

    @Test
    public void testListOrders()
    {
        Iterable<Order> orders = repo.findAll();

        Assertions.assertThat(orders).hasSizeGreaterThan(0);
        orders.forEach(System.out::println);
    }
    @Test
    public void testUpdateOrder()
    {
        Integer orderId = 2;
        Order order = repo.findById(orderId).get();

        order.setStatus(OrderStatus.SHIPPING);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        order.setDeliverDays(2);

        Order updatedOrder = repo.save(order);
        Assertions.assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }
    @Test
    public void testGetOrder()
    {
        Integer orderId = 2;
        Order order = repo.findById(orderId).get();

        Assertions.assertThat(order).isNotNull();
        System.out.println(order);
    }
    @Test
    public void testDeleteOrder()
    {
        Integer orderId = 3;
        repo.deleteById(orderId);

        Optional<Order> result = repo.findById(orderId);
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    public void testUpdateOrderTracks()
    {
        Integer orderId = 8;
        Order order = repo.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.PICKED);
        newTrack.setNotes(OrderStatus.PICKED.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PACKAGED);
        processingTrack.setNotes(OrderStatus.PACKAGED.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updateOrder = repo.save(order);

        Assertions.assertThat(updateOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }
    @Test
    public void testFindByOrderTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);

        Assertions.assertThat(listOrders.size()).isGreaterThan(0);
        for(Order order: listOrders)
        {
            System.out.printf("%s | %s | %.2f | %.2f | %.2f \n", order.getId(), order.getOrderTime(), order.getProductCost(), order.getSubtotal(), order.getTotal());
        }
    }
}
