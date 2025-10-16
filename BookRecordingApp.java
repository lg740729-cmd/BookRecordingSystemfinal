import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Book {
    String title, author;
    int year;

    Book(String t, String a, int y) {
        title = t;
        author = a;
        year = y;
    }

    public String toString() {
        return title + " by " + author + " (" + year + ")";
    }

    public String toCSV() {
        return title + "," + author + "," + year;
    }

    public static Book fromCSV(String line) {
        String[] p = line.split(",");
        return new Book(p[0], p[1], Integer.parseInt(p[2]));
    }
}

public class BookRecordingApp extends JFrame {
    CardLayout layout;
    JPanel mainPanel;
    JTextField titleField, authorField, yearField, searchField;
    JTextArea displayArea;
    ArrayList<Book> books = new ArrayList<>();
    File file = new File("books.txt");

    public BookRecordingApp() {
        setTitle("Book Recording System");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        mainPanel.add(loginPage(), "Login");
        mainPanel.add(homePage(), "Home");
        mainPanel.add(managePage(), "Manage");

        add(mainPanel);
        loadBooksFromFile();

        layout.show(mainPanel, "Login");
        setVisible(true);
    }

    JPanel loginPage() {
        JPanel p = new JPanel(new FlowLayout());
        JTextField user = new JTextField(10);
        JPasswordField pass = new JPasswordField(10);
        JButton loginBtn = new JButton("Login");

        p.add(new JLabel("Username:")); p.add(user);
        p.add(new JLabel("Password:")); p.add(pass);
        p.add(loginBtn);

        loginBtn.addActionListener(e -> {
            if (user.getText().equals("admin") && new String(pass.getPassword()).equals("123")) {
                layout.show(mainPanel, "Home");
            } else {
                JOptionPane.showMessageDialog(this, "Login failed!");
            }
        });
        return p;
    }

    JPanel homePage() {
        JPanel p = new JPanel();
        JButton manageBtn = new JButton("Manage Books");
        manageBtn.addActionListener(e -> layout.show(mainPanel, "Manage"));
        p.add(manageBtn);
        return p;
    }

    JPanel managePage() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(4, 2));
        titleField = new JTextField(10);
        authorField = new JTextField(10);
        yearField = new JTextField(5);
        searchField = new JTextField(10);

        JButton addBtn = new JButton("Add");
        JButton showBtn = new JButton("Show All");
        JButton searchBtn = new JButton("Search");
        JButton deleteBtn = new JButton("Delete");
        JButton editBtn = new JButton("Edit");

        top.add(new JLabel("Title:")); top.add(titleField);
        top.add(new JLabel("Author:")); top.add(authorField);
        top.add(new JLabel("Year:")); top.add(yearField);
        top.add(new JLabel("Search Title:")); top.add(searchField);

        JPanel buttons = new JPanel();
        buttons.add(addBtn); buttons.add(showBtn);
        buttons.add(searchBtn); buttons.add(deleteBtn); buttons.add(editBtn);

        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);

        p.add(top, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.CENTER);
        p.add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addBook());
        showBtn.addActionListener(e -> showBooks());
        searchBtn.addActionListener(e -> searchBook());
        deleteBtn.addActionListener(e -> deleteBook());
        editBtn.addActionListener(e -> editBook());

        return p;
    }

    void addBook() {
        try {
            String t = titleField.getText();
            String a = authorField.getText();
            int y = Integer.parseInt(yearField.getText());
            books.add(new Book(t, a, y));
            saveAllBooks();
            JOptionPane.showMessageDialog(this, "Book Added!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input!");
        }
    }

    void showBooks() {
        displayArea.setText("");
        for (Book b : books) displayArea.append(b.toString() + "\n");
    }

    void searchBook() {
        String t = searchField.getText();
        for (Book b : books) {
            if (b.title.equalsIgnoreCase(t)) {
                displayArea.setText(b.toString());
                return;
            }
        }
        displayArea.setText("Not Found!");
    }

    void deleteBook() {
        String t = searchField.getText();
        for (Book b : books) {
            if (b.title.equalsIgnoreCase(t)) {
                books.remove(b);
                saveAllBooks();
                displayArea.setText("Book Deleted!");
                return;
            }
        }
        displayArea.setText("Not Found!");
    }

    void editBook() {
        String t = searchField.getText();
        for (Book b : books) {
            if (b.title.equalsIgnoreCase(t)) {
                b.title = titleField.getText();
                b.author = authorField.getText();
                b.year = Integer.parseInt(yearField.getText());
                saveAllBooks();
                displayArea.setText("Book Updated!");
                return;
            }
        }
        displayArea.setText("Not Found!");
    }

    void saveAllBooks() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            for (Book b : books) {
                w.write(b.toCSV());
                w.newLine();
            }
        } catch (Exception e) {}
    }

    void loadBooksFromFile() {
        books.clear();
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) books.add(Book.fromCSV(line));
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        new BookRecordingApp();
    }
}
