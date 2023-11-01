package com.shop.admin.order;

import com.shop.common.entity.order.OrderDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class OrderDetailRepositoryTests {
    @Autowired private  OrderDetailRepository repo;

    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = repo.findWithCategoryAndTimeBetween(startTime, endTime);

        Assertions.assertThat(listOrderDetails.size()).isGreaterThan(0);

        for(OrderDetail detail : listOrderDetails)
        {
            System.out.printf("%s | %d | %.2f | %.2f | %.2f \n", detail.getProduct().getCategory().getName(), detail.getQuantity(),
                    detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
        }
    }
    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = repo.findWithProductAndTimeBetween(startTime, endTime);

        Assertions.assertThat(listOrderDetails.size()).isGreaterThan(0);

        for(OrderDetail detail : listOrderDetails)
        {
            System.out.printf("%d | %s | %.2f | %.2f | %.2f \n",  detail.getQuantity(), detail.getProduct().getName(),
                    detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
        }
    }
}
