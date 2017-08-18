package com.hierarchy.api.resources;

import com.google.gson.Gson;
import com.hierarchy.api.controller.UserController;
import com.hierarchy.api.entity.Message;
import com.hierarchy.api.entity.User;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/user")
@Produces({MediaType.APPLICATION_JSON})
public class UserResource {

    private UserController userController;

    public UserResource(UserController userController) {
        this.userController = userController;
    }

    @GET
    @UnitOfWork
    public List<User> findAllUsers() {
        return userController.findAll();
    }

    @GET
    @Path("/{hash}")
    @UnitOfWork
    public User findUserByHash(@PathParam(value = "hash") String id) {
        return userController.findByHash(id);
    }

    @POST
    @Path("/validate/{hash}")
    @UnitOfWork
    public Response isUserExisting(@PathParam(value = "hash") String id) {
        Gson gson = new Gson();

        User user = userController.findByHash(id);

        if (user == null) {
            Message errorMessage = new Message("Token validation failed.", false, null);
            return Response.ok().entity(gson.toJson(errorMessage)).build();
        }

        Message message = new Message("Token validated successfully.", true, user);
        return Response.ok().entity(gson.toJson(message)).build();
    }

    @GET
    @Path("/children/{referral_hash}")
    @UnitOfWork
    public List<User> findAllChildren(@PathParam(value = "referral_hash") String hash) {
        return userController.findChildrenByHash(hash);
    }

    @POST
    @Path("/{referral_hash}")
    @UnitOfWork
    public Response createUser(@PathParam("referral_hash") String hash, @Valid User user) {

        User referrer = userController.findByHash(hash);

        if (referrer == null) {
            Gson gson = new Gson();
            ErrorMessage errorMessage = new ErrorMessage("Referrer not found..");
            return Response.status(400).entity(gson.toJson(errorMessage)).build();
        }

        user.setReferralToken(referrer.getToken());
        user.setToken(UUID.randomUUID().toString());
        userController.insert(user);

        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Response update(@PathParam("id") Integer id, @Valid User person) {

        if (userController.findById(id) != null) {
            userController.update(person);
        } else {
            return Response.notModified().entity("User not found.").build();
        }

        return Response.ok().build();
    }
}


