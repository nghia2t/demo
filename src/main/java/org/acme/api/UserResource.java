package org.acme.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.acme.model.User;
import org.acme.service.UserService;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@RolesAllowed("user")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") @NotNull Long id) {
        return Response.ok(userService.getUser(id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUsers(@QueryParam("firstName") String firstName,
                            @QueryParam("lastName") String lastName,
                            @QueryParam("street") String street,
                            @QueryParam("houseNumber") String houseNumber,
                            @QueryParam("grade") String grade) {
        return Response.ok(userService.searchUsers(firstName, lastName, street, houseNumber, grade)).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User u) {
        return Response.ok(userService.createUser(u))
                .status(Response.Status.CREATED)
                .build();
    }

    @POST
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") @NotNull Long id, @Valid User u) {
        return Response.ok(userService.updateUser(id, u)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") @NotNull Long id) {
        userService.deleteUser(id);
        return Response.ok().build();
    }

    @GET
    @Path("/roles/get")
    @NoCache
    public String roles() throws JsonProcessingException {
        return objectMapper.writeValueAsString(identity.getRoles());
    }
}