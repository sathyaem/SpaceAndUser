package io.pivotal.pal.tracker;

import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.spaces.CreateSpaceRequest;
import org.cloudfoundry.operations.spaces.DeleteSpaceRequest;
import org.cloudfoundry.operations.spaces.RenameSpaceRequest;
import org.cloudfoundry.operations.spaces.SpaceSummary;
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
public class SpaceControllerImpl implements  ISpaceController{
    private final PasswordGrantTokenProvider tokenProvider;
    private final DefaultConnectionContext context;
    private final ReactorCloudFoundryClient cfClient;
    private final ReactorDopplerClient dopplerClient;
    private final ReactorUaaClient uaaClient;
    private final DefaultCloudFoundryOperations operations;

    public SpaceControllerImpl(PasswordGrantTokenProvider passwordGrantTokenProvider,
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
    public List<String> listSpaces() {
        List<String> spaceList = new ArrayList<>();
        List<SpaceSummary> summaryList = new ArrayList<>();
        Flux<SpaceSummary> responseMono = operations.spaces().list();

        try {

            Mono<List<SpaceSummary>> spaces = responseMono.collectList();
            summaryList = spaces.block();
            summaryList.forEach(summary->spaceList.add(summary.getName()));

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return spaceList;
    }

    @Override
    public boolean createSpace(String space) {
        try {
            CreateSpaceRequest createSpaceRequest = CreateSpaceRequest.builder().name(space).build();
            Mono<Void> responseMono = operations.spaces().create(createSpaceRequest);
            responseMono.block();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    @Override
    public boolean deleteSpace(String space) {
        try {
            DeleteSpaceRequest deleteSpaceRequest = DeleteSpaceRequest.builder().name(space).build();
            Mono<Void> responseMono = operations.spaces().delete(deleteSpaceRequest);
            responseMono.block();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    @Override
    public boolean renameSpace(String newSpaceName, String oldSpaceName) {
        try {
            RenameSpaceRequest renameSpaceRequest= RenameSpaceRequest.builder().
                    newName(newSpaceName).name(oldSpaceName).build();
            Mono<Void> responseMono = operations.spaces().rename(renameSpaceRequest);
            responseMono.block();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
