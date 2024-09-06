package com.temx.workflow.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class FlowService {

    // Define the SCREEN_RESPONSES object
    private Map<String, Object> SCREEN_RESPONSES = new HashMap<String, Object>() {{
        put("LOAN", new HashMap<String, Object>() {{
            put("screen", "LOAN");
            put("data", new HashMap<String, Object>() {{
                put("tenure", Arrays.asList(
                        new HashMap<String, String>() {{
                            put("id", "months12");
                            put("title", "12 months");
                        }},
                        new HashMap<String, String>() {{
                            put("id", "months24");
                            put("title", "24 months");
                        }},
                        new HashMap<String, String>() {{
                            put("id", "months36");
                            put("title", "36 months");
                        }},
                        new HashMap<String, String>() {{
                            put("id", "months48");
                            put("title", "48 months");
                        }}
                ));
                put("amount", Arrays.asList(
                        new HashMap<String, String>() {{
                            put("id", "amount1");
                            put("title", "₹ 7,20,000");
                        }},
                        new HashMap<String, String>() {{
                            put("id", "amount2");
                            put("title", "₹ 3,20,000");
                        }}
                ));
                put("emi", "₹ 18,000");
                put("rate", "9% pa");
                put("fee", "500");
                put("selected_amount", "amount1");
                put("selected_tenure", "months48");
            }});
        }});

        put("DETAILS", new HashMap<String, Object>() {{
            put("screen", "DETAILS");
            put("data", new HashMap<String, Object>() {{
                put("is_upi", false);
                put("is_account", false);
                put("emi", "₹ 20,000");
                put("tenure", "12 months");
                put("amount", "₹ 500");
            }});
        }});

        put("SUMMARY", new HashMap<String, Object>() {{
            put("screen", "SUMMARY");
            put("data", new HashMap<String, Object>() {{
                put("amount", "₹ 7,20,000");
                put("tenure", "12 months");
                put("rate", "9% pa");
                put("emi", "₹ 3,500");
                put("fee", "₹ 500");
                put("payment_mode", "Transfer to account xxxx2342");
            }});
        }});

        put("COMPLETE", new HashMap<String, Object>() {{
            put("screen", "COMPLETE");
            put("data", new HashMap<String, Object>());
        }});

        put("SUCCESS", new HashMap<String, Object>() {{
            put("screen", "SUCCESS");
            put("data", new HashMap<String, Object>() {{
                put("extension_message_response", new HashMap<String, Object>() {{
                    put("params", new HashMap<String, String>() {{
                        put("flow_token", "REPLACE_FLOW_TOKEN");
                        put("some_param_name", "PASS_CUSTOM_VALUE");
                    }});
                }});
            }});
        }});
    }};

    // Define the LOAN_OPTIONS object
    private static final Map<String, Map<String, String>> LOAN_OPTIONS = new HashMap<String, Map<String, String>>() {{
        put("amount1", new HashMap<String, String>() {{
            put("months12", "₹ 63,000");
            put("months24", "₹ 33,000");
            put("months36", "₹ 23,000");
            put("months48", "₹ 18,000");
        }});
        put("amount2", new HashMap<String, String>() {{
            put("months12", "₹ 28,000");
            put("months24", "₹ 14,600");
            put("months36", "₹ 10,000");
            put("months48", "₹ 8,000");
        }});
    }};

}
