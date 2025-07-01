create table grupos_precificacao
(
	gru_id integer default nextval('grupos_precificacao_grp_id_seq'::regclass) not null
		constraint pk_11
			primary key,
	gru_nome varchar(50) not null,
	gru_margem_lucro numeric(5,2) not null
);

create table clientes
(
	cli_id integer generated always as identity
		constraint pk_1
			primary key,
	cli_nome varchar(255) not null,
	cli_nascimento date not null,
	cli_genero varchar(255) not null,
	cli_idade integer not null,
	cli_senha varchar(255) not null,
	cli_cpf varchar(255) not null,
	cli_email varchar(255) not null,
	cli_telefone varchar(255) not null,
	cli_status varchar(255) not null
);

create table categorias
(
	cat_id integer default nextval('categorias_cat_id_seq'::regclass) not null
		constraint pk_8
			primary key,
	cat_nome varchar(50) not null
);

create table cartoes
(
	car_id integer generated always as identity
		constraint pk_2
			primary key,
	car_numero varchar(255) not null,
	car_bandeira varchar(255) not null,
	car_codigo varchar(255) not null,
	car_nome varchar(255) not null,
	car_vencimento date not null,
	car_status varchar(255) not null,
	cli_id integer not null
		constraint fk_1
			references clientes
);

create table enderecos
(
	end_id integer generated always as identity
		constraint pk_3
			primary key,
	end_cep varchar(255) not null,
	end_rua varchar(255) not null,
	end_numero varchar(255) not null,
	end_complemento varchar(255) not null,
	end_cidade varchar(255) not null,
	end_bairro varchar(255) not null,
	end_estado varchar(255) not null,
	end_status varchar(255) not null,
	cli_id integer not null
		constraint fk_1
			references clientes
);

create table livros
(
	liv_id bigint default nextval('livros_liv_id_seq'::regclass) not null
		constraint pk_6
			primary key,
	liv_autor varchar(255) not null,
	liv_ano date not null,
	liv_titulo varchar(255) not null,
	liv_editora varchar(255) not null,
	liv_edicao varchar(255) not null,
	liv_isbn varchar(255) not null,
	liv_qtd_paginas integer not null,
	liv_sinopse text not null,
	liv_dimensao varchar(255) not null,
	gru_id integer not null
		constraint fk_1
			references grupos_precificacao,
	liv_imagem varchar(255),
	liv_venda varchar(255),
	liv_cod_barras varchar(255)
);

create table livros_categorias
(
	cat_id integer not null
		constraint fk_1
			references categorias,
	liv_id bigint not null
		constraint fk_2
			references livros,
	constraint pk_9
		primary key (cat_id, liv_id)
);

create table ordem
(
	ord_id bigint default nextval('ordem_ord_id_seq'::regclass) not null
		constraint pk_5
			primary key,
	ord_preco_total numeric(38,2) not null,
	ord_status varchar(255) not null,
	ord_data date not null,
	end_id integer not null
		constraint fk_1
			references enderecos,
	cli_id integer not null
		constraint fk_2
			references clientes
);

create table ordem_livros
(
	ord_liv_id bigint default nextval('ordem_livros_ol_id_seq'::regclass) not null
		constraint pk_7
			primary key,
	ord_liv_qtd integer not null,
	ord_liv_pre numeric(38,2) not null,
	ord_id bigint not null
		constraint fk_1
			references ordem
		constraint fk_ordem_livros_ordem
			references ordem
				on delete cascade,
	liv_id bigint not null
		constraint fk_2
			references livros
);

create table pagamento
(
	pag_id integer default nextval('pagamento_pag_id_seq'::regclass) not null
		constraint pk_4
			primary key,
	pag_status varchar(255) not null,
	ord_id bigint not null
		constraint fk_1
			references ordem,
	car_id integer not null
		constraint fk_2
			references cartoes
);

create table cupom
(
	id bigint default nextval('seq_cupom_id'::regclass) not null
		primary key,
	codigo varchar(255) not null
		unique,
	valor numeric(38,2) not null,
	tipo varchar(255) not null,
	troca boolean default false,
	validade date not null,
	uso_maximo integer default 1,
	usado integer default 0,
	cliente_id integer
		constraint fk_cupom_cliente
			references clientes,
	origem_troca_ord_id bigint
		constraint fkgmgy8pf2hj6b9jnd8ur6e8ty7
			references ordem
);

create table estoque
(
	id_estoque serial
		primary key,
	id_livro bigint not null
		constraint fk_livro
			references livros,
	data_entrada date not null,
	fornecedor varchar(255),
	quantidade integer,
	valor_de_custo numeric(10,2)
);


