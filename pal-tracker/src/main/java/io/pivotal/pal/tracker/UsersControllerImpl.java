package io.pivotal.pal.tracker;

import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.spaces.SpaceSummary;
import org.cloudfoundry.operations.useradmin.*;
import org.cloudfoundry.operations.useradmin.SpaceRole;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UsersControllerImpl implements IUsersController{
    private final PasswordGrantTokenProvider tokenProvider;
    private final DefaultConnectionContext context;
    private final ReactorCloudFoundryClient cfClient;
    private final ReactorDopplerClient dopplerClient;
    private final ReactorUaaClient uaaClient;
    private final DefaultCloudFoundryOperations operations;

    @Value("${cf.organization}")
    private String orgName;

    public UsersControllerImpl(PasswordGrantTokenProvider passwordGrantTokenProvider,
                               DefaultConnectionContext defaultConnectionContext,
                               ReactorCloudFoundryClient reactorCloudFoundryClient,
                               ReactorDopplerClient reactorDopplerClient,
                               ReactorUaaClient reactorUaaClient,
                               DefaultCloudFoundryOperations defaultCloudFoundryOperations)
    {
        tokenProvider = passwordGrantTokenProvider;
        context = defaultConnectionContext;
        cfClient = reactorCloudFoundryClient;
        dopplerClient = reactorDopplerClient;
        uaaClient = reactorUaaClient;
        operations = defaultCloudFoundryOperations;
    }

    @Override
    public List<String> listUsers(String space) {
        List<String> usersList = new ArrayList<>();
        ListSpaceUsersRequest listSpaceUsersRequest = ListSpaceUsersRequest
                .builder()
                .spaceName(space)
                .organizationName(orgName)
                .build();
        try {

            Mono<SpaceUsers> responseMono = operations.userAdmin().listSpaceUsers(listSpaceUsersRequest);
            SpaceUsers users = responseMono.block();
            usersList = users.getDevelopers();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return usersList;
    }

    @Override
    public boolean createUser(String userName) {

        Email email = Email.builder().value(userName).primary(true).build();
       CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(userName)
                .password("DellEMC@123")
                .build();
        try {
            Mono<Void> responseMono = operations.userAdmin().create(createUserRequest);
            responseMono.block();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteUser(String userName) {
       DeleteUserRequest deleteUserRequest = DeleteUserRequest.builder().username(userName).build();

        try {
            Mono<Void> responseMono = operations.userAdmin().delete(deleteUserRequest);
            responseMono.block();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Value("${cf.organization}")
    String defaultOrg;

    @Override
    public boolean assignSpaceToUser(String spaceName, String userName){
        try {

         boolean result = createUser(userName);
      //      boolean result = createUserUsingUAA(userName);
           // boolean result = true; //User already created for pal.tracker.new@dell.com

            if(result){
                SetSpaceRoleRequest spaceRoleRequest = SetSpaceRoleRequest.builder()
                        .spaceRole(SpaceRole.MANAGER).spaceRole(SpaceRole.DEVELOPER)
                        .spaceName(spaceName)
                        .username(userName)
                        .organizationName(defaultOrg)
                        .build();
                Mono<Void> responseMono = operations.userAdmin().setSpaceRole(spaceRoleRequest);
                responseMono.block();
                return true;
            }
            else return false;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    //@Override
    public boolean createUserUsingUAA(String userName) {
        Map result = new HashMap();
        try {
            Email email = Email.builder().value(userName).primary(true).build();
            org.cloudfoundry.uaa.users.CreateUserRequest createUserRequest =
                    org.cloudfoundry.uaa.users.CreateUserRequest
                    .builder()
                            .userName(userName)
                            .password("DellEMC@123")
                            .email(email)
                            .build();
            uaaClient.users().create(createUserRequest).block();


           return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
