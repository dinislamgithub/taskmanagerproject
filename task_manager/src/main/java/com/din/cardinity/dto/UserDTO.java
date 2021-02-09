package com.din.cardinity.dto;

import java.util.Date; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO { 
    private int userId;
    private String userName;
    private String passWord;
    private String email;
    private String mobile;
    
    private boolean activeStatus;
    private Date createDate; 
    private String accessDeniedMsf;     
}
