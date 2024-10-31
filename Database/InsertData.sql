INSERT INTO users (password, username, full_name)
VALUES ('123456', 'admin', 'John Doe'),
       ('123456', 'user2', 'Jane Smith'),
       ('123456', 'user3', 'Alice Johnson'),
       ('123456', 'user4', 'Bob Brown'),
       ('123456', 'user5', 'Charlie Green');

INSERT INTO role (role_id, role_name)
VALUES (1, 'Admin'),
       (2, 'User');

INSERT INTO account (account_id, fullname, phone, address, image, account.created_by, created_on, modified_by,
                     modified_on, role_id, user_id)
VALUES (1, 'John Doe', 1234567890, '123 Main St', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 1, '2024-01-01', 1, '2024-01-01',
        1, 1),
       (2, 'Jane Smith', 2345678901, '456 Elm St', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 2, '2024-01-02', 2, '2024-01-02',
        2, 2),
       (3, 'Alice Johnson', 3456789012, '789 Oak St', 'https://example.com/image3.jpg', 1, '2024-01-03', 1,
        '2024-01-03', 1, 3),
       (4, 'Bob Brown', 4567890123, '101 Pine St', 'https://example.com/image4.jpg', 2, '2024-01-04', 2, '2024-01-04',
        2, 4),
       (5, 'Charlie Green', 5678901234, '202 Maple St', 'https://example.com/image5.jpg', 1, '2024-01-05', 1,
        '2024-01-05', 1, 5);


INSERT INTO categorie (category_id, category_name)
VALUES (1, 'Jean'),
       (2, 'Suit'),
       (3, 'Shoe'),
       (4, 'Vest'),
       (5, 'T-shirt'),
        (6, 'Watch'),
       (7, 'Item'),
       (8, 'Hat'),
       (9, 'Belt'),
       (10, 'Sandal'),
       (11, 'Unisex');

INSERT INTO product (product_id, product_name, image, price, stock_quantity, category_id, image_link, type,
                     purchase_count, rate, created_by)
VALUES (1, 'Phone', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 500, 50, 1, 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 'Jean',
        20, 4.5, 1),
       (2, 'Laptop', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 1500, 20, 1, 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg',
        'Jean', 10, 4.7, 2),
       (3, 'T-Shirt', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 20, 100, 3, 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 'Jean',
        50, 4.0, 3),
       (4, 'Microwave', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 100, 30, 4, 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg',
        'Jean', 15, 4.3, 4),
       (5, 'Basketball', 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 30, 60, 5, 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg',
        'Jean', 25, 4.2, 5);

INSERT INTO product_detail (image_product, product_id)
VALUES ('https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 1),
       ('https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 2),
       ('https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 3),
       ('https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 4),
       ('https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg', 5);


INSERT INTO coupon (id, coupon_code, min_order_value, max_order_value,discount_value , start_date, end_date, usage_limit, usage_count,
                    is_active, created_by, coupon_type)
VALUES (1, 'SAVE10', 1, 10, 5, '2024-01-01', '2024-06-30', 100, 5, 1, 1, 0),
       (2, 'SAVE20', 2, 20, 10, '2024-01-01', '2024-06-30', 50, 10, 1, 2, 1),
       (3, 'NEWUSER', 1, 15, 10, '2024-01-01', '2024-12-31', 100, 0, 1, 3, 1),
       (4, 'BLACKFRIDAY', 1, 50, 20, '2024-11-01', '2024-11-30', 200, 0, 1, 4, 1),
       (5, 'FREESHIP', 2, 5, 0, '2024-01-01', '2024-12-31', 500, 0, 1, 5, 1);

INSERT INTO orders (order_id, order_date, total_quantity, status, account_id, total_price)
VALUES (1, '2024-01-01', 100, 0, 1, 10000),
       (2, '2024-01-02', 200, 1, 2, 20000),
       (3, '2024-01-03', 300, 1, 3, 30000),
       (4, '2024-01-04', 400, 1, 4, 40000),
       (5, '2024-01-05', 500, 1, 5, 50000);

INSERT INTO order_detail (id, quantity, unit_price, order_order_id, product_product_id, coupon_id)
VALUES (1, 2, 50, 1, 1, 1),
       (2, 1, 100, 2, 2, 2),
       (3, 3, 80, 3, 3, 3),
       (4, 1, 400, 4, 4, 4),
       (5, 2, 250, 5, 5, 5);

INSERT INTO flipper (id, image_link)
values (1, 'https://anhvienpiano.com/wp-content/uploads/2018/08/dich-vu-chup-anh-thoi-trang-cho-shop-quan-ao-dep-gia-re.jpg'),
       (2, 'https://watermark.lovepik.com/photo/20211208/large/lovepik-fashionable-womens-clothing-picture_501667331.jpg'),
       (3, 'https://lavenderstudio.com.vn/wp-content/uploads/2021/06/cach-chup-hinh-san-pham-quan-ao-dep-1.jpg');