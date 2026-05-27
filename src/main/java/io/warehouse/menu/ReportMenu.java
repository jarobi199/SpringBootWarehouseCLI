package io.warehouse.menu;

import io.warehouse.service.ReportService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReportMenu implements IMenu {

    @Autowired
    private ReportService reportService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> inventorySummary();
                case 3 -> expiryReport();
                case 4 -> movementHistoryReport();
            }
        }
        while (choice != 0);
    }

    public void movementHistoryReport() {

        System.out.print("Start date (yyyy-MM-dd): ");
        LocalDate startDate = InputHandler.getDateInput();
        System.out.print("End date (yyyy-MM-dd): ");
        LocalDate endDate = InputHandler.getDateInput();

        reportService.getMovementHistoryReport(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    public void expiryReport() {
        reportService.generateExpiryReport();
    }

    public void inventorySummary() {
        reportService.generateReportSummary();
    }

    @Override
    public void printOptions() {
        System.out.println("[1] Inventory summary");
        System.out.println("[2] Stock value by zone");
        System.out.println("[3] Expiry report");
        System.out.println("[4] Movement history report");
        System.out.println("[0] Back");
    }
}
