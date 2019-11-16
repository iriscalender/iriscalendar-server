package com.javaproject.iriscalender.model.request;

import com.javaproject.iriscalender.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpModel {
    private String id;
    @NotNull
    private String password1;
    @NotNull
    private String password2;

    private void checkPasswordCorrect() {
        if (!getPassword1().equals(getPassword2())) {
            throw new BadRequestException("Bad request param.");
        }
    }

    public String getPassword() {
        checkPasswordCorrect();
        return getPassword1();
    }
}
