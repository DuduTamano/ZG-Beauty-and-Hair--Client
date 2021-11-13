package com.example.zgbeautyandhair.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.zgbeautyandhair.Database.CartItem;
import com.google.firebase.Timestamp;

import java.io.Serializable;

import java.util.List;

public class Order implements Parcelable {
    String userName, UserPhone, UserAddress, shippingAddress, comment, orderId, shippingOtherAddress;
    String salonId, salonName, salonAddress, salonPhone;
    String totalPayment;
    String productLength;
    List<CartItem> cartItemList;
    long createDate;
    String orderNumber;
    int orderStatus;
    String cardNumber, cardHoldName, cardValidity;
    Timestamp timestamp;

    public Order() {
    }

    protected Order(Parcel in) {
        userName = in.readString();
        UserPhone = in.readString();
        UserAddress = in.readString();
        shippingAddress = in.readString();
        comment = in.readString();
        orderId = in.readString();
        salonId = in.readString();
        salonName = in.readString();
        salonAddress = in.readString();
        salonPhone = in.readString();
        totalPayment = in.readString();
        createDate = in.readLong();
        orderNumber = in.readString();
        orderStatus = in.readInt();
        cardNumber = in.readString();
        cardValidity = in.readString();
        cardHoldName = in.readString();
        shippingOtherAddress = in.readString();
        productLength = in.readString();
        timestamp = in.readParcelable( Timestamp.class.getClassLoader() );

    }

    public String getProductLength() {
        return productLength;
    }

    public void setProductLength(String productLength) {
        this.productLength = productLength;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order( in );
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserAddress() {
        return UserAddress;
    }

    public void setUserAddress(String userAddress) {
        UserAddress = userAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public void setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
    }

    public String getSalonPhone() {
        return salonPhone;
    }

    public void setSalonPhone(String salonPhone) {
        this.salonPhone = salonPhone;
    }

    public String getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHoldName() {
        return cardHoldName;
    }

    public void setCardHoldName(String cardHoldName) {
        this.cardHoldName = cardHoldName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getShippingOtherAddress() {
        return shippingOtherAddress;
    }

    public void setShippingOtherAddress(String shippingOtherAddress) {
        this.shippingOtherAddress = shippingOtherAddress;
    }

    public String getCardValidity() {
        return cardValidity;
    }

    public void setCardValidity(String cardValidity) {
        this.cardValidity = cardValidity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( userName );
        dest.writeString( UserPhone );
        dest.writeString( UserAddress );
        dest.writeString( shippingAddress );
        dest.writeString( comment );
        dest.writeString( orderId );
        dest.writeString( salonId );
        dest.writeString( salonName );
        dest.writeString( salonAddress );
        dest.writeString( salonPhone );
        dest.writeString( totalPayment );
        dest.writeLong( createDate );
        dest.writeString( orderNumber );
        dest.writeString(cardValidity);
        dest.writeInt( orderStatus );
        dest.writeString( cardNumber );
        dest.writeString( cardHoldName );
        dest.writeString( shippingOtherAddress );
        dest.writeParcelable( timestamp, flags );
        dest.writeString( productLength );

    }
}
