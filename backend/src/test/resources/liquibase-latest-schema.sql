-- Create schema
CREATE SCHEMA "recollector";

-- Create users table
CREATE TABLE "recollector"."users" (
    "user_id" bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
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
    "category_id" bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "user_id"     bigint NOT NULL REFERENCES "recollector"."users" ("user_id") ON DELETE CASCADE,
    "category_name" VARCHAR(255) NOT NULL,
    "created_at"    timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"    timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        );

-- Create items table
CREATE TABLE "recollector"."items" (
    "item_id"     bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "category_id" bigint NOT NULL REFERENCES "recollector"."categories" ("category_id") ON DELETE CASCADE,
    "item_name"   VARCHAR(255) NOT NULL,
    "item_status" VARCHAR(50)  NOT NULL CHECK ("item_status" IN ('FINISHED', 'IN_PROGRESS', 'TODO_LATER')),
    "item_notes"  text,
    "created_at"  timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"  timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
                                   );

CREATE UNIQUE INDEX "idx_category_name_user_id" ON "recollector"."categories" ("category_name", "user_id");
CREATE UNIQUE INDEX "idx_item_name_category_id" ON "recollector"."items" ("item_name", "category_id");

CREATE
OR REPLACE VIEW "recollector"."category_item_counts" AS
SELECT "c"."category_id",
       "c"."user_id",
       "c"."category_name",
       "c"."created_at",
       "c"."updated_at",
       -- Count of items with status TODO_LATER
       COUNT(CASE WHEN "i"."item_status" = 'TODO_LATER' THEN 1 END)  AS "count_todo_later",
       -- Count of items with status IN_PROGRESS
       COUNT(CASE WHEN "i"."item_status" = 'IN_PROGRESS' THEN 1 END) AS "count_in_progress",
       -- Count of items with status FINISHED
       COUNT(CASE WHEN "i"."item_status" = 'FINISHED' THEN 1 END)    AS "count_finished"
FROM "recollector"."categories" "c"
         -- Left join with items to include categories even with no items
         LEFT JOIN "recollector"."items" "i" ON "c"."category_id" = "i"."category_id"
GROUP BY "c"."category_id",
         "c"."user_id",
         "c"."category_name",
         "c"."created_at",
         "c"."updated_at";

-- Create user_settings table
CREATE TABLE "recollector"."user_settings" (
    "setting_id"                bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "user_id"                   bigint     NOT NULL UNIQUE REFERENCES "recollector"."users" ("user_id") ON DELETE CASCADE,

    -- Settings for categories
    "category_background_color" VARCHAR(7) NOT NULL,
    "category_item_color"       VARCHAR(7) NOT NULL,
    "category_fab_color"        VARCHAR(7) NOT NULL,
    "category_page_size"        INTEGER     NOT NULL,

    -- Settings for items
    "item_background_color"     VARCHAR(7) NOT NULL,
    "item_item_color"           VARCHAR(7) NOT NULL,
    "item_fab_color"            VARCHAR(7) NOT NULL,
    "item_page_size"            INTEGER     NOT NULL,

    "created_at"                timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"                timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
                                           );

-- Create the invalidated_tokens table
CREATE TABLE "recollector"."invalidated_tokens" (
    "token_id"       bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "user_id"        bigint        NOT NULL REFERENCES "recollector"."users" ("user_id") ON DELETE CASCADE,
    "token"          VARCHAR(1024) NOT NULL,                               -- Store JWT token as a string (may need to adjust length)
    "invalidated_at" timestamptz   NOT NULL DEFAULT CURRENT_TIMESTAMP,     -- Timestamp of when token was invalidated
    "expires_at"     timestamptz   NOT NULL,                               -- Timestamp when the token is set to expire

    CONSTRAINT "unique_invalid_token_per_user" UNIQUE ("user_id", "token") -- Ensure unique tokens per user
                                                );

-- Create an index on the expires_at column for efficient deletion
CREATE INDEX "idx_invalidated_tokens_expires_at" ON "recollector"."invalidated_tokens" ("expires_at");