package io.pivotal.pal.tracker;

import jdk.nashorn.internal.runtime.logging.DebugLogger;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.users.DeleteUserRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.ListUsersResponse;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.useradmin.CreateUserRequest;
import org.cloudfoundry.operations.useradmin.ListOrganizationUsersRequest;
import org.cloudfoundry.operations.useradmin.OrganizationUsers;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.ArrayList;
import java.util.List;

@Component
public class CloudControllerImpl implements ICloudController {
    private final PasswordGrantTokenProvider tokenProvider;
    private final DefaultConnectionContext context;
    private final ReactorCloudFoundryClient cfClient;
    private final ReactorDopplerClient dopplerClient;
    private final ReactorUaaClient uaaClient;
    private final DefaultCloudFoundryOperations operations;

    public CloudControllerImpl(PasswordGrantTokenProvider passwordGrantTokenProvider,
                               DefaultConnectionContext defaultConnectionContext,
                               ReactorCloudFoundryClient reactorCloudFoundryClient,
                               ReactorDopplerClient reactorDopplerClient,
                               ReactorUaaClient reactorUaaClient,
                               DefaultCloudFoundryOperations defaultCloudFoundryOperations
                               ){
        tokenProvider = passwordGrantTokenProvider;
        context = defaultConnectionContext;
        cfClient = reactorCloudFoundryClient;
        dopplerClient = reactorDopplerClient;
        uaaClient = reactorUaaClient;
        operations = defaultCloudFoundryOperations;
    }

    //WORKS PERFECT!!!
    public List<String> WORKS_BUDDY() {
        List<String> matchingUsers = new ArrayList<String>();

        ListOrganizationUsersRequest request = ListOrganizationUsersRequest.
                builder().
                organizationName("DellEMC_PAL").
                build();

        Mono<OrganizationUsers> responseMono = operations.userAdmin().listOrganizationUsers(request);

        try {
            OrganizationUsers response = responseMono.block();
            matchingUsers = response.getManagers();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return matchingUsers;
    }

    public void CreateUserTest(){
        String user = "tester.pal@dell.com";
        CreateUserRequest request = CreateUserRequest
                .builder()
                .username(user)
                .password("DellEMC@123")
                .build();
        Mono<Void> response = operations.userAdmin().create(request);
        return;
    }

    public void DeleteUserTest(){
        String user = "tester.pal@dell.com";

        org.cloudfoundry.operations.useradmin.DeleteUserRequest request = org.cloudfoundry.operations.useradmin.DeleteUserRequest
                .builder()
                .username("tester.pal@dell.com")
                .build();
        Mono<Void> response = operations.userAdmin().delete(request);
        return;
    }

    //NOT WORKING
    public List<String> getALLtheUsers(){
        List<String> matchingUsers = new ArrayList<String>();
        ListUsersRequest request = ListUsersRequest.builder().build();


        Mono<ListUsersResponse> responseMono = cfClient.users().list(request);

        try {
            ListUsersResponse response = responseMono.block();
            response.getResources().forEach(app ->
                    matchingUsers.add(app.getEntity().getUsername()));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return matchingUsers;
    }

    @Override
    public List<String> getUsers()
    {
        return null;
    }
    @Override
    public List<String> getOrgUsers() {
        List<String> matchingUsers = new ArrayList<String>();
        //operations.userAdmin().create();

        ListOrganizationUsersRequest request = ListOrganizationUsersRequest.
                builder().
                organizationName("DellEMC_PAL").
                build();

        Mono<OrganizationUsers> responseMono = operations.userAdmin().listOrganizationUsers(request);

        try {
            OrganizationUsers response = responseMono.block();
            matchingUsers = response.getManagers();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return matchingUsers;
    }

    public List<String> getApps() {
        List<String> matchingApps = new ArrayList<String>();
        ApplicationsV2 applicationsV2 = cfClient.applicationsV2();

        ListApplicationsRequest listApplicationsRequest = ListApplicationsRequest.builder()
                .build();

        Mono<ListApplicationsResponse> responseMono = applicationsV2.list(listApplicationsRequest);

        try {
            ListApplicationsResponse response = responseMono.block();

            if (response.getTotalResults() > 0) {
                System.out.println(response.getTotalResults());
                response.getResources().forEach(app ->
                        matchingApps.add(app.getEntity().getName()));
            }
        }
        catch (Exception e) {
            System.out.println("Failed to lookup V2 apps.");
        }
        return matchingApps;
    }

    private void Test1(){
        org.cloudfoundry.operations.spaces.Spaces spaces = operations.spaces();
        spaces.list().log();
    }

    @Override
    public List<String> getSpaces() {

        return null;
    }

    @Override
    public boolean spaceExists(String spaceName) {
        return false;
    }

    @Override
    public boolean userExists(String userName) {
        return false;
    }

    @Override
    public boolean createSpace(String spaceName, List<String> roles) {
        return false;
    }

    @Override
    public boolean deleteSpace(String spaceName) {
        return false;
    }

    @Override
    public boolean createUser(String userName, List<String> spaces)
    {
        CreateUserTest();
        return false;
    }

    @Override
    public boolean deleteUser(String userName)
    {
        DeleteUserTest();
        return false;
    }
}
