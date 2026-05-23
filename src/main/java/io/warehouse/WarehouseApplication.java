package io.warehouse;

import io.warehouse.menu.MainMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WarehouseApplication implements CommandLineRunner {

    @Autowired
    private MainMenu mainMenu;

    public static void main(String[] args) {
        new SpringApplicationBuilder(WarehouseApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        mainMenu.show();
    }
}