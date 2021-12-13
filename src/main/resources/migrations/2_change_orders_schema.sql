DROP TABLE "product_in_cart";

DROP TABLE "orders";

CREATE TABLE "orders" (
    "id" SERIAL PRIMARY KEY,
    "user_id" INT REFERENCES "users" ("id"),
    "amount" INT NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "phone" VARCHAR(255) NOT NULL,
    "payment" VARCHAR(255) NOT NULL,
    "status" VARCHAR (255) NOT NULL DEFAULT 'in progress'
);

CREATE TABLE "product_in_order" (
    "order_id" INT PRIMARY KEY REFERENCES "orders" ("id"),
     "id" INT REFERENCES "products" ("id"),
     "title" VARCHAR(255) NOT NULL UNIQUE,
     "size" VARCHAR(255),
     "color" VARCHAR(255),
     "price" INT NOT NULL,
     "quantity" INT NOT NULL
);

