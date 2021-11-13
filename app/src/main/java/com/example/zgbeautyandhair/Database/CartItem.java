package com.example.zgbeautyandhair.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cart")
public class CartItem {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "productId")
    String productId;

    @ColumnInfo(name = "productName")
    String productName;

    @ColumnInfo(name = "productImage")
    String productImage;

    @ColumnInfo(name = "productPrice")
    Long productPrice;

    @ColumnInfo(name = "productQuantity")
    int productQuantity;

    @ColumnInfo(name = "productSize")
    int productSize;

    @ColumnInfo(name = "productTotalPrice")
    float productTotalPrice;

    @ColumnInfo(name = "userPhone")
    String userPhone;

    @ColumnInfo(name = "productLength")
    String productLength;

    //Getter & Setter


    public String getProductLength() {
        return productLength;
    }

    public void setProductLength(String productLength) {
        this.productLength = productLength;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getProductSize() {
        return productSize;
    }

    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }

    public Float getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(Float productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }
}
