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

import static java.text.MessageFormat.format;

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
    @Path("/{token}")
    @UnitOfWork
    public User findUserByHash(@PathParam(value = "token") String token) {
        return userController.findByToken(token);
    }

    @POST
    @Path("/validate/{token}")
    @UnitOfWork
    public Response isUserExisting(@PathParam(value = "token") String id) {
        Gson gson = new Gson();

        User user = userController.findByToken(id);

        if (user == null) {
            Message errorMessage = new Message("Token validation failed.", false, null);
            return Response.status(400).entity(gson.toJson(errorMessage)).build();
        }

        Message message = new Message("Token validated successfully.", true, user);
        return Response.ok().entity(gson.toJson(message)).build();
    }

    @POST
    @Path("/validate/{token}/{secret}")
    @UnitOfWork
    public Response isUserSecretValid(@PathParam(value = "token") String token, @PathParam(value = "secret") String secret) {
        Gson gson = new Gson();

        User user = userController.findByToken(token);

        if(user == null) {
            Message errorMessage = new Message("Wops! User not found.", false);
            return Response.status(400).entity(gson.toJson(errorMessage)).build();
        }

        if (user.isLocked()) {
            // Lock expired!
            if (System.currentTimeMillis() > user.getLockedUntil().getTime()) {
                userController.unlockUserToken(user);
                userController.resetTokenAttempts(user);
                return validateUserSecret(user, secret, gson);
            }

            // User is still locked..
            Message errorMessage = new Message("The token is locked, please try again later.", false);
            return Response.status(400).entity(gson.toJson(errorMessage)).build();
        }

        if (userController.incrementTokenAttempt(user)) {
            return validateUserSecret(user, secret, gson);
        } else {
            userController.lockUserToken(user);
            Message errorMessage = new Message("Too many attempts, token locked for 15 minutes. Please try again later.",
                    false);
            return Response.status(400).entity(gson.toJson(errorMessage)).build();
        }
    }

    private Response validateUserSecret(User user, String secret, Gson gson) {
        boolean isUserSecretValid = userController.validateHashAndSecret(user.getToken(), secret);

        if (!isUserSecretValid) {
            int attemptsLeft = userController.findSecretValidationAttemptsLeft(user);
            String message = format("That is not the secret {0} gave you.. {1} attempt(s) left.", user.getFirstName(), attemptsLeft);
            Message errorMessage = new Message(message, false);
            return Response.status(400).entity(gson.toJson(errorMessage)).build();
        }
        userController.resetTokenAttempts(user);
        Message message = new Message("Welcome to the network!", true);
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

        User referrer = userController.findByToken(hash);

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


