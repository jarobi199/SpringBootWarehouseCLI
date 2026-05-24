package io.warehouse.menu;

import io.warehouse.service.UserService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticateMenu implements IMenu {

    @Autowired
    private UserService userService;

    @Override
    public void show() {
        boolean authenticated = false;

        while (!authenticated) {
            System.out.println("Please enter your username:");
            String username = InputHandler.getStringInput();
            System.out.println("Please enter your PIN:");
            String password = InputHandler.getStringInput();
            authenticated = userService.authenticate(username, password);
            if (authenticated) {
                System.out.println("You have successfully logged in with the username: " + username + "!");
            }
            else
            {
                System.out.println("Invalid username or password!\n");
            }
        }
    }

    @Override
    public void printOptions() {
        //Not needed. No options.
    }

}
