package com.shop.admin.setting;

import com.shop.admin.AmazonS3Util;
import com.shop.admin.FileUploadUtil;
import com.shop.common.Constants;
import com.shop.common.entity.Currency;
import com.shop.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {
    @Autowired private SettingService service;
    @Autowired private CurrencyRepository currencyRepo;
    @GetMapping("/settings")
    public String listAll(Model model)
    {
        List<Setting> listSettings = service.listAllSettings();

        List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);

        for(Setting setting : listSettings)
        {
            model.addAttribute(setting.getKey(), setting.getValue());

        }
        model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
        return "settings/settings";
    }
    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes ra) throws IOException {

        GeneralSettingBag settingBag = service.getGeneralSettings();

        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);
        updateSettingValuesFromForm(request, settingBag.list());
        ra.addFlashAttribute("message", "General settings have been saved");
        return "redirect:/settings";
    }

    private static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if(!multipartFile.isEmpty())
        {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value="/site-logo/"+fileName;
            settingBag.updateSiteLogo(value);
            String uploadDir = "site-logo";

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        }
    }
    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag)
    {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);

        if(findByIdResult.isPresent())
        {
            Currency currency = findByIdResult.get();
            String symbol = currency.getSymbol();
            settingBag.updateCurrencySymbol(symbol);
        }
    }
    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings)
    {
        for(Setting setting : listSettings)
        {
            String value = request.getParameter(setting.getKey());
            if(value != null)
            {
                setting.setValue(value);
            }
        }
        service.saveAll(listSettings);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra)
    {
        List<Setting> mailServerSettings = service.getMailServerSettings();
        updateSettingValuesFromForm(request, mailServerSettings);

        ra.addFlashAttribute("message", "Mail Server settings have been saved");
        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes ra)
    {
        List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
        updateSettingValuesFromForm(request, mailTemplateSettings);

        ra.addFlashAttribute("message", "Mail Template settings have been saved");
        return "redirect:/settings";
    }
    @PostMapping("/settings/save_payment")
    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes ra)
    {
        List<Setting> paymentSettings = service.getPaymentSettings();
        updateSettingValuesFromForm(request, paymentSettings);

        ra.addFlashAttribute("message", "Payment settings have been saved");

        return "redirect:/settings";
    }


}
