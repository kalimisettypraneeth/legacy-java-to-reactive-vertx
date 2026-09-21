CREATE TABLE IF NOT EXISTS work_items (
    id BIGINT PRIMARY KEY,
    payload TEXT NOT NULL
);

INSERT INTO work_items (id, payload)
VALUES (1, 'benchmark-fixture')
ON CONFLICT (id) DO NOTHING;
