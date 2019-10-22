package com.javaproject.iriscalender.model.request;

import com.javaproject.iriscalender.exception.BadRequestException;

public class LoginParam {
    String id;
    String password1;
    String password2;

    public String getId() {
        return id;
    }

    public String getPassword() {
        if (password1.equals(password2)) {
            return password1;
        } else {
            throw new BadRequestException("Bad Request Param");
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
