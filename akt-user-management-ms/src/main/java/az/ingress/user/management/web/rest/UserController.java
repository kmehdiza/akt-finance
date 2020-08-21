package az.ingress.user.management.web.rest;

import az.ingress.user.management.service.UserService;
import az.ingress.user.management.web.rest.vm.ManagedUserVm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public void createUser(@RequestBody @Valid ManagedUserVm vm) {
        userService.createUser(vm);
    }

}
