package co.acueducto.sgdea.application;

import co.acueducto.sgdea.adapters.IdentityProviderUserAdapter;
import co.acueducto.sgdea.domain.services.UserService;

public class UserApplicationFactory {

    public UserApplicationFactory(){}

    public static UserApplication build(){
        IdentityProviderUserAdapter identityProviderUserPort=new IdentityProviderUserAdapter();
        UserService getUsersByRolAndDependency =new UserService(identityProviderUserPort);
        return new UserApplication(getUsersByRolAndDependency);
    }
}
