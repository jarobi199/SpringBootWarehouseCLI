package io.warehouse.menu;

import org.springframework.stereotype.Component;

@Component
public class GoodbyeMenu implements IMenu {

    @Override
    public void show() {
        printOptions();
    }

    @Override
    public void printOptions() {
        System.out.println("Goodbye!");
    }

}
