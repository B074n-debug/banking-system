CREATE TABLE IF NOT EXISTS customers (
                                         customer_id TEXT PRIMARY KEY,
                                         first_name  TEXT NOT NULL,
                                         last_name   TEXT NOT NULL,
                                         address     TEXT,
                                         phone       TEXT,
                                         username    TEXT UNIQUE NOT NULL,
                                         password    TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
                                        account_number   TEXT PRIMARY KEY,
                                        customer_id      TEXT NOT NULL,
                                        account_type     TEXT NOT NULL,
                                        balance          REAL NOT NULL,
                                        branch           TEXT NOT NULL,
                                        date_opened      TEXT NOT NULL,
                                        interest_rate    REAL NOT NULL,
                                        company          TEXT,
                                        company_address  TEXT,
                                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id   TEXT PRIMARY KEY,
                                            account_number   TEXT NOT NULL,
                                            transaction_type TEXT NOT NULL,
                                            amount           REAL NOT NULL,
                                            balance_after    REAL NOT NULL,
                                            timestamp        TEXT NOT NULL,
                                            FOREIGN KEY (account_number) REFERENCES accounts(account_number)
    );

