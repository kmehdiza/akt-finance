package az.ingress.user.management.web.rest.vm;

import az.ingress.user.management.config.Constants;
import az.ingress.user.management.service.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class ManagedUserVm extends UserDto {

    @Size(max = Constants.PASSWORD_MAX_LENGTH, min = Constants.PASSWORD_MIN_LENGTH)
    private String password;

}
