package io.warehouse.menu;

import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainMenu implements IMenu {

    @Autowired
    private AuthenticateMenu authenticateMenu;
    @Autowired
    private ProductMenu productMenu;
    @Autowired
    private SettingsMenu settingsMenu;
    @Autowired
    private GoodbyeMenu goodbyeMenu;

    @Override
    public void show() {
        int choice = 0;
        IMenu menu;

        displayTitle();
        authenticateMenu.show();
        System.out.println();

        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            menu = switch (choice) {
                case 1 -> productMenu;
                case 5 -> settingsMenu;
                case 0 -> goodbyeMenu;
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            };
            menu.show();
        }
        while (choice != 0);


        InputHandler.closeInput();
    }

    @Override
    public void printOptions() {
        System.out.println("[1] Products");
        System.out.println("[2] Zones");
        System.out.println("[3] Stock movements");
        System.out.println("[4] Reports");
        System.out.println("[5] Settings");
        System.out.println("[0] Exit");
        System.out.println("Please make a selection:");
    }

    public void displayTitle() {
        System.out.println("=============================================================");
        System.out.println("   Welcome To The Warehouse Inventory Manager Application!");
        System.out.println("=============================================================");
    }


}