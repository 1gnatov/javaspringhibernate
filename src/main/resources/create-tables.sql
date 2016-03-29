CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    nickname VARCHAR (128),
    credits INTEGER,
    premium INTEGER
);
CREATE TABLE bills (
    bill_id SERIAL PRIMARY KEY,
    title VARCHAR (128),
    credits INTEGER
);