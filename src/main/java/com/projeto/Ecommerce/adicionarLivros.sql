UPDATE livros
SET LIV_IMAGEM = '/assets/ItACoisa.jpg'
WHERE LIV_ID = 2;

UPDATE livros
SET LIV_IMAGEM = '/assets/DomCasmurro.jpg'
WHERE LIV_ID = 3;

UPDATE livros
SET LIV_IMAGEM = '/assets/1984.jpg'
WHERE LIV_ID = 4;

UPDATE livros
SET LIV_IMAGEM = '/assets/PequenoPrincipe.jpg'
WHERE LIV_ID = 5;

UPDATE livros
SET LIV_IMAGEM = '/assets/MeninaQueRoubavaLivros.jpg'
WHERE LIV_ID = 6;

UPDATE livros
SET LIV_IMAGEM = '/assets/SolDaMeiaNoite.jpg'
WHERE LIV_ID = 7;

UPDATE livros
SET LIV_IMAGEM = '/assets/SenhorDosAneis.jpg'
WHERE LIV_ID = 10;

UPDATE livros
SET LIV_IMAGEM = '/assets/TorreDoElefante.jpg'
WHERE LIV_ID = 8;

UPDATE livros
SET LIV_IMAGEM = '/assets/PaiRicoPaiPobre.jpeg'
WHERE LIV_ID = 9;

UPDATE livros
SET LIV_IMAGEM = '/assets/HarryPotterPedraFilosofal.jpg'
WHERE LIV_ID = 1;

INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'J.K. Rowling',
             '1997-06-26',
             'Harry Potter e a Pedra Filosofal',
             'Rocco',
             '1ª Edição',
             '978-8532530783',
             224,
             'Harry descobre aos 11 anos que é um bruxo e começa sua jornada em Hogwarts, onde conhece o mundo mágico e enfrenta os primeiros perigos.',
             '14 x 21 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Stephen King',
             '1986-09-15',
             'It: A Coisa',
             'Suma',
             '1ª Edição',
             '978-8556510517',
             1104,
             'Uma força maligna aterrorizando crianças em Derry, Maine. “A Coisa” assume formas diversas e retorna a cada 27 anos para matar.',
             '16 x 23 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Machado de Assis',
             '1899-01-01',
             'Dom Casmurro',
             'Editora Principis',
             '1ª Edição',
             '978-8572329795',
             208,
             'Clássico da literatura brasileira, Dom Casmurro narra a história de Bentinho e seu amor por Capitu, com a eterna dúvida sobre traição.',
             '14 x 21 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'George Orwell',
             '1949-06-08',
             '1984',
             'Companhia das Letras',
             '1ª Edição',
             '978-8535914849',
             416,
             '1984 é uma distopia clássica que retrata um regime totalitário em que o governo controla tudo, até os pensamentos das pessoas.',
             '14 x 21 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Antoine de Saint-Exupéry',
             '1943-04-06',
             'O Pequeno Príncipe',
             'Agir',
             '1ª Edição',
             '978-8522005230',
             96,
             'O clássico acompanha a história de um pequeno príncipe que viaja por planetas e ensina valiosas lições sobre amizade, amor e a essência da vida.',
             '13,5 x 20,5 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Markus Zusak',
             '2005-01-01',
             'A Menina que Roubava Livros',
             'Intrínseca',
             '1ª Edição',
             '978-8578070455',
             480,
             'Narrado pela Morte, o livro conta a história de Liesel, uma menina alemã que encontra conforto nos livros durante os horrores da Segunda Guerra Mundial.',
             '16 x 23 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Stephenie Meyer',
             '2020-08-04',
             'Sol da Meia-Noite',
             'Intrínseca',
             '1ª Edição',
             '978-6555600056',
             736,
             'Sol da Meia-Noite reconta a história do romance entre Edward e Bella, do universo Crepúsculo, sob o ponto de vista de Edward Cullen, revelando seus pensamentos mais profundos e o conflito interno entre amor e perigo.',
             '16 x 23 cm'
         );



INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Gesley Lins',
             '2023-01-01',
             'Terra do Elefante',
             'Autografia',
             '1ª Edição',
             '978-6559573004',
             276,
             'Terra do Elefante é uma obra de ficção que mistura aventura, drama e elementos históricos, acompanhando uma jornada intensa em busca de liberdade, identidade e descobertas pessoais.',
             '14 x 21 cm'
         );



INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'Robert T. Kiyosaki',
             '1997-04-01',
             'Pai Rico, Pai Pobre',
             'Alta Books',
             '1ª Edição',
             '978-8573123729',
             336,
             'Pai Rico, Pai Pobre é um livro sobre educação financeira que apresenta os ensinamentos do “pai rico” sobre como alcançar a independência financeira por meio de investimentos, imóveis, criação de negócios e educação financeira.',
             '16 x 23 cm'
         );


INSERT INTO livros (
    LIV_AUTOR,
    LIV_ANO,
    LIV_TITULO,
    LIV_EDITORA,
    LIV_EDICAO,
    LIV_ISBN,
    LIV_QTD_PAGINAS,
    LIV_SINOPSE,
    LIV_DIMENSAO
) VALUES (
             'J.R.R. Tolkien',
             '1954-07-29',
             'O Senhor dos Anéis',
             'HarperCollins Brasil',
             '1ª Edição',
             '978-8595084742',
             1216,
             'O Senhor dos Anéis é uma das maiores obras de fantasia épica da literatura mundial, acompanhando a jornada de Frodo Bolseiro para destruir o Um Anel e derrotar Sauron.',
             '16 x 23 cm'
         );
