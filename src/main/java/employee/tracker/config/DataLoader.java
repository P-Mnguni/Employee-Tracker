package employee.tracker.config;

import employee.tracker.model.*;
import employee.tracker.repository.EmployeeRepository;
import employee.tracker.repository.TimeEntryRepository;
import employee.tracker.repository.TimesheetRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DataLoader - Loads mock data into the database on application startup
 * This is only for development and testing purposes
 */
@Component
public class DataLoader implements CommandLineRunner {
    
    private final TimesheetRepository timesheetRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    DataLoader(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only load data if database is empty
        if (employeeRepository.count() == 0) {
            loadMockData();
            System.out.println("✅ Mock data loaded successfully!");
        } else {
            System.out.println("⚠️  Database already contains data. Skipping mock data loading.");
        }
    }

    private void loadMockData() {
        // === 1. Create Employees ===

        // Admin User
        Employee admin = new Employee("John Admin", "admin@employeetracker.com", "IT", Role.ADMIN);

        // Manager Users
        Employee managerSarah = new Employee("Sarah Manager", "sarah.manager@employeetracker.com", "Sales", Role.MANAGER);
        Employee managerMike = new Employee("Mike Manager", "mike.manager@employeetracker.com", "Sales", Role.MANAGER);

        // Employee Users
        Employee empJohn = new Employee("John Doe", "john.doe@employeetracker.com", "Engineering", Role.EMPLOYEE);
        Employee empJane = new Employee("Jane Smith", "jane.smith@employeetracker.com", "Engineering", Role.EMPLOYEE);
        Employee empBob = new Employee("Bob Johnson", "bob.johnson@employeetracker.com", "Sales", Role.EMPLOYEE);
        Employee empAlice = new Employee("Alice Brown", "alice.brown@employeetracker.com", "Sales", Role.EMPLOYEE);
        Employee empCharlie = new Employee("Charlie Wilson", "charlie.wilson@employeetracker.com", "Marketing", Role.EMPLOYEE);

        // Save all employee
        employeeRepository.save(admin);
        employeeRepository.save(managerSarah);
        employeeRepository.save(managerMike);
        employeeRepository.save(empJohn);
        employeeRepository.save(empJane);
        employeeRepository.save(empBob);
        employeeRepository.save(empAlice);
        employeeRepository.save(empCharlie);

        System.out.println("📝 Created " + employeeRepository.count() + " employees");

        // === 2. Create Time Entries for the past week ===

        LocalDate today = LocalDate.now();

        // John Doe's time entries (Engineering)
        createTimeEntry(empJohn, today.minusDays(6), LocalTime.of(9, 0), LocalTime.of(17, 0));
        createTimeEntry(empJohn, today.minusDays(5), LocalTime.of(8, 45), LocalTime.of(17, 15));
        createTimeEntry(empJohn, today.minusDays(4), LocalTime.of(9, 15), LocalTime.of(18, 0));
        createTimeEntry(empJohn, today.minusDays(3), LocalTime.of(9, 0), LocalTime.of(16, 30));
        createTimeEntry(empJohn, today.minusDays(2), LocalTime.of(8, 30), LocalTime.of(17, 0));
        createTimeEntry(empJohn, today.minusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 30));
        createTimeEntry(empJohn, today, LocalTime.of(9, 0), null);

