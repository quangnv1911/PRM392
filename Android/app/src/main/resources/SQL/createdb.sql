create table feedback
(
    id             bigint auto_increment
        primary key,
    number_dislike int          not null,
    number_like    int          not null,
    user_id        bigint       not null,
    value          varchar(255) null,
    product_id     bigint       not null
);

create table flipper
(
    id         bigint auto_increment
        primary key,
    image_link varchar(255) null
);

create table product
(
    id             bigint auto_increment
        primary key,
    description    varchar(255) null,
    image_link     varchar(255) null,
    name           varchar(255) null,
    price          double       null,
    type           varchar(255) null,
    purchase_count int          not null,
    rate           double       not null
);

create table product_detail
(
    id            bigint auto_increment
        primary key,
    image_product varchar(255) null,
    product_id    bigint       null
);

create table users
(
    id         bigint auto_increment
        primary key,
    password   varchar(255) null,
    username   varchar(255) not null,
    full_name  varchar(255) null,
    image_link varchar(255) null,
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username)
);

