CREATE TABLE IF NOT EXISTS data_table (
    id SERIAL PRIMARY KEY,
    row_number INTEGER NOT NULL,
    value VARCHAR(255) NOT NULL
);
