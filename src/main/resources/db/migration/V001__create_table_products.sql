create table products
(
    id          serial primary key,
    name        varchar(90) not null,
    price       int,
    in_stock    boolean
);