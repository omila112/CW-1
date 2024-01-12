import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Main {
    private static final String WINDOW_TITLE = "Westminster Shopping Center";
    public static WestminsterShoppingManager shoppingManager = new WestminsterShoppingManager();
    private static List<Product> shoppingCart = new ArrayList<>();

    public static final JPanel mainPanel = new JPanel();
    public static final JPanel headerPanel = new JPanel(new FlowLayout());
    public static final JPanel tablePanel = new JPanel();
    public static final JPanel detailPanel = new JPanel();

    static {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JFrame shoppingCartFrame;

        while (true) {
            System.out.println("Westminster Shopping Manager Menu");
            System.out.println("1. Add a new product");
            System.out.println("2. Delete a product");
            System.out.println("3. Print product list");
            System.out.println("4. Save product list to file");
            System.out.println("5. Load product list from file");
            System.out.println("6. To open GUI Menu");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.next();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case "1":
                    // Add a new product
                    if (shoppingManager.getProductList().size() < 50) {
                        addNewProduct(scanner, shoppingManager);
                        System.out.println("Product added successfully.");
                    } else {
                        System.out.println("Maximum limit of products reached. Cannot add more products.");
                    }
                    break;

                case "2":
                    // Delete a product
                    System.out.print("Enter product ID to delete: ");
                    String deleteProductId = scanner.nextLine();
                    boolean productDeleted = shoppingManager.deleteProductFromSystem(deleteProductId);

                    if (productDeleted) {
                        System.out.println("Product with ID " + deleteProductId + " has been deleted.");
                        System.out.println("Total number of products in the system: " + shoppingManager.getProductList().size());
                    } else {
                        System.out.println("Product with ID " + deleteProductId + " not found.");
                    }
                    break;

                case "3":
                    // Print product list alphabetically
                    System.out.println("List of Products in the System (Ordered Alphabetically):");
                    shoppingManager.printProductListAlphabetically();
                    break;

                case "4":
                    // Save product list to file
                    System.out.print("Enter file name to save: ");
                    String saveFileName = scanner.nextLine();
                    shoppingManager.saveToFile(saveFileName);
                    break;

                case "5":
                    // Load product list from file
                    System.out.print("Enter file name to load: ");
                    String loadFileName = scanner.nextLine();
                    shoppingManager.readFromFile(loadFileName);
                    break;

                case "6":
                    initGui(shoppingManager);
                    break;

                case "7":
                    // Exit
                    System.out.println("Exiting Westminster Shopping Manager. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        }
    }

    private static void addNewProduct(Scanner scanner, WestminsterShoppingManager shoppingManager) {
        System.out.println("Select product type: 1. Electronics 2. Clothing");
        int productType = 0;

        // Validate user input for product type
        while (true) {
            System.out.print("Enter product type (1 for Electronics, 2 for Clothing): ");
            if (scanner.hasNextInt()) {
                productType = scanner.nextInt();
                if (productType == 1 || productType == 2) {
                    break;
                } else {
                    System.out.println("Invalid product type. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Consume invalid input
            }
        }


        String productId = scanner.nextLine();
        while (true) {
            System.out.print("Enter product ID: ");
            productId = scanner.nextLine();

            // Check if the product ID is unique
            final String finalProductId = productId;
            boolean isUnique = shoppingManager.getProductList().stream()
                    .noneMatch(product -> product.getProductId().equals(finalProductId));

            if (isUnique) {
                break; // Valid input, exit the loop
            } else {
                System.out.println("Product ID already exists. Please enter a unique product ID.");
            }
        }
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter available items: ");
        int availableItems = scanner.nextInt();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (productType == 1) {
            System.out.print("Enter brand: ");
            String brand = scanner.nextLine();
            System.out.print("Enter warranty period: ");
            int warrantyPeriod = scanner.nextInt();

            Electronics electronics = new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod);
            shoppingManager.addProductToSystem(electronics);

        } else if (productType == 2) {
            // Clothing
            System.out.print("Enter size: ");
            String size = scanner.nextLine();
            System.out.print("Enter color: ");
            String color = scanner.nextLine();

            Clothing clothing = new Clothing(productId, productName, availableItems, price, size, color);
            shoppingManager.addProductToSystem(clothing);
        }
    }

    private static void initGui(WestminsterShoppingManager shoppingManager) { //referenced from stackoverflow and lecture notes
        if (shoppingManager.hasGui) return;

        JFrame f = new JFrame(WINDOW_TITLE);

        JLabel label1 = new JLabel("Select product category: ");
        String[] choices = {"All", "Electronics", "Clothing"};
        final JComboBox<String> cb = new JComboBox<>(choices);
        f.setSize(800, 600);
        f.setLocation(600, 600);

        headerPanel.add(label1);
        headerPanel.add(cb);
        ProductTableModel tableModel = new ProductTableModel(shoppingManager.getProductList());
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        tablePanel.add(scrollPane);
        detailPanel.add(new JLabel("Selected Product Details:"));

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                Product selectedProduct = shoppingManager.getProductList().get(selectedRow);
                addToShoppingCart(selectedProduct);
            }
        });
        detailPanel.add(addToCartButton);
        mainPanel.add(headerPanel);
        mainPanel.add(tablePanel);
        mainPanel.add(detailPanel);
        f.add(mainPanel);

        //toodo
        cb.addActionListener(e -> {
            String item = (String) cb.getSelectedItem();
            if (item == null) return;

            switch (item.toLowerCase()) {
                case "all": {
                    break;
                }
                case "electronics": {
                    break;
                }
                case "clothing": {
                    break;
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                Product selectedProduct = shoppingManager.getProductList().get(selectedRow);
                updateDetailsPanel(selectedProduct);
            }
        });

        cb.setVisible(true);
        f.setVisible(true);

        shoppingManager.hasGui = true;
    }

    private static void addToShoppingCart(Product product) {
        // Create or update the shopping cart window
        JPopupMenu shoppingCartFrame = new JPopupMenu();
        if (shoppingCartFrame == null || !shoppingCartFrame.isVisible()) {
            createShoppingCartFrame();
        }
        addToShoppingCartWindow(product);
    }

    private static void createShoppingCartFrame() { //referenced from stackoverflow and lecture notes
        JFrame shoppingCartFrame     = new JFrame("Shopping Cart");
        shoppingCartFrame.setSize(400, 300);
        shoppingCartFrame.setLayout(new BorderLayout());
        DefaultTableModel cartTableModel = new DefaultTableModel(new Object[][]{}, new Object[]{"Product", "Quantity", "Price"});
        JTable cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        shoppingCartFrame.add(cartScrollPane, BorderLayout.CENTER);

        shoppingCartFrame.setVisible(true);
    }

    private static void addToShoppingCartWindow(Product product) { //referenced from stackoverflow and lecture notes
        JDialog shoppingCartFrame = new JDialog();
        JTable cartTable = (JTable) ((JScrollPane) shoppingCartFrame.getContentPane().getComponent(0)).getViewport().getView();
        DefaultTableModel cartTableModel = (DefaultTableModel) cartTable.getModel();

        cartTableModel.addRow(new Object[]{product.getProductName(), 1, product.getPrice()});
    }
    private static void updateDetailsPanel(Product selectedProduct) {
        if (selectedProduct != null) {
            Arrays.stream(detailPanel.getComponents())
                    .filter(c -> c instanceof JLabel)
                    .forEach(detailPanel::remove);

            detailPanel.add(new JLabel("Product ID: " + selectedProduct.getProductId()));
            detailPanel.add(new JLabel("Category: " + selectedProduct.getType()));
            detailPanel.add(new JLabel("Name: " + selectedProduct.getProductName()));

            if (selectedProduct instanceof Clothing) {
                Clothing clothing = (Clothing) selectedProduct;
                detailPanel.add(new JLabel("Size: " + clothing.getSize()));
                detailPanel.add(new JLabel("Color: " + clothing.getColor()));
            } else if (selectedProduct instanceof Electronics) {
                Electronics electronics = (Electronics) selectedProduct;
                detailPanel.add(new JLabel("Brand: " + electronics.getBrand()));
                detailPanel.add(new JLabel("Warranty Period: " + electronics.getWarrantyPeriod()));
            }

            detailPanel.add(new JLabel("Item Available: " + selectedProduct.getAvailableItems()));
        }

        detailPanel.revalidate();
    }
}


abstract class Product implements Serializable {
    private String productId;
    private String productName;
    private int availableItems;
    private double price;

    public Product(String productId, String productName, int availableItems, double price) {
        this.productId = productId;
        this.productName = productName;
        this.availableItems = availableItems;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getAvailableItems() {
        return availableItems;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getType();
}

class Electronics extends Product {
    private String brand;
    private int warrantyPeriod;

    public Electronics(String productId, String productName, int availableItems, double price, String brand, int warrantyPeriod) {
        super(productId, productName, availableItems, price);
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }

    public String getBrand() {
        return brand;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    @Override
    public String getType() {
        return "Electronics";
    }
}

class Clothing extends Product {
    private String size;
    private String color;

    public Clothing(String productId, String productName, int availableItems, double price, String size, String color) {
        super(productId, productName, availableItems, price);
        this.size = size;
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getType() {
        return "Clothing";
    }
}

class WestminsterShoppingManager {
    private List<Product> productList;
    public boolean hasGui = false;

    public WestminsterShoppingManager() {
        this.productList = new ArrayList<>();
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void addProductToSystem(Product product) {
        if (productList.size() < 50) {
            productList.add(product);
            System.out.println("Product added to the system.");
        } else {
            System.out.println("Maximum limit of products reached. Cannot add more products.");
        }
    }

    public boolean deleteProductFromSystem(String productId) {
        for (Product product : productList) {
            if (product.getProductId().equals(productId)) {
                productList.remove(product);
                return true;
            }
        }
        return false;
    }

    public void printProductListAlphabetically() {
        Collections.sort(productList, Comparator.comparing(Product::getProductName));

        for (Product product : productList) {
            System.out.println("Product ID: " + product.getProductId());
            System.out.println("Product Name: " + product.getProductName());
            System.out.println("Available Items: " + product.getAvailableItems());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Type: " + product.getType());

            if (product instanceof Electronics) {
                Electronics electronics = (Electronics) product;
                System.out.println("Brand: " + electronics.getBrand());
                System.out.println("Warranty Period: " + electronics.getWarrantyPeriod());
            } else if (product instanceof Clothing) {
                Clothing clothing = (Clothing) product;
                System.out.println("Size: " + clothing.getSize());
                System.out.println("Color: " + clothing.getColor());
            }

            System.out.println("------------");
        }
    }

    public void saveToFile(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(productList);
            System.out.println("Product list saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            productList = (List<Product>) ois.readObject();
            System.out.println("Product list loaded from file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading product list from file: " + e.getMessage());
        }
    }
}

class ProductTableModel extends AbstractTableModel {
    private final List<Product> productList;
    private final String[] columnNames = {"Product ID", "Name", "Category", "Price", "Info"};

    public ProductTableModel(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public int getRowCount() {
        return productList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = productList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return product.getProductId();
            case 1:
                return product.getProductName();
            case 2:
                return product.getType();
            case 3:
                return product.getPrice();
            case 4:
                return getProductInfo(product);
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    private String getProductInfo(Product product) {
        if (product instanceof Electronics) {
            Electronics electronics = (Electronics) product;
            return "Brand: " + electronics.getBrand() + ", Warranty Period in weeks: " + electronics.getWarrantyPeriod();
        } else if (product instanceof Clothing) {
            Clothing clothing = (Clothing) product;
            return "Size: " + clothing.getSize() + ", Color: " + clothing.getColor();
        }
        return "";
    }
}
