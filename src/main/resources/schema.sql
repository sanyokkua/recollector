CREATE TABLE IF NOT EXISTS users
(
    user_id
    SERIAL
    PRIMARY
    KEY,
    email
    VARCHAR
    UNIQUE
    NOT
    NULL
(
    255
) UNIQUE NOT NULL,
    password_hash VARCHAR
(
    255
) NOT NULL,
    reset_token VARCHAR
(
    255
),
    reset_token_expiry TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS categories
(
    category_id
    SERIAL
    PRIMARY
    KEY,
    user_id
    INTEGER
    REFERENCES
    users
(
    user_id
),
    category_name VARCHAR
(
    255
) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS items
(
    item_id
    SERIAL
    PRIMARY
    KEY,
    category_id
    INTEGER
    REFERENCES
    categories
(
    category_id
),
    item_name VARCHAR
(
    255
) NOT NULL,
    item_status VARCHAR
(
    50
) NOT NULL,
    item_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS custom_fields
(
    field_id
    SERIAL
    PRIMARY
    KEY,
    item_id
    INTEGER
    REFERENCES
    items
(
    item_id
),
    field_name VARCHAR
(
    255
) NOT NULL,
    field_value TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
