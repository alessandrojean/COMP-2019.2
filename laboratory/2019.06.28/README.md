# Análise Léxica - Implementação
*Retirado do roteiro de aula do Prof. Francisco Isidro e Prof. Valério
Ramos Batista, CMCC/UFABC.*

## Introdução

Passos para implementar o Analisador Léxico do MicroJAVA:
  
1. Estudar as especificações do MicroJava no Apêndice A:
   - Quais são os tokens da gramática do MicroJAVA?
   - Qual o formato dos nomes, números, caracteres constantes e comentários?
   - Quais as palavras reservadas e os nomes pré-declarados?
2. Criar um pacote `MJ` e salvar os arquivos `Scanner.java` e `Token.java`
   (http://ssw.jku.at/Misc/CC/) neste pacote. Estudar os dois arquivos e
   tentar entender o que foi implementado.
3. Complementar a implementação do analisador léxico (`Scanner.java`) de
   acordo com o que foi visto em aula.
4. Compilar os arquivos `Token.java` e `Scanner.java`.
5. Salvar `TestScanner.java` no pacote `MJ` e compilar o arquivo;
6. Salvar o programa exemplo `sample.mj` e compilar o arquivo
   `TestScanner.java`.

## Características Gerais da Linguagem

O MicroJAVA consiste:

- Programa simples com *campos* e *métodos estáticos*;
- Não tem *classes externas*;
- O *método principal* é chamado de `main()`;
- Constante do tipo `int` e `char`;
- Não tem `string`;
- Todas as *variáveis* são *estáticas*;

## Programa de exemplo

```java
program P
final int size = 10;

class Table {
  int[] pos;
  int[] neg;
}

Table val;

void main()
  int x, i; {
  // Initialize val.
  val = new Table;
  val.pos = new int[size];
  val.neg = new int[size];
  i = 0;

  while (i < size) {
    val.pos[i] = 0;
    val.neg[i] = 0;
    i = i + 1;
  }  

  // Read values.
  read(x);
  while (x != 0) {
    if (x >= 0) {
      val.pos[x] = val.pos[x] + 1;
    } else if (x < 0) {
      val.neg[-x] = val.neg[-x] + 1;
    }
    read(x);
  }
}
```

## Estrutura léxica

*Character classes*:

```
letter     = 'a' .. 'z' | 'A' .. 'Z'
digit      = '0' .. '9'
whiteSpace = ' ' | '\t' | '\r' | '\n'
```

*Terminal classes*:

```
ident     = letter {letter | digit}
number    = digit {digit}
charConst = "'" char "'" // including '\r', '\t', '\n'
```

*Keywords*:

```
program class
if      else    while   read   print   return
void    final   new
```

*Operators*:

```
+       -       *       /       %
==      !=      >       >=      <       <=
(       )       [       ]       {       }
=       ;       ,       .
```

*Comments*:

```
// to the end of line
```

## Referências

- Compiler Construction Course:
  - http://ssw.jku.at/Misc/CC/
