CREATE TABLE "categories" (
    "id" SERIAL PRIMARY KEY,
    "image" VARCHAR(255) NOT NULL ,
    "title" VARCHAR(255) NOT NULL UNIQUE
);