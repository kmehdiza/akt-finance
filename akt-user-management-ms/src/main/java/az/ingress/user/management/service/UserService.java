package az.ingress.user.management.service;

import az.ingress.user.management.web.rest.vm.ManagedUserVm;

public interface UserService {
    void createUser(ManagedUserVm userVm);
}
