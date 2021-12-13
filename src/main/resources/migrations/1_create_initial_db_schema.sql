CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "username" VARCHAR(255) NOT NULL UNIQUE,
    "email" VARCHAR(255) NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    "role" VARCHAR(255) DEFAULT 'user' NOT NULL,
    "first_name" VARCHAR(255) NOT NULL,
    "last_name" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now()
);

CREATE TABLE "products" (
    "id" SERIAL PRIMARY KEY,
    "title" VARCHAR(255) NOT NULL UNIQUE,
    "description" VARCHAR(255) NOT NULL,
    "image" VARCHAR(255) NOT NULL,
    "categories" VARCHAR[],
    "size" VARCHAR[],
    "color" VARCHAR[],
    "price" INT NOT NULL,
    "in_stock" BOOLEAN DEFAULT TRUE
);

CREATE TABLE "product_in_cart" (
    "user_id" INT PRIMARY KEY REFERENCES "users" ("id"),
    "product_id" INT NOT NULL REFERENCES "products" ("id"),
    "quantity" INT NOT NULL,
    "status" VARCHAR(255) NOT NULL
);

CREATE TABLE "orders" (
    "user_id" INT PRIMARY KEY REFERENCES "users" ("id"),
    "amount" INT NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "status" VARCHAR (255) NOT NULL
);

CREATE TABLE "tokens" (
    "user_id" INT PRIMARY KEY REFERENCES "users" ("id"),
    "token" VARCHAR(255) NOT NULL
);