        // Jane Smith's time entries (Engineering)
        createTimeEntry(empJane, today.minusDays(6), LocalTime.of(8, 30), LocalTime.of(16, 30));
        createTimeEntry(empJane, today.minusDays(5), LocalTime.of(9, 0), LocalTime.of(17, 0));
        createTimeEntry(empJane, today.minusDays(4), LocalTime.of(8, 45), LocalTime.of(17, 30));
        createTimeEntry(empJane, today.minusDays(3), LocalTime.of(9, 30), LocalTime.of(18, 0));
        createTimeEntry(empJane, today.minusDays(2), LocalTime.of(8, 15), LocalTime.of(16, 45));
        createTimeEntry(empJane, today.minusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0));
        createTimeEntry(empJane, today, LocalTime.of(8, 55), null);

        // Bob Johnson's time entries (Sales)
        createTimeEntry(empBob, today.minusDays(6), LocalTime.of(10, 0), LocalTime.of(18, 0));
        createTimeEntry(empBob, today.minusDays(5), LocalTime.of(9, 30), LocalTime.of(17, 30));
        createTimeEntry(empBob, today.minusDays(4), LocalTime.of(10, 15), LocalTime.of(19, 0));
        createTimeEntry(empBob, today.minusDays(3), LocalTime.of(9, 45), LocalTime.of(18, 15));
        createTimeEntry(empBob, today.minusDays(2), LocalTime.of(10, 0), LocalTime.of(17, 0));
        createTimeEntry(empBob, today.minusDays(1), LocalTime.of(9, 30), LocalTime.of(18, 0));
        createTimeEntry(empBob, today, LocalTime.of(9, 45), null);

        // Alice Brown's time entries (Sales)
        createTimeEntry(empAlice, today.minusDays(6), LocalTime.of(8, 0), LocalTime.of(16, 0));
        createTimeEntry(empAlice, today.minusDays(5), LocalTime.of(8, 30), LocalTime.of(16, 30));
        createTimeEntry(empAlice, today.minusDays(4), LocalTime.of(9, 0), LocalTime.of(17, 0));
        createTimeEntry(empAlice, today.minusDays(3), LocalTime.of(8, 15), LocalTime.of(16, 15));
        createTimeEntry(empAlice, today.minusDays(2), LocalTime.of(8, 45), LocalTime.of(16, 45));

        // Charlie Wilson's time entries (Marketing)
        createTimeEntry(empCharlie, today.minusDays(6), LocalTime.of(9, 0), LocalTime.of(17, 0));
        createTimeEntry(empCharlie, today.minusDays(5), LocalTime.of(10, 0), LocalTime.of(18, 0));
        createTimeEntry(empCharlie, today.minusDays(4), LocalTime.of(9, 30), LocalTime.of(17, 30));
        createTimeEntry(empCharlie, today.minusDays(3), LocalTime.of(9, 15), LocalTime.of(17, 15));
        createTimeEntry(empCharlie, today.minusDays(2), LocalTime.of(10, 30), LocalTime.of(18, 30));

        // Manager Sarah's time entries
        createTimeEntry(managerSarah, today.minusDays(6), LocalTime.of(8, 0), LocalTime.of(17, 0));
        createTimeEntry(managerSarah, today.minusDays(5), LocalTime.of(8, 0), LocalTime.of(17, 0));
        createTimeEntry(managerSarah, today.minusDays(4), LocalTime.of(8, 30), LocalTime.of(17, 30));
        createTimeEntry(managerSarah, today.minusDays(3), LocalTime.of(8, 0), LocalTime.of(16, 0));
        createTimeEntry(managerSarah, today, LocalTime.of(8, 15), null);

        System.out.println("⏰ Created " + timeEntryRepository.count() + " time entries");

        // === 3. Sample Timesheet ===
        LocalDate lastWeek = today.minusDays(7);
        LocalDate twoWeeksAgo = today.minusDays(14);

        // John Doe's timesheets (Engineering)
        createTimesheet(empJohn.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerSarah.getId());
        createTimesheet(empJohn.getId(), lastWeek, lastWeek.plusDays(6), TimesheetStatus.PENDING, null);
        
        // Jane Smith's timesheets (Engineering)
        createTimesheet(empJane.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerSarah.getId());
        createTimesheet(empJane.getId(), lastWeek, lastWeek.plusDays(6), TimesheetStatus.REJECTED, managerSarah.getId());
        
        // Bob Johnson's timesheets (Sales)
        createTimesheet(empBob.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerMike.getId());
        createTimesheet(empBob.getId(), lastWeek, lastWeek.plusDays(6), TimesheetStatus.PENDING, null);

        // Alice Brown's timesheets (Sales)
        createTimesheet(empAlice.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerMike.getId());
        
        // Charlie Wilson's timesheets (Marketing)
        createTimesheet(empCharlie.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.PENDING, null);
        
        System.out.println("📋 Created " + timesheetRepository.count() + " timesheets");
    }

    private void createTimeEntry(Employee employee, LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDateTime clockIn = LocalDateTime.of(date, startTime);
        TimeEntry entry = new TimeEntry(employee, clockIn);

        if (endTime != null) {
            LocalDateTime clockOut = LocalDateTime.of(date, endTime);
            entry.setClockOutTime(clockOut);
        }

        // Set some entries as APPROVED for variety
        if (endTime != null && date.isBefore(LocalDate.now())) {
            entry.setStatus(TimeEntryStatus.APPROVED);
        }

        timeEntryRepository.save(entry);
    }

    @Transactional
    private void createTimesheet(Long employeeId, LocalDate startDate, LocalDate endDate, TimesheetStatus status, Long approverId) {
        // Fetch time entries within this date range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<TimeEntry> entries = timeEntryRepository.findByEmployeeIdAndClockInTimeBetween(employeeId, startDateTime, endDateTime);

        // Only create timesheet if there are entries
        if (!entries.isEmpty()) {
            Employee employee = employeeRepository.findById(employeeId)
                                .orElseThrow(() -> new RuntimeException("Employee not found"));
            
            Timesheet timesheet = new Timesheet(employee, startDate, endDate);
            
            // Set submission and approval info based on status
            if (status != TimesheetStatus.DRAFT) {
                timesheet.setSubmittedAt(LocalDateTime.now().minusDays(5));
            }

            if (status == TimesheetStatus.APPROVED && approverId != null) {
                timesheet.submit();
                Employee approver = employeeRepository.findById(approverId)
                                    .orElseThrow(() -> new RuntimeException("Approver not found"));
                timesheet.approve(approver);
            } else if (status == TimesheetStatus.REJECTED && approverId != null) {
                timesheet.submit();
                Employee approver = employeeRepository.findById(approverId)
                                    .orElseThrow(() -> new RuntimeException("Approver not found"));
                timesheet.reject(approver, "Missing required information. Please correct and resubmit.");
            } else if (status == TimesheetStatus.PENDING) {
                timesheet.submit();
            }

            timesheet = timesheetRepository.save(timesheet);

            // Add all entries to the timesheet
            for (TimeEntry entry : entries) {
                timesheet.addTimeEntry(entry);
                timeEntryRepository.save(entry);
            }

            timesheetRepository.save(timesheet);
        }
    }
}
