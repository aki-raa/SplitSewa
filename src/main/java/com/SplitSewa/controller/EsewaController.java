package com.SplitSewa.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class EsewaController {

    @GetMapping("/esewa-pay")
    @ResponseBody
    public String esewaPayPage(
            @RequestParam String amount,
            @RequestParam String txUuid,
            @RequestParam String signature) {

        return "<!DOCTYPE html><html><body onload='document.forms[0].submit()'>" +
                "<form action='https://rc-epay.esewa.com.np/api/epay/main/v2/form' method='POST'>" +
                "<input type='hidden' name='amount' value='" + amount + "'>" +
                "<input type='hidden' name='tax_amount' value='0'>" +
                "<input type='hidden' name='total_amount' value='" + amount + "'>" +
                "<input type='hidden' name='transaction_uuid' value='" + txUuid + "'>" +
                "<input type='hidden' name='product_code' value='EPAYTEST'>" +
                "<input type='hidden' name='product_service_charge' value='0'>" +
                "<input type='hidden' name='product_delivery_charge' value='0'>" +
                "<input type='hidden' name='success_url' value='http://localhost:8080/success'>" +
                "<input type='hidden' name='failure_url' value='http://localhost:8080/failure'>" +
                "<input type='hidden' name='signed_field_names' value='total_amount,transaction_uuid,product_code'>" +
                "<input type='hidden' name='signature' value='" + signature + "'>" +
                "</form></body></html>";
    }

    @GetMapping("/success")
    @ResponseBody
    public String success() {
        return "<h2 style='color:green;font-family:sans-serif;padding:40px'>✅ Payment Successful!</h2>";
    }

    @GetMapping("/failure")
    @ResponseBody
    public String failure() {
        return "<h2 style='color:red;font-family:sans-serif;padding:40px'>❌ Payment Failed.</h2>";
    }
}