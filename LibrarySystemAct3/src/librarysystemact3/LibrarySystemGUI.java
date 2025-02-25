package librarysystemact3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;

import java.util.Date;

import java.util.Queue;

import javax.swing.JFrame;

class Book {
    private String title;
    private String author;
    private boolean isAvailable;
    private Date dueDate;
    private Queue<Member> reservationQueue;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.reservationQueue = new LinkedList<>();
    }

    // Getters and setters for the Book attributes
    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void checkOut() {
        isAvailable = false;
        dueDate = new Date(System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000)); // 2 weeks
    }

    public void returnBook() {
        isAvailable = true;
        reservationQueue.poll(); // Remove the first member in the queue
        dueDate = null;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void addReservation(Member member) {
        reservationQueue.add(member);
    }

    public Queue<Member> getReservationQueue() {
        return reservationQueue;
    }

    public void renew() {
        dueDate = new Date(System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000)); // 2 more weeks
    }
}

class Member {
    private String name;
    private int membershipId;
    private final List<Book> borrowedBooks;

    public Member(String name, int membershipId) {
        this.name = name;
        this.membershipId = membershipId;
        borrowedBooks = new ArrayList<>();
    }

    // Getters and setters for the Member attributes
    public String getName() {
        return name;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }
}

class LibrarySystem {
    private final List<Book> books;
    private final List<Member> members;

    public LibrarySystem() {
        books = new ArrayList<>();  // Initialize the books list here
        members = new ArrayList<>();  // Initialize the members list here
    }

    // The rest of your code remains unchanged...

    public void addBook(Book book) {
        books.add(book);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public Member findMemberById(int memberId) {
        for (Member member : members) {
            if (member.getMembershipId() == memberId) {
                return member;
            }
        }
        return null;
    }

    public void checkOutBook(Member member, String bookTitle) {
        Book book = findBookByTitle(bookTitle);
        if (book != null && book.isAvailable()) {
            book.checkOut();
            member.borrowBook(book);
            JOptionPane.showMessageDialog(null, "Book checked out successfully!");
        } else if (book != null && !book.isAvailable()) {
            book.addReservation(member);
            JOptionPane.showMessageDialog(null, "Book is currently unavailable. You have been added to the reservation queue.");
        } else {
            JOptionPane.showMessageDialog(null, "Book not found.");
        }
    }

    public void returnBook(Member member, String bookTitle) {
        Book book = findBookByTitle(bookTitle);
        if (book != null && member.getBorrowedBooks().contains(book)) {
            member.returnBook(book);
            book.returnBook();
            JOptionPane.showMessageDialog(null, "Book returned successfully!");
            if (!book.getReservationQueue().isEmpty()) {
                Member nextMember = book.getReservationQueue().peek();
                JOptionPane.showMessageDialog(null, "Notifying " + nextMember.getName() + " that the book is now available.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "You haven't borrowed this book.");
        }
    }

    public void renewBook(Member member, String bookTitle) {
        Book book = findBookByTitle(bookTitle);
        if (book != null && member.getBorrowedBooks().contains(book)) {
            book.renew();
            JOptionPane.showMessageDialog(null, "Book renewed successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "You haven't borrowed this book.");
        }
    }

    public double calculateLateFee(Book book) {
        if (book.getDueDate() == null) return 0;
        long diff = new Date().getTime() - book.getDueDate().getTime();
        long daysLate = diff / (1000 * 60 * 60 * 24);
        return daysLate > 0 ? daysLate * 0.5 : 0; // $0.5 per day late
    }
}

public class LibrarySystemGUI {
    private JFrame frame;
    private JTextField bookTitleField;
    private JTextField memberIdField;
    private JTextArea textArea;
    private LibrarySystem librarySystem;

    public LibrarySystemGUI() {
        librarySystem = new LibrarySystem();
        librarySystem.addBook(new Book("Java Programming", "John Doe"));
        librarySystem.addBook(new Book("Data Structures", "Jane Smith"));
        librarySystem.addMember(new Member("Alice", 101));
        librarySystem.addMember(new Member("Bob", 102));

        // Setup GUI
        frame = new JFrame("Library System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout());

        // Panel for input fields
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("Book Title:"));
        bookTitleField = new JTextField(20);
        panel.add(bookTitleField);

        panel.add(new JLabel("Member ID:"));
        memberIdField = new JTextField(10);
        panel.add(memberIdField);

        JButton checkOutButton = new JButton("Check Out Book");
        checkOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookTitle = bookTitleField.getText();
                int memberId = Integer.parseInt(memberIdField.getText());
                Member member = librarySystem.findMemberById(memberId);
                if (member != null) {
                    librarySystem.checkOutBook(member, bookTitle);
                    updateLog("Checked out book: " + bookTitle);
                } else {
                    JOptionPane.showMessageDialog(null, "Member not found.");
                }
            }
        });
        panel.add(checkOutButton);

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookTitle = bookTitleField.getText();
                int memberId = Integer.parseInt(memberIdField.getText());
                Member member = librarySystem.findMemberById(memberId);
                if (member != null) {
                    librarySystem.returnBook(member, bookTitle);
                    updateLog("Returned book: " + bookTitle);
                } else {
                    JOptionPane.showMessageDialog(null, "Member not found.");
                }
            }
        });
        panel.add(returnBookButton);

        JButton renewBookButton = new JButton("Renew Book");
        renewBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookTitle = bookTitleField.getText();
                int memberId = Integer.parseInt(memberIdField.getText());
                Member member = librarySystem.findMemberById(memberId);
                if (member != null) {
                    librarySystem.renewBook(member, bookTitle);
                    updateLog("Renewed book: " + bookTitle);
                } else {
                    JOptionPane.showMessageDialog(null, "Member not found.");
                }
            }
        });
        panel.add(renewBookButton);

        // Text area to show logs or messages
        textArea = new JTextArea(10, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Add components to frame
        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public void updateLog(String message) {
        textArea.append(message + "\n");
    }

    public static void main(String[] args) {
        new LibrarySystemGUI();
    }
}
