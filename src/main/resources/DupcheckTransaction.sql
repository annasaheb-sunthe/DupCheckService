DROP TABLE DUPCHECKTRANSACTIONIF EXISTS;
CREATE TABLE DUPCHECKTRANSACTION(
TRANSACTION_ID		NUMERIC,
TRANSACTION_TYPE	VARCHAR(100),
TRANSACTION_SUB_TYPE	VARCHAR(100),
FIELD_VALUE		VARCHAR(100),
CREATED_ON		TIMESTAMP,
UPDATED_ON		TIMESTAMP
);