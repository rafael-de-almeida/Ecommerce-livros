CREATE TABLE log_transacao (
                               id SERIAL PRIMARY KEY,
                               tabela_nome TEXT NOT NULL,
                               operacao TEXT NOT NULL, -- INSERT ou UPDATE
                               data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               usuario TEXT DEFAULT CURRENT_USER,
                               dados_anteriores JSONB,
                               dados_novos JSONB
);