-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-09-29 19:42:27.766
USE master;
CREATE DATABASE ClothesShop;
USE ClothesShop;

-- Table: Account
CREATE TABLE Account (
    accountId INT NOT NULL,
    fullname NVARCHAR(50) NOT NULL,
    phone INT NOT NULL,
    address NVARCHAR(254) NOT NULL,
    image NVARCHAR(254) NOT NULL,
    createdBy INT NOT NULL,
    createdOn DATE NOT NULL,
    modifiedBy INT NOT NULL,
    modifiedOn DATE NOT NULL,
    roleId INT NOT NULL,
    CONSTRAINT Account_pk PRIMARY KEY (accountId)
);

-- Table: Cart
CREATE TABLE Cart (
    cartId INT NOT NULL,
    quantity INT NOT NULL,
    accountId INT NOT NULL,
    productId INT NOT NULL,
    CONSTRAINT Cart_pk PRIMARY KEY (cartId)
);

-- Table: Categorie
CREATE TABLE Categorie (
    categoryId INT NOT NULL,
    categoryName NVARCHAR(254) NOT NULL,
    CONSTRAINT Categorie_pk PRIMARY KEY (categoryId)
);

-- Table: Comment
CREATE TABLE Comment (
    commentId INT NOT NULL,
    content NVARCHAR(254) NOT NULL,
    dateCreated DATE NOT NULL,
    accountId INT NOT NULL,
    productId INT NOT NULL,
    CONSTRAINT Comment_pk PRIMARY KEY (commentId)
);

-- Table: Coupon
CREATE TABLE Coupon (
    couponId INT NOT NULL,
    couponCode VARCHAR(50) NOT NULL,
    discountId INT NOT NULL,
    discountValue INT NOT NULL,
    minOrderValue INT NOT NULL,
    maxOrderValue INT NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    usageLimit INT NOT NULL,
    usageCount INT NOT NULL,
    isActive BIT NOT NULL,
    createdBy INT NOT NULL,
    CONSTRAINT Coupon_pk PRIMARY KEY (couponId)
);

-- Table: CouponType
CREATE TABLE CouponType (
    discountId INT NOT NULL,
    discountType VARCHAR(50) NOT NULL,
    CONSTRAINT CouponType_pk PRIMARY KEY (discountId)
);

-- Table: GroupChat
CREATE TABLE GroupChat (
    groupchatId INT NOT NULL,
    user1 INT NOT NULL,
    user2 INT NOT NULL,
    CONSTRAINT GroupChat_pk PRIMARY KEY (groupchatId)
);

-- Table: Message
CREATE TABLE Message (
    messageId INT NOT NULL,
    content NVARCHAR(254) NOT NULL,
    createDate DATE NOT NULL,
    groupchatId INT NOT NULL,
    sendBy INT NOT NULL,
    CONSTRAINT Message_pk PRIMARY KEY (messageId)
);

-- Table: Orders
CREATE TABLE Orders (
    orderId INT NOT NULL,
    orderDate DATE NOT NULL,
    totalAmount INT NOT NULL,
    status NVARCHAR(50) NOT NULL,
    accountId INT NOT NULL,
    invoice INT NOT NULL,
    CONSTRAINT Orders_pk PRIMARY KEY (orderId)
);

-- Table: OrderDetail
CREATE TABLE OrderDetail (
    orderDetailId INT NOT NULL,
    quantity INT NOT NULL,
    unitPrice INT NOT NULL,
    orderId INT NOT NULL,
    productId INT NOT NULL,
    couponId INT NOT NULL,
    CONSTRAINT OrderDetail_pk PRIMARY KEY (orderDetailId)
);

-- Table: Product
CREATE TABLE Product (
    productId INT NOT NULL,
    productName NVARCHAR(50) NOT NULL,
    image NVARCHAR(254) NOT NULL,
    price INT NOT NULL,
    stockQuantity INT NOT NULL,
    categoryId INT NOT NULL,
    createdBy INT NOT NULL,
    CONSTRAINT Product_pk PRIMARY KEY (productId)
);

-- Table: Role
CREATE TABLE Role (
    roleId INT NOT NULL,
    roleName VARCHAR(50) NOT NULL,
    CONSTRAINT Role_pk PRIMARY KEY (roleId)
);

-- Foreign keys
-- Reference: Account_Role (table: Account)
ALTER TABLE Account ADD CONSTRAINT Account_Role FOREIGN KEY (roleId)
    REFERENCES Role (roleId);

-- Reference: Cart_Customer (table: Cart)
ALTER TABLE Cart ADD CONSTRAINT Cart_Customer FOREIGN KEY (accountId)
    REFERENCES Account (accountId);

-- Reference: Cart_Product (table: Cart)
ALTER TABLE Cart ADD CONSTRAINT Cart_Product FOREIGN KEY (productId)
    REFERENCES Product (productId);

-- Reference: Comment_Customer (table: Comment)
ALTER TABLE Comment ADD CONSTRAINT Comment_Customer FOREIGN KEY (accountId)
    REFERENCES Account (accountId);

-- Reference: Comment_Product (table: Comment)
ALTER TABLE Comment ADD CONSTRAINT Comment_Product FOREIGN KEY (productId)
    REFERENCES Product (productId);

-- Reference: Coupon_CouponType (table: Coupon)
ALTER TABLE Coupon ADD CONSTRAINT Coupon_CouponType FOREIGN KEY (discountId)
    REFERENCES CouponType (discountId);

-- Reference: GroupChat_Account (table: GroupChat)
ALTER TABLE GroupChat ADD CONSTRAINT GroupChat_Account FOREIGN KEY (user1)
    REFERENCES Account (accountId);

-- Reference: GroupChat_Account1 (table: GroupChat)
ALTER TABLE GroupChat ADD CONSTRAINT GroupChat_Account1 FOREIGN KEY (user2)
    REFERENCES Account (accountId);

-- Reference: Message_GroupChat (table: Message)
ALTER TABLE Message ADD CONSTRAINT Message_GroupChat FOREIGN KEY (groupchatId)
    REFERENCES GroupChat (groupchatId);

-- Reference: OrderDetail_Coupon (table: OrderDetail)
ALTER TABLE OrderDetail ADD CONSTRAINT OrderDetail_Coupon FOREIGN KEY (couponId)
    REFERENCES Coupon (couponId);

-- Reference: OrderDetails_Order (table: OrderDetail)
ALTER TABLE OrderDetail ADD CONSTRAINT OrderDetails_Order FOREIGN KEY (orderId)
    REFERENCES Orders (orderId);

-- Reference: OrderDetails_Product (table: OrderDetail)
ALTER TABLE OrderDetail ADD CONSTRAINT OrderDetails_Product FOREIGN KEY (productId)
    REFERENCES Product (productId);

-- Reference: Order_Customer (table: Orders)
ALTER TABLE Orders ADD CONSTRAINT Order_Customer FOREIGN KEY (accountId)
    REFERENCES Account (accountId);

-- Reference: Product_Categorie (table: Product)
ALTER TABLE Product ADD CONSTRAINT Product_Categorie FOREIGN KEY (categoryId)
    REFERENCES Categorie (categoryId);

-- End of file.
