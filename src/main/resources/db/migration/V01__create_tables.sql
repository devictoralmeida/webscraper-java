CREATE SEQUENCE IF NOT EXISTS public.tb_news_id_seq
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
    author     VARCHAR(255) NULL DEFAULT NULL,
    content    TEXT         NOT NULL,
    publish_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_news_url ON tb_news (url);