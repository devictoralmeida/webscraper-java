CREATE SEQUENCE IF NOT EXISTS public.tb_news_id_seq
  INCREMENT BY 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START WITH 1
  CACHE 1
  NO CYCLE;

CREATE SEQUENCE IF NOT EXISTS public.tb_author_id_seq
  INCREMENT BY 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START WITH 1
  CACHE 1
  NO CYCLE;


CREATE TABLE tb_news
(
    id         BIGSERIAL PRIMARY KEY,
    url        TEXT UNIQUE  NOT NULL,
    title      VARCHAR(150) NOT NULL,
    subtitle   VARCHAR(255) NULL DEFAULT NULL,
    author_id  BIGSERIAL NULL DEFAULT NULL REFERENCES tb_author (id) ON DELETE SET NULL,
    content    TEXT         NOT NULL,
    publish_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_author
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_news_url ON tb_news (url);

-- índice por timestamp range + author_id (bom para range queries por hora)
CREATE INDEX idx_news_publish_at_author
    ON tb_news (publish_at, author_id);

-- Índice para buscar notícias de um author ordenadas por publish_at
CREATE INDEX idx_news_author_publish_at
    ON tb_news (author_id, publish_at);

-- 4) Incluir colunas para permitir index-only-scan (titulo, url, id)
CREATE INDEX idx_news_author_publish_at_inc
    ON tb_news (author_id, publish_at) INCLUDE (id, title, url);