-- Create schema
CREATE SCHEMA "recollector";

-- Create users table
CREATE TABLE "recollector"."users" (
    "user_id"            serial PRIMARY KEY,
    "email"              VARCHAR(255) NOT NULL UNIQUE,
    "password_hash"      VARCHAR(255) NOT NULL,
    "reset_token"        VARCHAR(255),
    "reset_token_expiry" timestamptz,
    "last_login"         timestamptz,
    "created_at"         timestamptz  NOT NULL,
    "updated_at"         timestamptz  NOT NULL
                                   );

-- Create categories table
CREATE TABLE "recollector"."categories" (
    "category_id"   serial PRIMARY KEY,
    "user_id"       INTEGER      NOT NULL REFERENCES "recollector"."users" ("user_id") ON DELETE CASCADE,
    "category_name" VARCHAR(255) NOT NULL,
    "created_at"    timestamptz  NOT NULL,
    "updated_at"    timestamptz  NOT NULL
                                        );

-- Create items table
CREATE TABLE "recollector"."items" (
    "item_id"     serial PRIMARY KEY,
    "category_id" INTEGER      NOT NULL REFERENCES "recollector"."categories" ("category_id") ON DELETE CASCADE,
    "item_name"   VARCHAR(255) NOT NULL,
    "item_status" VARCHAR(50)  NOT NULL,
    "item_notes"  text,
    "created_at"  timestamptz  NOT NULL,
    "updated_at"  timestamptz  NOT NULL
                                   );

CREATE UNIQUE INDEX "idx_category_name_user_id" ON "recollector"."categories" ("category_name", "user_id");
CREATE UNIQUE INDEX "idx_item_name_category_id" ON "recollector"."items" ("item_name", "category_id");