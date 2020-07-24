package com.galvanize.crudAPICheckpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;
    public UserController(UserRepository repository){
        this.repository = repository;
    }

    @GetMapping("")
    public Iterable<User> getUsers(){
        return this.repository.findAll();
    }

    @PostMapping("")
    public User postUser(@RequestBody User user){
        return this.repository.save(user);
    }

    @GetMapping("/{uId}")
    public Optional<User> getUser(@PathVariable Long uId){
        return this.repository.findById(uId);

    }

    @PatchMapping("{uId}")
    public Object patchUser(@PathVariable Long uId, @RequestBody User user) {
        Optional<User> qResult = this.repository.findById(uId);
        if (qResult.isEmpty()) {
            return "User with this id not found.";
        } else {
            User userToUpdate = qResult.get();
            if (user.getEmail() != null) {
                userToUpdate.setEmail(user.getEmail());
            }
            if (user.getPassword() != null) {
                userToUpdate.setPassword(user.getPassword());
            }
            return this.repository.save(userToUpdate);
        }
    }

    @DeleteMapping("{uId}")
    public Object deleteUser(@PathVariable Long uId){
        Optional<User> qResult = this.repository.findById(uId);
        if (qResult.isEmpty()) {
            return "User with this id not found.";
        } else {
            this.repository.delete(qResult.get());
            return "User deleted";
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class AuthenticatedUser{
        public AuthenticatedUser(boolean authenticated, User user) {
            this.authenticated = authenticated;
            this.user = user;
        }

        private boolean authenticated;
        private User user;

        public AuthenticatedUser(boolean b) {
            this.authenticated = false;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    @PostMapping("/authenticate")
    public Object authUser(@RequestBody User user){
        Optional<User> qResult = this.repository.findByEmail(user.getEmail());
        if (qResult.isEmpty()) {
            return "User with this email not found.";
        } else {
            User toLogin = qResult.get();
            if (user.getPassword().equals(toLogin.getPassword())){
                AuthenticatedUser loggedIn = new AuthenticatedUser(true, user);
                return loggedIn;
            } else {
                AuthenticatedUser notloggedIn = new AuthenticatedUser(false);
                return notloggedIn;
            }

        }
    }

}
