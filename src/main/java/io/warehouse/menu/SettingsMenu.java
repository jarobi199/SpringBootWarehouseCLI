package io.warehouse.menu;

import io.warehouse.service.UserService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsMenu implements IMenu {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticateMenu authenticateMenu;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> addUser();
                case 2 -> changePassword();
                case 3 -> deleteUser();
                case 4 -> switchUser();
            }
        }
        while (choice != 0);
    }

    public void switchUser() {
        authenticateMenu.show();
    }

    public void deleteUser() {
        System.out.print("Enter username of the user to be deleted: ");
        String username = InputHandler.getStringInput();
        if (userService.deleteUser(username)) {
            System.out.println("User deleted successfully!");
        }
        else
        {
            System.out.println("User not found!");
        }
    }

    public void changePassword() {
        System.out.print("Enter new PIN: ");
        String newPIN = InputHandler.getStringInput();
        if (userService.changePIN(newPIN)) {
            System.out.println("PIN changed successfully!");
        }
        else
        {
            System.out.println("PIN not changed. Old PIN and new PIN cannot be the same!");
        }
    }

    public void addUser() {
        System.out.println("Enter full name:");
        String name = InputHandler.getStringInput();
        System.out.println("Enter username:");
        String username = InputHandler.getStringInput();
        System.out.println("Enter PIN:");
        String pin = InputHandler.getStringInput();

        userService.addUser(name, username, pin);
        System.out.println("User added successfully!");
    }

    @Override
    public void printOptions() {
        System.out.println("[1] Add user");
        System.out.println("[2] Change PIN");
        System.out.println("[3] Delete user");
        System.out.println("[4] Switch user");
        System.out.println("[0] Back");
    }

}
