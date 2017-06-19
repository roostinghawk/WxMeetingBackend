package com.leadingsoft.liuw.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WxAppAuthenticationDTO {
    private String code;
    private String encryptedData;
    private String iv;
}
