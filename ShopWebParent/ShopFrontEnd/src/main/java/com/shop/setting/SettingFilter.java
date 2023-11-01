package com.shop.setting;

import com.shop.common.Constants;
import com.shop.common.entity.setting.Setting;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SettingFilter implements jakarta.servlet.Filter {

    @Autowired SettingService service;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg"))
        {
            chain.doFilter(request, response);
            return;
        }

        List<Setting> generalSettings = service.getGeneralSettings();
        generalSettings.forEach(setting -> {
            request.setAttribute(setting.getKey(), setting.getValue());
            System.out.println(setting.getKey() + "== > " +setting.getValue());

        });

        request.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI);

        System.out.println(url);
        chain.doFilter(request, response);
    }
}
