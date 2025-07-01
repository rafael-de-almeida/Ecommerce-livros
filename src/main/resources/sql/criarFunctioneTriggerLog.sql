CREATE OR REPLACE FUNCTION fn_log_transacao()
    RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO log_transacao (tabela_nome, operacao, dados_novos)
        VALUES (TG_TABLE_NAME, TG_OP, to_jsonb(NEW));
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO log_transacao (tabela_nome, operacao, dados_anteriores, dados_novos)
        VALUES (TG_TABLE_NAME, TG_OP, to_jsonb(OLD), to_jsonb(NEW));
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Triggers para cada tabela
CREATE TRIGGER trg_log_grupos_precificacao
    AFTER INSERT OR UPDATE ON grupos_precificacao
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_clientes
    AFTER INSERT OR UPDATE ON clientes
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_categorias
    AFTER INSERT OR UPDATE ON categorias
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_cartoes
    AFTER INSERT OR UPDATE ON cartoes
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_enderecos
    AFTER INSERT OR UPDATE ON enderecos
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_livros
    AFTER INSERT OR UPDATE ON livros
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

-- livros_categorias é uma tabela de junção, normalmente são INSERTs e DELETEs, mas manteremos INSERT e UPDATE
CREATE TRIGGER trg_log_livros_categorias
    AFTER INSERT OR UPDATE ON livros_categorias
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_ordem
    AFTER INSERT OR UPDATE ON ordem
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_ordem_livros
    AFTER INSERT OR UPDATE ON ordem_livros
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_pagamento
    AFTER INSERT OR UPDATE ON pagamento
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_cupom
    AFTER INSERT OR UPDATE ON cupom
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();

CREATE TRIGGER trg_log_estoque
    AFTER INSERT OR UPDATE ON estoque
    FOR EACH ROW EXECUTE FUNCTION fn_log_transacao();
