import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoListGUI {
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private JTextField taskInput;
    private JButton exportButton;
    private static final String FILE_NAME = "tasks.txt";

    public ToDoListGUI() {
        JFrame frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        taskInput = new JTextField();
        taskInput.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Remove Task");
        JButton completeButton = new JButton("Mark Completed");
        exportButton = new JButton("Export to CSV");

        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeTask());
        completeButton.addActionListener(e -> markCompleted());
        exportButton.addActionListener(e -> exportToCSV());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(completeButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(exportButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadTasks(); // Load saved tasks
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveTasks(); // Save tasks when closing
            }
        });

        frame.setVisible(true);
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            listModel.addElement(task + " (Added: " + timestamp + ")");
            taskInput.setText("");
        }
    }

    private void removeTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
        }
    }

    private void markCompleted() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String task = listModel.get(selectedIndex);
            if (!task.startsWith("[Completed] ")) {
                listModel.set(selectedIndex, "[Completed] " + task);
            }
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < listModel.size(); i++) {
                writer.write(listModel.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String task;
                while ((task = reader.readLine()) != null) {
                    listModel.addElement(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.csv"))) {
            writer.write("Task,Status,Date Added\n");
            for (int i = 0; i < listModel.size(); i++) {
                String task = listModel.get(i);
                String status = task.startsWith("[Completed] ") ? "Completed" : "Pending";
                String cleanTask = task.replace("[Completed] ", "");
                writer.write(cleanTask + "," + status + "\n");
            }
            JOptionPane.showMessageDialog(null, "Tasks exported to tasks.csv successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListGUI::new);
    }
}
