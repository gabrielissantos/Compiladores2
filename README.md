# Compiladores2

Se mudar a gramática:

java -jar ".\Desktop\materias\1. Compiladores 2\antlr\antlr-4.7.2-complete.jar" -package t1cc2 -o "C:\T1\Compiladores2\src\t1cc2" "C:\T1\Compiladores2\src\t1cc2\LA.g4"


Para executar:

java -jar "C:\T1\Compiladores2\dist\T1CC2.jar" "caminho do arquivo teste de entrada" arquivosaida.txt


Para testar:

java -jar "C:\T1\CorretorTrabalho1\CorretorTrabalho1.jar" "java -jar C:\T1\Compiladores2\dist\T1CC2.jar" gcc "c:\T1\temp" "C:\T1\casosDeTesteT1\casosDeTesteT1" "726523, 726588" sintatico   
