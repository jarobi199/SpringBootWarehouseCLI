package io.warehouse.service;

import io.warehouse.authentication.PasswordEncryptor;
import io.warehouse.authentication.SessionContext;
import io.warehouse.model.User;
import io.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean authenticate(String username, String password) {
        boolean authenticated = false;
        User user = findByUser(username);
        if ((user != null) && PasswordEncryptor.authenticate(password, user.getPin())) {
            authenticated = true;
            SessionContext.setCurrentUser(user);
        }

        return authenticated;
    }

    public void addUser(String name, String username,  String pin) {
        String encrypted = PasswordEncryptor.encrypt(pin);
        User user = new User(name, username, encrypted);
        userRepository.save(user);
    }

    public User findByUser(String username) {
        List<User> users = userRepository.findByUsername(username);
        return (users.isEmpty()) ? null : users.getFirst();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public boolean changePIN(String newPIN) {
        boolean changed = false;
        String oldPIN = SessionContext.getCurrentUser().getPin();
        if (!oldPIN.equals(newPIN)) {
            SessionContext.getCurrentUser().setPin(PasswordEncryptor.encrypt(newPIN));
            userRepository.save(SessionContext.getCurrentUser());
            changed = true;
        }

        return changed;
    }

    public boolean deleteUser(String username) {
        boolean deleted = false;
        User user = findByUser(username);
        if (user != null) {
            userRepository.delete(user);
            deleted = true;
        }

        return deleted;
    }

}
