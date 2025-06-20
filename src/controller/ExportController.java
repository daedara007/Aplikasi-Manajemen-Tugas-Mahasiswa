package controller;

import model.User;
import model.abstractes.TaskBase;
import model.AcademicTask;
import model.PersonalTask;
import model.enums.Status;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import utils.CustomDialog;

public class ExportController {
    
    public static void exportUserTasks(User user, Component parent) {
        // Get all tasks for the user
        List<TaskBase> allTasks = TaskController.getAllTasksByUserId(user.getUserId());
        
        if (allTasks.isEmpty()) {
            CustomDialog.showError(parent, "Tidak ada data tugas untuk diekspor!");
            return;
        }
        
        // Show file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File Export");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        
        // Set default filename with current date
        String defaultFilename = "Tugas_" + user.getName().replaceAll("\\s+", "_") + 
                                "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));
        
        int userSelection = fileChooser.showSaveDialog(parent);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure .csv extension
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try {
                exportToCSV(allTasks, user, fileToSave);
                CustomDialog.showSuccess(parent, "Data berhasil diekspor ke:\n" + fileToSave.getAbsolutePath());

//                JOptionPane.showMessageDialog(parent, 
//                    "Data berhasil diekspor ke:\n" + fileToSave.getAbsolutePath(), 
//                    "Export Berhasil", 
//                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                CustomDialog.showSuccess(parent, "Gagal mengekspor data: " + e.getMessage());

//                JOptionPane.showMessageDialog(parent, 
//                    "Gagal mengekspor data: " + e.getMessage(), 
//                    "Export Gagal", 
//                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void exportToCSV(List<TaskBase> tasks, User user, java.io.File file) throws IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        try (FileWriter writer = new FileWriter(file)) {
            // Write header with user info
            writer.append("# Export Data Tugas\n");
            writer.append("# User: ").append(user.getName()).append("\n"); 
            writer.append("# Tanggal Export: ").append(LocalDate.now().format(dateFormatter)).append("\n");
            writer.append("# Total Tugas: ").append(String.valueOf(tasks.size())).append("\n");
            writer.append("\n");
            
            // Write CSV header
            writer.append("No,Jenis Tugas,Judul,Deskripsi,Deadline,Status,Mata Kuliah/Kategori,Dibuat Tanggal,Status Deadline\n");
            
            // Write task data
            int no = 1;
            for (TaskBase task : tasks) {
                writer.append(String.valueOf(no++)).append(",");
                
                // Task type
                String taskType = task instanceof AcademicTask ? "Akademik" : "Personal";
                writer.append("\"").append(taskType).append("\",");
                
                // Title (escape quotes)
                writer.append("\"").append(escapeCSV(task.getTitle())).append("\",");
                
                // Description (escape quotes)
                writer.append("\"").append(escapeCSV(task.getDescription())).append("\",");
                
                // Deadline
                writer.append("\"").append(task.getDeadline().format(dateFormatter)).append("\",");
                
                // Status
                writer.append("\"").append(task.getStatus().getDisplayName()).append("\",");
                
                // Course/Category
                String courseOrCategory = "";
                if (task instanceof AcademicTask) {
                    courseOrCategory = TaskController.getCourseName(((AcademicTask) task).getCourseId());
                } else if (task instanceof PersonalTask) {
                    courseOrCategory = ((PersonalTask) task).getCategory();
                }
                writer.append("\"").append(escapeCSV(courseOrCategory)).append("\",");
                
                // Created date (using current date as placeholder since we don't store creation date)
                writer.append("\"").append(LocalDate.now().format(dateFormatter)).append("\",");
                
                // Deadline status
                String deadlineStatus = getDeadlineStatus(task);
                writer.append("\"").append(deadlineStatus).append("\"");
                
                writer.append("\n");
            }
            
            // Write summary statistics
            writer.append("\n# STATISTIK\n");
            
            long completedTasks = tasks.stream().filter(t -> t.getStatus() == Status.SELESAI).count();
            long inProgressTasks = tasks.stream().filter(t -> t.getStatus() == Status.SEDANG_DIKERJAKAN).count();
            long notStartedTasks = tasks.stream().filter(t -> t.getStatus() == Status.BELUM_MULAI).count();
            long overdueTasks = tasks.stream().filter(t -> 
                t.getDeadline().isBefore(LocalDate.now()) && t.getStatus() != Status.SELESAI).count();
            
            writer.append("# Tugas Selesai: ").append(String.valueOf(completedTasks)).append("\n");
            writer.append("# Tugas Dalam Pengerjaan: ").append(String.valueOf(inProgressTasks)).append("\n");
            writer.append("# Tugas Belum Mulai: ").append(String.valueOf(notStartedTasks)).append("\n");
            writer.append("# Tugas Terlambat: ").append(String.valueOf(overdueTasks)).append("\n");
            
            double completionRate = tasks.isEmpty() ? 0 : (double) completedTasks / tasks.size() * 100;
            writer.append("# Tingkat Penyelesaian: ").append(String.format("%.1f%%", completionRate)).append("\n");
        }
    }
    
    private static String escapeCSV(String value) {
        if (value == null) return "";
        // Escape quotes by doubling them
        return value.replace("\"", "\"\"");
    }
    
    private static String getDeadlineStatus(TaskBase task) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = task.getDeadline();
        
        if (task.getStatus() == Status.SELESAI) {
            return "Selesai";
        } else if (deadline.isBefore(today)) {
            return "Terlambat";
        } else if (deadline.isEqual(today)) {
            return "Hari Ini";
        } else if (deadline.isBefore(today.plusDays(3))) {
            return "Mendesak";
        } else if (deadline.isBefore(today.plusDays(7))) {
            return "Minggu Ini";
        } else {
            return "Normal";
        }
    }
}
