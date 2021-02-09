package edu.yonatan.myshoppingcart;

public class ShoppingItem {


    private String name;
    private String description;
    private String date;
    private String quantity;
    private boolean checked;


    public ShoppingItem(String name, String description, String date, String quantity, boolean checked) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.quantity = quantity;
        this.checked = checked;
    }

    public ShoppingItem() {
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return "ShoppingItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
