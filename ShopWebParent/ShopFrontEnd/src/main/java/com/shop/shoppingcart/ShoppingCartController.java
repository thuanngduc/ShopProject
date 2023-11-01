package com.shop.shoppingcart;

import com.shop.Utility;
import com.shop.address.AddressService;
import com.shop.common.entity.Address;
import com.shop.common.entity.CartItem;
import com.shop.common.entity.Customer;
import com.shop.common.entity.ShippingRate;
import com.shop.common.exception.CustomerNotFoundException;
import com.shop.customer.CustomerService;
import com.shop.shipping.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired private CustomerService customerService;
    @Autowired private ShoppingCartService cartService;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shipService;
    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        float estimatedTotal = 0.0F;
        for(CartItem item : cartItems)
        {
            estimatedTotal += item.getSubtotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;
        if(defaultAddress != null)
        {
            shippingRate = shipService.getShippingRateForAddress(defaultAddress);
        }
        else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shipService.getShippingRateForCustomer(customer);
        }
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);
        return "cart/shopping_cart";
    }
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

}
