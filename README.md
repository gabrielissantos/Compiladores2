# Compiladores2

Se mudar a gramática:

java -jar "..\antlr-4.7.2-complete.jar" -package t1cc2 -o "..\Compiladores2\src\t1cc2" "..\Compiladores2\src\t1cc2\LA.g4"


Para executar:

java -jar "..\Compiladores2\dist\T1CC2.jar" <caminho do arquivo teste de entrada> arquivosaida.txt


Para testar:

java -jar "..\CorretorTrabalho1.jar" "java -jar ..\Compiladores2\dist\T1CC2.jar" gcc "..\temp" "..\casosDeTesteT1" "726523, 726588" tudo  
