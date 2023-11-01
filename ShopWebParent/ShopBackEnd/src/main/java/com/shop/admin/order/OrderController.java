package com.shop.admin.order;

import com.shop.admin.paging.PagingAndSortingHelper;
import com.shop.admin.paging.PagingAndSortingParam;
import com.shop.admin.security.ShopUserDetails;
import com.shop.admin.setting.SettingService;
import com.shop.common.entity.Country;
import com.shop.common.entity.order.Order;
import com.shop.common.entity.order.OrderDetail;
import com.shop.common.entity.order.OrderStatus;
import com.shop.common.entity.order.OrderTrack;
import com.shop.common.entity.product.Product;
import com.shop.common.entity.setting.Setting;
import com.shop.common.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {
    private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired private OrderService orderService;
    @Autowired private SettingService settingService;
    @GetMapping("/orders")
    public String listFirstPage()
    {
        return defaultRedirectURL;
    }
    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders")PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum,
                             HttpServletRequest request,
                             @AuthenticationPrincipal ShopUserDetails loggedUser)
    {
        orderService.listByPage(pageNum, helper);
        loadCurrencySetting(request);

        if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper"))
        {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }
    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();

        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra, HttpServletRequest request,
                                   @AuthenticationPrincipal ShopUserDetails loggedUser)
    {
        try {
            Order order = orderService.get(id);
            loadCurrencySetting(request);

            boolean isVisibleForAdminOrSalesperson = false;
            if(loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson"))
            {
                isVisibleForAdminOrSalesperson = true;
            }

            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);

            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra)
    {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
        }catch (OrderNotFoundException ex)
        {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }
    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra, HttpServletRequest request)
    {
        try {
            Order order = orderService.get(id);

            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", "Edit Order (ID: "+id+")");
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";
        }catch (OrderNotFoundException ex)
        {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/order/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra)
    {

        String countryName = request.getParameter("countryName");
        order.setCountry(countryName);

        updateProductDetails(order, request);
        updateOrderTracks(order, request);

        orderService.save(order);
        ra.addFlashAttribute("message", "The order ID "+ order.getId() + "has been updated successfully");

        return defaultRedirectURL;
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackNotes = request.getParameterValues("trackNotes");
        String[] trackDates = request.getParameterValues("trackDate");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for(int i = 0; i<trackIds.length; i++)
        {
            OrderTrack trackRecord = new OrderTrack();

            Integer trackId = Integer.parseInt(trackIds[i]);
            if(trackId > 0)
            {
                trackRecord.setId(trackId);
            }
            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            trackRecord.setNotes(trackNotes[i]);
            try {
                trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            orderTracks.add(trackRecord);
        }
    }

    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();


        for (int i = 0; i < detailIds.length; i++)
        {
            System.out.println("Detail ID: " + detailIds[i]);
            System.out.println("\t Product ID: " + productIds[i]);
            System.out.println("\t Product Cost: "+ productCosts[i]);
            System.out.println("\t Product Quantity: "+ quantities[i]);
            System.out.println("\t Product Subtotal: "+ productSubtotals[i]);
            System.out.println("\t Product Shipping Cost: "+ productShipCosts[i]);
            OrderDetail orderDetail = new OrderDetail();
            Integer detailId = Integer.parseInt(detailIds[i]);
            if(detailId > 0)
            {
                orderDetail.setId(detailId);
            }
            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);
        }
    }
}
