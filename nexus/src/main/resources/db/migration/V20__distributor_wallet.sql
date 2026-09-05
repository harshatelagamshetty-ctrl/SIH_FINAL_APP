-- Distributor virtual wallet. Deliberately simulated — no real
-- payment provider is connected, per direct instruction. The
-- minimum-balance bidding gate is real; the funding mechanism is
-- not.
CREATE TABLE wallets (
                         id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         user_id     BIGINT NOT NULL REFERENCES users (id),
                         balance     DECIMAL(12,2) NOT NULL DEFAULT 0,
                         updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
                         CONSTRAINT uq_wallet_user UNIQUE (user_id)
);
